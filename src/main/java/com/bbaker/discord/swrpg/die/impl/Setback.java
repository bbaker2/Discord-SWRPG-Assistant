package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class Setback extends BasicDie {

	private static DieResult[] sides = new DieResult[] {
		die(DieType.SETBACK).getFace(),
		die(DieType.SETBACK).getFace(),
		die(DieType.SETBACK).failure().getFace(),
		die(DieType.SETBACK).failure().getFace(),
		die(DieType.SETBACK).threat().getFace(),
		die(DieType.SETBACK).threat().getFace()
	};
	
	public Setback(int side) {
		super(side);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected DieResult[] getSides() {
		return sides;
	}
	
	@Override
	public DieType getType() {
		return DieType.SETBACK;
	}

}
