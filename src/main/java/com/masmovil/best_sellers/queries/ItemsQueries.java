package com.masmovil.best_sellers.queries;

public class ItemsQueries {

	public static final String TOP_TEN_UPDATE_EACH_HOUR_QUERY = "select * from bestsellers.item where last_update <= (SELECT date_trunc('hour', NOW())) - '1 day'::INTERVAL order by sold_units desc limit 10";
	public static final String TOP_TEN_UPDATE_ONCE_PER_DAY_QUERY = "select * from bestsellers.item where last_update <= (SELECT date_trunc('day', NOW())) order by sold_units desc limit 10";
	public static final String TOP_TEN_REAL_TIME_QUERY = "select * from bestsellers.item where last_update <= NOW() - '1 day'::INTERVAL order by sold_units desc limit 10";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String TYPE = "type";
	public static final String SOLD_UNITS = "sold_units";
	public static final String LAST_UPDATE = "last_update";

}
