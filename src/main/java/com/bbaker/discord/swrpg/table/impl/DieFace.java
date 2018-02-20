package com.bbaker.discord.swrpg.table.impl;

import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.DieType;

public class DieFace implements DieResult {
	
	int check, consequence, trimph, despair, light, dark;
	DieType type;
	String face;
	
	public DieFace(DieType type, String emoji, int check, int consequence, int trimph, int despair, int light, int dark) {
		this.type = type;
		this.face = emoji;
		this.check = check;
		this.consequence = consequence;
		this.trimph = trimph;
		this.despair = despair;
		this.light = light;
		this.dark = dark;		
	}

	@Override
	public int getCheck() {
		return check;
	}

	@Override
	public int getConsequence() {
		return consequence;
	}

	@Override
	public int getTriumph() {
		return trimph;
	}

	@Override
	public int getDespair() {
		return despair;
	}

	@Override
	public int getLightSide() {
		return light;
	}

	@Override
	public int getDarkSide() {
		return dark;
	}

	@Override
	public String getFace() {
		return face;
	}
	
	
	@Override
	public String toString() {
		return "(" + getFace() + ")";
	}

}
