package com.bbaker.discord.swrpg.die;

import com.bbaker.discord.swrpg.die.impl.Ability;
import com.bbaker.discord.swrpg.die.impl.Adjustment;
import com.bbaker.discord.swrpg.die.impl.Boost;
import com.bbaker.discord.swrpg.die.impl.Challenge;
import com.bbaker.discord.swrpg.die.impl.Difficulty;
import com.bbaker.discord.swrpg.die.impl.Force;
import com.bbaker.discord.swrpg.die.impl.Proficiency;
import com.bbaker.discord.swrpg.die.impl.Setback;

public interface Die {
	
	public DieResult roll();
	
	public String getFace();
	
	public DieType getType();
	
	// Common Die
	public static final Die ABILITY = new Ability();
	public static final Die PROFICIENCY = new Proficiency();
	public static final Die BOOST = new Boost();
	public static final Die DIFFICULTY = new Difficulty();
	public static final Die CHALLENGE = new Challenge();
	public static final Die SETBACK = new Setback();
	public static final Die FORCE = new Force();
	
	// Adjustment Die, used for adjust the TableResult
	public static final Die SUCCESS = 	new Adjustment(DieType.SUCCESS, 	new DieFaceBuilder(DieType.SUCCESS).success().getFace());
	public static final Die ADVANTAGE = new Adjustment(DieType.ADVANTAGE, 	new DieFaceBuilder(DieType.ADVANTAGE).advantage().getFace());
	public static final Die TRIUMPH = 	new Adjustment(DieType.TRIMPH, 		new DieFaceBuilder(DieType.TRIMPH).triumph().getFace());
	public static final Die FAILURE = 	new Adjustment(DieType.FAILURE, 	new DieFaceBuilder(DieType.FAILURE).failure().getFace());
	public static final Die THREAT = 	new Adjustment(DieType.THREAT, 		new DieFaceBuilder(DieType.THREAT).threat().getFace());
	public static final Die DESPAIR = 	new Adjustment(DieType.DESPAIR, 	new DieFaceBuilder(DieType.DESPAIR).despair().getFace());
	public static final Die LIGHT = 	new Adjustment(DieType.LIGHT, 		new DieFaceBuilder(DieType.LIGHT).light().getFace());
	public static final Die DARK = 		new Adjustment(DieType.DARK, 		new DieFaceBuilder(DieType.DARK).dark().getFace());
	
}
