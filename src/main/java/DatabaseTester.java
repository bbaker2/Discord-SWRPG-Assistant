

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.exceptions.SetupException;
import com.bbaker.discord.swrpg.database.JdbiService;
import com.bbaker.discord.swrpg.die.DieType;

public class DatabaseTester {

    public static void main(String...args) throws SetupException {
        try {
            Class.forName("org.h2.Driver");

            JdbiService dbService = new JdbiService(new Properties());
            //dbService.checkTables(System.out);

            dbService.createTables();

            System.out.println(dbService.hasTable(JdbiService.TABLE_ROLL));

            dbService.checkTables(System.out);

            List<RollableDie> dice = Arrays.asList(
                    RollableDie.newDie(DieType.ABILITY, 0),
                    RollableDie.newDie(DieType.ADVANTAGE, 1),
                    RollableDie.newDie(DieType.BOOST, 2)
                );

            dbService.storeDiceResults(11111, 22222, dice);
            List<RollableDie> result = dbService.retrieveDiceResults(11111, 22222).getDice();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
