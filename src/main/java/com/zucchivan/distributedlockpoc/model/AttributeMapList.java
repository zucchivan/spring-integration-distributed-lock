package com.zucchivan.distributedlockpoc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "attribute_map_data")
public class AttributeMapList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Lob
	@Column(name = "xml_data", nullable = false)
	private byte[] xmlData;

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
}