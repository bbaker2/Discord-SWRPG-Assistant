package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Ability extends BasicDie {

	private static DieResult[] sides = new DieResult[] {
		die(DieType.ABILITY).getFace(),
		die(DieType.ABILITY).success().getFace(),
		die(DieType.ABILITY).success().getFace(),
		die(DieType.ABILITY).success().success().getFace(),
		die(DieType.ABILITY).advantage().getFace(),
		die(DieType.ABILITY).advantage().getFace(),
		die(DieType.ABILITY).success().advantage().getFace(),
		die(DieType.ABILITY).advantage().advantage().getFace()
	};
	
	public Ability(int side) {
		super(side);
	}
	
	@Override
	protected DieResult[] getSides() {
		return sides;
	}
	
	@Override
	public DieType getType() {
		return DieType.ABILITY;
	}

}
