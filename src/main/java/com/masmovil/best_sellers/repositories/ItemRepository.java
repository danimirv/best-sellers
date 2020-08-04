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
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.masmovil.best_sellers.model.TopTenUpdate;
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

	public Single<JsonArray> topTen(BestSellerRequest request) {
		LOGGER.info("Entry top-ten list");
		Single<SqlConnection> connection = pgClient.rxGetConnection();
		return connection.flatMapObservable(conn -> {
			return conn.rxPrepare(getUpdateQuery(request.getUpdateTime())).flatMapObservable(preparedStatement -> {
				return preparedStatement.createStream(50).toObservable();
			});
		}).map(row -> {
			return new Item(row.getInteger(ID)).withType(ItemType.valueOf(row.getString(TYPE))).withName(row.getString(NAME))
			.withDescription(row.getString(DESCRIPTION)).withSoldUnits(row.getInteger(SOLD_UNITS)).withLastUpdate(row.getLocalDateTime(LAST_UPDATE));
			
			/*return new Item(row.getInteger(ID), ItemType.valueOf(row.getString(TYPE)), row.getString(NAME),
					row.getString(DESCRIPTION), row.getInteger(SOLD_UNITS), row.getLocalDateTime(LAST_UPDATE));*/
		}).collect(ArrayList::new, (l, v) -> l.add(v)).map(asd -> {
			return new JsonArray(asd);
		});
	}

	private String getUpdateQuery(TopTenUpdate update) {
		if (TopTenUpdate.EACH_HOUR.equals(update)) {
			return ItemUtil.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
		} else if (TopTenUpdate.ONCE_PER_DAY.equals(update)) {
			return ItemUtil.TOP_TEN_UPDATE_ONCE_PER_DAY_QUERY;
		} else if (TopTenUpdate.REAL_TIME.equals(update)) {
			return ItemUtil.TOP_TEN_REAL_TIME_QUERY;
		}
		return ItemUtil.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
	}
}
