package com.bbaker.discord.swrpg.die;

import com.bbaker.discord.swrpg.table.Result;

public interface DieResult extends Result {
	
	/**
	 * The formatted emoji styel face of the die
	 * @return an emoji string
	 */
	public String getFace();
	
}
