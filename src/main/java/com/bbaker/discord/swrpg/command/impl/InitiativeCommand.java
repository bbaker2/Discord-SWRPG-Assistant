package com.bbaker.discord.swrpg.command.impl;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;

import com.bbaker.discord.swrpg.command.ArgumentParser;
import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.InitiativeProcessor;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;
import com.bbaker.discord.swrpg.printer.InitiativePrinter;
import com.bbaker.discord.swrpg.printer.RollerPrinter;
import com.bbaker.discord.swrpg.roller.DiceProcessor;
import com.bbaker.discord.swrpg.roller.DiceTower;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class InitiativeCommand extends BasicCommand implements CommandExecutor {
    public static final String ROLLS_NOT_ALLOWED_MSG = "Someone previously use the `set` command and rolls are disallowed until the initiatives are cleared";
    public static final String EMPTY_INIT_MSG = "There are no characters in the initiative. Please add some first.";
    public static final String NO_CHARACTER_MSG = "No characters were provided. Please include 'pc', 'npc', 'dpc', 'dnpc'. No changes were made to the initiative.";
    public static final String NO_DIE_MSG = "No die were provided. No changes were made to the initiative.";

    private static final Logger logger = LogManager.getLogger(InitiativeCommand.class);
    private static final String NUMERIC_RGX = "[0-9]+";
    RollerPrinter rollerPrinter;
    InitiativePrinter initPrinter;


    public InitiativeCommand(DatabaseService dbService) {
        super(dbService);
        initPrinter = new InitiativePrinter();
        rollerPrinter = new RollerPrinter();
        parser = new ArgumentParser();
    }


    @Command(aliases = {"i", "init", "initiative"}, description = "Manages the initiatives", usage = "!init roll pc gg")
    public String handleInit(Message message) {
        try {
            List<String> tokens = getList(message.getContent());

            InitiativeTracker initTracker = dbService.retrieveInitiative(message.getChannel().getId());

            String response;
            if(tokens.isEmpty()) {
                response = initPrinter.printRoundTurn(initTracker.getRound(), initTracker.getTurn());
            } else {
                response = getResponse(tokens, initTracker);
            }

            dbService.storeInitiative(message.getChannel().getId(), initTracker);

            return response + "\n" + initPrinter.printInit(initTracker);
        } catch (BadArgumentException e) {
            logger.debug(e);
            return e.getMessage();
        } catch (Exception e) {
            logger.debug(e);
            return ERROR_MSG;
        }
    }

    private String getResponse(List<String> tokens, InitiativeTracker initTracker) throws BadArgumentException {
        switch(tokens.remove(0)) {
            case "roll":
            case "r":
                return rollCharacter(tokens, initTracker);
            case "reorder":
                return reorder(tokens, initTracker);
            case "set":
            case "s":
                return set(tokens, initTracker);
            case "confirm":
            case "c":
                return confirmSet(initTracker);
            case "reset":
                initTracker.forceSet(Arrays.asList());
                break;
            case "next":
            case "n":
                return adjust(tokens, initTracker, 1);
            case "previous":
            case "p":
                return adjust(tokens, initTracker, -1);
            case "kill":
            case "k":
                return kill(tokens, initTracker);
            case "revive":
                return revive(tokens, initTracker);
        }

        return "";

    }


    private String rollCharacter(List<String> tokens, InitiativeTracker initTracker) throws BadArgumentException {
        if(!initTracker.canRoll()) {
            throw new BadArgumentException(ROLLS_NOT_ALLOWED_MSG);
        }

        // Get pc, npc, dpc, and dnpc tokens
        InitiativeProcessor initProcessor = new InitiativeProcessor();
        parser.processArguments(tokens.iterator(), initProcessor);
        List<CharacterType> newCharacters = initProcessor.getCharacters();

        // Get the dice
        DiceProcessor diceProcessor = new DiceProcessor();
        parser.processArguments(tokens.iterator(), diceProcessor);
        DiceTower rollResult = diceProcessor.getDiceTower();
        rollResult.roll();
        List<RollableDie> dice = rollResult.getDice();


        if(dice.isEmpty()) {
            throw new BadArgumentException(NO_DIE_MSG);
        }

        if(newCharacters.isEmpty()) {
            throw new BadArgumentException(NO_CHARACTER_MSG);
        }

        String label = "";
        if(!tokens.isEmpty()) {
            label = String.join(" ", tokens);
        }

        for(int i = 0; i < newCharacters.size(); i++) {
            initTracker.addCharacter(
                newCharacters.get(i),
                format(label, i+1),
                rollResult);
        }

        StringBuilder result = new StringBuilder();
        result.append(rollerPrinter.printDice(dice));
        result.append(" = ");
        result.append(rollerPrinter.printResult(rollResult));

        if(!label.isEmpty()) {
            result.append(" for ");
            result.append(label);
        }

        return result.toString();
    }


    private String adjust(List<String> tokens, InitiativeTracker initTracker, int operator) throws BadArgumentException {
        if(initTracker.getInit().isEmpty()) {
            throw new BadArgumentException(EMPTY_INIT_MSG);
        }


        if(tokens.size() == 0) {
            initTracker.adjustTurn(operator * 1, "");
        } else if(tokens.size() == 1 && tokens.get(0).matches(NUMERIC_RGX)) {
            initTracker.adjustTurn(operator * Integer.valueOf(tokens.get(0)), "");
        } else {
            Iterator<String> tokenItr = tokens.iterator();
            parser.processArguments(tokens.iterator(), (token, left, right) -> {
                initTracker.adjustTurn(operator * DiceProcessor.getTotal(left, right), token);
                return true;
            });
        }

        return initPrinter.printRoundTurn(initTracker.getRound(), initTracker.getTurn());
    }

    private String kill(List<String> tokens, InitiativeTracker initTracker) {
        return "Not yet supported";
    }


    private String reorder(List<String> tokens, InitiativeTracker initTracker) throws BadArgumentException {
        // Get pc, npc, dpc, and dnpc tokens
        InitiativeProcessor initProcessor = new InitiativeProcessor();
        parser.processArguments(tokens.iterator(), initProcessor);

        initTracker.reorder(initProcessor.getCharacters());
        return "Reorder successful";
    }


    private String set(List<String> tokens, InitiativeTracker initTracker) throws BadArgumentException {
        // Get pc, npc, dpc, and dnpc tokens
        InitiativeProcessor initProcessor = new InitiativeProcessor();
        parser.processArguments(tokens.iterator(), initProcessor);

        initTracker.forceSet(initProcessor.getCharacters());

        return "Init was force set.";
    }


    private String confirmSet(InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String revive(List<String> tokens, InitiativeTracker initTracker) {
        return "Not yet supported";
    }

    private String format(String template, int index) {
        try {
            return String.format(template, index);
        } catch (IllegalFormatException e) {
            return template;
        }
    }


    public void setInitPrinter(InitiativePrinter initPrinter) {
        this.initPrinter = initPrinter;
    }

    public void setRollPinter(RollerPrinter rollPrinter) {
        this.rollerPrinter = rollPrinter;
    }

    private boolean isNext(String token) {
        switch(token.toLowerCase()) {
            case "n":
            case "next":
                return true;
            default:
                return false;
        }
    }

}
