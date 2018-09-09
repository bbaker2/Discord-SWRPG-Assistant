package com.bbaker.discord.swrpg;

import java.util.Iterator;

import com.bbaker.discord.swrpg.table.impl.DiceTower;
import com.bbaker.exceptions.BadArgumentException;

public interface ArgumentHandler {

    /**
     * Will update the table based on the provided <code>args</code>. The implementing class
     * is allowed to {@link Iterator#remove()} arguments if the concrete class thinks no one
     * else should process the value
     * @param args the collection of arguments. Must not contain whitespace
     * @param table the table that will be updated
     * @return true if it is suggested that no more arguments need to processed
     * @throws BadArgumentException
     */
    public boolean processArguments(Iterator<String> args, DiceTower table) throws BadArgumentException;
}
