package com.zucchivan.distributedlockpoc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "filtering_context")
public class FilteringContext {

	public FilteringContext() {}

	public FilteringContext(Integer id, String type, String region, AttributeMapData attributeMapData) {
		this.id = id;
		this.type = type;
		this.region = region;
		this.attributeMapData = attributeMapData;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "region", nullable = false)
	private String region;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "attribute_map_id", nullable = false)
	private AttributeMapData attributeMapData;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}