package com.masmovil.best_sellers;

import com.masmovil.best_sellers.router.Routing;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import java.util.Arrays;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class.getName());

	private String host = "0.0.0.0";
	private Integer port = 8080;

	private final Routing routing;

	public MainVerticle() {
		this(new Routing(Vertx.vertx()));
	}	

	public MainVerticle(Routing routing) {
		this.routing = routing;
	}
	
	public MainVerticle(String host, Integer port) {
		this();
		this.host = host;
		this.port = port;
	}

	@Override
	public Completable rxStart() {
		vertx.exceptionHandler(error -> LOGGER.info(error.getMessage() + error.getCause()
			+ Arrays.toString(error.getStackTrace()) + error.getLocalizedMessage()));
		return routing.createRouter().flatMap(router -> startHttpServer(host, port, router)).flatMapCompletable(httpServer -> {
			LOGGER.info("HTTP server started on http://{0}:{1}", host, port.toString());
			return Completable.complete();
		});
	}

	private Single<HttpServer> startHttpServer(String httpHost, Integer httpPort, Router router) {
		LOGGER.info("Starting HttpServer");
		return vertx.createHttpServer().requestHandler(router).rxListen(httpPort, httpHost);
	}
}
