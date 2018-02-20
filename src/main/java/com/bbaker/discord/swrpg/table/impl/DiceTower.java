package com.bbaker.discord.swrpg.table.impl;

import java.util.ArrayList;
import java.util.List;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.table.Result;

public class DiceTower implements TableResult {
	int check = 0, consequence = 0,
		trimph = 0, despair = 0,
		light = 0, dark = 0;
	
	List<Die> dice = new ArrayList<Die>();
	
	public void adjustTable(Result dr) {
		check += dr.getCheck();
		consequence += dr.getConsequence();
		trimph += dr.getTriumph();
		despair += dr.getDespair();
		light += dr.getLightSide();
		dark += dr.getDarkSide();
	}
	
	public void addDie(Die die) {
		adjustTable(die.peek());
		dice.add(die);
		System.out.println(die.getType() + " -> " + die.peek());
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
		return "";
	}

	@Override
	public List<Die> getDice() {
		return dice;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Die dr : getDice()) {
			sb.append(dr.peek()).append(" ");
		}
		if(sb.length() > 0) {
			sb.append("\n");
		}
		sb.append( String.format("check: %s; consequence: %s; trimph: %s; despair: %s; light: %s; dark: %s", 
				getCheck(), getConsequence(), getTriumph(), getDespair(), getLightSide(), getDarkSide()) );
		return sb.toString();
	}

}
