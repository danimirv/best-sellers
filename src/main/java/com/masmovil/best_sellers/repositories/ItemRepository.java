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
			conn -> conn.rxPrepare(request.getUpdateTime().getItemsQuery())
				.flatMapObservable(preparedStatement -> preparedStatement.createStream(50).toObservable()))
			.map(this::buildItemFromRow).collect(ArrayList::new, ArrayList::add).map(JsonArray::new);
	}

	private Item buildItemFromRow(io.vertx.reactivex.sqlclient.Row row) {
		return Item.builder().withId(row.getInteger(ID)).withType(ItemType.valueOf(row.getString(TYPE)))
			.withName(row.getString(NAME))
			.withDescription(row.getString(DESCRIPTION)).withSoldUnits(row.getInteger(SOLD_UNITS))
			.withLastUpdate(row.getLocalDateTime(LAST_UPDATE)).build();
	}
}
