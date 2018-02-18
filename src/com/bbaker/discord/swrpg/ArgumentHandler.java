package com.bbaker.discord.swrpg;

import java.util.Iterator;

import com.bbaker.discord.swrpg.table.Table;

public interface ArgumentHandler {
	
	/**
	 * Will update the table based on the provided <code>args</code>. The implementing class
	 * is allowed to {@link Iterator#remove()} arguments if the concrete class thinks no one
	 * else should process the value
	 * @param args the collection of arguments. Must not contain whitespace
	 * @param table the table that will be updated
	 * @return true if it is suggested that no more arguments need to processed
	 */
	public boolean processArguments(Iterator<String> args, Table table);
}
