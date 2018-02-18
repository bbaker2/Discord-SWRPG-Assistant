package com.bbaker.discord.swrpg.die;

import java.util.List;

public interface TableResult extends DieResult {
	
	/**
	 * All the dice used for this roll
	 * @return
	 */
	public List<DieResult> getDice();
}
