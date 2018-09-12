package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Force extends BasicDie {

	private static DieResult[] sides = new DieResult[] {
		die(DieType.FORCE).dark().getFace(),
		die(DieType.FORCE).dark().getFace(),
		die(DieType.FORCE).dark().getFace(),
		die(DieType.FORCE).dark().getFace(),
		die(DieType.FORCE).dark().getFace(),
		die(DieType.FORCE).dark().getFace(),
		die(DieType.FORCE).dark().dark().getFace(),
		die(DieType.FORCE).light().getFace(),
		die(DieType.FORCE).light().getFace(),
		die(DieType.FORCE).light().light().getFace(),
		die(DieType.FORCE).light().light().getFace(),
		die(DieType.FORCE).light().light().getFace()
	};
	
	public Force(int side) {
		super(side);
	}

	@Override
	protected DieResult[] getSides() {
		return sides;
	}
	
	@Override
	public DieType getType() {
		return DieType.FORCE;
	}

}
