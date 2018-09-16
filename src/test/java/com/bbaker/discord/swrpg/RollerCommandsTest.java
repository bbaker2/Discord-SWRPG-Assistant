package com.bbaker.discord.swrpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.bbaker.discord.swrpg.command.impl.RollerCommands;
import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.printer.RollerPrinter;
import com.bbaker.discord.swrpg.roller.DiceTower;

class RollerCommandsTest extends CommonUtils{

    private List<RollableDie> storedDice;

    @BeforeEach
    public void setup() {
        storedDice = new ArrayList<RollableDie>();
        dbService = mock(DatabaseService.class);

        doAnswer(invocation ->
            storedDice = (List<RollableDie>)invocation.getArgument(2)
        ).when(dbService).storeDiceResults(anyLong(), anyLong(), anyList());

        doAnswer(invocation -> {
            DiceTower dt = new DiceTower();
            for(RollableDie rd : storedDice) dt.addDie(rd);
            return dt;

        }).when(dbService).retrieveDiceResults(anyLong(), anyLong());
    }

    @Test
    public void handleRollTest() {
        RollerCommands rollerService = new RollerCommands(dbService);

        RollerPrinter mockPrinter = mock(RollerPrinter.class);
        rollerService.setPrinter(mockPrinter);

        // Testing short hand and long hand dice
        rollerService.handleRoll(genMsg("!r ygbrpkf"));
        rollerService.handleRoll(genMsg("!r white black purple red blue green yellow"));
        verify(mockPrinter, times(2)).print(argThat(new ContainsDie(
                DieType.PROFICIENCY, DieType.ABILITY, DieType.BOOST,
                DieType.CHALLENGE, DieType.DIFFICULTY, DieType.SETBACK,
                DieType.FORCE)));

        // Testing results directly
        rollerService.handleRoll(genMsg("!r darkside threat failure lightside advantage success despair triumph"));
        verify(mockPrinter).print(argThat(new ContainsDie(
                DieType.SUCCESS, DieType.ADVANTAGE, DieType.TRIUMPH,
                DieType.FAILURE, DieType.THREAT, DieType.DESPAIR,
                DieType.LIGHT, DieType.DARK)));

        // Testing duplicate values
        rollerService.handleRoll(genMsg("!r yg br pk f white black purple red blue green yellow"));
        verify(mockPrinter).print(argThat(new ContainsDie(
                DieType.PROFICIENCY, DieType.PROFICIENCY, DieType.ABILITY, DieType.ABILITY, DieType.BOOST, DieType.BOOST,
                DieType.CHALLENGE, DieType.CHALLENGE, DieType.DIFFICULTY, DieType.DIFFICULTY, DieType.SETBACK, DieType.SETBACK,
                DieType.FORCE, DieType.FORCE)));

        // Testing numeric multipliers to dice rolls
        rollerService.handleRoll(genMsg("!r green green2 3green g4 5g 6g7 g0"));
        verify(mockPrinter).print(argThat(new ContainsDie(
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY,
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY,
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY,
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY,
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY,
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY,
                DieType.ABILITY, DieType.ABILITY, DieType.ABILITY, DieType.ABILITY)));

    }

    @Test
    public void basicReRollTest() {
        RollerCommands rollerService = new RollerCommands(dbService);

        RollerPrinter mockPrinter = mock(RollerPrinter.class);
        rollerService.setPrinter(mockPrinter);

        // Test empty rerolls
        rollerService.handleReroll(genMsg("!rr"));
        verify(mockPrinter).print(argThat(new ContainsDie()));

        // Test with regular dice
        rollerService.handleRoll(genMsg("!r ygbrpkf"));
        rollerService.handleReroll(genMsg("!rr"));
        rollerService.handleReroll(genMsg("!rr"));
        rollerService.handleReroll(genMsg("!rr"));
        verify(mockPrinter, times(4)).print(argThat(new ContainsDie(
                DieType.PROFICIENCY, DieType.ABILITY, DieType.BOOST,
                DieType.CHALLENGE, DieType.DIFFICULTY, DieType.SETBACK,
                DieType.FORCE)));

        // Testing results directly
        rollerService.handleRoll(genMsg("!r darkside threat failure lightside advantage success despair triumph"));
        rollerService.handleReroll(genMsg("!rr"));
        verify(mockPrinter, times(2)).print(argThat(new ContainsDie(
                DieType.SUCCESS, DieType.ADVANTAGE, DieType.TRIUMPH,
                DieType.FAILURE, DieType.THREAT, DieType.DESPAIR,
                DieType.LIGHT, DieType.DARK)));
    }

    @Test
    public void badReRollTest() {
        RollerCommands rollerService = new RollerCommands(dbService);

        RollerPrinter mockPrinter = mock(RollerPrinter.class);

        rollerService.handleRoll(genMsg("!r gggp"));

        String expected, response;

        response = rollerService.handleReroll(genMsg("!rr 1g2"));
        expected = String.format(RollerCommands.TWO_INDEX_MSG, DieType.ABILITY, 1, 2);
        assertEquals(expected, response, "A friendly error message should have been returned for having too many indexes");

        response = rollerService.handleReroll(genMsg("!rr 0g"));
        expected = RollerCommands.ZERO_INDEX_MSG;
        assertEquals(expected, response, "A friendly error message should have been returned for having a 0-index");


        response = rollerService.handleReroll(genMsg("!rr p2"));
        expected = String.format(DiceTower.OUT_OF_BOUNDS_MSG, 1, DieType.DIFFICULTY);
        assertEquals(expected, response, "Trying to reroll a die type that exists but with an incorrect index");

        response = rollerService.handleReroll(genMsg("!rr r1"));
        expected = String.format(DiceTower.NONE_EXIST_MSG, DieType.CHALLENGE);
        assertEquals(expected, response, "Trying to reroll a die type that exists but with an incorrect index");

        response = rollerService.handleReroll(genMsg("!rr advantage"));
        expected = String.format(DiceTower.NOT_SUPPORTED_MSG, DieType.ADVANTAGE);
        assertEquals(expected, response, "Trying to reroll a die type that exists but with an incorrect index");

    }

    @Test
    public void accuracyReRollTest() {
        RollerCommands rollerService = new RollerCommands(dbService);

        RollableDie doNotRoll = mock(RollableDie.class, Mockito.RETURNS_DEEP_STUBS);
        when(doNotRoll.getType()).thenReturn(DieType.ABILITY);

        RollableDie willRoll = mock(RollableDie.class, Mockito.RETURNS_DEEP_STUBS);
        when(willRoll.getType()).thenReturn(DieType.ABILITY);

        // prepare a table with 5 green die
        DiceTower dt = new DiceTower();
        dt.addDie(doNotRoll);
        dt.addDie(doNotRoll);
        dt.addDie(doNotRoll);
        dt.addDie(willRoll); // only the 4th index should reroll
        dt.addDie(doNotRoll);

        when(dbService.retrieveDiceResults(anyLong(), anyLong())).thenReturn(dt);

        rollerService.handleReroll(genMsg("!rr 4green"));
        verify(willRoll, 	times(1).description("Only the 4th die should roll.")).roll();
        verify(doNotRoll, 	times(0)).roll();

        // resetting the willRoll die
        willRoll = mock(RollableDie.class, Mockito.RETURNS_DEEP_STUBS);
        when(willRoll.getType()).thenReturn(DieType.ABILITY);

        // prepare a (new) table with 5 green die
        dt = new DiceTower();
        dt.addDie(willRoll); // only the 1st index should reroll
        dt.addDie(doNotRoll);
        dt.addDie(doNotRoll);
        dt.addDie(doNotRoll);
        dt.addDie(doNotRoll);

        when(dbService.retrieveDiceResults(anyLong(), anyLong())).thenReturn(dt);

        rollerService.handleReroll(genMsg("!rr ability"));
        verify(willRoll, 	times(1).description("Since no index was provided, only the 1st die should reroll")).roll();
        verify(doNotRoll, 	times(0)).roll();

    }

    private class ContainsDie implements ArgumentMatcher<TableResult> {

        private DieType[] expected;
        public ContainsDie(DieType...dice) {
            this.expected = dice;
        }

        @Override
        public boolean matches(TableResult tr) {
            List<RollableDie> actual = tr.getDice();

            if(expected.length != actual.size()) {
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
            return "contains " + expected;
        }

    }

}
