package com.bbaker.discord.swrpg.printer;

import org.javacord.api.DiscordApi;

import com.bbaker.discord.swrpg.die.Die;

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
