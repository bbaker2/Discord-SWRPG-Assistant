package com.bbaker.discord.swrpg.roller.impl;

import java.util.OptionalInt;

import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.roller.DiceHandler;
import com.bbaker.discord.swrpg.table.impl.DiceTower;

public class RollerDiceHandlerImpl extends DiceHandler {


    @Override
    public boolean evaluate(DiceTower table, DieType dt, OptionalInt leftNumeric, OptionalInt rightNumeric) {
        int total = 0;
        boolean useDefault = true;

        if(leftNumeric.isPresent()) {
            total += leftNumeric.getAsInt();
            useDefault = false;
        }

        if(rightNumeric.isPresent()) {
            total += rightNumeric.getAsInt();
            useDefault = false;
        }

        if(useDefault) {
            total = 1;
        }

        table.addDie(dt, total);

        return true;
    }

}
