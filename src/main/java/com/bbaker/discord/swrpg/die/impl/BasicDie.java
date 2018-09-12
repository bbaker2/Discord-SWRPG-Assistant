package com.bbaker.discord.swrpg.die.impl;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieFaceBuilder;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.RollableDie;

public abstract class BasicDie implements RollableDie {
	private int index = 0;
	protected abstract DieResult[] getSides();
	
	/**
	 * Lets you create a die with a pre-set side. 
	 * @param side the starting side
	 */
	public BasicDie(int side) {
		this.index = side;
	}
	
	@Override
	public int getSide() {
		return index;
	}
	
	@Override
	public DieResult getResults() {
		DieResult[] sides = getSides();
		return sides[index];
	}
	
	@Override
	public DieResult roll() {
		DieResult[] sides = getSides();
		index = (int) (Math.random() * sides.length);
		
		return sides[index];
	}

	@Override
	public String getFace() {
		return getSides()[index].getFace();
	}
	
	protected static DieFaceBuilder die(DieType dt) {
		return new DieFaceBuilder(dt);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof RollableDie) {
			Die trg = (Die)o;
			return trg.getType() == this.getType() && trg.getSide() == this.getSide();
		}
		return super.equals(o);
	}
	
}
