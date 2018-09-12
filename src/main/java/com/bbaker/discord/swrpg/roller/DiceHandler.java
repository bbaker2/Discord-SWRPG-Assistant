package com.bbaker.discord.swrpg.roller;

import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;

import com.bbaker.discord.swrpg.ArgumentHandler;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.table.impl.DiceTower;
import com.bbaker.exceptions.BadArgumentException;

public abstract class DiceHandler implements ArgumentHandler {

    ArgumentParser parser = new ArgumentParser();

    public abstract boolean evaluate(DiceTower table, DieType dt, OptionalInt left, OptionalInt right) throws BadArgumentException;

    @Override
    public boolean processArguments(Iterator<String> args, DiceTower table) throws BadArgumentException {
        return parser.processArguments(args, (token, left, right) -> {
            DieType dieType = findDie(token);
            if(dieType == null) {
                return false;
            }

            return evaluate(table, dieType, left, right);
        });
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


    /**
     * Never throws an exception. Assumes 0 if anything goes wrong
     * @param val
     * @return the numeric value of a string. 0 if unsuccessful for any reason
     */
    private Optional<Integer> getCount(String val) {
        if(val == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.valueOf(val));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }


}
