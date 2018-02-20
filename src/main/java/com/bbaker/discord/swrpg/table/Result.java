package com.bbaker.discord.swrpg.table;

public interface Result {

	/**
	 * Determines if this Die Result passed or failed, and by how much.
	 * @return Negative or 0 for failure. Positive for pass.
	 */
	public int getCheck();

	/**
	 * Determines if this Die Result had advantages or threats, and by how much.
	 * @return Negative or 0 for threats. Positive for advantage.
	 */
	public int getConsequence();

	/**
	 * Determines if this Die Result had any Triumphs. 
	 * @return 0 or 1
	 */

	public int getTriumph();

	/**
	 * Determines if this Die Result had any Despairs
	 * @return 0 or 1
	 */
	public int getDespair();

	/**
	 * Determines if this Die Result had any Light Side Points
	 * @return 0,1,2
	 */
	public int getLightSide();

	/**
	 * Determines if this Die Result had any Dark Side Points
	 * @return 0,1,2
	 */
	public int getDarkSide();


}