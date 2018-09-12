package com.bbaker.discord.swrpg.roller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieType;
import com.bbaker.discord.swrpg.die.Result;
import com.bbaker.discord.swrpg.die.RollableDie;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public class DiceTower implements TableResult {
    public static final String OUT_OF_BOUNDS_MSG = "Please only reroll between 1 and %d for %s";
    public static final String NONE_EXIST_MSG = "No %s exist to reroll";
    public static final String NOT_SUPPORTED_MSG = "Rerolls not supported for %s";

    private static final Logger logger = LogManager.getLogger(DiceTower.class);

    int totalCheck = 0, totalConsequence = 0,
        totalTriumph = 0, totalDespair = 0,
        totalLight = 0, totalDark = 0;

    // Dice
    private List<RollableDie> ability = new ArrayList<RollableDie>(), proficiency = new ArrayList<RollableDie>() , boost = new ArrayList<RollableDie>();
    private List<RollableDie> difficulty = new ArrayList<RollableDie>(), challenge = new ArrayList<RollableDie>(), setback = new ArrayList<RollableDie>();
    private List<RollableDie> force = new ArrayList<RollableDie>();

    // Adjustments
    private List<RollableDie> success = new ArrayList<RollableDie>(), advantage = new ArrayList<RollableDie>(), triumph = new ArrayList<RollableDie>();
    private List<RollableDie> failure = new ArrayList<RollableDie>(), threat = new ArrayList<RollableDie>(), despaire = new ArrayList<RollableDie>();
    private List<RollableDie> light = new ArrayList<RollableDie>(), dark = new ArrayList<RollableDie>();


    private void adjustTable(Result dr) {
        totalCheck += dr.getCheck();
        totalConsequence += dr.getConsequence();
        totalTriumph += dr.getTriumph();
        totalDespair += dr.getDespair();
        totalLight += dr.getLightSide();
        totalDark += dr.getDarkSide();
    }

    public void roll() {
        // reset all results
        totalCheck = 0;
        totalConsequence = 0;
        totalTriumph = 0;
        totalDespair = 0;
        totalLight = 0;
        totalDark = 0;

        // loop through everything
        List<RollableDie> allDice = getRollableDice();
        for(RollableDie d : allDice) {
            d.roll();
            adjustTable(d.getResults());
        }
    }

    public void roll(DieType dt, int index) throws BadArgumentException {
        List<RollableDie> targetDice;
        switch(dt) {
            // Positive
            case PROFICIENCY:
                targetDice = proficiency;
                break;
            case ABILITY:
                targetDice = ability;
                break;
            case BOOST:
                targetDice = boost;
                break;

            // Negative
            case CHALLENGE:
                targetDice = challenge;
                break;
            case DIFFICULTY:
                targetDice = difficulty;
                break;
            case SETBACK:
                targetDice = setback;
                break;

            // Force
            case FORCE:
                targetDice = force;
                break;

            default:
                throw new BadArgumentException(NOT_SUPPORTED_MSG, dt);
        }

        if(targetDice.size() == 0) {
            throw new BadArgumentException(NONE_EXIST_MSG, dt);
        }

        if(targetDice.size() <= index) {
            throw new BadArgumentException(OUT_OF_BOUNDS_MSG, targetDice.size(), dt);
        }

        RollableDie targetDie = targetDice.get(index);
        Result reverseResult = new NegatedResult(targetDie.getResults());
        adjustTable(reverseResult);
        adjustTable(targetDie.roll());
    }

    public void addDie(DieType dt, int count) {
        for(int i = 0; i < count; i++) {
            addDie(RollableDie.newDie(dt));
        }
    }

    public void addDie(RollableDie die) {
        adjustTable(die.getResults());
        logger.trace(die.getType() + " -> " + die.getResults());

        switch(die.getType()) {
            // Positive
            case PROFICIENCY:
                proficiency.add(die);
                break;
            case ABILITY:
                ability.add(die);
                break;
            case BOOST:
                boost.add(die);
                break;
            case SUCCESS:
                success.add(die);
                break;
            case ADVANTAGE:
                advantage.add(die);
                break;
            case TRIUMPH:
                triumph.add(die);
                break;

            // Negative
            case CHALLENGE:
                challenge.add(die);
                break;
            case DIFFICULTY:
                difficulty.add(die);
                break;
            case SETBACK:
                setback.add(die);
                break;
            case FAILURE:
                failure.add(die);
                break;
            case THREAT:
                threat.add(die);
                break;
            case DESPAIR:
                despaire.add(die);
                break;

            // Force
            case FORCE:
                force.add(die);
                break;
            case LIGHT:
                light.add(die);
                break;
            case DARK:
                dark.add(die);
                break;
        }
    }

    private List<RollableDie> getRollableDice() {
        List<RollableDie> allDice = new ArrayList<RollableDie>();
        // positive results
        allDice.addAll(proficiency);
        allDice.addAll(ability);
        allDice.addAll(boost);
        allDice.addAll(success);
        allDice.addAll(advantage);
        allDice.addAll(triumph);
        // negative results
        allDice.addAll(challenge);
        allDice.addAll(difficulty);
        allDice.addAll(setback);
        allDice.addAll(failure);
        allDice.addAll(threat);
        allDice.addAll(despaire);
        // force results
        allDice.addAll(force);
        allDice.addAll(light);
        allDice.addAll(dark);
        return allDice;
    }

    @Override
    public int getCheck() {
        return totalCheck;
    }

    @Override
    public int getConsequence() {
        return totalConsequence;
    }

    @Override
    public int getTriumph() {
        return totalTriumph;
    }

    @Override
    public int getDespair() {
        return totalDespair;
    }

    @Override
    public int getLightSide() {
        return totalLight;
    }

    @Override
    public int getDarkSide() {
        return totalDark;
    }

    @Override
    public String getFace() {
        return "";
    }

    @Override
    public List<RollableDie> getDice() {
        return getRollableDice();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Die dr : getDice()) {
            sb.append(dr.getResults()).append(" ");
        }
        if(sb.length() > 0) {
            sb.append("\n");
        }
        sb.append( String.format("check: %s; consequence: %s; trimph: %s; despair: %s; light: %s; dark: %s",
                getCheck(), getConsequence(), getTriumph(), getDespair(), getLightSide(), getDarkSide()) );
        return sb.toString();
    }

    private class NegatedResult implements Result {

        private int check, consequence, triumph, despair, light, dark;

        public NegatedResult(Result result) {
            check = -result.getCheck();
            consequence = -result.getConsequence();
            triumph = -result.getTriumph();
            despair = -result.getDespair();
            light = - result.getLightSide();
            dark = -result.getDarkSide();
        }

        @Override
        public int getCheck() {
            return this.check;
        }

        @Override
        public int getConsequence() {
            return this.consequence;
        }

        @Override
        public int getTriumph() {
            return this.triumph;
        }

        @Override
        public int getDespair() {
            return this.despair;
        }

        @Override
        public int getLightSide() {
            return this.light;
        }

        @Override
        public int getDarkSide() {
            return this.dark;
        }

    }

}
