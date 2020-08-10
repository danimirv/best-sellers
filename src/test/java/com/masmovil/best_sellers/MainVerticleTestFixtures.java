package com.masmovil.best_sellers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masmovil.best_sellers.model.ErrorMessage;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.impl.ConversionHelper;
import io.vertx.core.json.JsonArray;

public class MainVerticleTestFixtures {

	public static final ObjectMapper MAPPER = new ObjectMapper();

	public static List<Item> getTopTepItemList() {
		Item item = Item.builder().withId(5).withType(ItemType.GADGET)
				.withDescription("Smartwatch - AmazFit GTS, 20mm, Black").withName("Amazfit Smartwatch")
				.withSoldUnits(10256).withLastUpdate(LocalDateTime.parse("2020-07-30T22:05:15")).build();
		Item item2 = Item.builder().withId(11).withType(ItemType.EBOOK).withDescription("The hobbit")
				.withName("The hobbit").withSoldUnits(9638).withLastUpdate(LocalDateTime.parse("2020-07-31T08:30:32"))
				.build();
		Item item3 = Item.builder().withId(3).withType(ItemType.COMPUTER_ACCESSORIES)
				.withDescription("Microphone for computer").withName("Logitech microphone").withSoldUnits(9578)
				.withLastUpdate(LocalDateTime.parse("2020-07-31T11:11:00")).build();

		return Arrays.asList(item, item2, item3);
	}

	public ResultSet performQuery(JdbcDatabaseContainer<?> container, String sql) throws SQLException {
		DataSource ds = getDataSource(container);
		Statement statement = ds.getConnection().createStatement();
		statement.execute(sql);
		ResultSet resultSet = statement.getResultSet();
		resultSet.next();
		return resultSet;
	}

	public DataSource getDataSource(JdbcDatabaseContainer<?> container) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(container.getJdbcUrl());
		hikariConfig.setUsername(container.getUsername());
		hikariConfig.setPassword(container.getPassword());
		hikariConfig.setDriverClassName(container.getDriverClassName());
		return new HikariDataSource(hikariConfig);
	}

	public static List<Object> fromJsonArray(JsonArray json) {
		if (json == null) {
			return null;
		}
		List<Object> list = new ArrayList<>(json.getList());
		return list.stream().map(item -> {
			return ConversionHelper.fromObject(item);
		}).collect(Collectors.toList());
	}

	public String getErrorMessage() {
		try {
			return MAPPER.writeValueAsString(new ErrorMessage("update_time cannot be resolved to a type"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

}
