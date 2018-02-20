package com.bbaker.discord.swrpg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;

import com.bbaker.database.JdbiService;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.roller.RollerHandler;
import com.bbaker.discord.swrpg.roller.RollerPrinter;
import com.bbaker.discord.swrpg.table.Table;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class CheckCommands  implements CommandExecutor {
	
	private JdbiService dbService;

	public CheckCommands(JdbiService db) {
		this.dbService = db;
	}
	
	@Command(aliases = {"!r", "!roll"}, description = "Rolls dice", usage = "!roll")
	public String handleRoll(DiscordApi api, Message message) {
		try {
			
			List<String> tokens = getList(message);
			Table table = new Table();
			new RollerHandler().processArguments(tokens.iterator(), table);
			RollerPrinter printer = new RollerPrinter(api);
			TableResult result = table.roll();
			dbService.storeDiceResults(message.getAuthor().getId(), message.getChannel().getId(), result.getDice());
			Collection<Die> fromDB = dbService.retrieveDiceResult(message.getAuthor().getId(), message.getChannel().getId());
			for(Die d : fromDB) {
				System.out.println(d.getFace());
			}
			return printer.print(result);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return "Hello world";
	}
	
	private List<String> getList(Message message){
		String[] args = message.getContent().split("\\s+");
		List<String> tokens = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
		return new ArrayList<String>(tokens);
	}
}
