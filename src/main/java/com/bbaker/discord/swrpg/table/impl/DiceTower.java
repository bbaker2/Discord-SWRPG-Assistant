package com.bbaker.discord.swrpg.table.impl;

import java.util.ArrayList;
import java.util.List;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.TableResult;
import com.bbaker.discord.swrpg.table.Result;

public class DiceTower implements TableResult {
    int totalCheck = 0, totalConsequence = 0,
        totalTriumph = 0, totalDespair = 0,
        totalLight = 0, totalDark = 0;

    // Dice
    private List<Die> ability = new ArrayList<Die>(), proficiency = new ArrayList<Die>() , boost = new ArrayList<Die>();
    private List<Die> difficulty = new ArrayList<Die>(), challenge = new ArrayList<Die>(), setback = new ArrayList<Die>();
    private List<Die> force = new ArrayList<Die>();

    // Adjustments
    private List<Die> success = new ArrayList<Die>(), advantage = new ArrayList<Die>(), triumph = new ArrayList<Die>();
    private List<Die> failure = new ArrayList<Die>(), threat = new ArrayList<Die>(), despaire = new ArrayList<Die>();
    private List<Die> light = new ArrayList<Die>(), dark = new ArrayList<Die>();


    public void adjustTable(Result dr) {
        totalCheck += dr.getCheck();
        totalConsequence += dr.getConsequence();
        totalTriumph += dr.getTriumph();
        totalDespair += dr.getDespair();
        totalLight += dr.getLightSide();
        totalDark += dr.getDarkSide();
    }

    public void addDie(Die die) {
        adjustTable(die.peek());
        System.out.println(die.getType() + " -> " + die.peek());

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
    public List<Die> getDice() {
        List<Die> allDice = new ArrayList<Die>();
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Die dr : getDice()) {
            sb.append(dr.peek()).append(" ");
        }
        if(sb.length() > 0) {
            sb.append("\n");
        }
        sb.append( String.format("check: %s; consequence: %s; trimph: %s; despair: %s; light: %s; dark: %s",
                getCheck(), getConsequence(), getTriumph(), getDespair(), getLightSide(), getDarkSide()) );
        return sb.toString();
    }

}
