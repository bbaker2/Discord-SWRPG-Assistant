package com.bbaker.discord.swrpg.roller;

import java.util.Optional;

import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.table.impl.DiceTower;
import com.bbaker.exceptions.BadArgumentException;

public class ReRollerDiceHandlerImpl extends DiceHandler {

    public static final String TWO_INDEX_MSG = "Die type %s has two indexes. Please pick %d or %d";
    public static final String ZERO_INDEX_MSG = "There is no 0th position. Did you mean index 1?";

    @Override
    public boolean evaluate(DiceTower table, DieType dt, Optional<Integer> leftNumeric, Optional<Integer> rightNumeric) throws BadArgumentException {
        if(leftNumeric.isPresent() && rightNumeric.isPresent()) {
            throw new BadArgumentException(TWO_INDEX_MSG,
                    dt.name(), leftNumeric.get(), rightNumeric.get());
        }

        // If no numbers are provided, assume the 1st position
        int index = leftNumeric.orElse(rightNumeric.orElse(1));

        // we ask users to give an index where arrays start at 1.
        // So we subtract one so that our 0-based indexes works
        index--;

        if(index == -1) {
            throw new BadArgumentException(ZERO_INDEX_MSG);
        }

        table.roll(dt, index);

        return true;
    }

}
