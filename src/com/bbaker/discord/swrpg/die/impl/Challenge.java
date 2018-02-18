package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.BasicDie;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Challenge extends BasicDie {
	private static DieResult[] sides = new DieResult[] {
		die(DieType.CHALLENGE).getFace(),
		die(DieType.CHALLENGE).failure().getFace(),
		die(DieType.CHALLENGE).failure().getFace(),
		die(DieType.CHALLENGE).failure().failure().getFace(),
		die(DieType.CHALLENGE).failure().failure().getFace(),
		die(DieType.CHALLENGE).threat().getFace(),
		die(DieType.CHALLENGE).threat().getFace(),
		die(DieType.CHALLENGE).failure().threat().getFace(),
		die(DieType.CHALLENGE).failure().threat().getFace(),
		die(DieType.CHALLENGE).threat().threat().getFace(),
		die(DieType.CHALLENGE).threat().threat().getFace(),
		die(DieType.CHALLENGE).despair().getFace(),
	};
	
	@Override
	protected DieResult[] getSides() {
		return sides;
	}
	
	@Override
	public DieType getType() {
		return DieType.CHALLENGE;
	}

}
