package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.BasicDie;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Boost extends BasicDie {

	private static DieResult[] sides = new DieResult[] {
		die(DieType.BOOST).getFace(),
		die(DieType.BOOST).getFace(),
		die(DieType.BOOST).success().getFace(),
		die(DieType.BOOST).success().advantage().getFace(),
		die(DieType.BOOST).advantage().advantage().getFace(),
		die(DieType.BOOST).advantage().getFace()
	};
	
	@Override
	protected DieResult[] getSides() {
		return sides;
	}
	
	@Override
	public DieType getType() {
		return DieType.BOOST;
	}

}
