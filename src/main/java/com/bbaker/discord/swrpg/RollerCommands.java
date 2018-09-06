package com.bbaker.discord.swrpg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;

import com.bbaker.database.DatabaseService;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.roller.ReRollerDiceHandlerImpl;
import com.bbaker.discord.swrpg.roller.RollerDiceHandlerImpl;
import com.bbaker.discord.swrpg.roller.RollerPrinter;
import com.bbaker.discord.swrpg.table.TableBuilder;
import com.bbaker.exceptions.BadArgumentException;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class RollerCommands  implements CommandExecutor {

    public static final String ERROR_MSG = "Woops, I ran into an error.";
    private static final Logger logger = LogManager.getLogger(RollerCommands.class);
    private DatabaseService dbService;
    private ArgumentHandler rollService;
    private ArgumentHandler rerollService;
    private RollerPrinter printer;

    public RollerCommands(DatabaseService db, DiscordApi discordApi) {
        this.dbService = db;
        //this.parsingService = new RollerHandler();
        this.rollService = new RollerDiceHandlerImpl();
        this.rerollService = new ReRollerDiceHandlerImpl();
        this.printer = new RollerPrinter(discordApi);
    }

    /**
     * Used for overriding the printer. Usually used for unit tests.
     * @param printer any class that extends the {@link RollerPrinter}
     */
    public void setPrinter(RollerPrinter p) {
        this.printer = p;
    }

    @Command(aliases = {"r", "roll"}, description = "Rolls dice", usage = "!roll")
    public String handleRoll(Message message) {
        try {
            // parse the user message
            List<String> tokens = getList(message.getContent());
            TableBuilder table = new TableBuilder();
            rollService.processArguments(tokens.iterator(), table);

            TableResult result = table.roll(); // actually roll the dice

            // store to the database
            dbService.storeDiceResults(message.getAuthor().getId(), message.getChannel().getId(), result.getDice());

            // print
            return printer.print(result);
        } catch (BadArgumentException e) {
            logger.debug(e);
            return e.getMessage();
        } catch (Exception e) {
            logger.debug("Exception thrown during rolls", e);
            return ERROR_MSG;
        }
    }

    @Command(aliases = {"rr", "reroll"}, description = "Rerolls dice", usage = "!reroll")
    public String handleReroll(Message message) {
        try {
            // parse the user message
            Collection<RollableDie> fromDB = dbService.retrieveDiceResults(message.getAuthor().getId(), message.getChannel().getId());
            TableBuilder table = new TableBuilder();
            for(Die die : fromDB) {
                table.adjustDice(die.getType(), 1);
            }
            List<String> tokens = getList(message.getContent());
            TableResult result;
            if(tokens.isEmpty()) {
                result = table.roll(); // the reroll
            } else {
                rerollService.processArguments(tokens.iterator(), table);
                result = table.peekResult();
            }

            // store to the database, again
            dbService.storeDiceResults(message.getAuthor().getId(), message.getChannel().getId(), result.getDice());

            // print
            return printer.print(result);
        } catch (Exception e) {
            logger.debug("Exception thrown durng rerolls.", e);
            return ERROR_MSG;
        }
    }

    private List<String> getList(String message){
        String[] args = message.split("\\s+");
        List<String> tokens = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
        return new ArrayList<String>(tokens);
    }
}
