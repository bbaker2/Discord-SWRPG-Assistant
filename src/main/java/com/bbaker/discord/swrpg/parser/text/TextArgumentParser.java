package com.bbaker.discord.swrpg.parser.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public class TextArgumentParser {

    private static final Pattern diceRgx = Pattern.compile("(\\d+)?([A-Za-z]+)(\\d+)?");
    private static final int LEFT_COUNT = 1;
    private static final int DIE_TOKEN = 2;
    private static final int RIGHT_COUNT = 3;


    public boolean processArguments(Iterator<String> args, TextArgumentEvaluator eval) throws BadArgumentException {
        return processArguments(args, (token)->true, eval);
    }

    public boolean processArguments(Iterator<String> args, TextArgumentChecker checker, TextArgumentEvaluator eval) throws BadArgumentException {
        return processArguments(args, new TextArgumentProcessor() {

            @Override
            public boolean isToken(String token) {
                return checker.isToken(token);
            }

            @Override
            public boolean evaluate(String token, OptionalInt left, OptionalInt right) throws BadArgumentException {
                return eval.evaluate(token, left, right);
            }
        });
    }

    public boolean processArguments(Iterator<String> args, TextArgumentProcessor processor) throws BadArgumentException {
        boolean allRemoved = true;
        Matcher m; String token; OptionalInt leftNum; OptionalInt rightNum; boolean success;
        while(args.hasNext()) {
            m = diceRgx.matcher(args.next());

            // In general, we remove any token that matches a die
            if(m.find()) {
                token = m.group(DIE_TOKEN); // the actual string token
                leftNum = getCount(m.group(LEFT_COUNT)); // left side numeric
                rightNum = getCount(m.group(RIGHT_COUNT)); // right side numeric
                if(token != null) {
                    if(processor.evaluate(token, leftNum, rightNum)) {
                        args.remove(); // remove since a die was found
                    // If no die was found immediately, split apart into a char array and try again
                    } else if (tryAgain(token, processor)) {
                        allRemoved = false;
                        args.remove(); // remove since the tryAgain was successful
                    }
                } else {
                    allRemoved = false;
                }
            }
        }
        return allRemoved;
    }

    /**
     * Splits <code>value</code> into chars and tries to convert them into
     * die (only using the shorthand version of die tokens)
     * @param value the string that will be split up by character
     * @param eval process the token
     * @return TRUE if ALL characters can be converted to a die. Otherwise FALSE
     * @throws BadArgumentException
     */
    private boolean tryAgain(String value, TextArgumentProcessor process) throws BadArgumentException {
        char[] splitUp = value.toCharArray();
        List<String> tokens = new ArrayList<String>();
        String token;
        for(char c : splitUp) {
            token = Character.toString(c);
            if(process.isToken(token)) {
                tokens.add(token);
            } else {
                return false; // Then we cannot process this
            }
        }

        for(String t : tokens) {
            if(!process.evaluate(t, OptionalInt.empty(), OptionalInt.empty())) { // actually process this
                return false;
            }

        }
        return true;
    }

    /**
     * Never throws an exception. Assumes 0 if anything goes wrong
     * @param val
     * @return the numeric value of a string. 0 if unsuccessful for any reason
     */
    private OptionalInt getCount(String val) {
        if(val == null) {
            return OptionalInt.empty();
        }

        try {
            return OptionalInt.of(Integer.valueOf(val));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
