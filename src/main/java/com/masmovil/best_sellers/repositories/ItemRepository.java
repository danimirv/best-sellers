package com.masmovil.best_sellers.repositories;

import static com.masmovil.best_sellers.util.ItemUtil.*;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.core.json.JsonArray;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import com.masmovil.best_sellers.config.PostgreConfiguration;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.masmovil.best_sellers.model.TOP_TEN_UPDATE;
import com.masmovil.best_sellers.util.ItemUtil;

public class ItemRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemRepository.class);

	private PgPool pgClient;

	public ItemRepository() {
		this(PostgreConfiguration.init(Vertx.currentContext().owner()));
	}

	public ItemRepository(PgPool pgClient) {
		this.pgClient = pgClient;
	}

	public Single<JsonArray> topTen(TOP_TEN_UPDATE update) {
		LOGGER.info("Entry top-ten list");
		Single<SqlConnection> connection = pgClient.rxGetConnection();
		return connection.flatMapObservable(conn -> {
			return conn.rxPrepare(getUpdateQuery(update)).flatMapObservable(preparedStatement -> {
				return preparedStatement.createStream(50).toObservable();
			});
		}).map(row -> {
			return new Item(row.getInteger(ID), ItemType.valueOf(row.getString(TYPE)), row.getString(NAME),
					row.getString(DESCRIPTION), row.getInteger(SOLD_UNITS), row.getLocalDateTime(LAST_UPDATE));
		}).collect(ArrayList::new, (l, v) -> l.add(v)).map(asd -> {
			return new JsonArray(asd);
		});
	}

	private String getUpdateQuery(TOP_TEN_UPDATE update) {
		if (update.equals(TOP_TEN_UPDATE.EACH_HOUR)) {
			return ItemUtil.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
		} else if (update.equals(TOP_TEN_UPDATE.ONCE_PER_DAY)) {
			return ItemUtil.TOP_TEN_UPDATE_ONCE_PER_DAY_QUERY;
		} else if (update.equals(TOP_TEN_UPDATE.REAL_TIME)) {
			return ItemUtil.TOP_TEN_REAL_TIME_QUERY;
		}
		return ItemUtil.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
	}
}
