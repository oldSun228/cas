package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 令牌表Entity
 * 
 * @author 陈志华
 * @version 1.0
 * 创建时间： 2017-05-04
 */

public class TokenEntity implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	/** 用户名 */
	private String userid;
    /** 令牌 */
    private String token;
    /** 应用名称 */
    private Date createtime;
    /** 描述 */
    private String description;
    /** 失效时间 */
    private Long invalidtime;
    /** 令牌有效时间 */
    private String tokenvalidtime;
    /** 登录IP */
    private String loginip;
    /** 备用字段1 */
    private String property1;
    /** 备用字段2 */
    private String property2;
    /** 备用字段3 */
    private String property3;
    /** 备用字段4 */
    private String property4;
    /** 备用字段5 */
    private String property5;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getInvalidtime() {
		return invalidtime;
	}
	public void setInvalidtime(Long invalidtime) {
		this.invalidtime = invalidtime;
	}
	public String getTokenvalidtime() {
		return tokenvalidtime;
	}
	public void setTokenvalidtime(String tokenvalidtime) {
		this.tokenvalidtime = tokenvalidtime;
	}
	public String getProperty1() {
		return property1;
	}
	public void setProperty1(String property1) {
		this.property1 = property1;
	}
	public String getProperty2() {
		return property2;
	}
	public void setProperty2(String property2) {
		this.property2 = property2;
	}
	public String getProperty3() {
		return property3;
	}
	public void setProperty3(String property3) {
		this.property3 = property3;
	}
	public String getProperty4() {
		return property4;
	}
	public void setProperty4(String property4) {
		this.property4 = property4;
	}
	public String getProperty5() {
		return property5;
	}
	public void setProperty5(String property5) {
		this.property5 = property5;
	}
	public String getLoginip() {
		return loginip;
	}
	public void setLoginip(String loginip) {
		this.loginip = loginip;
	}
}
