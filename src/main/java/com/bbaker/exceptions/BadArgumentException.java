package com.bbaker.exceptions;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.roller.EmojiService;

public class BadArgumentException extends Exception {


    public BadArgumentException(String messageTemplate, Object... args) {
        super(String.format(messageTemplate, processArgs(args)));
    }

    private static Object[] processArgs(Object[] args) {
        EmojiService emojies = EmojiService.getInstance();
        for(int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if(arg instanceof DieType) {
                Die d = RollableDie.newDie((DieType)arg);
                args[i] = emojies.findEmoji(d);
            }
        }
        return args;
    }

}
