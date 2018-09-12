package com.bbaker.discord.swrpg.roller;

import org.javacord.api.DiscordApi;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.roller.impl.EmojiServiceImpl;

public interface EmojiService {
    static EmojiServiceImpl esInstance = new EmojiServiceImpl();

    String findEmoji(String name);

    String findEmoji(Die die);

    public static void setApi(DiscordApi api) {
        esInstance.setApi(api);
    }

    public static EmojiService getInstance() {
        return esInstance;
    }


}
