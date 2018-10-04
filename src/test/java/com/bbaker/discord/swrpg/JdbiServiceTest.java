package com.bbaker.discord.swrpg;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bbaker.discord.swrpg.database.JdbiService;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.exceptions.SetupException;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;

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
        assertTrue(dbService.hasTable(JdbiService.TABLE_DESTINY), "Make sure the destiny table survived.");
        assertTrue(dbService.hasTable(JdbiService.TABLE_INIT_TRACKER), "Make sure the init trackr table survived.");
        assertTrue(dbService.hasTable(JdbiService.TABLE_INIT), "Make sure the inittable survived.");
        assertFalse(dbService.createTables(), "The table was already created. It should not be created again");
    }

    @Test
    public void testDieInsert() {

        // Test a fresh insert
        List<RollableDie> dice = Arrays.asList(
            RollableDie.newDie(DieType.ABILITY, 0),
            RollableDie.newDie(DieType.ADVANTAGE, 1),
            RollableDie.newDie(DieType.BOOST, 2),
            RollableDie.newDie(DieType.FORCE),
            RollableDie.newDie(DieType.LIGHT)
        );

        dbService.storeDiceResults(USER_ID, CHANNEL_ID, dice);
        List<RollableDie> result = dbService.retrieveDiceResults(USER_ID, CHANNEL_ID).getDice();

        assertEquals(dice.size(), result.size(), "Make sure the same results are returned for the first insert");
        assertTrue(dice.containsAll(result), "Not all die were found after the first insert: " + result);

        // Test the second insert
        dice = Arrays.asList(
            RollableDie.newDie(DieType.DIFFICULTY, 3),
            RollableDie.newDie(DieType.CHALLENGE, 4),
            RollableDie.newDie(DieType.SETBACK, 5),
            RollableDie.newDie(DieType.DESPAIR),
            RollableDie.newDie(DieType.DARK)
        );

        dbService.storeDiceResults(USER_ID, CHANNEL_ID, dice);
        result = dbService.retrieveDiceResults(USER_ID, CHANNEL_ID).getDice();

        assertEquals(dice.size(), result.size(), "Make sure the same results are returned for the second insert");
        assertTrue(dice.containsAll(result), "Not all die were found after the second insert: " + result);
    }

    @Test
    public void testInitInserts() {
        List<InitCharacter> inits = Arrays.asList(
            new InitCharacter("first", 	0, 0, 5, CharacterType.NPC),
            new InitCharacter("second", 0, 1, 4, CharacterType.NPC),
            new InitCharacter("third", 	1, 0, 3, CharacterType.NPC),
            new InitCharacter("forth", 	1, 1, 2, CharacterType.NPC),
            new InitCharacter("fith", 	2, 0, 1, CharacterType.NPC)
        );

        InitiativeTracker expected, actual;

        // round 2, turn 3, uses success/advantage
        expected = new InitiativeTracker(inits, 2, 3, false);
        dbService.storeInitiative(CHANNEL_ID, expected);
        actual = dbService.retrieveInitiative(CHANNEL_ID);

        assertEquals(expected.getRound(), actual.getRound(), "Make sure the rounds are the same");
        assertEquals(expected.getTurn(), actual.getTurn(), "Make sure the turns are the same");

        assertEquals(expected.getInit().size(), actual.getInit().size(), "Make sure the same number of dice are returned");
        assertTrue(expected.getInit().containsAll(actual.getInit()), "Make sure all the characters are retrieved");

        // round 1, turn 4, uses provided order
        expected = new InitiativeTracker(inits, 1, 4, true);
        dbService.storeInitiative(CHANNEL_ID, expected);
        actual = dbService.retrieveInitiative(CHANNEL_ID);

    }

}
