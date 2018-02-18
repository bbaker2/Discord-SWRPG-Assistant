package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Adjustment implements Die {
	private DieResult diceResult;
	private DieType dieType;

	public Adjustment(DieType dieType, DieResult diceResult) {
		this.dieType = dieType;
		this.diceResult = diceResult;
	}
	
	@Override
	public DieType getType() {
		return dieType;
	}

	@Override
	public DieResult roll() {
		return diceResult;
	}

	@Override
	public String getFace() {
		return dieType.getEmoji();
	}

}
