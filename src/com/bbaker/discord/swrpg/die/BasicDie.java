package com.bbaker.discord.swrpg.die;

public abstract class BasicDie implements Die {
	protected int index = 0;
	protected abstract DieResult[] getSides();
	
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
	
}
