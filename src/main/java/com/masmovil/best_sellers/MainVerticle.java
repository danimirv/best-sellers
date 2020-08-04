package com.masmovil.best_sellers;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.ErrorMessage;
import com.masmovil.best_sellers.model.TopTenUpdate;
import com.masmovil.best_sellers.repositories.ItemRepository;
import com.masmovil.best_sellers.util.ItemUtil;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

	private static final String ROOT = "/best-sellers";
	private static final String TOP_TEN = ROOT + "/top-ten";

	private final String HOST = "0.0.0.0";
	private final Integer PORT = 8080;
	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class.getName());

	private final ObjectMapper mapper;

	private final ItemRepository itemRepository;

	public MainVerticle() {
	        this(new ItemRepository(), new ObjectMapper());
	}

	public MainVerticle(ItemRepository itemRepository, ObjectMapper mapper) {
	        this.itemRepository = itemRepository;
		this.mapper = mapper;
	}

	@Override
	public Completable rxStart() {
		vertx.exceptionHandler(error -> LOGGER.info(error.getMessage() + error.getCause()
				+ Arrays.toString(error.getStackTrace()) + error.getLocalizedMessage()));
		return createRouter().flatMap(router -> startHttpServer(HOST, PORT, router)).flatMapCompletable(httpServer -> {
			LOGGER.info("HTTP server started on http://{0}:{1}", HOST, PORT.toString());
			return Completable.complete();
		});
	}

	private Single<Router> createRouter() {
		long bodyLimit = 1024;
		Router router = Router.router(vertx);
		router.post(TOP_TEN).handler(BodyHandler.create().setBodyLimit(bodyLimit * bodyLimit));
		router.post(TOP_TEN).handler(this::topTen);
		return Single.just(router);
	}

	private void topTen(RoutingContext context) {
		try {
			itemRepository.topTen(getRequest(context)).subscribe(asd -> {
				context.response().putHeader("content-type", "application/json").end(Json.encodePrettily(asd));
			});
		} catch (JsonProcessingException e) {
			LOGGER.error("Error getting request:", e.getMessage());
			reponseErrorMessage(context);
		}
	}

	private void reponseErrorMessage(RoutingContext context) {
		context.response().setStatusCode(400).putHeader("content-type", "application/json")
				.end(Json.encode(new ErrorMessage("update_time cannot be resolved to a type")));
	}

	private BestSellerRequest getRequest(RoutingContext context) throws JsonProcessingException, JsonMappingException {
		return mapper.readValue(context.getBodyAsString(), BestSellerRequest.class);
	}

	private Single<HttpServer> startHttpServer(String httpHost, Integer httpPort, Router router) {
		return vertx.createHttpServer().requestHandler(router).rxListen(httpPort, httpHost);
	}

}
