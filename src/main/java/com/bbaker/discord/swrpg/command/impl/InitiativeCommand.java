package com.bbaker.discord.swrpg.command.impl;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;

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
    RollerPrinter rollerPrinter;
    InitiativePrinter initPrinter;

    public InitiativeCommand(DatabaseService dbService) {
        super(dbService);
        initPrinter = new InitiativePrinter();
        rollerPrinter = new RollerPrinter();
        parser = new ArgumentParser();
    }


    @Command(aliases = {"i", "init", "initiative"}, description = "Manages the initiatives", usage = "!init roll pc gg")
    public String handleInit(Message message) throws BadArgumentException {
        List<String> tokens = getList(message.getContent());

        InitiativeTracker initTracker = dbService.getInitiative(message.getChannel().getId());

        if(tokens.isEmpty()) {
            return initPrinter.printInit(initTracker);
        }

        String response = "";

        switch(tokens.remove(0)) {
            case "roll":
            case "r":
                response = rollCharacter(tokens, initTracker);
                break;
            case "reorder":
                response = reorder(tokens, initTracker);
                break;
            case "set":
            case "s":
                response = set(tokens, initTracker);
                break;
            case "confirm":
            case "c":
                response = confirmSet(initTracker);
            case "reset":
                initTracker.forceSet(Arrays.asList());
                break;
            case "next":
            case "n":
                response = next(tokens, initTracker);
                break;
            case "previous":
            case "p":
                response = previous(initTracker);
                break;
            case "kill":
            case "k":
                response = kill(tokens, initTracker);
                break;
            case "revive":
                response = revive(tokens, initTracker);
                break;
        }

        dbService.storeInitiative(message.getChannel().getId(), initTracker.getInit());

        return response + "\n" + initPrinter.printInit(initTracker);
    }


    private String rollCharacter(List<String> tokens, InitiativeTracker initTracker) throws BadArgumentException {
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
            // throw error
        }

        if(rollResult.getCheck() < 0) {
            // throw error
        }

        if(rollResult.getConsequence() < 0) {
            // throw error
        }


        if(newCharacters.isEmpty()) {
            // throw error
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


    private String next(List<String> tokens, InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String previous(InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String kill(List<String> tokens, InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String reorder(List<String> tokens, InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String set(List<String> tokens, InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String confirmSet(InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }


    private String revive(List<String> tokens, InitiativeTracker initTracker) {
        // TODO Auto-generated method stub
        return null;
    }

    private String format(String template, int index) {
        try {
            return String.format(template, index);
        } catch (IllegalFormatException e) {
            return template;
        }
    }

}
