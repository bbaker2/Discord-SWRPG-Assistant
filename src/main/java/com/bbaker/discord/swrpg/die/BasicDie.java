package com.bbaker.discord.swrpg.die;

public abstract class BasicDie implements Die {
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
	public DieResult peek() {
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
		if(o instanceof Die) {
			Die trg = (Die)o;
			return trg.getType() == this.getType() && trg.getSide() == this.getSide();
		}
		return super.equals(o);
	}
	
}
