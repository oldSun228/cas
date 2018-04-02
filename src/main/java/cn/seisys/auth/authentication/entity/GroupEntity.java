package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class GroupEntity implements Serializable, RowMapper<GroupEntity> {
    
	private static final long serialVersionUID = 1L;

	/** 编号 */
	private Long id;
	/** 标识 */
	private String identity;
	/** 标识 */
    private String name;
	/** 父编号 */
	private String parentId;
	/** 序号 */
	private String no;
	/** 层级路径 */
	private String path;
	/** 层级 */
	private String level;
	/** 是否可用 */
	private String available;
	/** 是否为默认 */
	private String isDefault;
	/** 备用属性1 */
	private String property1;
	/** 备用属性2 */
	private String property2;
	/** 备用属性3 */
	private String property3;
	/** 备用属性4 */
	private String property4;
	/** 备用属性5 */
	private String property5;
	/** 备用属性6 */
	private String property6;
	/** 备用属性7 */
	private String property7;
	/** 备用属性8 */
	private String property8;
	/** 备用属性9 */
	private String property9;
	/** 备用属性10 */
	private String property10;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
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
	public String getProperty6() {
		return property6;
	}
	public void setProperty6(String property6) {
		this.property6 = property6;
	}
	public String getProperty7() {
		return property7;
	}
	public void setProperty7(String property7) {
		this.property7 = property7;
	}
	public String getProperty8() {
		return property8;
	}
	public void setProperty8(String property8) {
		this.property8 = property8;
	}
	public String getProperty9() {
		return property9;
	}
	public void setProperty9(String property9) {
		this.property9 = property9;
	}
	public String getProperty10() {
		return property10;
	}
	public void setProperty10(String property10) {
		this.property10 = property10;
	}
	@Override
	public GroupEntity mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		GroupEntity data = new GroupEntity();
		data.setId(rs.getLong("id"));
		data.setIdentity(rs.getString("identity"));
		data.setName(rs.getString("name"));
		data.setParentId(rs.getString("parent_id"));
		data.setNo(rs.getString("no"));
		data.setPath(rs.getString("path"));
		try {
			data.setLevel(rs.getString("depth_level"));
		} catch (Exception e) {
			data.setLevel(rs.getString("level"));
		}
		data.setAvailable(rs.getBoolean("available") ? "1" : "0");
		data.setIsDefault(rs.getBoolean("is_default") ? "1" : "0");
		data.setIsDefault(rs.getString("is_default"));
		data.setProperty1(rs.getString("property1"));
		data.setProperty2(rs.getString("property2"));
		data.setProperty3(rs.getString("property3"));
		data.setProperty4(rs.getString("property4"));
		data.setProperty5(rs.getString("property5"));
		data.setProperty6(rs.getString("property6"));
		data.setProperty7(rs.getString("property7"));
		data.setProperty8(rs.getString("property8"));
		data.setProperty9(rs.getString("property9"));
		data.setProperty10(rs.getString("property10"));
		return data;
	}
}
