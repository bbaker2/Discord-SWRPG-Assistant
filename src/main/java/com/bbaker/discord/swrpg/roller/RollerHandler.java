package com.bbaker.discord.swrpg.roller;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bbaker.discord.swrpg.ArgumentHandler;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.table.impl.DiceTower;

public class RollerHandler implements ArgumentHandler {
    private static final Pattern diceRgx = Pattern.compile("(\\d+)?([A-Za-z]+)(\\d+)?");
    private static final int LEFT_COUNT = 1;
    private static final int DIE_TOKEN = 2;
    private static final int RIGHT_COUNT = 3;

    @Override
    public boolean processArguments(Iterator<String> args, DiceTower diceTower) {
        boolean allRemoved = true;
        Matcher m; DieType dieType; int count;
        while(args.hasNext()) {
            m = diceRgx.matcher(args.next());

            // In general, we remove any token that matches a die
            if(m.find()) {
                dieType = findDie(m.group(DIE_TOKEN)); // the actual string token
                if(dieType == null) {
                    // If no die was found immediately, split apart into a char array and try again
                    if(tryAgain(m.group(DIE_TOKEN), diceTower)) {
                        args.remove(); // remove since a die was found
                    } else {
                        allRemoved = false;
                    }
                } else {
                    count = getQuanity(m);
                    diceTower.addDie(dieType, count);
                    args.remove(); // remove since a die was found
                }
            }
        }
        return allRemoved;
    }

    private int getQuanity(Matcher m) {
        int count = 0;
        boolean useDefault = true;
        String numStr;

        if((numStr = m.group(LEFT_COUNT)) != null) {
            count += getCount(numStr); // left side numeric
            useDefault = false;
        }

        if((numStr = m.group(RIGHT_COUNT)) != null) {
            count += getCount(numStr); // right side numeric
            useDefault = false;
        }

        // If no count was provided, assume 1
        if(useDefault) {
            count = 1;
        }

        return count;
    }

    /**
     * Splits <code>value</code> into chars and tries to convert them into
     * die (only using the shorthand version of die tokens)
     * @param value the string that will be split up by character
     * @param diceTower the table to update
     * @return TRUE if ALL characters can be converted to a die. Otherwise FALSE
     */
    private boolean tryAgain(String value, DiceTower diceTower) {
        char[] splitUp = value.toCharArray();
        DieType[] foundDice = new DieType[splitUp.length];
        DieType dieType;
        for(int i = 0; i < splitUp.length; i++) {
            dieType = findDie(Character.toString(splitUp[i]));
            if(dieType == null) {
                return false;
            } else {
                foundDice[i] = dieType;
            }
        }

        for(DieType td : foundDice) {
            diceTower.addDie(td, 1);
        }
        return true;
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
    private int getCount(String val) {
        try {
            return Integer.valueOf(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
