package com.bbaker.discord.swrpg.roller;

import java.util.OptionalInt;

import com.bbaker.exceptions.BadArgumentException;

public interface ArgumentEvaluator {

    boolean evaluate(String token, OptionalInt left, OptionalInt right) throws BadArgumentException;

}
