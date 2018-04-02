package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PermissionEntity implements Serializable, RowMapper<PermissionEntity> {
    
	private static final long serialVersionUID = 1L;

	/** 编号 */
	private Long id;
	/** 标识 */
    private String permission;
    /** 名称 */
    private String name;
    /** 是否显示 */
    private Integer is_show;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIs_show() {
		return is_show;
	}
	public void setIs_show(Integer is_show) {
		this.is_show = is_show;
	}
	@Override
	public PermissionEntity mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		PermissionEntity data = new PermissionEntity();
		data.setId(rs.getLong("id"));
		data.setName(rs.getString("name"));
		data.setPermission(rs.getString("permission"));
		data.setIs_show(rs.getInt("is_show"));
		return data;
	}
}
