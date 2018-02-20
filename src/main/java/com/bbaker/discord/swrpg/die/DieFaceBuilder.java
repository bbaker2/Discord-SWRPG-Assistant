package com.bbaker.discord.swrpg.die;

import com.bbaker.discord.swrpg.table.impl.DieFace;

public class DieFaceBuilder {
	
	public static final int CHECK = 			0;
	public static final int CONSEQUENCE = 	1;
	public static final int TRIUMPH = 		2;
	public static final int DESPAIR = 		3;
	public static final int LIGHT = 			4;
	public static final int DARK = 			5;
	
	private int[] faceValues = new int[]{0,0,0,0,0,0};
	private String emoji;
	private DieType dt;
	
	
	public DieFaceBuilder(DieType dieType) {
		this.dt = dieType;
		emoji = dt.getEmoji();
	}
	
	public DieFaceBuilder success() {
		faceValues[CHECK]++;
		emoji += "s";
		return this;
	}
	
	public DieFaceBuilder advantage() {
		faceValues[CONSEQUENCE]++;
		emoji += "a";
		return this;
	}
	
	public DieFaceBuilder triumph() {
		faceValues[TRIUMPH]++;
		faceValues[CHECK]++;
		emoji += "r";
		return this;
	}
	
	public DieFaceBuilder failure() {
		faceValues[CHECK]--;
		emoji += "f";
		return this;
	}
	
	public DieFaceBuilder threat() {
		faceValues[CONSEQUENCE]--;
		emoji += "t";
		return this;
	}
	
	public DieFaceBuilder despair() {
		faceValues[DESPAIR]++;
		faceValues[CHECK]--;
		emoji += "d";
		return this;
	}
	
	public DieFaceBuilder light() {
		faceValues[LIGHT]++;
		emoji += "l";
		return this;
	}
	
	public DieFaceBuilder dark() {
		faceValues[DARK]++;
		emoji += "n";
		return this;
	}
	
	public DieResult getFace() {
		return new DieFace(
				dt, 
				emoji,
				faceValues[CHECK], faceValues[CONSEQUENCE], faceValues[TRIUMPH], faceValues[DESPAIR], faceValues[LIGHT], faceValues[DARK]);
	}
	
}
