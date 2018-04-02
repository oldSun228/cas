package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserResourcePermissionEntity implements Serializable, RowMapper<UserResourcePermissionEntity> {
    
	private static final long serialVersionUID = 1L;
	
	/** 编号 */
	private Long id;
    /** 用户编号 */
    private Long user_id;
    /** 资源编号 */
    private Long resource_id;
    /** 权限编号 */
    private String permission_ids;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public Long getResource_id() {
		return resource_id;
	}
	public void setResource_id(Long resource_id) {
		this.resource_id = resource_id;
	}
	public String getPermission_ids() {
		return permission_ids;
	}
	public void setPermission_ids(String permission_ids) {
		this.permission_ids = permission_ids;
	}
	@Override
	public UserResourcePermissionEntity mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		UserResourcePermissionEntity data = new UserResourcePermissionEntity();
		data.setId(rs.getLong("id"));
		data.setUser_id(rs.getLong("user_id"));
		data.setResource_id(rs.getLong("resource_id"));
		data.setPermission_ids(rs.getString("permission_ids"));
		return data;
	}

}
