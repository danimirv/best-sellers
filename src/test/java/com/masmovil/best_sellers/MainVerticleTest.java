package com.masmovil.best_sellers;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.TopTenUpdate;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest extends MainVerticleTestFixtures {

	private static final String INIT_SCRIPT_PATH = "init_postgres.sql";

	@ClassRule
	public static PostgreSQLContainer postgreSQLContainer;

	@Rule
	public RunTestOnContext rule = new RunTestOnContext();

	private Vertx vertx;
	private Integer port;
	private String host = "0.0.0.0";

	@BeforeClass
	public static void beforeClass(TestContext testContext) throws IOException {
	}

	@Before
	public void before(TestContext testContext) throws IOException {
		// vertx = rule.vertx();
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
		vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle(host, port), options, testContext.asyncAssertSuccess());

		postgreSQLContainer = new PostgreSQLContainer();
		postgreSQLContainer.withInitScript(INIT_SCRIPT_PATH);
		postgreSQLContainer.start();
	}

	@Test
	public void verticle_deployed(TestContext testContext) throws Throwable {
		ResultSet resultSet = performQuery(postgreSQLContainer, "SELECT * from item");
		String resultSetInt = resultSet.getString(6);
		System.out.println("RESULT SET: " + resultSetInt);
	}

	@Test
	public void canGetHello(TestContext context) throws JsonProcessingException {
		Async async = context.async();
		HttpClient client = vertx.createHttpClient();
		client.post(port, "localhost", "/best-sellers/top-ten", response -> {
			System.out.println(response.statusCode());
			context.assertEquals(200, response.statusCode());
			response.bodyHandler(body -> {
				client.close();
				async.complete();
			});
		}).putHeader("content-type", "application/json")
				.end(MAPPER.writeValueAsString(new BestSellerRequest(TopTenUpdate.EACH_HOUR)));

	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

}
