package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户操作日志表Bo
 * 
 * @author 陈志华
 * @version 1.0
 * 创建时间： 2016-07-03
 */

public class OperationLogEntity implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	/** 编号 */
	private Long id;
    /** 应用编号 */
    private String app_id;
    /** 应用名称 */
    private String app_name;
    /** 组织编号 */
    private String organization_id;
    /** 组织名称 */
    private String organization_name;
    /** 用户编号 */
    private String user_id;
    /** 用户标识 */
    private String userid;
    /** 用户名 */
    private String username;
    /** 中断IP地址 */
    private String terminal_ip;
    /** 操作URI */
    private String operation_uri;
    /** 操作名称 */
    private String operation_name;
    /** 用户代理信息 */
    private String user_agent;
    /** 状态 */
    private String status;
    /** 访问时间 */
    private Date access_time;
    /** 会话ID */
    private String session;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUser_agent() {
		return user_agent;
	}
	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public Date getAccess_time() {
		return access_time;
	}
	public void setAccess_time(Date access_time) {
		this.access_time = access_time;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public String getOrganization_name() {
		return organization_name;
	}
	public void setOrganization_name(String organization_name) {
		this.organization_name = organization_name;
	}
	public String getTerminal_ip() {
		return terminal_ip;
	}
	public void setTerminal_ip(String terminal_ip) {
		this.terminal_ip = terminal_ip;
	}
	public String getOperation_uri() {
		return operation_uri;
	}
	public void setOperation_uri(String operation_uri) {
		this.operation_uri = operation_uri;
	}
	public String getOperation_name() {
		return operation_name;
	}
	public void setOperation_name(String operation_name) {
		this.operation_name = operation_name;
	}
}
