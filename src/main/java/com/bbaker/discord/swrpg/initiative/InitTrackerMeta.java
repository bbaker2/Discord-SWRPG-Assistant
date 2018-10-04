package com.bbaker.discord.swrpg.initiative;

public class InitTrackerMeta {

    public InitTrackerMeta(long id, int round, int turn, boolean usesOrder) {
        this.id = id;
        this.round = round;
        this.turn = turn;
        this.usesOrder = usesOrder;
    }
    public long id;
    public int round;
    public int turn;
    public boolean usesOrder;

}