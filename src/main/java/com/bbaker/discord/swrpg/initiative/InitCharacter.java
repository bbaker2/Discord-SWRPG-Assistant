package com.bbaker.discord.swrpg.initiative;

public class InitCharacter {

    private String label;
    private int success;
    private int advantage;
    private int round;
    private int order;
    private boolean usesOrder;
    private CharacterType type;

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


}
