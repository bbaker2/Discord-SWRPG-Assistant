package com.bbaker.discord.swrpg.die;

import org.javacord.api.DiscordApi;

import com.bbaker.discord.swrpg.die.impl.Ability;
import com.bbaker.discord.swrpg.die.impl.Adjustment;
import com.bbaker.discord.swrpg.die.impl.Boost;
import com.bbaker.discord.swrpg.die.impl.Challenge;
import com.bbaker.discord.swrpg.die.impl.Difficulty;
import com.bbaker.discord.swrpg.die.impl.Force;
import com.bbaker.discord.swrpg.die.impl.Proficiency;
import com.bbaker.discord.swrpg.die.impl.Setback;

public interface Die {

	/**
	 * An identifier for the type of die this represents
	 * @return the Die's type
	 */
	public DieType getType();
	
	/**
	 * The numeric representation of the side that is face up.
	 * By default, this should be 0 until {@link #roll()} is called.
	 * @return any number between 0 and the max # of sides - 1
	 */
	public int getSide();
	
	/**
	 * Retrieve the die value without changing the {@link #getSide()}
	 * @return the current {@link DieResult value} that correlates with {@link #getSide()}
	 */
	public DieResult peek();
	
	/**
	 * Randomly selects a side between 0 and the max # of sides - 1
	 * @return {@link #peek()} after the {@link #getSide() side has been updated}
	 */
	public DieResult roll();
	
	/**
	 * The string emoji version of the current {@link #getSide()}
	 * @return an emoji string, unprocessed by {@link DiscordApi#getCustomEmojisByName(String) discord api}
	 */
	public String getFace();
	
	// Adjustment Die, used for adjust the TableResult
	public static final Die SUCCESS = 	new Adjustment(DieType.SUCCESS, 	new DieFaceBuilder(DieType.SUCCESS).success().getFace());
	public static final Die ADVANTAGE = 	new Adjustment(DieType.ADVANTAGE, 	new DieFaceBuilder(DieType.ADVANTAGE).advantage().getFace());
	public static final Die TRIUMPH = 	new Adjustment(DieType.TRIUMPH, 		new DieFaceBuilder(DieType.TRIUMPH).triumph().getFace());
	public static final Die FAILURE = 	new Adjustment(DieType.FAILURE, 	new DieFaceBuilder(DieType.FAILURE).failure().getFace());
	public static final Die THREAT = 	new Adjustment(DieType.THREAT, 		new DieFaceBuilder(DieType.THREAT).threat().getFace());
	public static final Die DESPAIR = 	new Adjustment(DieType.DESPAIR, 	new DieFaceBuilder(DieType.DESPAIR).despair().getFace());
	public static final Die LIGHT = 		new Adjustment(DieType.LIGHT, 		new DieFaceBuilder(DieType.LIGHT).light().getFace());
	public static final Die DARK = 		new Adjustment(DieType.DARK, 		new DieFaceBuilder(DieType.DARK).dark().getFace());
	
	public static Die newDie(DieType dieType) {
		return newDie(dieType, 0);
	}
	
	public static Die newDie(DieType dieType, int side) {
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
