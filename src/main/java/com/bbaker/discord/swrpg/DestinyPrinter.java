package com.bbaker.discord.swrpg;

import com.bbaker.discord.swrpg.destiny.DestinyTracker;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.roller.EmojiService;

public class DestinyPrinter {

    private EmojiService emojies = EmojiService.getInstance();

    public String printPoints(DestinyTracker destiny) {
        StringBuilder sb = new StringBuilder("Destiny Points: ");
        if(destiny.getLightSide() + destiny.getDarkSide() == 0) {
            return sb.append("`none`").toString();
        } else {
            append(Die.LIGHT, destiny.getLightSide(), sb);
            append(Die.DARK, destiny.getDarkSide(), sb);
        }

        return sb.toString();
    }

    private void append(Die die, int count, StringBuilder sb) {

        for(int i = 0; i < count; i++) {
            sb.append(emojies.findEmoji(die));
        }
    }

    public String printRoll(Die forceDie) {
        return "Destiny roll result: " + emojies.findEmoji(forceDie);
    }

}
