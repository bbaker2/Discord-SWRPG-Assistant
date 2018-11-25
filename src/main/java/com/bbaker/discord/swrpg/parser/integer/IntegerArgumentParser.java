package com.bbaker.discord.swrpg.parser.integer;

import java.util.Iterator;

import com.bbaker.discord.swrpg.exceptions.BadArgumentException;


public class IntegerArgumentParser {

    private static final String NUMERIC_RGX = "\\-?[0-9]+";

    public void processArguments(Iterator<String> args, IntegerArgumentEvaluator evaluator) throws BadArgumentException {

        boolean successful = false; String token = null;
        while(args.hasNext()) {
            token = args.next();
            if(token.matches(NUMERIC_RGX)) {
                successful = evaluator.evaluate(Integer.valueOf(token));
                if(successful) {
                    args.remove();
                }
            }
        }
    }


}
