package com.masmovil.best_sellers.config;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class PostgreConfiguration {

	private PostgreConfiguration() {
	}

	public static PgPool init(Vertx vertx) {
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		return PgPool.pool(vertx, pgConnectOptions(), poolOptions);
	}

	private static PgConnectOptions pgConnectOptions() {
		PgConnectOptions connectOptions = new PgConnectOptions().setPort(5432).setHost("localhost")
				.setDatabase("postgres").setUser("postgres").setPassword("user");
		connectOptions.addProperty("search_path", "bestsellers");
		return connectOptions;
	}
}
