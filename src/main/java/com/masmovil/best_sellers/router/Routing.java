package com.masmovil.best_sellers.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.ErrorMessage;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.repositories.ItemRepository;
import io.reactivex.Single;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class Routing {

	private static final Logger LOGGER = LoggerFactory.getLogger(Routing.class.getName());

	private static final String ROOT = "/best-sellers";
	private static final String TOP_TEN = ROOT + "/top-ten";

	private final Vertx vertx;
	private final ItemRepository itemRepository;
	private final ObjectMapper mapper;

	public Routing(Vertx vertx) {
		this(vertx, new ItemRepository(), new ObjectMapper());
	}

	public Routing(Vertx vertx, ItemRepository itemRepository, ObjectMapper mapper) {
		this.vertx = vertx;
		this.itemRepository = itemRepository;
		this.mapper = mapper;
	}

	public Single<io.vertx.reactivex.ext.web.Router> createRouter() {
		long bodyLimit = 1024;
		io.vertx.reactivex.ext.web.Router router = io.vertx.reactivex.ext.web.Router.router(vertx);
		router.post(ROOT).handler(BodyHandler.create().setBodyLimit(bodyLimit * bodyLimit));
		router.post(TOP_TEN).handler(BodyHandler.create().setBodyLimit(bodyLimit * bodyLimit));
		router.post(ROOT).handler(this::test);
		router.post(TOP_TEN).handler(this::topTen);
		return Single.just(router);
	}

	private void topTen(RoutingContext context) {
		LOGGER.info("Entry topTen method");
		try {
			itemRepository.topTen(getRequest(context)).subscribe(asd -> {
				context.response().putHeader("content-type", "application/json").end(Json.encodePrettily(asd));
			});
		} catch (JsonProcessingException e) {
			System.out.println(e.getLocalizedMessage());
			LOGGER.error("Error getting request:", e.getMessage());
			reponseErrorMessage(context);
		}
	}

	private void test(RoutingContext context) {
		LOGGER.error("--->>> ENTRY TEST");
		context.response().putHeader("content-type", "application/json").end("{\"message\":\"Hello\"}");
	}

	private void reponseErrorMessage(RoutingContext context) {
		context.response().setStatusCode(400).putHeader("content-type", "application/json")
				.end(Json.encode(new ErrorMessage("update_time cannot be resolved to a type")));
	}

	private BestSellerRequest getRequest(RoutingContext context) throws JsonProcessingException {
		return mapper.readValue(context.getBodyAsString(), BestSellerRequest.class);
	}

}
