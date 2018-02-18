package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.BasicDie;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Proficiency extends BasicDie {
	private static DieResult[] sides = new DieResult[] {
		die(DieType.PROFICIENCY).getFace(),
		die(DieType.PROFICIENCY).success().getFace(),
		die(DieType.PROFICIENCY).success().getFace(),
		die(DieType.PROFICIENCY).success().success().getFace(),
		die(DieType.PROFICIENCY).success().success().getFace(),
		die(DieType.PROFICIENCY).advantage().getFace(),
		die(DieType.PROFICIENCY).success().advantage().getFace(),
		die(DieType.PROFICIENCY).success().advantage().getFace(),
		die(DieType.PROFICIENCY).success().advantage().getFace(),
		die(DieType.PROFICIENCY).advantage().advantage().getFace(),
		die(DieType.PROFICIENCY).advantage().advantage().getFace(),
		die(DieType.PROFICIENCY).triumph().getFace()
	};
	
	@Override
	protected DieResult[] getSides() {
		return sides;
	}

	@Override
	public DieType getType() {
		return DieType.PROFICIENCY;
	}
}
