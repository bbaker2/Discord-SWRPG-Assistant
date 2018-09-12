package com.bbaker.discord.swrpg.command.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;

import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;
import com.bbaker.discord.swrpg.printer.RollerPrinter;
import com.bbaker.discord.swrpg.roller.DiceTower;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class RollerCommands extends BasicCommand implements CommandExecutor {

    public static final String TWO_INDEX_MSG = "Die type %s has two indexes. Please pick %d or %d";
    public static final String ZERO_INDEX_MSG = "There is no 0th position. Did you mean index 1?";

    private static final Logger logger = LogManager.getLogger(RollerCommands.class);

    private RollerPrinter printer;

    public RollerCommands(DatabaseService db, DiscordApi discordApi) {
        super(db);
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
            DiceTower table = new DiceTower();

            parser.processArguments(tokens.iterator(), (token, left, right)-> {
                DieType dieType = findDie(token);
                if(dieType == null) {
                    return false;
                }
                int total = getTotal(left, right);
                table.addDie(dieType, total);
                return true;
            });


            table.roll(); // actually roll the dice

            // store to the database
            dbService.storeDiceResults(message.getAuthor().getId(), message.getChannel().getId(), table.getDice());

            // print
            return printer.print(table);
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
            DiceTower diceTower = dbService.retrieveDiceResults(message.getAuthor().getId(), message.getChannel().getId());
            List<String> tokens = getList(message.getContent());
            if(tokens.isEmpty()) {
                diceTower.roll(); // reroll all
            } else {
                parser.processArguments(tokens.iterator(), (token, left, right) -> {
                    DieType dieType = findDie(token);
                    if(left.isPresent() && right.isPresent()) {
                        throw new BadArgumentException(TWO_INDEX_MSG,
                                dieType, left.getAsInt(), right.getAsInt());
                    }

                    // If no numbers are provided, assume the 1st position
                    int index = left.orElse(right.orElse(1));

                    // we ask users to give an index where arrays start at 1.
                    // So we subtract one so that our 0-based indexes works
                    index--;

                    if(index == -1) {
                        throw new BadArgumentException(ZERO_INDEX_MSG);
                    }

                    diceTower.roll(dieType, index);
                    return true;
                });
            }

            // store to the database, again
            dbService.storeDiceResults(message.getAuthor().getId(), message.getChannel().getId(), diceTower.getDice());

            // print
            return printer.print(diceTower);
        } catch (BadArgumentException e) {
            logger.debug(e);
            return e.getMessage();
        } catch (Exception e) {
            logger.debug("Exception thrown durng rerolls.", e);
            return ERROR_MSG;
        }
    }

    public static DieType findDie(String strToken) {
        switch(strToken) {
            case "g":
            case "green":
            case "a":
            case "ability":
                return DieType.ABILITY;


            case "y":
            case "yellow":
            case "proficiency":
                return DieType.PROFICIENCY;

            case "b":
            case "blue":
            case "boost":
                return DieType.BOOST;

            case "p":
            case "purple":
            case "d":
            case "difficulty":
                return DieType.DIFFICULTY;

            case "r":
            case "red":
            case "c":
            case "challenge":
                return DieType.CHALLENGE;

            case "k":
            case "black":
            case "s":
            case "setback":
                return DieType.SETBACK;

            case "w":
            case "white":
            case "f":
            case "force":
                return DieType.FORCE;

            case "success":
                return DieType.SUCCESS;

            case "advantage":
                return DieType.ADVANTAGE;

            case "triumph":
                return DieType.TRIUMPH;

            case "failure":
                return DieType.FAILURE;

            case "threat":
                return DieType.THREAT;

            case "despair":
                return DieType.DESPAIR;

            case "dark":
            case "darkside":
                return DieType.DARK;

            case "l":
            case "light":
            case "lightside":
                return DieType.LIGHT;

        }
        return null;
    }

}
