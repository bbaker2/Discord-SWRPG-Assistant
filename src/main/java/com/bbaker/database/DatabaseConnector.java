package com.bbaker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.h2.tools.Server;
import org.jdbi.v3.core.Jdbi;



public class DatabaseConnector {
	
	private Properties dbProps;
	
	public DatabaseConnector(Properties p) {
		dbProps = new Properties();
		dbProps.put("url", 		"jdbc:h2:./discord_swrpg");
		dbProps.put("port", 		"9138");		
		dbProps.put("password", 	"tk421");
		dbProps.put("user", 		"su");
		dbProps.put("prefix", 	"swrpgab__"); // star wars rpg assistant bot
		dbProps.putAll(p);
	}
	
	public Properties getProperties() {
		return dbProps;
	}
	
	public Server createServer() throws SQLException {
		return Server.createTcpServer(
			"-tcpPort", 	dbProps.getProperty("port"), 
			"-tcpPassword", dbProps.getProperty("password")
		);
	}
	

	public Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(dbProps.getProperty("url"), dbProps);
		return conn;
	}
	
	public Jdbi getJdbi() {
		return Jdbi.create(dbProps.getProperty("url"));
	}
	
	public ResultSet query(Connection conn, String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			if(stmt.execute(sql)) {
				rs = stmt.getResultSet();
				ResultSetMetaData rsmd = rs.getMetaData();
				int max = rsmd.getColumnCount();
				System.out.print("     ");
				for(int i = 1; i < max; i++) {
					System.out.printf("%-20.20s ", rsmd.getColumnLabel(i));
				}
				System.out.println();
				
				while(rs.next()) {
					System.out.printf("%03d: ", rs.getRow());
					for(int i = 1; i < max; i++) {
						System.out.printf("%-20.20s ", rs.getObject(i));
					}
					System.out.print("\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(rs, stmt);
		}
		return rs;
	}
	
	private void closeAll(ResultSet rs, Statement stmt) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
