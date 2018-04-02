/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package cn.seisys.auth.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.ThreadContext;
import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.AccountDisabledException;
import org.jasig.cas.authentication.InvalidLoginLocationException;
import org.jasig.cas.authentication.InvalidLoginTimeException;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.google.gson.Gson;

import cn.seisys.auth.authentication.entity.GroupEntity;
import cn.seisys.auth.authentication.entity.OperationLogEntity;
import cn.seisys.auth.authentication.entity.TokenEntity;
import cn.seisys.auth.web.util.DBUtil;
import cn.seisys.auth.web.util.PasswordHelper;

/**
 * Class that if provided a query that returns a password (parameter of query
 * must be username) will compare that password to a translated version of the
 * password provided by the user. If they match, then authentication succeeds.
 * Default password translator is plaintext translator.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 * @since 3.0
 */
public class QueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {
    private static final String IP_REGEX = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

    // URLEncoding
    private static final String URL_ENCODING = "UTF-8";

    // 获取用户信息用SQL
    @NotNull
    private String sqlUser;

    @NotNull
    private String sqlUpdatePassErrorCount;

    @NotNull
    private String sqlUpdateAvailable;

    // 获取用户IP信息用SQL
    @NotNull
    private String sqlUserIp;

    @NotNull
    private String sqlSysIPBlacklist;
    @NotNull
    private String sqlSysIPWhitelist;

    @NotNull
    private String sqlInsertSysIPBlacklist;

    // 获取用户组ID用SQL
    @NotNull
    private String sqlGroupId;

    //  获取角色ID用SQL
    @NotNull
    private String sqlRoleId;

    //  获取角色用SQL
    @NotNull
    private String sqlRole;

    // 获取用户组信息用SQL
    @NotNull
    private String sqlGroup;

    // 修改登录时间
    @NotNull
    private String sqlUpdateLoginTime;
    //  获取组织机构信息用SQL
    //    private String sqlOrganization = "select group_concat(o.id) id,group_concat(o.name) name from sys_organization_user t,sys_organization o  where t.organization_id = o.id and user_id = ?";
    //  获取角色信息用SQL
    //    private String sqlGetRoles = "select role_id from sys_user_role where user_id = ?";
    // 根据用户取得权限列表
    //	private final String sqlGetUserPermissions = "select id,user_id,resource_id,permission_ids from sys_user_resource_permission where user_id = ?";
    // 根据角色取得权限列表
    //	private final String sqlGetRolePermissions = "select id,role_id,resource_id,permission_ids from sys_role_resource_permission where role_id = ?";
    // 根据编号取得权限
    //	private final String sqlGetPermission = "select * from sys_permission where id = ?";

    // 根据编号取得权限
    @NotNull
    private String sqlPermissions;
    // 根据编号取得角色
    //	String sqlGetRole = "select * from sys_role where id = ?";
    // 根据编号取得资源
    //	String sqlGetResource = "select * from sys_resource where id = ?";
    // 取得所有资源
    //	String sqlGetAllResource = "select id,identity,url,name,parent_id,is_show from sys_resource";

    // 新增操作日志
    @NotNull
    private String sqlInsertLog;

    // 新增令牌
    @NotNull
    private String sqlInsertToken;

    // 清空令牌
    @NotNull
    private String sqlDeleteToken;

    // 资源列表
    //	private List<ResourceEntity> resourceList;
    // 资源权限
    //	private Set<String> permissions;
    // Url权限
    //	private Set<String> permissionsUrl;

    private String orgId;

    private String orgName;

    private int maxPasswordErrorCount;

    @PostConstruct
    public void init() {
        System.out.println("权限服务正在启动，开始清除令牌");
        getJdbcTemplate().execute(sqlDeleteToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Principal authenticateUsernamePasswordInternal(final String username, final String password) throws GeneralSecurityException, PreventedException {

        Map<String, Object> attrs = new HashMap<String, Object>();
        // 当前时间
        Date now = new Date();

        // 插入登入日志
        OperationLogEntity log = new OperationLogEntity();
        log.setUserid(username);
        log.setOperation_uri("/login");
        log.setOperation_name("用户登录");
        log.setAccess_time(now);

        // 取得客户端IP
        Object objClientIP = ThreadContext.get("clientIP");
        String clientIP = null;
        if (objClientIP != null) {
            clientIP = objClientIP.toString();

            if (clientIP.matches(IP_REGEX)) {
                //黑名单
                List<String> sysIPBlackList = getSysIPBlacklist();
                //白名单
                List<String> sysIPWhiteList = getSysIPWhitelist();
                if (CollectionUtils.isNotEmpty(sysIPWhiteList)) {
                    if (!sysIPWhiteList.contains(clientIP)) {
                        if (CollectionUtils.isNotEmpty(sysIPBlackList)) {
                            if (sysIPBlackList.contains(clientIP)) {
                                throw new InvalidLoginLocationException("IP地址已被列入黑名单");
                            }
                        }
                    }
                } else {
                    if (CollectionUtils.isNotEmpty(sysIPBlackList)) {
                        if (sysIPBlackList.contains(clientIP)) {
                            throw new InvalidLoginLocationException("IP地址已被列入黑名单");
                        }
                    }
                }

            }
        }
        log.setTerminal_ip(clientIP);


        // 取得当前用户账号会话数
//    	Object objUserSessionCount = ThreadContext.get("userSessionCount");
//        Integer userSessionCount = 0;
//    	if (objUserSessionCount != null) {
//    		userSessionCount = (Integer) objUserSessionCount;
//    	}
        try {
            // 进行用户名/密码对校验
            Map<String, Object> row = getJdbcTemplate().queryForMap(this.sqlUser, username);

            // 将数据复制到属性Map，key转换为小写
            for (String key : row.keySet()) {
                Object value = row.get(key);
                attrs.put(key.toLowerCase(), value);
            }

            // 用户编号
            Object id = attrs.get("id");
            if (id != null) {
                log.setUser_id(id.toString());
            }
            // 用户姓名
            Object userRealName = attrs.get("username");
            if (userRealName != null) {
                log.setUsername(userRealName.toString());
            }
            log.setApp_id("0");
            log.setApp_name("authority");

            String dbPassword = attrs.get("password").toString();    //密码
            String salt = attrs.get("salt").toString();    //盐
            boolean available = DBUtil.convertBigDecimal2Boolean(attrs.get("available"));    //是否可用
            boolean deleted = DBUtil.convertBigDecimal2Boolean(attrs.get("deleted"));    //是否删除

            if (null != attrs.get("password_valid_time")) {
                Date passwordValidTime = (Date) attrs.get("password_valid_time"); //密码有效期
            }

            int passErrorCount = 0;
            if (null != attrs.get("pass_error_count")) {
                passErrorCount = DBUtil.convertBigDecimal2Integer(attrs.get("pass_error_count")); //密码错误次数
            }

            Date validTimeBegin = (Date) attrs.get("valid_time_begin");    //有效开始时间
            Date validTimeEnd = (Date) attrs.get("valid_time_end");    //有效结束时间

            final String encryptedPassword = new PasswordHelper().encryptPassword(password, salt);    //加密密码

            if (!dbPassword.equals(encryptedPassword)) {    // 密码错误
                log.setStatus("failed");

                //更新密码错误次数据
                passErrorCount = passErrorCount + 1;

                getJdbcTemplate().update(sqlUpdatePassErrorCount, passErrorCount, username);

                //错误次断超限，锁定用户
                if (passErrorCount >= maxPasswordErrorCount) {
                    getJdbcTemplate().update(sqlUpdateAvailable, 0, username);

                    if (null != clientIP && clientIP.matches(IP_REGEX)) {
                        getJdbcTemplate().update(sqlInsertSysIPBlacklist, UUID.randomUUID().toString(), clientIP);
                    }
                    throw new FailedLoginException("用户名密码错误，错误次数超限，用户被锁定");
                }

                if (passErrorCount == 1) {
                    throw new FailedLoginAndErrorOneNumException();
                } else if (passErrorCount == 2) {
                    throw new FailedLoginAndErrorTwoNumException();
                } else if (passErrorCount == 3) {
                    throw new FailedLoginAndErrorThreeNumException();
                } else if (passErrorCount == 4) {
                    throw new FailedLoginAndErrorFourNumException();
                } else if (passErrorCount == 5) {
                    throw new FailedLoginAndErrorFiveNumException();
                }

            } else if (!available) {    // 用户已锁定
                log.setStatus("failed");
                throw new AccountLockedException("账号已加入黑名单");
            } else if (deleted) {    // 用户已删除
                log.setStatus("failed");
                throw new AccountDisabledException("账号已删除");
            } else if ((validTimeBegin != null && now.before(validTimeBegin)) || (validTimeEnd != null && now.after(validTimeEnd))) {    // 当前不在有效期内
                log.setStatus("failed");
                throw new InvalidLoginTimeException("账号已过有效期");
            }
            // 进行用户登录IP校验
            Object accessIpRestrict = attrs.get("access_ip_restrict");    // 是否启用IP限制
            // 用户IP地址无效
            if (accessIpRestrict != null && DBUtil.convertBigDecimal2Boolean(accessIpRestrict) && clientIP != null) {
                String accessIPs = getUserIpByUserId(id);
                String[] ipList = accessIPs.split(",", -1);
                Map<String, String> mapIPs = new HashMap<String, String>();
                for (String ip : ipList) {
                    mapIPs.put(ip, ip);
                }
                if (!mapIPs.containsKey(clientIP)) {
                    log.setStatus("failed");
                    throw new InvalidLoginLocationException("当前登录IP不受信任");
                }
            }
            // 校验用户账号登录数是否超过上限
            // TODO 暂时不用，需要踢用户下线功能
            //            Object objMaxOnline = row.get("max_online");
            //            Integer maxOnline = 0;
            //            if (objMaxOnline != null) {
            //            	maxOnline = (Integer) objMaxOnline;
            //            }
            //            if (maxOnline > 0 && userSessionCount >= maxOnline) {
            //            	log.setStatus("failed");
            //                throw new MaxOnlineException("账号连接数已到达上限");
            //            }

            //验证通过，归零密码错误次数
            getJdbcTemplate().update(sqlUpdatePassErrorCount, 0, username);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //上一次登录时间
            String beforeTime = (String) attrs.get("property1");
            if (StringUtils.isNotEmpty(beforeTime)) {
                //记录登录时间
                getJdbcTemplate().update(sqlUpdateLoginTime, sdf.format(new Date()), beforeTime, username);
            } else {
                getJdbcTemplate().update(sqlUpdateLoginTime, sdf.format(new Date()), sdf.format(new Date()), username);
            }


            // 验证通过，获取用户信息及权限信息，并设置到返回参数
            attrs.put("available", available ? "1" : "0");
            attrs.put("deleted", deleted ? "1" : "0");
            // 取得用户所属组织机构ID
            //            OrganizationEntity org = getOrgByUserId(id);
            //            if (org != null) {
            //                row.put("organizationId", org.getId().toString());
            //                row.put("organizationName", org.getName());
            //            }
            // 取得用户所属用户组ID
            attrs.put("groupId", getGroupIdByUserId(id));
            // 取得用户组信息
            attrs.put("groups", getGroupByUserId(id));
            // 取得用户所属角色
            attrs.put("roles", getRoleIdsByUserId(id));
            // 取得用户所属角色
            attrs.put("roleSigns", getRolesByUserId(id));
            // 取得用户拥有的权限列表
            //            getPermissionsByUserId(id);
            //        	row.put("permissions", convSetToString(permissions));
//            String permissionString = getPermissions(id);
//            attrs.put("permissions", permissionString);
            attrs.put("permissions", null);

            log.setOrganization_id(orgId);
            log.setOrganization_name(orgName);
            log.setStatus("normal");

        } catch (final IncorrectResultSizeDataAccessException e) {
            log.setStatus("failed");
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            } else {
                throw new FailedLoginException("Multiple records found for " + username);
            }
        } catch (final DataAccessException e) {
            log.setStatus("failed");
            throw new PreventedException("SQL exception while executing query for " + username, e);
        } finally {
//            insertOperationLog(log);
        }
        //    	row.put("permissionsUrl", permissionsUrl);
        // 取得用户菜单
/*    	try {
            row.put("menus", getMenuList(id));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}*/
        // 插入令牌表
        TokenEntity token = new TokenEntity();
        token.setUserid(username);
        token.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
        token.setCreatetime(now);
        token.setLoginip(clientIP);
        insertToken(token);
        attrs.put("token", token.getToken());

        attrs = encodeResult(attrs);
        attrs.remove("password");
        attrs.remove("salt");
        return new SimplePrincipal(username, attrs);
    }

    public void setSqlUser(String sqlUser) {
        this.sqlUser = sqlUser;
    }

    public void setSqlUpdatePassErrorCount(String sqlUpdatePassErrorCount) {
        this.sqlUpdatePassErrorCount = sqlUpdatePassErrorCount;
    }

    public void setSqlUpdateAvailable(String sqlUpdateAvailable) {
        this.sqlUpdateAvailable = sqlUpdateAvailable;
    }

    public void setSqlGroup(String sqlGroup) {
        this.sqlGroup = sqlGroup;
    }

    public void setSqlUserIp(String sqlUserIp) {
        this.sqlUserIp = sqlUserIp;
    }

    public void setSqlSysIPBlacklist(String sqlSysIPBlacklist) {
        this.sqlSysIPBlacklist = sqlSysIPBlacklist;
    }

    public void setSqlSysIPWhitelist(String sqlSysIPWhitelist) {
        this.sqlSysIPWhitelist = sqlSysIPWhitelist;
    }

    public void setSqlInsertSysIPBlacklist(String sqlInsertSysIPBlacklist) {
        this.sqlInsertSysIPBlacklist = sqlInsertSysIPBlacklist;
    }

    public void setSqlGroupId(String sqlGroupId) {
        this.sqlGroupId = sqlGroupId;
    }

    public void setSqlRoleId(String sqlRoleId) {
        this.sqlRoleId = sqlRoleId;
    }

    public void setSqlRole(String sqlRole) {
        this.sqlRole = sqlRole;
    }

    public void setSqlInsertLog(String sqlInsertLog) {
        this.sqlInsertLog = sqlInsertLog;
    }

    public String getSqlInsertToken() {
        return sqlInsertToken;
    }

    public void setSqlInsertToken(String sqlInsertToken) {
        this.sqlInsertToken = sqlInsertToken;
    }

    public String getSqlDeleteToken() {
        return sqlDeleteToken;
    }

    public void setSqlDeleteToken(String sqlDeleteToken) {
        this.sqlDeleteToken = sqlDeleteToken;
    }

    public void setMaxPasswordErrorCount(int maxPasswordErrorCount) {
        this.maxPasswordErrorCount = maxPasswordErrorCount;
    }

    public void setSqlUpdateLoginTime(String sqlUpdateLoginTime) {
        this.sqlUpdateLoginTime = sqlUpdateLoginTime;
    }

    //    /**
    //     * 根据用户ID取得组织机构ID
    //     *
    //     * @param userId
    //     *
    //     * @return 用户所属组织机构ID
    //     */
    //    private OrganizationEntity getOrgByUserId(Object userId) {
    //    	List<OrganizationEntity> orgList =
    //    			getJdbcTemplate().query(sqlOrganization, new OrganizationEntity(), userId);
    //    	if (orgList.size() > 0) {
    //    		return orgList.get(0);
    //    	} else {
    //    		return null;
    //    	}
    //    }
    public void setSqlPermissions(String sqlPermissions) {
        this.sqlPermissions = sqlPermissions;
    }

    /**
     * 根据用户ID取得用户组ID
     *
     * @param userId
     * @return 用户所属用户组ID（可能有复数个，以逗号分割）
     */
    private String getGroupIdByUserId(Object userId) {
        List<String> groupIdList = getJdbcTemplate().queryForList(sqlGroupId, String.class, userId);
        return StringUtils.strip(groupIdList.toString(), "[]");
    }

    /**
     * 根据用户ID取得用户组信息
     *
     * @param userId
     * @return 用户所属用户组
     * @throws UnsupportedEncodingException
     */
    private String getGroupByUserId(Object userId) {
        List<GroupEntity> groupList = getJdbcTemplate().query(sqlGroup, new GroupEntity(), userId);
        for (GroupEntity group : groupList) {
            if (group.getIdentity() != null && group.getIdentity().equals("org")) {
                orgId = group.getId().toString();
                orgName = group.getName();
                break;
            }
        }
        Gson gson = new Gson();
        String resultJson = gson.toJson(groupList);
        return resultJson;
    }

    /**
     * 根据用户ID取得角色列表
     *
     * @param userId
     * @return 用户所属角色名称（可能有复数个，以逗号分割）
     */
    private String getRoleIdsByUserId(Object userId) {
        List<String> roleIdList = getJdbcTemplate().queryForList(sqlRoleId, String.class, userId);
        return StringUtils.strip(roleIdList.toString(), "[]");
    }

    /**
     * 根据用户ID取得角色列表
     *
     * @param userId
     * @return 用户所属角色名称（可能有复数个，以逗号分割）
     */
    private String getRolesByUserId(Object userId) {
        List<String> roleList = getJdbcTemplate().queryForList(sqlRole, String.class, userId);
        return StringUtils.strip(roleList.toString(), "[]");
    }

    /**
     * 根据用户编号取得有效IP地址
     *
     * @param user_id
     * @return 有效IP地址（可能有复数个，以逗号分割）
     */
    private String getUserIpByUserId(Object user_id) {
        List<String> userIpList = getJdbcTemplate().queryForList(sqlUserIp, String.class, user_id);
        return StringUtils.strip(userIpList.toString(), "[]");
    }

    /**
     * IP黑名单
     *
     * @return
     */
    private List<String> getSysIPBlacklist() {
        return getJdbcTemplate().queryForList(sqlSysIPBlacklist, String.class);
    }

    /**
     * IP白名单
     *
     * @return
     */
    private List<String> getSysIPWhitelist() {
        return getJdbcTemplate().queryForList(sqlSysIPWhitelist, String.class);
    }

    /**
     * 根据用户ID取得权限列表
     * @param userId
     * @return 用户对资源拥有的权限列表（以逗号分割）
     */
/*    private String getPermissionsByUserId(Object userId) {
        permissions = new HashSet<String>();
//        permissionsUrl = new HashSet<String>();
        resourceList = getAllResource();
        
    	// 取得用户资源权限关联列表
    	List<UserResourcePermissionEntity> urpList = 
    			getJdbcTemplate().query(sqlGetUserPermissions, new UserResourcePermissionEntity(), userId);
    	for (UserResourcePermissionEntity urp : urpList) {
    		// 资源ID
    		String resouce_id = urp.getResource_id().toString();
            // 匹配资源权限对
            matchResourcePermissions(resouce_id, urp.getPermission_ids());
    	}
    	
        List<String> roleIds = getJdbcTemplate().queryForList(sqlGetRoles, String.class, userId);
        for (String roleId : roleIds) {
        	// 根据角色取得权限列表
        	List<RoleResourcePermissionEntity> rrpList = 
        			getJdbcTemplate().query(sqlGetRolePermissions, new RoleResourcePermissionEntity(), roleId);
            for (RoleResourcePermissionEntity rrp : rrpList) {
        		// 资源ID
        		String resouce_id = rrp.getResource_id().toString();
                matchResourcePermissions(resouce_id, rrp.getPermission_ids());
            }
        }

        StringBuffer  result = new StringBuffer();
        for (String permission : permissions) {
        	result.append(permission + ",");
        }
        if (result.length() > 0) {
        	result.deleteCharAt(result.length() - 1);
        }
        
        return result.toString();
    }*/

    /**
     * 匹配资源-权限
     * @param resource_id
     * @param permissionIds
     * @throws Exception
     */
/*	private void matchResourcePermissions(String resource_id, String permissionIds) {
        ResourceEntity resource = getResource(resource_id);
        if (resource == null) {
        	return;
        }
//        String actualResourceIdentity = findActualResourceIdentity(resource);

        //不可用 即没查到 或者标识字符串不存在
        if (resource == null || StringUtils.isEmpty(resource.getIdentity()) 
        		|| Boolean.FALSE.equals(resource.getIs_show())) {
            return;
        }

        if (resource.getIdentity().equals("RequestCertification")) {
        	System.out.println("RequestCertification");
        }
        
        // 如权限列表不为空则进行解析
        if (permissionIds != null) {
            for (String permissionId : permissionIds.split(",", -1)) {
                List<PermissionEntity> permissionList = getJdbcTemplate().query(sqlGetPermission, new PermissionEntity(), permissionId);
                if (permissionList.size() == 0) {
                	continue;
                }

                PermissionEntity permission = permissionList.get(0);
                
                //不可用
                if (permission == null || Boolean.FALSE.equals(permission.getIs_show())) {
                    continue;
                }
                permissions.add(resource.getIdentity() + ":" + permission.getPermission());
//                if (!StringUtils.isEmpty(resource.getUrl())) {
//                	permissionsUrl.add(resource.getUrl() + ":" + permission.getPermission());
//                }
            }
        }	
	}*/

    /**
     * 得到真实的资源标识  即 父亲:儿子
     * @param resource
     * @return
     * @throws Exception
     */
/*    private String findActualResourceIdentity(ResourceEntity resource) {

        if(resource == null) {
            return null;
        }

        StringBuilder s = new StringBuilder(resource.getIdentity());

        boolean hasResourceIdentity = !StringUtils.isEmpty(resource.getIdentity());

        if (resource.getParent_id() != null) {
	        ResourceEntity parent = getResource(resource.getParent_id().toString());
	        while(parent != null) {
	            if(!StringUtils.isEmpty(parent.getIdentity())) {
	                s.insert(0, parent.getIdentity() + ":");
	                hasResourceIdentity = true;
	            }
	            if (parent.getParent_id() == null) {
	            	break;
	            }
	            parent = getResource(parent.getParent_id().toString());
	        }
        }

        //如果用户没有声明 资源标识  且父也没有，那么就为空
        if(!hasResourceIdentity) {
            return "";
        }


        //如果最后一个字符是: 因为不需要，所以删除之
        int length = s.length();
        if(length > 0 && s.lastIndexOf(":") == length - 1) {
            s.deleteCharAt(length - 1);
        }

        //如果有儿子 最后拼一个*
        boolean hasChildren = false;
        for(ResourceEntity r : resourceList) {
            if(resource.getId().equals(r.getParent_id())) {
                hasChildren = true;
                break;
            }
        }
        if(hasChildren) {
            s.append(":*");
        }

        return s.toString();
    }*/

    /**
     * 根据编号取得资源
     * @param id
     * @return
     */
/*    private ResourceEntity getResource(String id) {
        for (ResourceEntity resource : resourceList) {
    		if (resource.getId().toString().equals(id)) {
    			return resource;
    		}
    	}
    	return null;
    }*/

    /**
     * 取得全部资源
     *
     * @return
     */
/*    private List<ResourceEntity> getAllResource() {
        return getJdbcTemplate().query(sqlGetAllResource, new ResourceEntity());
    }*/

    //    /**
    //     * 取得菜单
    //     *
    //     * @param userId
    //     *
    //     * @return
    //     *
    //     * @throws JsonProcessingException
    //     */
/*    private String getMenuList(Object id) throws JsonProcessingException {
        List<MenuEntity> menuList =
    			getJdbcTemplate().query(sqlGetMenu, new MenuEntity(), id, id);
		ObjectMapper objectMapper = new ObjectMapper();
		String resultJson = objectMapper.writeValueAsString(menuList);	
    	return resultJson;
    }*/
    
/*    private String convSetToString(Set<String> permissions) {
    	StringBuffer permissionString = new StringBuffer();
    	for (String permission : permissions) {
    		permissionString.append(permission + ",");
    	}
    	if (permissionString.length() > 0) {
    		permissionString.deleteCharAt(permissionString.length() - 1);
    	}
    	return permissionString.toString();
    }*/
    private Map<String, Object> encodeResult(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            for (String key : data.keySet()) {
                Object value = data.get(key);
                if (value != null) {
                    result.put(key, URLEncoder.encode(value.toString(), URL_ENCODING));
                } else {
                    result.put(key, "");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void insertOperationLog(final OperationLogEntity log) {
        PreparedStatementSetter param = new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, log.getApp_id());
                ps.setObject(2, log.getApp_name());
                ps.setObject(3, log.getUser_id());
                ps.setObject(4, log.getUserid());
                ps.setObject(5, log.getUsername());
                ps.setObject(6, log.getTerminal_ip());
                ps.setObject(7, log.getOperation_uri());
                ps.setObject(8, log.getOperation_name());
                ps.setObject(9, log.getStatus());
                ps.setObject(10, new java.sql.Timestamp(log.getAccess_time().getTime()));
                ps.setObject(11, log.getOrganization_id());
                ps.setObject(12, log.getOrganization_name());
            }
        };
        getJdbcTemplate().update(sqlInsertLog, param);
    }

    private void insertToken(final TokenEntity token) {
        PreparedStatementSetter param = new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                // 用户名
                ps.setObject(1, token.getUserid());
                // 令牌
                ps.setObject(2, token.getToken());
                // 创建时间
                ps.setObject(3, new java.sql.Timestamp(token.getCreatetime().getTime()));
                // 描述
                ps.setObject(4, token.getDescription());
                // 登录IP
                ps.setObject(5, token.getLoginip());
            }
        };
        getJdbcTemplate().update(sqlInsertToken, param);
    }

    private String getPermissions(Object user_id) {
        StringBuffer permissions = new StringBuffer();
        List<String> permissionList = getJdbcTemplate().queryForList(sqlPermissions, String.class, user_id, user_id);
        for (String permission : permissionList) {
            permissions.append(permission + ",");
        }
        if (permissions.length() > 0) {
            permissions.deleteCharAt(permissions.length() - 1);
        }
        return permissions.toString();
    }
}
