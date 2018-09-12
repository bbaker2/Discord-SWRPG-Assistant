package com.bbaker.discord.swrpg.printer;

import java.util.Collection;
import java.util.Optional;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.KnownCustomEmoji;

import com.bbaker.discord.swrpg.die.Die;

public class EmojiServiceImpl implements EmojiService {

    private Optional<DiscordApi> api = Optional.empty();

    public void setApi(DiscordApi api) {
        this.api = Optional.ofNullable(api);
    }

    @Override
    public String findEmoji(Die die) {
        if(api.isPresent()) {
            DiscordApi discord = api.get();
            Collection<KnownCustomEmoji> emojies = discord.getCustomEmojisByName(die.getFace());
            if(emojies.size() > 0) {
                return emojies.iterator().next().getMentionTag(); // I guess we only care about the first one found
            }
        }
        return die.getType().name();
    }

    @Override
    public String findEmoji(String name) {
        if(api.isPresent()) {
            DiscordApi discord = api.get();
            Collection<KnownCustomEmoji> emojies = discord.getCustomEmojisByName(name);
            if(emojies.size() > 0) {
                return emojies.iterator().next().getMentionTag(); // I guess we only care about the first one found
            } else {
                return name; // I guess return the original name
            }
        } else {
            return name;
        }
    }

}
