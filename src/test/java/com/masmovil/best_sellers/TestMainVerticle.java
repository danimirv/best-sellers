package com.masmovil.best_sellers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.masmovil.best_sellers.model.TopTenUpdate;
import com.masmovil.best_sellers.repositories.ItemRepository;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(VertxUnitRunner.class)
public class TestMainVerticle {

	// TODO VERTX TEST INTEGRATION.

	@Rule
	public RunTestOnContext rule = new RunTestOnContext();

	@Mock
	private ItemRepository itemRepository;

	private ObjectMapper mapper = new ObjectMapper();

	private Vertx vertx;
	int port;

	@Before
	public void deploy_verticle(TestContext testContext) throws IOException {
		vertx = rule.vertx();
		ServerSocket socket = new ServerSocket(0);
		socket.close();
		port = socket.getLocalPort();
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
		vertx.deployVerticle(new MainVerticle(), options, testContext.asyncAssertSuccess());
		// vertx.deployVerticle(new MainVerticle(), testContext.asyncAssertSuccess());
		itemRepository = mock(ItemRepository.class);
	}

	// @Test
	public void verticle_deployed(TestContext testContext) throws Throwable {
		Async async = testContext.async();
		async.complete();
	}

	// @Test
	public void getTopTen_should_return_ok() throws JsonProcessingException {
		BestSellerRequest bsr = new BestSellerRequest(TopTenUpdate.EACH_HOUR);
		givenTopTenItemsInfoWorks(bsr);
	}

	// @Test
	public void testMyApplication(TestContext context) {
		final Async async = context.async();
		vertx.createHttpClient().getNow(port, "localhost", "/best-sellers/top-ten", response -> {
			response.handler(body -> {
				context.assertTrue(body.toString().contains("{}"));
				async.complete();
			});
		});
	}

	private void givenTopTenItemsInfoWorks(BestSellerRequest bsr) throws JsonProcessingException {

		Item item = Item.builder().withId(1).withType(ItemType.EBOOK).withDescription("Ebook lord of the ring")
				.withName("Lord of the rings").withSoldUnits(5151).withLastUpdate(LocalDateTime.now()).build();

		String json = mapper.writeValueAsString(Arrays.asList(""));
		when(itemRepository.topTen(bsr)).thenReturn(Single.just(new JsonArray(json)));

	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}
}
