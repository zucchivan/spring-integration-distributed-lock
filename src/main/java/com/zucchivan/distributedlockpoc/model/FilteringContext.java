package com.zucchivan.distributedlockpoc.model;

import generated.SampleData;
import jakarta.persistence.*;

@Entity
@Table(name = "filtering_context")
public class FilteringContext {

	public FilteringContext() {}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "region", nullable = false)
	private String region;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "attribute_map_id", nullable = false)
	private AttributeMapList attributeMapList;

	public FilteringContext(SampleData.FilteringContext filteringContext) {
		this.type = filteringContext.getType();
		this.region = filteringContext.getRegion();
	}

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