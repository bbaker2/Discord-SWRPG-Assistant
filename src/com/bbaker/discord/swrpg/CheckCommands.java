package com.bbaker.discord.swrpg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bbaker.discord.swrpg.roller.RollerHandler;
import com.bbaker.discord.swrpg.roller.RollerPrinter;
import com.bbaker.discord.swrpg.table.Table;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class CheckCommands  implements CommandExecutor {

	@Command(aliases = {"!r", "!roll"}, description = "Rolls dice", usage = "!roll")
	public String handleRoll(DiscordApi api, Message message) {
		try {
			List<String> tokens = getList(message);
			Table table = new Table();
			new RollerHandler().processArguments(tokens.iterator(), table);
			RollerPrinter printer = new RollerPrinter(api);
			return printer.print(table.roll());
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
