package com.bbaker.discord.swrpg.command;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;

import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.IndexProcessor;
import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.InitiativeProcessor;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;
import com.bbaker.discord.swrpg.parser.integer.IntegerArgumentParser;
import com.bbaker.discord.swrpg.parser.text.TextArgumentParser;
import com.bbaker.discord.swrpg.printer.InitiativePrinter;
import com.bbaker.discord.swrpg.printer.RollerPrinter;
import com.bbaker.discord.swrpg.roller.DiceProcessor;
import com.bbaker.discord.swrpg.roller.DiceTower;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class InitiativeCommand extends BasicCommand implements CommandExecutor {
    public static final String POSITION_NOT_FOUND_MSG = "No characters found at position %d";
    public static final String TYPE_NOT_FOUND_MSG = "Unable to find %s";
    public static final String MISSING_KILL_MSG = "Please specify a character to kill (ie: pc or npc) or an index position to kill (starting at 1)";
    public static final String ROLLS_NOT_ALLOWED_MSG = "Someone previously use the `set` command and rolls are disallowed until the initiatives are cleared";
    public static final String EMPTY_INIT_MSG = "There are no characters in the initiative. Please add some first.";
    public static final String NO_CHARACTER_MSG = "No characters were provided. Please include 'pc', 'npc', 'dpc', 'dnpc'. No changes were made to the initiative.";
    public static final String NO_DIE_MSG = "No die were provided. No changes were made to the initiative.";

    private static final Logger logger = LogManager.getLogger(InitiativeCommand.class);
    private static final String NUMERIC_RGX = "[0-9]+";
    RollerPrinter rollerPrinter;
    InitiativePrinter initPrinter;
    IntegerArgumentParser indexParser;


    public InitiativeCommand(DatabaseService dbService) {
        super(dbService);
        initPrinter = new InitiativePrinter();
        rollerPrinter = new RollerPrinter();
        parser = new TextArgumentParser();
        indexParser = new IntegerArgumentParser();
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
                kill(tokens, initTracker);
                break;
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
        } else if(tokens.size() == 1 && peek(tokens).matches(NUMERIC_RGX)) {
            initTracker.adjustTurn(operator * Integer.valueOf(pop(tokens)), "");
        } else {
            parser.processArguments(tokens.iterator(), (token, left, right) -> {
                initTracker.adjustTurn(operator * DiceProcessor.getTotal(left, right), token);
                return true;
            });
        }

        return initPrinter.printRoundTurn(initTracker.getRound(), initTracker.getTurn());
    }

    private void kill(List<String> tokens, InitiativeTracker initTracker) throws BadArgumentException {
        List<InitCharacter> activeCharacters = initTracker.getInit();
        if(activeCharacters.isEmpty()) {
            throw new BadArgumentException(EMPTY_INIT_MSG);
        }

        if(tokens.isEmpty()) {
            throw new BadArgumentException(MISSING_KILL_MSG);
        }

        killByPosition(tokens, activeCharacters);

        killByType(tokens, activeCharacters);
    }


    private void killByType(List<String> tokens, List<InitCharacter> activeCharacters) throws BadArgumentException {
        InitiativeProcessor initProcessor = new InitiativeProcessor();
        parser.processArguments(tokens.iterator(), initProcessor);
        List<CharacterType> deadCharacters = initProcessor.getCharacters();

        for(CharacterType toKill : deadCharacters) {
            searchAndKill(toKill, activeCharacters);
        }
    }


    private void killByPosition(List<String> tokens, List<InitCharacter> activeCharacters) throws BadArgumentException {
        IndexProcessor indexEvalutor = new IndexProcessor();
        indexParser.processArguments(tokens.iterator(), indexEvalutor);
        SortedSet<Integer> indexs = indexEvalutor.getIndexs();


        if(!indexs.isEmpty()) {
            for(int i : indexs) {
                killAtIndex(activeCharacters, i-1);
            }
        }
    }

    private void searchAndKill(CharacterType target, List<InitCharacter> activeCharacters) throws BadArgumentException {
        for(int i = activeCharacters.size()-1; i > 0; i--) {
            if(activeCharacters.get(i).getType() == target) {
                killAtIndex(activeCharacters, i);
                return;
            }
        }
        throw new BadArgumentException(TYPE_NOT_FOUND_MSG, target);
    }

    private CharacterType reverseType(CharacterType ct) {
        if(ct == CharacterType.NPC) {
            return CharacterType.DNPC;
        } else if(ct == CharacterType.PC) {
            return CharacterType.DPC;
        } else {
            return ct; // assume the character is already dead
        }
    }


    private void killAtIndex(List<InitCharacter> activeCharacters, int index) throws BadArgumentException {

        if(index < 0 || index >= activeCharacters.size()) {
            throw new BadArgumentException(POSITION_NOT_FOUND_MSG, index+1);
        }

        InitCharacter curChar = activeCharacters.get(index);
        CharacterType ct = curChar.getType();
        curChar.setType(reverseType(ct));
    }

    private <T> T peek(List<T> list) {
        return list.get(0);
    }

    private <T> T pop(List<T> list) {
        return list.remove(0);
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

}
