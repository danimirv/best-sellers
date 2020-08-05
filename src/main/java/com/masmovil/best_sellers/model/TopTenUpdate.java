package com.masmovil.best_sellers.model;

import com.masmovil.best_sellers.queries.ItemsQueries;

public enum TopTenUpdate {

	EACH_HOUR{
		@Override
		public String getItemsQuery(){
			return ItemsQueries.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
		}
	}, ONCE_PER_DAY{
		@Override
		public String getItemsQuery(){
			return ItemsQueries.TOP_TEN_UPDATE_ONCE_PER_DAY_QUERY;
		}
	}, REAL_TIME{
		@Override
		public String getItemsQuery(){
			return ItemsQueries.TOP_TEN_REAL_TIME_QUERY;
		}
	};

	public String getItemsQuery(){
		return ItemsQueries.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
	}
}
