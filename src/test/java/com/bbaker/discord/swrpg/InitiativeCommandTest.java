package com.bbaker.discord.swrpg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import com.bbaker.discord.swrpg.command.impl.InitiativeCommand;
import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.InitTrackerMeta;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;
import com.bbaker.discord.swrpg.printer.InitiativePrinter;
import com.bbaker.discord.swrpg.printer.RollerPrinter;


class InitiativeCommandTest extends CommonUtils {

    private List<InitCharacter> init;
    private InitTrackerMeta initMeta;
    private InitiativeCommand initCommand;
    private InitiativePrinter initPrinter;
    private RollerPrinter rollPrinter;

    @BeforeEach
    public void setup() {
        FallbackLoggerConfiguration.setDebug(true);
        FallbackLoggerConfiguration.setTrace(true);

        dbService = mock(DatabaseService.class);

        // for retrieving inits
        init = new ArrayList<InitCharacter>();
        initMeta = new InitTrackerMeta(DatabaseService.IS_NEW, 1, 1, false);
        doAnswer(invocation -> new InitiativeTracker(init, initMeta)).when(dbService).retrieveInitiative(anyLong());
        // for storing inits
        doAnswer(invocation -> init = ((InitiativeTracker)invocation.getArgument(1)).getInit())
            .when(dbService).storeInitiative(anyLong(), any());

        initPrinter = mock(InitiativePrinter.class);
        rollPrinter = mock(RollerPrinter.class);

        initCommand = new InitiativeCommand(dbService);
        initCommand.setInitPrinter(initPrinter);
        initCommand.setRollPinter(rollPrinter);

    }

    private void preloadInit(int round, int turn) {
//        init.clear();
        init.addAll(Arrays.asList(
            new InitCharacter("4th", 3, 6, CharacterType.DNPC),
            new InitCharacter("3rd", 2, 4, CharacterType.NPC),
            new InitCharacter("2nd", 1, 2, CharacterType.DPC),
            new InitCharacter("1st", 0, 0, CharacterType.PC)
        ));

        initMeta.round = round;
        initMeta.turn = turn;
    }

    @Test
    void rollRegularAddTest() {
        InOrder order = inOrder(initPrinter);

        initCommand.handleInit(genMsg("!i r success advantage npc")); // third
        order.verify(initPrinter).printInit(argThat(
                new ContainsCharacterType(
                        CharacterType.NPC
                        )
                ));

        initCommand.handleInit(genMsg("!i r success pc")); // fourth
        order.verify(initPrinter).printInit(argThat(
                new ContainsCharacterType(
                        CharacterType.NPC,
                        CharacterType.PC
                        )
                ));

        initCommand.handleInit(genMsg("!i r success advantage2 dpc")); //second
        order.verify(initPrinter).printInit(argThat(
                new ContainsCharacterType(
                        CharacterType.DPC,
                        CharacterType.NPC,
                        CharacterType.PC
                        )
                ));

        initCommand.handleInit(genMsg("!i r 2success dnpc")); // first
        order.verify(initPrinter).printInit(argThat(
            new ContainsCharacterType(
                CharacterType.DNPC,
                CharacterType.DPC,
                CharacterType.NPC,
                CharacterType.PC
            )
        ));

    }

    @Test
    void failedRollAddTest() {
        String actual;

        actual = initCommand.handleInit(genMsg("!i r ggg"));
        assertEquals(InitiativeCommand.NO_CHARACTER_MSG, actual, "No charater provided");

        actual = initCommand.handleInit(genMsg("!r r pc"));
        assertEquals(InitiativeCommand.NO_DIE_MSG, actual, "No dice provided");

        verify(dbService, never().description("At no point should the DB have been updated")).storeInitiative(anyLong(), any());

    }

    @Test
    void reOrderInitTest() {
        preloadInit(2, 2);

        initCommand.handleInit(genMsg("!i reorder pc dpc npc dnpc"));

        verify(dbService, description("Different types, but the success/advantage should be the same. Labels get cleared."))
            .storeInitiative(anyLong(), argThat(new ContainsCharacter(
                new InitCharacter("", 3, 6, CharacterType.PC),
                new InitCharacter("", 2, 4, CharacterType.DPC),
                new InitCharacter("", 1, 2, CharacterType.NPC),
                new InitCharacter("", 0, 0, CharacterType.DNPC)
            )));

        String actual = initCommand.handleInit(genMsg("!i reorder pc"));
        String expected = String.format(InitiativeTracker.WRONG_SIZE_MSG, 4, 1);

        assertEquals(expected, actual, "Make sure a friendly error message is returned when the sizes are incorrect");
        verify(dbService, never().description("This should NOT save to the database."))
            .storeDestiny(anyLong(), any());
    }

    @Test
    void nextTest() {
        preloadInit(1, 0);

        System.out.println("======NEXT======");

        initCommand.handleInit(genMsg("!i"));
        verify(initPrinter, description("Starting with Round 1, Turn 0")).printRoundTurn(1, 0);

        initCommand.handleInit(genMsg("!i n"));
        verify(initPrinter, description("one next = Round 1, Turn 1")).printRoundTurn(1, 1);
        initCommand.handleInit(genMsg("!i n"));
        verify(initPrinter, description("one next = Round 1, Turn 2")).printRoundTurn(1, 2);

        initCommand.handleInit(genMsg("!i n 2"));
        verify(initPrinter, description("two nexts = Round 1, Turn 4")).printRoundTurn(1, 4);

        initCommand.handleInit(genMsg("!i n luke"));
        verify(initPrinter, description("one next w/ luke label = Round 2, Turn 1")).printRoundTurn(2, 1);
        verify(dbService, atLeastOnce().description("Make sure the luke label was saved"))
            .storeInitiative(anyLong(), argThat(it -> "luke".equals(it.getInit().get(0).getLabel())));

        initCommand.handleInit(genMsg("!i n stormtrooper2"));
        verify(initPrinter, description("two nexts w/ labels = Round 2, Turn 3")).printRoundTurn(2, 3);
        verify(dbService, atLeastOnce().description("Make sure the stormtrooper label was saved"))
            .storeInitiative(anyLong(), argThat(it ->
                "luke".equals(it.getInit().get(0).getLabel()) &&
                "stormtrooper".equals(it.getInit().get(1).getLabel()) &&
                "stormtrooper".equals(it.getInit().get(2).getLabel())
            ));

        initCommand.handleInit(genMsg("!i n 2yoda"));
        verify(initPrinter, description("two yoda = Round 3, Turn 1")).printRoundTurn(3, 1);
        verify(dbService, atLeastOnce().description("Make sure the yoda label was saved"))
            .storeInitiative(anyLong(), argThat(it ->
                "yoda".equals(it.getInit().get(0).getLabel()) &&
                "stormtrooper".equals(it.getInit().get(1).getLabel()) &&
                "stormtrooper".equals(it.getInit().get(2).getLabel()) &&
                "yoda".equals(it.getInit().get(3).getLabel())
            ));

        initCommand.handleInit(genMsg("!i n 3"));
        verify(initPrinter, description("three next = Round 3, Turn 4")).printRoundTurn(3, 4);
        verify(dbService, atLeastOnce().description("Make sure the next 3 lables were cleared"))
            .storeInitiative(anyLong(), argThat(it ->
                "yoda".equals(it.getInit().get(0).getLabel()) &&
                "".equals(it.getInit().get(1).getLabel()) &&
                "".equals(it.getInit().get(2).getLabel()) &&
                "".equals(it.getInit().get(3).getLabel())
            ));
    }

    @Test
    public void previousTest() {
        preloadInit(8, 1);

        initCommand.handleInit(genMsg("!i"));
        verify(initPrinter, description("Starting with Round 8, Turn 1")).printRoundTurn(8, 1);

    }

//    @Test
    public void previousErrorTest() {
        String actual;

        actual = initCommand.handleInit(genMsg("!i p"));
        assertEquals(InitiativeCommand.EMPTY_INIT_MSG, actual, "Make sure a friendly message about not having init was returned");

        preloadInit(2, 2);

        actual = initCommand.handleInit(genMsg("!i p 3"));
        assertEquals(InitiativeTracker.NEGATIVE_ROUND_MSG, actual, "Make sure a friendly message about negative rounds was returned");

        verify(dbService, never().description("The db should never be called since an error was thrown"))
            .storeInitiative(anyLong(), any());
    }


    private class ContainsCharacter implements ArgumentMatcher<InitiativeTracker> {
        private InitCharacter[] expected;

        public ContainsCharacter(InitCharacter... characters) {
            this.expected = characters;
        }

        @Override
        public boolean matches(InitiativeTracker actualIt) {
            List<InitCharacter> actual = actualIt.getInit();
            if(expected.length != actual.size()) {
                System.out.println(expected.length + " vs.  " + actual.size());
                return false;
            }

            for(int i = 0; i < expected.length; i++) {
                if(!expected[i].equals(actual.get(i))) {
                    System.out.println("Failed equals: " + expected[i] + " vs " + actual.get(i));
                    return false;
                }
            }
            return true;
        }

    }

    private class ContainsCharacterType implements ArgumentMatcher<InitiativeTracker> {

        private CharacterType[] expected;
        public ContainsCharacterType(CharacterType...dice) {
            this.expected = dice;
        }

        @Override
        public boolean matches(InitiativeTracker it) {
            List<InitCharacter> actual = it.getInit();

            if(expected.length != actual.size()) {
                System.out.println(expected.length + " vs.  " + actual.size());
                return false;
            }

            for(int i = 0; i < expected.length; i++) {
                if(expected[i] != actual.get(i).getType()) {
                    System.out.println(expected[i].name());
                    System.out.println(actual.get(i).getType());
                    return false;
                }
            }
            return true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Expected: ");

            for(CharacterType ct : expected) {
                sb.append(ct.name()).append(" ");
            }

            return sb.toString();
        }

    }

}
