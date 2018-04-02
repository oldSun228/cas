package cn.seisys.auth.authentication.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ResourceEntity implements Serializable, RowMapper<ResourceEntity> {
    
	private static final long serialVersionUID = 1L;

	/** 编号 */
	private Long id;
	/** 标识 */
    private String identity;
    /** URL */
    private String url;
    /** 名称 */
    private String name;
    /** 父资源ID */
    private Long parent_id;
    /** 是否显示 */
    private Integer is_show;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public Integer getIs_show() {
		return is_show;
	}

	public void setIs_show(Integer is_show) {
		this.is_show = is_show;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public ResourceEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResourceEntity resource = new ResourceEntity();
		resource.setId(rs.getLong("id"));
		resource.setName(rs.getString("name"));
		resource.setIdentity(rs.getString("identity"));
		resource.setParent_id(rs.getLong("parent_id"));
		resource.setIs_show(rs.getInt("is_show"));
		resource.setUrl(rs.getString("url"));
		return resource;
	}
    


}
