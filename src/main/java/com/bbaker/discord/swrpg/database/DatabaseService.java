package com.bbaker.discord.swrpg.database;

import java.util.List;

import com.bbaker.discord.swrpg.destiny.DestinyTracker;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;
import com.bbaker.discord.swrpg.roller.DiceTower;
public interface DatabaseService {

    public static final long IS_NEW = -1;

    boolean createTables();

    /**
     * Upsert Die to the database. Will truncate previous die results that match userId and channelId
     * @param userId the user who made the roll
     * @param channelId the channel the roll was made on
     * @param dice the die to store into the database
     */
    void storeDiceResults(long userId, long channelId, List<RollableDie> dice);

    /**
     * Will retrieve any die in the database and populated a {@link DiceTower} for you.
     * @param userId the user who made the roll
     * @param channelId the channel the roll was made from
     * @return a prebuilt {@link DiceTower} who's dice are already pre-populated
     */
    DiceTower retrieveDiceResults(long userId, long channelId);

    /**
     * Will retrieve the destiny points for a given channel
     * @param channelId the channel the destiny roll was made from
     * @return a prebuilt {@link DestinyTracker}
     */
    DestinyTracker retrieveDestiny(long channelId);

    /**
     * For storing light/dark side points for a channel. This will truncate any old values
     * @param chanelId the channel the destiny points were updated from
     * @param tracker must return a value for {@link DestinyTracker#getId()}
     * @return
     */
    void storeDestiny(long chanelId, DestinyTracker tracker);

    /**
     * For retrieving ordered {@link InitCharacter}
     * @param channelId the channel the initiative is managed from
     * @return a populated {@link InitiativeTracker}
     */
    InitiativeTracker getInitiative(long channelId);

    void storeInitiative(long channelId, List<InitCharacter> init);

}