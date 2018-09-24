package com.bbaker.discord.swrpg.initiative;

import org.h2.util.StringUtils;

public class InitCharacter {

    private String label;
    private int success;
    private int advantage;
    private int round;
    private int order;
    private boolean usesOrder;
    private CharacterType type;

    public InitCharacter(String label, int order, int round, CharacterType type) {
        this(label, InitiativeTracker.DNE, InitiativeTracker.DNE, round, order, true, type);
    }

    public InitCharacter(String label, int success, int advantage, int round, CharacterType type) {
        this(label, success, advantage, round, InitiativeTracker.DNE, false, type);
    }

    public InitCharacter(String label, int success, int advantage, int round, int order, boolean usesOrder, CharacterType type) {
        this.label = label;
        this.success = success;
        this.advantage = advantage;
        this.round = round;
        this.order = order;
        this.usesOrder = usesOrder;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public int getSuccess() {
        return success;
    }

    public int getAdvantage() {
        return advantage;
    }

    public int getRound() {
        return round;
    }

    public int getOrder() {
        return order;
    }

    public boolean usesOrder() {
        return this.usesOrder;
    }

    public CharacterType getType() {
        return type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setUsesOrder(boolean usesRoller) {
        this.usesOrder = usesRoller;
    }

    public void setType(CharacterType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof InitCharacter) {
            InitCharacter that = (InitCharacter)obj;
            return this.getType() == that.getType()
                    && this.getRound() == that.getRound()
                    && this.getAdvantage() == that.getAdvantage()
                    && this.getSuccess() == that.getSuccess()
                    && this.usesOrder == that.usesOrder()
                    && this.getOrder() == that.getOrder()
                    && StringUtils.equals(this.getLabel(), that.getLabel());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("\n%4s, Round: %2d [%2d %2d %5s %2d] '%s'", type.name(), round, success, advantage, usesOrder, order, label);
    }


}
