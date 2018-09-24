package com.bbaker.discord.swrpg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import com.bbaker.discord.swrpg.command.impl.InitiativeCommand;
import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;
import com.bbaker.discord.swrpg.printer.InitiativePrinter;
import com.bbaker.discord.swrpg.printer.RollerPrinter;


class InitiativeCommandTest extends CommonUtils {

    private List<InitCharacter> init;
    private InitiativeCommand initCommand;
    private InitiativePrinter initPrinter;
    private RollerPrinter rollPrinter;

    @BeforeEach
    public void setup() {
        dbService = mock(DatabaseService.class);

        // for retrieving inits
        init = new ArrayList();
        when(dbService.retrieveInitiative(anyLong())).thenReturn(new InitiativeTracker(init));
        // for storing inits
        doAnswer(invocation -> init = (List<InitCharacter>)invocation.getArgument(1))
            .when(dbService).storeInitiative(anyLong(), anyList());

        initPrinter = mock(InitiativePrinter.class);
        rollPrinter = mock(RollerPrinter.class);

        initCommand = new InitiativeCommand(dbService);
        initCommand.setInitPrinter(initPrinter);
        initCommand.setRollPinter(rollPrinter);

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

        verify(dbService, never().description("At no point should the DB have been updated")).storeInitiative(anyLong(), anyList());

    }

    @Test
    void reOrderInitTest() {
        init.addAll(Arrays.asList(
            new InitCharacter("4th", 3, 6, 1, CharacterType.DNPC),
            new InitCharacter("3rd", 2, 4, 1, CharacterType.NPC),
            new InitCharacter("2nd", 1, 2, 2, CharacterType.DPC),
            new InitCharacter("1st", 0, 0, 2, CharacterType.PC)
        ));

        initCommand.handleInit(genMsg("!i reorder pc dpc npc dnpc"));

        verify(dbService, description("Different types, but the success/advantage should be the same. Labels get cleared."))
            .storeInitiative(anyLong(), argThat(new ContainsCharacter(
                new InitCharacter("", 3, 6, 1, CharacterType.PC),
                new InitCharacter("", 2, 4, 1, CharacterType.DPC),
                new InitCharacter("", 1, 2, 2, CharacterType.NPC),
                new InitCharacter("", 0, 0, 2, CharacterType.DNPC)
            )));

        String actual = initCommand.handleInit(genMsg("!i reorder pc"));
        String expected = String.format(InitiativeTracker.WRONG_SIZE_MSG, 4, 1);

        assertEquals(expected, actual, "Make sure a friendly error message is returned when the sizes are incorrect");
        verify(dbService, never().description("This should NOT save to the database."))
            .storeDestiny(anyLong(), any());
    }

    @Test
    void nextTest() {
        init.addAll(Arrays.asList(
            new InitCharacter("4th", 3, 6, 1, CharacterType.DNPC),
            new InitCharacter("3rd", 2, 4, 1, CharacterType.NPC),
            new InitCharacter("2nd", 1, 2, 2, CharacterType.DPC),
            new InitCharacter("1st", 0, 0, 2, CharacterType.PC)
        ));

        initCommand.handleInit(genMsg("!i"));
        verify(initPrinter, description("Starting with Round 1, Turn 1")).printRoundTurn(1, 1);
        
        initCommand.handleInit(genMsg("!i n"));
        verify(initPrinter, description("one next = Round 1, Turn 2")).printRoundTurn(1, 2);

        initCommand.handleInit(genMsg("!i n 2"));
        verify(initPrinter, description("two nexts = Round 1, Turn 4")).printRoundTurn(1, 4);

        initCommand.handleInit(genMsg("!i n luke"));
        verify(initPrinter, description("one next w/ luke label = Round 2, Turn 1")).printRoundTurn(2, 1);
        verify(dbService, atLeastOnce().description("Make sure the luke label was saved"))
        	.storeInitiative(anyLong(), argThat(characters -> "luke".equals(characters.get(0).getLabel())));
        System.out.println(init);

        initCommand.handleInit(genMsg("!i n stormtrooper2"));
        System.out.println(init);
        verify(initPrinter, description("two nexts w/ labels = Round 2, Turn 3")).printRoundTurn(2, 3);
        verify(dbService, atLeastOnce().description("Make sure the stormtrooper label was saved"))
    		.storeInitiative(anyLong(), argThat(characters -> 
    			"luke".equals(characters.get(0).getLabel()) &&
    			"stormtrooper".equals(characters.get(1).getLabel()) &&
    			"stormtrooper".equals(characters.get(2).getLabel())
			));
        System.out.println(init);
        
    }


    private class ContainsCharacter implements ArgumentMatcher<List<InitCharacter>> {
        private InitCharacter[] expected;

        public ContainsCharacter(InitCharacter... characters) {
            this.expected = characters;
        }

        @Override
        public boolean matches(List<InitCharacter> actual) {
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
