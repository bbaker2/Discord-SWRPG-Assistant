package com.bbaker.discord.swrpg.database;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

import org.h2.util.StringUtils;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.h2.H2DatabasePlugin;
import org.jdbi.v3.core.statement.PreparedBatch;

import com.bbaker.discord.swrpg.destiny.DestinyTracker;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.exceptions.SetupException;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.InitTrackerMeta;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;
import com.bbaker.discord.swrpg.roller.DiceTower;


public class JdbiService implements DatabaseService {
    private Jdbi jdbi;
    private Properties dbProps;
    private String prefix;

    public static final String TABLE_ROLL = "ROLL";
    public static final String TABLE_DESTINY = "DESTINY";
    public static final String TABLE_INIT = "INIT";
    public static final String TABLE_INIT_TRACKER = "INIT_TRACKER";

    public JdbiService(Properties p) throws SetupException {
        dbProps = new Properties();
        dbProps.put("url", 		"jdbc:h2:./discord_swrpg");
        dbProps.put("port", 	"9138");
        dbProps.put("password", "tk421");
        dbProps.put("user", 	"su");
        dbProps.put("prefix", 	"swrpgab_"); // star wars rpg assistant bot
        dbProps.putAll(p);

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

    private String query(String template, String tableName, String... extraArgs) {
        if(extraArgs.length > 0) {
            Object[] args = new Object[1+extraArgs.length];
            args[0] = qualifiedName(tableName);
            System.arraycopy(extraArgs, 0, args, 1, extraArgs.length);
            return String.format(template, args);
        } else {
            return String.format(template, qualifiedName(tableName));
        }
    }


    public Jdbi getJdbi() {
        return jdbi;
    }

    /* (non-Javadoc)
     * @see com.bbaker.database.DatabaseService#createTables()
     */
    @Override
    public boolean createTables() {
        boolean createdTables = false;

        // Table for holding onto dice rolls
        if(!hasTable(TABLE_ROLL)) {
            System.out.println("Creating " + TABLE_ROLL);
            String tableInsert = query(
                    "CREATE TABLE %s ("+
                        "id 		BIGINT 			SERIAL PRIMARY KEY, "+
                        "user_id 	BIGINT 			NOT NULL, "+
                        "channel_id	BIGINT 			NOT NULL, "+
                        "type 		VARCHAR(100) 	NOT NULL, "+
                        "side 		INT 			NOT NULL"+
                    ");"
                    ,TABLE_ROLL);

            jdbi.useHandle(handler -> {
                handler.execute(tableInsert);
            });
            createdTables = true;
        }

        // Table for holding onto destiny points
        if(!hasTable(TABLE_DESTINY)) {
            System.out.println("Creating " + TABLE_DESTINY);
            String tableInsert = query(
                    "CREATE TABLE %s ("+
                        "id 		BIGINT 			SERIAL PRIMARY KEY, "+
                        "channel_id	BIGINT 			NOT NULL UNIQUE, "+
                        "dark 		INT 			NOT NULL,"+
                        "light 		INT 			NOT NULL"+
                    ");"
                    ,TABLE_DESTINY);

            jdbi.useHandle(handler -> {
                handler.execute(tableInsert);
            });
            createdTables = true;
        }

        if(!hasTable(TABLE_INIT_TRACKER)) {
            System.out.println("Creating " + TABLE_INIT_TRACKER);
            String tableInsert = query(
                    "CREATE TABLE %s ("+
                        "id 			BIGINT 			SERIAL PRIMARY KEY, "+
                        "channel_id		BIGINT 			NOT NULL, "+
                        "round 			INT				NOT NULL, "+
                        "turn 			INT 			NOT NULL, "+
                        "uses_order 	BIT 			NOT NULL "+
                    ");"
                    ,TABLE_INIT_TRACKER);

            jdbi.useHandle(handler -> {
                handler.execute(tableInsert);
            });

        }

        if(!hasTable(TABLE_INIT)) {
            System.out.println("Creating " + TABLE_INIT);
            String tableInsert = query(
                    "CREATE TABLE %s ("+
                            "id 			BIGINT 			SERIAL PRIMARY KEY, "+
                            "init_key		BIGINT 			NOT NULL REFERENCES %s(id) ON DELETE CASCADE, "+
                            "type 			VARCHAR(100) 	NOT NULL, "+
                            "label 			VARCHAR(255) 	NOT NULL, "+
                            "success 		INT, "+
                            "advantage 		INT, "+
//                            "pending 		BIT 			NOT NULL, "+
                            "order_index	INT "+
                            ");"
                            ,TABLE_INIT, qualifiedName(TABLE_INIT_TRACKER));

            System.out.println(tableInsert);

            jdbi.useHandle(handler -> {
                handler.execute(tableInsert);
            });
        }
        return createdTables;
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

    /* (non-Javadoc)
     * @see com.bbaker.database.DatabaseService#storeDiceResults(long, long, java.util.List)
     */
    @Override
    public void storeDiceResults(long userId, long channelId, List<RollableDie> dice) {
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
            if(dice.size() > 0) {
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
            }

            handle.commit();
        }
    }

    /* (non-Javadoc)
     * @see com.bbaker.database.DatabaseService#retrieveDiceResults(long, long)
     */
    @Override
    public DiceTower retrieveDiceResults(long userId, long channelId){
        String query =  query("select TYPE, SIDE from %s where USER_ID = :userId and CHANNEL_ID = :channelId", TABLE_ROLL);

        List<RollableDie> results = jdbi.withHandle(
                handle -> handle.createQuery(query)
                    .bind("userId", userId)
                    .bind("channelId", channelId)
                    .map((rs, col, ctx)
                            -> RollableDie.newDie(
                                    DieType.valueOf(rs.getString("TYPE")),
                                    rs.getInt("SIDE")
                            ))
                    .list()
        );

        DiceTower dt = new DiceTower();
        for(RollableDie rd : results) {
            dt.addDie(rd);
        }
        return dt;
    }

    @Override
    public void storeDestiny(long channelId, DestinyTracker tracker) {
        String query;

        if(tracker.getId() == IS_NEW) {
            query = query("insert into %s (LIGHT, DARK, CHANNEL_ID) VALUES(:light, :dark, :channelId)", TABLE_DESTINY);
        } else {
            query = query("update %s set LIGHT = :light, DARK = :dark where CHANNEL_ID = :channelId", TABLE_DESTINY);
        }

        jdbi.useHandle(handler -> {

            handler.createUpdate(query)
            .bind("light", tracker.getLightSide())
            .bind("dark", tracker.getDarkSide())
            .bind("channelId", channelId)
            .execute();
        });

    }

    @Override
    public DestinyTracker retrieveDestiny(long channelId) {
        String query =  query("select id, light, dark from %s where CHANNEL_ID = :channelId", TABLE_DESTINY);

        List<DestinyTracker> trackers =  jdbi.withHandle(
            handle -> handle.createQuery(query)
                .bind("channelId", channelId)
                .map((rs, col, ctx) -> new DestinyTracker(
                        rs.getInt("light"),
                        rs.getInt("dark"),
                        rs.getLong("id")
                    ))
                .list()
        );

        if(trackers.size() == 1) {
            return trackers.get(0);
        } else {
            return new DestinyTracker(0, 0, IS_NEW);
        }
    }

    @Override
    public InitiativeTracker retrieveInitiative(long channelId) {
        String initTrackerQry = query("select ID, ROUND, TURN, USES_ORDER "+
                      "from %s " +
                      "where CHANNEL_ID = :channelId", TABLE_INIT_TRACKER);

        Optional<InitTrackerMeta> initMeta = jdbi.withHandle(handle ->
            handle.createQuery(initTrackerQry)
                .bind("channelId", channelId)
                .map((rs, col, ctx) ->
                new InitTrackerMeta(
                        rs.getLong("ID"),
                        rs.getInt("ROUND"),
                        rs.getInt("TURN"), rs.getBoolean("USES_ORDER") || false
                    ))
                .findFirst()
        );

        if(!initMeta.isPresent()) {
            return new InitiativeTracker(new ArrayList<InitCharacter>(), 0, 0, true);
        }

        String order = initMeta.get().usesOrder ? "ORDER_INDEX asc" : "SUCCESS desc, ADVANTAGE desc";
        String characterQry = query("select LABEL, SUCCESS, ADVANTAGE, ORDER_INDEX, TYPE "
                    + "from %s "
                    + "where INIT_KEY = :initKey "
                    + "order by %s", TABLE_INIT, order);

        List<InitCharacter> results = jdbi.withHandle(
                handle -> handle.createQuery(characterQry)
                    .bind("initKey", initMeta.get().id)
                    .map((rs, col, ctx)
                            -> new InitCharacter(
                                    rs.getString("LABEL"),
                                    rs.getInt("SUCCESS"),
                                    rs.getInt("ADVANTAGE"),
                                    rs.getInt("ORDER_INDEX"),
                                    CharacterType.valueOf(rs.getString("TYPE"))
                            ))
                    .list()
        );

        return new InitiativeTracker(results, initMeta.get());
    }

    @Override
    public void storeInitiative(long channelId, InitiativeTracker initTracker) {
        List<InitCharacter> init = initTracker.getInit();
        String query;
        try (Handle handle = jdbi.open()) {
            handle.begin();

            // First clear the old values
            query = query("delete from %s where CHANNEL_ID = :channelId", TABLE_INIT_TRACKER); // should cascade delete the init table as well
            handle.createUpdate(query)
                .bind("channelId", channelId)
                .execute();

            // insert the init meta first
            query = query("insert into %s(ROUND, TURN, USES_ORDER, CHANNEL_ID) VALUES(:round, :turn, :usesOrder, :channelId)", TABLE_INIT_TRACKER);
            long initMetaId = handle.createUpdate(query)
                .bind("round", 		initTracker.getRound())
                .bind("turn", 		initTracker.getTurn())
                .bind("usesOrder", 	!initTracker.canRoll())
                .bind("channelId", 	channelId)
                .executeAndReturnGeneratedKeys("ID")
                .mapTo(Long.class)
                .findOnly();

            // The insert the new ones
            if(init.size() > 0) {
                query = query("insert into %s(LABEL, SUCCESS, ADVANTAGE, ORDER_INDEX, TYPE, INIT_KEY)"+
                              "VALUES(:label, :success, :advantage, :order, :type, :initMetaId)", TABLE_INIT);
                PreparedBatch batch = handle.prepareBatch(query);
                for(InitCharacter ic : init) {
                    batch.bind("label", 	ic.getLabel());
                    batch.bind("success", 	ic.getSuccess());
                    batch.bind("advantage",	ic.getAdvantage());
                    batch.bind("order", 	ic.getOrder());
                    batch.bind("type", 		ic.getType().name());
                    batch.bind("initMetaId",initMetaId);
                    batch.add();
                }
                batch.execute();
            }

            handle.commit();
        }

    }

}
