
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bbaker.discord.swrpg.roller.RollerHandler;
import com.bbaker.discord.swrpg.table.Table;

public class RollTester {

	public static void main(String[] args) {
		RollerHandler roller = new RollerHandler();
		Table table = new Table();
		
		List<String> tokens = Arrays.asList("gybprkw", "yellow", "green", "blue", "purple", "red", "black", "blue", "white");
		tokens = new ArrayList<String>(tokens);
		roller.processArguments(tokens.iterator(), table);
		System.out.println(table.roll().toString() + "\n");
		System.out.println(table.roll().toString() + "\n");
		System.out.println(table.roll().toString() + "\n");
		System.out.println(table.roll().toString() + "\n");
		System.out.println(table.roll().toString() + "\n");
		System.out.println(table.roll().toString() + "\n");
		System.out.println(table.roll().toString() + "\n");

	}

}
