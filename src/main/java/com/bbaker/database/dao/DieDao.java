package com.bbaker.database.dao;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface DieDao {
	
	@SqlUpdate("CREATE TABLE swprg_rolls (id INTEGER PRIMARY KEY, user INT, channel INT, type VARCHAR, side INT	")
	public void createTable();
}
