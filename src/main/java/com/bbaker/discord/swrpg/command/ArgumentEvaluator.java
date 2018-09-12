package com.bbaker.discord.swrpg.command;

import java.util.OptionalInt;

import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public interface ArgumentEvaluator {

    boolean evaluate(String token, OptionalInt left, OptionalInt right) throws BadArgumentException;

}
