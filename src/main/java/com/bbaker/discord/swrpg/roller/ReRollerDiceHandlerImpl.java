package com.bbaker.discord.swrpg.roller;

import java.util.List;
import java.util.Optional;

import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.table.TableBuilder;
import com.bbaker.exceptions.BadArgumentException;

public class ReRollerDiceHandlerImpl extends DiceHandler {


    @Override
    public boolean evaluate(TableBuilder table, DieType dt, Optional<Integer> leftNumeric, Optional<Integer> rightNumeric) throws BadArgumentException {
        if(leftNumeric.isPresent() && rightNumeric.isPresent()) {
            throw new BadArgumentException("Die type %s has two indexes. Please pick %d or %d",
                    dt.name(), leftNumeric.get(), rightNumeric.get());
        }

        int index = leftNumeric.orElse(rightNumeric.get());
        // we ask users to give an index where arrays start at 1.
        // So we subtract one so that our 0-based indexes works
        index--;

        if(index == -1) {
            throw new BadArgumentException("There is no 0th position. Did you mean index 1?");
        }

        TableResult pastResult = table.peekResult();
        List<RollableDie> dice = pastResult.getDice();

        for(int i = 0; i < dice.size(); i++) {
            if(dice.get(i).getType() == dt) {
                if(dice.get(i+index).getType() == dt) {
                    dice.get(i+index).roll();
                } else {
                    throw new BadArgumentException("There is no %dth position for %s", index+1, dt.name());
                }
            }
        }
        return true;
    }

}
