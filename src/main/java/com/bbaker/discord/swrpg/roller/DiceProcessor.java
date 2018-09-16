package com.bbaker.discord.swrpg.roller;

import java.util.OptionalInt;

import com.bbaker.discord.swrpg.command.ArgumentProcessor;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public class DiceProcessor implements ArgumentProcessor {

    private DiceTower diceTower;

    public DiceProcessor() {
        this(new DiceTower());
    }

    public DiceProcessor(DiceTower diceTower){
        this.diceTower = diceTower;
    }

    public DiceTower getDiceTower() {
        return this.diceTower;
    }

    @Override
    public boolean evaluate(String token, OptionalInt left, OptionalInt right) throws BadArgumentException {
        DieType dieType = findDie(token);
        if(dieType == null) {
            return false;
        }
        int total = getTotal(left, right);
        diceTower.addDie(dieType, total);
        return true;
    }

    @Override
    public boolean isToken(String token) {
        return findDie(token) != null;
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

    public static int getTotal(OptionalInt left, OptionalInt right) {
        int total = 0;
        boolean useDefault = true;

        if(left.isPresent()) {
            total += left.getAsInt();
            useDefault = false;
        }

        if(right.isPresent()) {
            total += right.getAsInt();
            useDefault = false;
        }

        if(useDefault) {
            total = 1;
        }
        return total;
    }

}
