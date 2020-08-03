package com.masmovil.best_sellers.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {

	private Integer id;
	private ItemType type;
	private String name;
	private String description;

	@JsonProperty("sold_units")
	private Integer soldUnits;

	// @JsonIgnore
	@JsonProperty("last_update")
	private LocalDateTime lastUpdate;

	public Item() {
	}

	public Item(Integer id, ItemType type, String name, String description, Integer soldUnits,
			LocalDateTime lastUpdate) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
		this.soldUnits = soldUnits;
		this.lastUpdate = lastUpdate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSoldUnits() {
		return soldUnits;
	}

	public void setSoldUnits(Integer soldUnits) {
		this.soldUnits = soldUnits;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime localDateTime) {
		this.lastUpdate = localDateTime;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", type=" + type + ", name=" + name + ", description=" + description + ", soldUnits="
				+ soldUnits + ", lastUpdate=" + lastUpdate + "]";
	}

}
