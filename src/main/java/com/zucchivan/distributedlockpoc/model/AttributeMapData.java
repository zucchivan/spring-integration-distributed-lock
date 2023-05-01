package com.zucchivan.distributedlockpoc.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "attribute_map_data")
public class AttributeMapData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Lob
	@Column(name = "xml_data", nullable = false)
	private byte[] xmlData;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "filtering_context_id", nullable = false)
	private FilteringContext filteringContext;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getXmlData() {
		return xmlData;
	}

	public void setXmlData(byte[] xmlData) {
		this.xmlData = xmlData;
	}

	public FilteringContext getFilteringContext() {
		return filteringContext;
	}

	public void setFilteringContext(FilteringContext filteringContext) {
		this.filteringContext = filteringContext;
	}
}