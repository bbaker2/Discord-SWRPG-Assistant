package com.bbaker.discord.swrpg.roller;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bbaker.discord.swrpg.ArgumentHandler;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.table.Table;

public class RollerHandler implements ArgumentHandler {
	private static final Pattern diceRgx = Pattern.compile("(\\d+)?([A-Za-z]+)(\\d+)?");
	private static final int LEFT_COUNT = 1;
	private static final int DIE_TOKEN = 2;
	private static final int RIGHT_COUNT = 3;
	
	@Override
	public boolean processArguments(Iterator<String> args, Table table) {
		boolean allRemoved = true;
		Matcher m; DieType dieType; int count;
		while(args.hasNext()) {
			m = diceRgx.matcher(args.next());
			
			// In general, we remove any token that matches a die
			if(m.find()) {
				dieType = findDie(m.group(DIE_TOKEN)); // the actual string token
				if(dieType == null) {
					// If no die was found immediately, split apart into a char array and try again
					if(tryAgain(m.group(DIE_TOKEN), table)) {
						args.remove(); // remove since a die was found
					} else {
						allRemoved = false;
					}
				} else {
					count = 0;
					count += getCount(m.group(LEFT_COUNT)); // left side numeric
					count += getCount(m.group(RIGHT_COUNT)); // right side numeric
					
					// If no count was provided, assume 1
					if(count <= 0) {
						count = 1;
					}
					table.adjustDice(dieType, count);
					args.remove(); // remove since a die was found
				}
			}
		}
		return allRemoved;
	}
	
	/**
	 * Splits <code>value</code> into chars and tries to convert them into
	 * die (only using the shorthand version of die tokens)
	 * @param value the string that will be split up by character
	 * @param table the table to update
	 * @return TRUE if ALL characters can be converted to a die. Otherwise FALSE
	 */
	private boolean tryAgain(String value, Table table) {
		char[] splitUp = value.toCharArray();
		DieType[] foundDice = new DieType[splitUp.length];
		DieType dieType;
		for(int i = 0; i < splitUp.length; i++) {
			dieType = findDie(Character.toString(splitUp[i]));
			if(dieType == null) {
				return false;
			} else {
				foundDice[i] = dieType;
			}
		}
		
		for(DieType td : foundDice) {
			table.adjustDice(td, 1);
		}
		return true;
	}
	
	public static DieType findDie(String strToken) {
		switch(strToken) {
			case "g":
			case "green":
			case "a":
			case "ability":
				return DieType.ABILITY;
				
				
			case "y":
			case "yellow":
			case "proficiency":
				return DieType.PROFICIENCY;
				
			case "b":
			case "blue":
			case "boost":
				return DieType.BOOST;
				
			case "p":
			case "purple":
			case "d":
			case "difficulty":
				return DieType.DIFFICULTY;
				
			case "r":
			case "red":
			case "c":
			case "challenge":
				return DieType.CHALLENGE;
				
			case "k":
			case "black":
			case "s":
			case "setback":
				return DieType.SETBACK;
				
			case "w":
			case "white":
			case "f":
			case "force":
				return DieType.FORCE;
			
		}
		return null;
	}
	
	
	/**
	 * Never throws an exception. Assumes 0 if anything goes wrong
	 * @param val
	 * @return the numeric value of a string. 0 if unsuccessful for any reason
	 */
	private int getCount(String val) {
		if(val == null) {
			return 0;
		}
		
		try {
			return Integer.valueOf(val);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
