package com.bbaker.database;

import java.util.List;

import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.table.impl.DiceTower;

public interface DatabaseService {

    boolean createTables();

    /**
     * Upsert Die to the database
     * @param userId
     * @param channelId
     * @param dice
     * @return
     */
    void storeDiceResults(long userId, long channelId, List<RollableDie> dice);

    DiceTower retrieveDiceResults(long userId, long channelId);

}