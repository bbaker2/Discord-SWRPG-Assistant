package com.bbaker.discord.swrpg;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.bbaker.database.DatabaseService;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.roller.RollerPrinter;

class RollerCommandsTest {

    private static final long USER_ID = 11111111;
    private static final long CHANNEL_ID = 22222222;

    private DatabaseService dbService;
    private DiscordApi api;
    private List<Die> storedDice;

    @BeforeEach
    public void setup() {
        FallbackLoggerConfiguration.setDebug(true);
        storedDice = new ArrayList<Die>();
        api = mock(DiscordApi.class);
        dbService = mock(DatabaseService.class);

        doAnswer(invocation ->
            storedDice = (List<Die>)invocation.getArgument(2)
        ).when(dbService).storeDiceResults(anyLong(), anyLong(), anyList());

        doAnswer(invocation ->
            storedDice
        ).when(dbService).retrieveDiceResults(anyLong(), anyLong());
    }

    public Message genMsg(String content) {
        Message msg = mock(Message.class, Mockito.RETURNS_DEEP_STUBS);

        when(msg.getAuthor().getId()).thenReturn(USER_ID);
        when(msg.getChannel().getId()).thenReturn(CHANNEL_ID);
        when(msg.getContent()).thenReturn(content);

        return msg;
    }

    @Test
    public void handleRollTest() {
        RollerCommands rollerService = new RollerCommands(dbService, api);

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
    public void handleReRollTest() {
        RollerCommands rollerService = new RollerCommands(dbService, api);

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


    private class ContainsDie implements ArgumentMatcher<TableResult> {

        private DieType[] expected;
        public ContainsDie(DieType...dice) {
            this.expected = dice;
        }

        @Override
        public boolean matches(TableResult tr) {
            List<Die> actual = tr.getDice();

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
