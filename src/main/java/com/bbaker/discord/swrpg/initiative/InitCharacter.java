package com.bbaker.discord.swrpg.initiative;

import org.h2.util.StringUtils;

public class InitCharacter {

    private String label;
    private int success;
    private int advantage;
    private int order;
    private CharacterType type;

    public InitCharacter(String label, int order, CharacterType type) {
        this(label, InitiativeTracker.DNE, InitiativeTracker.DNE, order, type);
    }

    public InitCharacter(String label, int success, int advantage, CharacterType type) {
        this(label, success, advantage, InitiativeTracker.DNE, type);
    }

    public InitCharacter(String label, int success, int advantage, int order, CharacterType type) {
        this.label = label;
        this.success = success;
        this.advantage = advantage;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    public CharacterType getType() {
        return type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setType(CharacterType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof InitCharacter) {
            InitCharacter that = (InitCharacter)obj;
            return this.getType() == that.getType()
                    && this.getAdvantage() == that.getAdvantage()
                    && this.getSuccess() == that.getSuccess()
                    && this.getOrder() == that.getOrder()
                    && StringUtils.equals(this.getLabel(), that.getLabel());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("\n"
                + "%4s, [%2d %2d %2d] '%s'", type.name(), success, advantage, order, label);
    }


}
