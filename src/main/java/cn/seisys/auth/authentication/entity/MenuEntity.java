package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MenuEntity implements Serializable, RowMapper<MenuEntity> {

	private static final long serialVersionUID = 1L;

	/** 编号 */
	private String menuId;
	/** 标识 */
	private String identity;
	/** 名称 */
	private String menuDesc;
	/** URL */
	private String menuUrl;
	/** level */
	private String level;
	/** 图标 */
	private String icon;
	/** 图标样式 */
	private String iconCls;
	/** 父资源ID */
	private String parentId;

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getMenuDesc() {
		return menuDesc;
	}

	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	@Override
	public MenuEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		MenuEntity data = new MenuEntity();
		data.setMenuId(rs.getString("menuId"));
		data.setIdentity(rs.getString("identity"));
		data.setIcon(rs.getString("icon"));
		data.setMenuDesc(rs.getString("menuDesc"));
		data.setParentId(rs.getString("parentId"));
		data.setMenuUrl(rs.getString("menuUrl"));
		data.setLevel(rs.getString("level"));
		return data;
	}

}
