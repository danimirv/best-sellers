package com.masmovil.best_sellers.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BestSellerRequest {

	@JsonProperty("update_time")
	public TopTenUpdate updateTime;

	public BestSellerRequest(TopTenUpdate updateTime) {
		super();
		this.updateTime = updateTime;
	}

	public TopTenUpdate getUpdateTime() {
		return updateTime;
	}
}
