package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.BasicDie;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Difficulty extends BasicDie {

	private static DieResult[] sides = new DieResult[] {
		die(DieType.DIFFICULTY).getFace(),
		die(DieType.DIFFICULTY).failure().getFace(),
		die(DieType.DIFFICULTY).failure().failure().getFace(),
		die(DieType.DIFFICULTY).threat().getFace(),
		die(DieType.DIFFICULTY).threat().getFace(),
		die(DieType.DIFFICULTY).threat().getFace(),
		die(DieType.DIFFICULTY).threat().threat().getFace(),
		die(DieType.DIFFICULTY).failure().threat().getFace(),
	};
	
	@Override
	protected DieResult[] getSides() {
		return sides;
	}
	
	@Override
	public DieType getType() {
		return DieType.DIFFICULTY;
	}

}
