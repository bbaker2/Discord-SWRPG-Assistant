package com.bbaker.discord.swrpg.die;

public enum DieType {
    ABILITY("green"), PROFICIENCY("yellow"), BOOST("blue"),   		// positive die
    DIFFICULTY("purple"), CHALLENGE("red"), SETBACK("black"), 		// negative die
    FORCE("white"), 													// force die
    SUCCESS("success"), ADVANTAGE("advantage"), TRIUMPH("triumph"), 	// positive results
    FAILURE("failure"), THREAT("threat"), DESPAIR("despair"),			// negative results
    LIGHT("lightside"), DARK("darkside");							// force results

    private String emojieName;
    DieType(String emojiName) {
        this.emojieName = emojiName;
    }

    public String getEmoji() {
        return emojieName;
    }
}
