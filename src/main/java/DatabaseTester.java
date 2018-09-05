

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.bbaker.database.JdbiService;
import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.exceptions.SetupException;

public class DatabaseTester {

	public static void main(String...args) throws SetupException {
		try {
			Class.forName("org.h2.Driver");
			
			JdbiService dbService = new JdbiService(new Properties());
			//dbService.checkTables(System.out);
			
			dbService.createTables();
			
			System.out.println(dbService.hasTable(JdbiService.TABLE_ROLL));
			
			dbService.checkTables(System.out);
			
			List<Die> dice = Arrays.asList(
					Die.newDie(DieType.ABILITY, 0),
					Die.newDie(DieType.ADVANTAGE, 1),
					Die.newDie(DieType.BOOST, 2)
				);
				
			dbService.storeDiceResults(11111, 22222, dice);
			List<Die> result = dbService.retrieveDiceResults(11111, 22222);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
