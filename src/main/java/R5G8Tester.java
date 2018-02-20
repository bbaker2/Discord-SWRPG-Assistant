import java.util.Properties;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import com.bbaker.database.DatabaseConnector;
import com.bbaker.database.JdbiService;
import com.bbaker.discord.swrpg.CheckCommands;
import com.bbaker.exceptions.SetupException;

import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;

public class R5G8Tester {

	public static void main(String[] args) throws SetupException {
		// Prepare Database
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		DatabaseConnector dbc = new DatabaseConnector(new Properties());
		JdbiService dbService = new JdbiService(dbc.getProperties());
		dbService.createTables();

		// Start up bot
		String token = "MzkzOTE3NzI5MDQ5OTM1ODky.DWKtSQ.oS7U6tSu1Pi3Im46DF90Gz3tpcs";
		DiscordApiBuilder dab = new DiscordApiBuilder().setAccountType(AccountType.BOT).setToken(token);
		DiscordApi api = dab.login().join();
		CommandHandler ch = new JavacordHandler(api);
		ch.registerCommand(new CheckCommands(dbService));
		

	}

}
