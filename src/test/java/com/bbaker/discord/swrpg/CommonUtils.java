package com.bbaker.discord.swrpg;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.javacord.api.entity.message.Message;
import org.mockito.Mockito;

import com.bbaker.discord.swrpg.database.DatabaseService;

public class CommonUtils {

    public static final long USER_ID = 11111111;
    public static final long CHANNEL_ID = 22222222;

    protected DatabaseService dbService;

    public Message genMsg(String content) {
        Message msg = mock(Message.class, Mockito.RETURNS_DEEP_STUBS);

        when(msg.getAuthor().getId()).thenReturn(USER_ID);
        when(msg.getChannel().getId()).thenReturn(CHANNEL_ID);
        when(msg.getContent()).thenReturn(content);

        return msg;
    }

}
