package com.bbaker.discord.swrpg.parser.text;

import java.util.OptionalInt;

import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public interface TextArgumentEvaluator {

    /**
     * @param token the string token
     * @param left the numeric value that prefixes the token
     * @param right the numeric value that suffixes the token
     * @return true if the token was evaluated successfully. False otherwise.
     * @throws BadArgumentException if a non-recoverable error occurs
     */
    boolean evaluate(String token, OptionalInt left, OptionalInt right) throws BadArgumentException;

}