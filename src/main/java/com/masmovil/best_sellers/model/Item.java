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
	@JsonProperty("last_update")
	private LocalDateTime lastUpdate;
	
	private Item() {}

	public Item(Integer id) {
		this.id = id;
	}
	
	public Item withType(ItemType type) {
		this.type = type;
		return this;
	}
	
	public Item withName(String name) {
		this.name = name;
		return this;
	}
	
	public Item withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Item withSoldUnits(Integer soldUnits) {
		this.soldUnits = soldUnits;
		return this;
	}
	
	public Item withLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}
		
	public Item build() {
		Item item = new Item();
		item.id = this.id;
		item.type = this.type;
		item.name = this.name;
		item.description = this.description;
		item.soldUnits = this.soldUnits;
		item.lastUpdate = this.lastUpdate;		
		return item;
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
