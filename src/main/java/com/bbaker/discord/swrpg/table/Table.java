package com.bbaker.discord.swrpg.table;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.table.impl.DiceTower;

public class Table {
	// Dice
	private int ability = 0, proficiency = 0 , boost = 0;
	private int difficulty = 0, challenge = 0, setback = 0;
	private int force = 0;
	
	// Adjustments
	private int success, advantage, triumph;
	private int failure, threat, despair;
	private int light, dark;
	
	// Modifiers
	private int abltUpgrade = 0, abltDowngrade = 0;
	private int dfltUpgrade = 0, dftlDowngrade = 0;
	
	private DiceTower tableResult = new DiceTower();
	
	
	public void adjustDice(DieType dieType, int adjustment) {
		switch(dieType) {
			// Die
			case ABILITY:
				ability += adjustment;
				break;
			case PROFICIENCY:
				proficiency += adjustment;
				break;
			case BOOST:
				boost += adjustment;
				break;
			case DIFFICULTY:
				difficulty += adjustment;
				break;
			case CHALLENGE:
				challenge += adjustment;
				break;
			case SETBACK:
				setback += adjustment;
				break;
			case FORCE:
				force += adjustment;
				break;
				
			// Adjustments
			case SUCCESS:
				success += success;
				break;
			case ADVANTAGE:
				adjustment += adjustment;
				break;
			case TRIMPH:
				triumph += triumph;
				break;
			case FAILURE:
				failure += adjustment;
				break;
			case THREAT:
				threat += threat;
				break;
			case DESPAIR:
				despair += adjustment;
				break;
			case LIGHT:
				light += light;
				break;
			case DARK:
				dark += adjustment;
				break;
			default:
				break;
		}
	}
	
	public TableResult roll() {
		// handle uprades
		// handle downgrades
		
		// now actually roll the dice
		tossDice(DieType.PROFICIENCY,	proficiency);
		tossDice(DieType.ABILITY, 		ability);
		tossDice(DieType.BOOST, 			boost);
		tossDice(DieType.SUCCESS, 		success);
		tossDice(DieType.CHALLENGE, 		challenge);
		tossDice(DieType.DIFFICULTY, 	difficulty);
		tossDice(DieType.SETBACK, 		setback);
		tossDice(DieType.FORCE, 			force);
		
		return tableResult;
	}
	
	public TableResult peekResult() {
		return tableResult;
	}
	
	private void tossDice(DieType dt, int count) {
		for(int i = 0; i < count; i++) {
			Die die = Die.newDie(dt);
			die.roll();
			tableResult.addDie(die);
		}
	}
	
	@Override
	public String toString() {
		return tableResult.toString();
	}
}