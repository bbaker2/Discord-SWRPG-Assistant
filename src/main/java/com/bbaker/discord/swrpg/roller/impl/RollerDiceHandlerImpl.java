package com.bbaker.discord.swrpg.roller;

import java.util.Optional;

import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.table.impl.DiceTower;

public class RollerDiceHandlerImpl extends DiceHandler {


    @Override
    public boolean evaluate(DiceTower table, DieType dt, Optional<Integer> leftNumeric, Optional<Integer> rightNumeric) {
        int total = 0;
        boolean useDefault = true;

        if(leftNumeric.isPresent()) {
            total += leftNumeric.get();
            useDefault = false;
        }

        if(rightNumeric.isPresent()) {
            total += rightNumeric.get();
            useDefault = false;
        }

        if(useDefault) {
            total = 1;
        }

        table.addDie(dt, total);

        return true;
    }

}
