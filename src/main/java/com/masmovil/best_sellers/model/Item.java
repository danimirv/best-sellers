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

	private Item(Builder builder) {
		id = builder.id;
		type = builder.type;
		name = builder.name;
		description = builder.description;
		soldUnits = builder.soldUnits;
		lastUpdate = builder.lastUpdate;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private Integer id;
		private ItemType type;
		private String name;
		private String description;
		private Integer soldUnits;
		private LocalDateTime lastUpdate;

		private Builder() {
		}

		public Builder withId(Integer val) {
			id = val;
			return this;
		}

		public Builder withType(ItemType val) {
			type = val;
			return this;
		}

		public Builder withName(String val) {
			name = val;
			return this;
		}

		public Builder withDescription(String val) {
			description = val;
			return this;
		}

		public Builder withSoldUnits(Integer val) {
			soldUnits = val;
			return this;
		}

		public Builder withLastUpdate(LocalDateTime val) {
			lastUpdate = val;
			return this;
		}

		public Item build() {
			return new Item(this);
		}
	}
}
