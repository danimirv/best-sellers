package com.masmovil.best_sellers.repositories;

import static com.masmovil.best_sellers.queries.ItemsQueries.DESCRIPTION;
import static com.masmovil.best_sellers.queries.ItemsQueries.ID;
import static com.masmovil.best_sellers.queries.ItemsQueries.LAST_UPDATE;
import static com.masmovil.best_sellers.queries.ItemsQueries.NAME;
import static com.masmovil.best_sellers.queries.ItemsQueries.SOLD_UNITS;
import static com.masmovil.best_sellers.queries.ItemsQueries.TYPE;

import com.masmovil.best_sellers.config.PostgreConfiguration;
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.masmovil.best_sellers.model.TopTenUpdate;
import com.masmovil.best_sellers.queries.ItemsQueries;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.pgclient.PgPool;
import java.util.ArrayList;

public class ItemRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemRepository.class);

	private final PgPool pgClient;

	public ItemRepository(Vertx vertx) {
		this(PostgreConfiguration.init(vertx));
	}

	public ItemRepository(PgPool pgClient) {
		this.pgClient = pgClient;
	}

	public Single<JsonArray> topTen(BestSellerRequest request) {
		LOGGER.info("Entry top-ten list");
		return pgClient.rxGetConnection().flatMapObservable(
			conn -> conn.rxPrepare(getUpdateQuery(request.getUpdateTime()))
				.flatMapObservable(preparedStatement -> preparedStatement.createStream(50).toObservable())).map(row -> {
			return new Item(row.getInteger(ID)).withType(ItemType.valueOf(row.getString(TYPE)))
				.withName(row.getString(NAME))
				.withDescription(row.getString(DESCRIPTION)).withSoldUnits(row.getInteger(SOLD_UNITS))
				.withLastUpdate(row.getLocalDateTime(LAST_UPDATE));
			
			/*return new Item(row.getInteger(ID), ItemType.valueOf(row.getString(TYPE)), row.getString(NAME),
					row.getString(DESCRIPTION), row.getInteger(SOLD_UNITS), row.getLocalDateTime(LAST_UPDATE));*/
		}).collect(ArrayList::new, ArrayList::add).map(JsonArray::new);
	}

	private String getUpdateQuery(TopTenUpdate update) {
		if (TopTenUpdate.EACH_HOUR.equals(update)) {
			return ItemsQueries.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
		}
		if (TopTenUpdate.ONCE_PER_DAY.equals(update)) {
			return ItemsQueries.TOP_TEN_UPDATE_ONCE_PER_DAY_QUERY;
		}
		if (TopTenUpdate.REAL_TIME.equals(update)) {
			return ItemsQueries.TOP_TEN_REAL_TIME_QUERY;
		}
		return ItemsQueries.TOP_TEN_UPDATE_EACH_HOUR_QUERY;
	}
}
