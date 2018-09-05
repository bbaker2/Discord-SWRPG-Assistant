package com.bbaker.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.exceptions.SetupException;

public class JdbiServiceTest {
	
	private static final long USER_ID = 11111111;
	private static final long CHANNEL_ID = 22222222;
	
	private JdbiService dbService;
	
	@BeforeEach
	public void setupDatabase() {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			fail("Failed right off the bat. Unable to load the driver");
		}
		Properties testProperties = new Properties();
		testProperties.setProperty("url", 	 "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
		testProperties.setProperty("prefix", "test_");
		try {
			dbService = new JdbiService(testProperties);
			dbService.createTables();
		} catch (SetupException e) { 
			fail("Unable to prep the database service", e);
		}
	}
	
	@AfterEach
	public void dropAllTables() {
		dbService.getJdbi().withHandle(handle -> 
			handle.execute("DROP  ALL OBJECTS"));
	}
	
	@Test
	public void testBadTableCreation() {
		Properties testProperties = new Properties();
		testProperties.setProperty("url", 	 "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
		testProperties.setProperty("prefix", "");
		
		assertThrows(SetupException.class, () -> new JdbiService(testProperties), "Empty prefixes are not allowed");
		
		testProperties.setProperty("prefix", "omg");
		assertThrows(SetupException.class, () -> new JdbiService(testProperties), "Must contain correctly structured ");
		
		testProperties.setProperty("prefix", "omg_");
		try {
			new JdbiService(testProperties);
		} catch (SetupException e) {
			fail(e);
		}
	}
	
	@Test
	public void testTableCreation() {
		assertTrue(dbService.hasTable(JdbiService.TABLE_ROLL), "Make sure the dice roll table survived.");
		assertFalse(dbService.createTables(), "The table was already created. It should not be created again");
	}
	
	@Test 
	public void testDieInsert() {
		
		// Test a fresh insert
		List<Die> dice = Arrays.asList(
			Die.newDie(DieType.ABILITY, 0),
			Die.newDie(DieType.ADVANTAGE, 1),
			Die.newDie(DieType.BOOST, 2),
			Die.newDie(DieType.FORCE),
			Die.newDie(DieType.LIGHT)
		);
		
		dbService.storeDiceResults(USER_ID, CHANNEL_ID, dice);
		List<Die> result = dbService.retrieveDiceResults(USER_ID, CHANNEL_ID);
		
		assertEquals(dice.size(), result.size(), "Make sure the same results are returned for the first insert");
		assertTrue(dice.containsAll(result), "Not all die were found after the first insert: " + result);
		
		// Test the second insert 
		dice = Arrays.asList(
			Die.newDie(DieType.DIFFICULTY, 3),
			Die.newDie(DieType.CHALLENGE, 4),
			Die.newDie(DieType.SETBACK, 5),
			Die.newDie(DieType.DESPAIR),
			Die.newDie(DieType.DARK)
		);
		
		dbService.storeDiceResults(USER_ID, CHANNEL_ID, dice);
		result = dbService.retrieveDiceResults(USER_ID, CHANNEL_ID);
		
		assertEquals(dice.size(), result.size(), "Make sure the same results are returned for the second insert");
		assertTrue(dice.containsAll(result), "Not all die were found after the second insert: " + result);
	}

}
