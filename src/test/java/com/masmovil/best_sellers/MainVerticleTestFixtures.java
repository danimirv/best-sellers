package com.masmovil.best_sellers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.sql.DataSource;

import org.testcontainers.containers.JdbcDatabaseContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masmovil.best_sellers.model.Item;
import com.masmovil.best_sellers.model.ItemType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MainVerticleTestFixtures {

	public static final ObjectMapper MAPPER = new ObjectMapper();

	public static String getTopTepItemList() {
		Item item = Item.builder().withId(1).withType(ItemType.EBOOK).withDescription("Ebook Lord of the rings")
				.withName("Lord of the rings").withSoldUnits(3456)
				.withLastUpdate(LocalDateTime.parse("2020-07-31T09:10:59")).build();
		Item item2 = Item.builder().withId(2).withType(ItemType.MOBILE_PHONE)
				.withDescription("New Samsung mobile phone").withName("Samsung Galaxy z flip").withSoldUnits(6542)
				.withLastUpdate(LocalDateTime.parse("2020-07-30T11:10:00")).build();
		Item item3 = Item.builder().withId(3).withType(ItemType.COMPUTER_ACCESSORIES)
				.withDescription("Microphone for computer").withName("Logitech microphone").withSoldUnits(9578)
				.withLastUpdate(LocalDateTime.parse("2020-07-31T11:11:00")).build();
		try {
			return MAPPER.writeValueAsString(Arrays.asList(item, item2, item3));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
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

}
