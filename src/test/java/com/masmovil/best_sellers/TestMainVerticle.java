package com.masmovil.best_sellers;

import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masmovil.best_sellers.model.BestSellerRequest;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.masmovil.best_sellers.model.TopTenUpdate;
import com.masmovil.best_sellers.repositories.ItemRepository;

@RunWith(VertxUnitRunner.class)
public class TestMainVerticle {

	// TODO VERTX TEST INTEGRATION.

	@Rule
	public RunTestOnContext rule = new RunTestOnContext();

	@Mock
	private ItemRepository itemRepository;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void deploy_verticle(TestContext testContext) {
		Vertx vertx = rule.vertx();
		vertx.deployVerticle(new MainVerticle(), testContext.asyncAssertSuccess());
		itemRepository = mock(ItemRepository.class);
	}

	@Test
	public void verticle_deployed(TestContext testContext) throws Throwable {
		Async async = testContext.async();
		async.complete();
	}

	@Test
	public void getTopTen_should_return_ok() throws JsonProcessingException {
		BestSellerRequest bsr = new BestSellerRequest(TopTenUpdate.EACH_HOUR);
		givenTopTenItemsInfoWorks(bsr);
	}

	private void givenTopTenItemsInfoWorks(BestSellerRequest bsr) throws JsonProcessingException {
		
		Item item = new Item(1);/*.with ItemType.EBOOK, "Lord of the rings", "Ebook lord of the rings", 5151,
				LocalDateTime.now());*/

		String json = mapper.writeValueAsString(Arrays.asList(item));
		when(itemRepository.topTen(bsr)).thenReturn(Single.just(new JsonArray(json)));

	}
}
