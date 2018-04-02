package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class OrganizationEntity implements Serializable, RowMapper<OrganizationEntity> {
    
	private static final long serialVersionUID = 1L;

	/** 编号 */
	private Long id;
	/** 标识 */
    private String name;
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
	@Override
	public OrganizationEntity mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		OrganizationEntity data = new OrganizationEntity();
		data.setId(rs.getLong("id"));
		data.setName(rs.getString("name"));
		return data;
	}
}
