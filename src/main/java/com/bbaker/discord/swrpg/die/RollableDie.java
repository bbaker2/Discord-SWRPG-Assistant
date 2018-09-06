package com.bbaker.discord.swrpg.die;

import com.bbaker.discord.swrpg.die.impl.Ability;
import com.bbaker.discord.swrpg.die.impl.Adjustment;
import com.bbaker.discord.swrpg.die.impl.Boost;
import com.bbaker.discord.swrpg.die.impl.Challenge;
import com.bbaker.discord.swrpg.die.impl.Difficulty;
import com.bbaker.discord.swrpg.die.impl.Force;
import com.bbaker.discord.swrpg.die.impl.Proficiency;
import com.bbaker.discord.swrpg.die.impl.Setback;

public interface RollableDie extends Die {

	/**
	 * Randomly selects a side between 0 and the max # of sides - 1
	 * @return {@link #getResults()} after the {@link #getSide() side has been updated}
	 */
	public DieResult roll();
	
	public static RollableDie newDie(DieType dieType) {
		return newDie(dieType, 0);
	}
	
	public static RollableDie newDie(DieType dieType, int side) {
		switch(dieType) {
			// Die
			case ABILITY:
				return new Ability(side);
			case PROFICIENCY:
				return new Proficiency(side);
			case BOOST:
				return new Boost(side);
			case DIFFICULTY:
				return new Difficulty(side);
			case CHALLENGE:
				return new Challenge(side);
			case SETBACK:
				return new Setback(side);
			case FORCE:
				return new Force(side);
				
			// Adjustments
			case SUCCESS:
				return new Adjustment(DieType.SUCCESS, 	new DieFaceBuilder(DieType.SUCCESS).success().getFace());
			case ADVANTAGE:
				return new Adjustment(DieType.ADVANTAGE, new DieFaceBuilder(DieType.ADVANTAGE).advantage().getFace());
			case TRIUMPH:
				return new Adjustment(DieType.TRIUMPH, 	new DieFaceBuilder(DieType.TRIUMPH).triumph().getFace());
			case FAILURE:
				return new Adjustment(DieType.FAILURE, 	new DieFaceBuilder(DieType.FAILURE).failure().getFace());
			case THREAT:
				return new Adjustment(DieType.THREAT,	new DieFaceBuilder(DieType.THREAT).threat().getFace());
			case DESPAIR:
				return new Adjustment(DieType.DESPAIR, 	new DieFaceBuilder(DieType.DESPAIR).despair().getFace());
			case LIGHT:
				return new Adjustment(DieType.LIGHT, 	new DieFaceBuilder(DieType.LIGHT).light().getFace());
			case DARK:
				return new Adjustment(DieType.DARK, 		new DieFaceBuilder(DieType.DARK).dark().getFace());
			default:
				return null; // this should not be reachable
		}
		
	}
	
}
