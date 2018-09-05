package com.bbaker.database;

import java.util.List;

import com.bbaker.discord.swrpg.die.Die;

public interface DatabaseService {

	boolean createTables();

	/**
	 * Upsert Die to the database
	 * @param userId
	 * @param channelId
	 * @param dice
	 * @return
	 */
	void storeDiceResults(long userId, long channelId, List<Die> dice);

	List<Die> retrieveDiceResults(long userId, long channelId);

}