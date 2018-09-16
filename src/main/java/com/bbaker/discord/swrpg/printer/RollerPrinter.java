package com.bbaker.discord.swrpg.printer;

import java.util.List;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.die.TableResult;

public class RollerPrinter {
    private EmojiService emojies;

    public RollerPrinter() {
        this.emojies = EmojiService.getInstance();
    }

    public String printDice(List<RollableDie> dice) {
        StringBuilder sb = new StringBuilder();
        for(Die dr : dice) {
            sb.append(emojies.findEmoji(dr));
        }
        return sb.toString();
    }

    public String printResult(TableResult tr) {
        StringBuilder sb = new StringBuilder();

        boolean netZero = true;

        int check = tr.getCheck();
        if(check > 0) {
            sb.append(buildSingleResult(Die.SUCCESS, check));
            netZero = false;
        } else if(check < 0){
            sb.append(buildSingleResult(Die.FAILURE, check));
            netZero = false;
        }

        int consequence = tr.getConsequence();
        if(consequence > 0) {
            sb.append(buildSingleResult(Die.ADVANTAGE, consequence));
            netZero = false;
        } else if(consequence < 0){
            sb.append(buildSingleResult(Die.THREAT, consequence));
            netZero = false;
        }

        if(tr.getTriumph() > 0) {
            sb.append(buildSingleResult(Die.TRIUMPH, tr.getTriumph()));
            netZero = false;
        }

        if(tr.getDespair() > 0) {
            sb.append(buildSingleResult(Die.DESPAIR, tr.getDespair()));
            netZero = false;
        }

        if(tr.getLightSide() > 0) {
            sb.append(buildSingleResult(Die.LIGHT, tr.getLightSide()));
            netZero = false;
        }

        if(tr.getDarkSide() > 0) {
            sb.append(buildSingleResult(Die.DARK, tr.getDarkSide()));
            netZero = false;
        }

        if(netZero) {
            sb.append("`All dice have cancelled out`");
        }

        return sb.toString();
    }

    public String print(TableResult tr) {
        StringBuilder sb = new StringBuilder();
        List<RollableDie> dice = tr.getDice();

        sb.append(printDice(dice));

        if(dice.size() > 0) {
            sb.append("\n");
        }

        sb.append(printResult(tr));

        return sb.toString();
    }

    private String buildSingleResult(Die die, int count) {
        return emojies.findEmoji(die) + Math.abs(count) + " ";
    }
}
