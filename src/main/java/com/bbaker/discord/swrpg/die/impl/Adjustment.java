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
	public int getSide() {
		return 0;
	}

	@Override
	public DieResult peek() {
		return diceResult;
	}
	
	@Override
	public DieResult roll() {
		return diceResult;
	}

	@Override
	public String getFace() {
		return dieType.getEmoji();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Die) {
			Die trg = (Die)o;
			return trg.getType() == this.getType() && trg.getSide() == this.getSide();
		}
		return super.equals(o);
	}

}
