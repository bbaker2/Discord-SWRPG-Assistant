package com.bbaker.discord.swrpg.die;

import org.javacord.api.DiscordApi;

import com.bbaker.discord.swrpg.die.impl.Adjustment;

public interface Die {

	/**
	 * An identifier for the type of die this represents
	 * @return the Die's type
	 */
	DieType getType();

	/**
	 * The numeric representation of the side that is face up.
	 * By default, this should be 0 until {@link #roll()} is called.
	 * @return any number between 0 and the max # of sides - 1
	 */
	int getSide();

	/**
	 * Retrieve the die value without changing the {@link #getSide()}
	 * @return the current {@link DieResult value} that correlates with {@link #getSide()}
	 */
	DieResult getResults();

	/**
	 * The string emoji version of the current {@link #getSide()}
	 * @return an emoji string, unprocessed by {@link DiscordApi#getCustomEmojisByName(String) discord api}
	 */
	String getFace();

	// Adjustment Die, used for adjust the TableResult
	Die SUCCESS = new Adjustment(DieType.SUCCESS, new DieFaceBuilder(DieType.SUCCESS).success().getFace());
	Die ADVANTAGE = new Adjustment(DieType.ADVANTAGE, new DieFaceBuilder(DieType.ADVANTAGE).advantage().getFace());
	Die TRIUMPH = new Adjustment(DieType.TRIUMPH, new DieFaceBuilder(DieType.TRIUMPH).triumph().getFace());
	Die FAILURE = new Adjustment(DieType.FAILURE, new DieFaceBuilder(DieType.FAILURE).failure().getFace());
	Die THREAT = new Adjustment(DieType.THREAT, new DieFaceBuilder(DieType.THREAT).threat().getFace());
	Die DESPAIR = new Adjustment(DieType.DESPAIR, new DieFaceBuilder(DieType.DESPAIR).despair().getFace());
	Die LIGHT = new Adjustment(DieType.LIGHT, new DieFaceBuilder(DieType.LIGHT).light().getFace());
	Die DARK = new Adjustment(DieType.DARK, new DieFaceBuilder(DieType.DARK).dark().getFace());

}