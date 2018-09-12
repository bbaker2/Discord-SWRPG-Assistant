import java.util.Properties;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import com.bbaker.discord.swrpg.command.impl.DestinyCommand;
import com.bbaker.discord.swrpg.command.impl.RollerCommands;
import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.database.JdbiService;
import com.bbaker.discord.swrpg.exceptions.SetupException;
import com.bbaker.discord.swrpg.printer.EmojiService;

import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;

public class R5G8Tester {

    public static void main(String[] args) throws SetupException {
        FallbackLoggerConfiguration.setDebug(true);
        FallbackLoggerConfiguration.setTrace(true);

        // Prepare Database
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        DatabaseService dbService = new JdbiService(new Properties());
        dbService.createTables();

        // Start up bot
        String token = args[0];
        DiscordApiBuilder dab = new DiscordApiBuilder().setAccountType(AccountType.BOT).setToken(token);
        DiscordApi api = dab.login().join();
        EmojiService.setApi(api);
        CommandHandler ch = new JavacordHandler(api);
        ch.setDefaultPrefix("!");
        ch.registerCommand(new RollerCommands(dbService, api));
        ch.registerCommand(new DestinyCommand(dbService));


    }

}
