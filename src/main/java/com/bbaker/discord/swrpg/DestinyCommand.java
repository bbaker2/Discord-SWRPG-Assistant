package com.bbaker.discord.swrpg;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;

import com.bbaker.database.DatabaseService;
import com.bbaker.discord.swrpg.destiny.DestinyTracker;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.roller.EmojiService;
import com.bbaker.exceptions.BadArgumentException;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class DestinyCommand extends BasicCommand implements CommandExecutor {

    private static final Logger logger = LogManager.getLogger(DestinyCommand.class);

    private DestinyPrinter printer;

    public DestinyCommand(DatabaseService db) {
        super(db);
        printer = new DestinyPrinter();
    }

    @Command(aliases = {"d", "destiny"}, description = "Update and view destiny points", usage = "!destiny")
    public String handleRoll(Message message) {
        try {
            List<String> tokens = getList(message.getContent());

            DestinyTracker destiny = dbService.retrieveDestiny(message.getChannel().getId());
            String extraContent = "";
            if(tokens.size() == 0) {
                return printer.printPoints(destiny);
            }

            switch(tokens.remove(0)) {
            case "roll":
            case "r":
                extraContent = rollDestiny(destiny);
                break;
            case "light":
            case "l":
                extraContent = flipForceToken(destiny, true);
                break;
            case "dark":
            case "d":
                extraContent = flipForceToken(destiny, false);
                break;
            case "set":
                setDestiny(destiny, tokens);
                break;
            case "reset":
                extraContent = "Destiny cleared";
                destiny.setSides(0, 0);
                break;
            }

            dbService.storeDestiny(message.getChannel().getId(), destiny);

            return extraContent + "\n" + printer.printPoints(destiny);
        } catch (BadArgumentException e) {
            logger.debug(e);
            return e.getMessage();
        } catch (Exception e) {
            logger.debug(e);
            return ERROR_MSG;
        }
    }

    private String flipForceToken(DestinyTracker destiny, boolean flipLight) {
        int side;
        Die die;
        if(flipLight) {
            side = destiny.getLightSide();
            die = Die.LIGHT;
        } else {
            side = destiny.getDarkSide();
            die = Die.DARK;
        }
        EmojiService emojies = EmojiService.getInstance();
        if(--side < 0) {
            return String.format("There are no %s to flip", emojies.findEmoji(die));
        }

        if(flipLight) {
            destiny.adjustLightSide(-1);
            destiny.adjustDarkSide(1);
        } else {
            destiny.adjustLightSide(1);
            destiny.adjustDarkSide(-1);
        }
        return String.format("Flipping a %s", emojies.findEmoji(die));
    }

    private String rollDestiny(DestinyTracker destiny) {
        RollableDie forceDie = RollableDie.newDie(DieType.FORCE);
        DieResult result = forceDie.roll();
        destiny.adjustLightSide(result.getLightSide());
        destiny.adjustDarkSide(result.getDarkSide());
        return printer.printRoll(forceDie);
    }

    private void setDestiny(DestinyTracker destiny, List<String> tokens) throws BadArgumentException {
        parser.processArguments(tokens.iterator(), (token, left, right)-> {
            int adjustment = getTotal(left, right);
            switch(token) {
                case "light":
                case "l":
                    destiny.adjustLightSide(adjustment);
                    break;
                case "dark":
                case "d":
                    destiny.adjustDarkSide(adjustment);
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

}
