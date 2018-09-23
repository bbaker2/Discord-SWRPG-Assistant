package com.bbaker.discord.swrpg;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bbaker.discord.swrpg.command.impl.DestinyCommand;
import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.database.JdbiService;
import com.bbaker.discord.swrpg.destiny.DestinyTracker;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.printer.DestinyPrinter;

public class DestinyCommandTest extends CommonUtils {

    private DestinyCommand destinyCommand;
    private DestinyTracker destiny;
    private DestinyPrinter printer;

    @BeforeEach
    public void setup() {
        FallbackLoggerConfiguration.setTrace(true);
        destiny = new DestinyTracker(0, 0, JdbiService.IS_NEW);
        dbService = mock(DatabaseService.class);
        when(dbService.retrieveDestiny(anyLong())).thenReturn(destiny);
        printer = mock(DestinyPrinter.class);

        destinyCommand = new DestinyCommand(dbService);
        destinyCommand.setPrinter(printer);

    }

    @Test
    public void rollingTest() {
        AtomicInteger total = new AtomicInteger(0); //used for keeping track of sides returned

        destinyCommand.handleRoll(genMsg("!d roll"));
        verify(printer, description("Make sure only the force die was used"))
            .printRoll(argThat(die -> die.getType() == DieType.FORCE));
        verify(printer, description("At least one side should have been added"))
            .printPoints(argThat(dt -> {
                total.set(dt.getLightSide() + dt.getDarkSide()); // we don't care which sides were increased. Just incrase them.
                return total.get() > 0;
            }));

        reset(printer); // resets the 'verify' count

        destinyCommand.handleRoll(genMsg("!d roll"));
        verify(printer, description("Make sure only the force die was used, again"))
            .printRoll(argThat(die -> die.getType() == DieType.FORCE));
        verify(printer, description("There should be sides than the previous roll"))
            .printPoints(argThat(dt -> {
                return (dt.getLightSide() + dt.getDarkSide()) > total.get();
            }));
    }

    @Test
    public void flipTokenTest() {
        destiny.setSides(2, 2); // preload with 2 dark, 2 light



        destinyCommand.handleRoll(genMsg("!d d"));
        destinyCommand.handleRoll(genMsg("!d dark"));
        verify(printer, times(2).description("Flip the dark side twice"))
            .yesFlips(Die.DARK);

        destinyCommand.handleRoll(genMsg("!d d"));
        verify(printer, description("Reject the dark side on the 3rd flip"))
            .noFlips(Die.DARK);
        verify(printer, atLeastOnce().description("Make sure there are only no dark side and 4 light side"))
            .printPoints(argThat(dt ->
                dt.getDarkSide() == 0 && dt.getLightSide() == 4
            ));

        reset(printer); // resets the 'verify' count

        destinyCommand.handleRoll(genMsg("!d l"));
        destinyCommand.handleRoll(genMsg("!d l"));
        destinyCommand.handleRoll(genMsg("!d light"));
        destinyCommand.handleRoll(genMsg("!d light"));
        verify(printer, times(4).description("Flip the light side 4 times"))
            .yesFlips(Die.LIGHT);
        verify(printer, atLeastOnce().description("Make sure there are only 4 dark side and no light side"))
            .printPoints(argThat(dt ->
                dt.getDarkSide() == 4 && dt.getLightSide() == 0
            ));

        destinyCommand.handleRoll(genMsg("!d l"));
        verify(printer, description("Reject the light side on the 5th flip"))
            .noFlips(Die.LIGHT);
    }

    @Test
    public void setDestinyTest() {
        destinyCommand.handleRoll(genMsg("!d set light dark ll ddd"));
        verify(printer, description("Force set a three light and and four dark sides"))
            .printPoints(argThat(dt ->
                dt.getDarkSide() == 4 && dt.getLightSide() == 3
            ));
    }

    @Test
    public void resetDestinyTest() {
        destiny.setSides(2, 2); // preload with 2 dark, 2 light
        destinyCommand.handleRoll(genMsg("!d reset"));

        verify(printer, description("Make sure the user is informed that their dice have been cleared"))
            .cleared();

        verify(printer, description("Make sure there are no sides left"))
            .printPoints(argThat(dt ->
                dt.getDarkSide() == 0 && dt.getLightSide() == 0
            ));
    }

}
