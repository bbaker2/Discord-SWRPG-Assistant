package com.bbaker.database;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.h2.util.StringUtils;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.core.statement.PreparedBatch;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.exceptions.SetupException;


public class JdbiService {
	private Jdbi jdbi;
	private Properties dbProps;
	private String prefix;
	
	public static final String TABLE_ROLL = "ROLL";
	
	public JdbiService(Properties p) throws SetupException {
		dbProps = new Properties();
		dbProps.put("url", 		"jdbc:h2:./discord_swrpg");
		dbProps.put("port", 	"9138");		
		dbProps.put("password", "tk421");
		dbProps.put("user", 	"su");
		dbProps.put("prefix", 	"swrpgab_"); // star wars rpg assistant bot
		dbProps.putAll(p);
		System.out.println(dbProps.getProperty("url"));
		
		prefix = dbProps.getProperty("prefix");
		
		if(StringUtils.isNullOrEmpty(prefix)) {
			throw new SetupException("Missing the prefix for the database tables. Make sure 'prefix' is correctly populated in the properties.");
		}
		
		if(!Pattern.matches("[a-zA-Z]+_", prefix)) {
			throw new SetupException("'%s' is not an acceptable prefix. Must only contain characters and ends with one underscore", prefix);
		}
		
		this.jdbi = Jdbi.create(dbProps.getProperty("url"), dbProps);
		this.jdbi.installPlugin(new H2DatabasePlugin());
	}
	
	private String qualifiedName(String name) {
		return (dbProps.getProperty("prefix") + name).toUpperCase();
	}
	
	private String query(String template, String tableName) {
		return String.format(template, qualifiedName(tableName));
	}
	
	
	public Jdbi getJdbi() {
		return jdbi;
	}
	
	public void createTables() {
		String tableInsert;
		
		if(!hasTable(TABLE_ROLL)) {
			System.out.println("Creating " + TABLE_ROLL);
			tableInsert = query(
					"CREATE TABLE %s ("+
						"id 		INTEGER 		SERIAL PRIMARY KEY, "+ 
						"user_id 	BIGINT 			NOT NULL, "+
						"channel_id	BIGINT 			NOT NULL, "+
						"type 		VARCHAR(100) 	NOT NULL, "+
						"side 		INT 			NOT NULL"+
					");"
					,TABLE_ROLL);
			
			jdbi.useHandle(handler -> {
				handler.execute(tableInsert);
			});
		}
		
	}
	
	public void checkTables(PrintStream out) {
		List<Map<String, Object>> tables = getTables();
		
		out.println(tables.size());
		
		if(tables.size() > 0) {
			for(String keys : tables.get(0).keySet()) {
				out.printf("%-20.20s ", keys);				
			}
			out.println();
		}
		
		for(Map<String, Object> row : tables) {
			for(Object val : row.values()) {
				out.printf("%-20.20s", val);
			}
			out.println();
		}
	}
	
	public List<Map<String, Object>> getTables() {
		String query = String.format(""+
						"select * "+
						"from INFORMATION_SCHEMA.TABLES "+
						"where TABLE_TYPE='TABLE' "+
							"and TABLE_NAME LIKE '%s%%'", 
						prefix.toUpperCase());

		return jdbi.withHandle(handle -> 
			handle.createQuery(query)
				.mapToMap()
				.list()
		);
	}
	
	public boolean hasTable(String tableName) {
		String query = 	"select count(ID) "+
						"from INFORMATION_SCHEMA.TABLES "+
						"where TABLE_TYPE='TABLE' "+
							"and TABLE_NAME = :tableName";
		
		return jdbi.withHandle(handle ->
			handle.createQuery(query)
				.bind("tableName", qualifiedName(tableName))
				.mapTo(int.class)
				.findOnly().intValue() > 0
		);
	}
	
	public Collection<Die> retrieveDiceResult(long userId, long channelId){
		List<Die> dice = new ArrayList<>();
		System.out.println(userId + " " + channelId);
		
		String query = query("select TYPE, SIDE from %s WHERE USER_ID = :userId AND CHANNEL_ID = :channelId", TABLE_ROLL);
		
		List<Map<String, Object>> results = jdbi.withHandle(handler -> 
			handler.createQuery(query)
				.bind("userId", userId)
				.bind("channelId", channelId)
				.mapToMap()
		        .list()
		);
		
		for(Map<String, Object> row : results) {
			DieType dt = DieType.valueOf((String)row.get("type"));
			int side = (int)row.get("side");
			dice.add(Die.newDie(dt, side));
		}
		return dice;
	}
	
	/**
	 * Upsert Die to the database
	 * @param userId
	 * @param channelId
	 * @param dice
	 * @return
	 */
	public void storeDiceResults(long userId, long channelId, List<Die> dice) {
		String query;
		try (Handle handle = jdbi.open()) {
			handle.begin();
			
			// First clear the old values
			query = query("delete from %s where USER_ID = :userId and CHANNEL_ID = :channelId", TABLE_ROLL);
			handle.createUpdate(query)
				.bind("userId", userId)
				.bind("channelId", channelId)
				.execute();
			
			// The insert the new ones
			query = query("insert into %s(USER_ID, CHANNEL_ID, TYPE, SIDE) VALUES(:userId, :channelId, :type, :side)", TABLE_ROLL);
			PreparedBatch batch = handle.prepareBatch(query);
			for(Die die : dice) {
				batch.bind("userId", userId);
				batch.bind("channelId", channelId);
				batch.bind("type", die.getType().name());
				batch.bind("side", die.getSide());
				batch.add();
			}
			
			batch.execute();
			
			handle.commit();
		}
	}
	
	public List<Die> retrieveDiceResults(long userId, long channelId){
		String query =  query("select TYPE, SIDE from %s where USER_ID = :userId and CHANNEL_ID = :channelId", TABLE_ROLL);
						
		return jdbi.withHandle(
				handle -> handle.createQuery(query)
					.bind("userId", userId)
					.bind("channelId", channelId)
					.map((rs, col, ctx) 
							-> Die.newDie(
									DieType.valueOf(rs.getString("TYPE")), 
									rs.getInt("SIDE")
							))
					.list()
		);
	}
	
}
	