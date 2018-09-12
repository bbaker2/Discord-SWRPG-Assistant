package com.bbaker.discord.swrpg.destiny;

import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public class DestinyTracker {

    public static final String LIGHT_MSG = "There are no lightside points to use";
    public static final String DARK_MSG = "There are no darkside points to use.";

    private int darkSide;
    private int lightSide;
    private long id;

    public DestinyTracker(int light, int dark, long id) {
        this.lightSide = light;
        this.darkSide = dark;
        this.id = id;
    }

    public int getDarkSide() {
        return darkSide;
    }

    public int getLightSide() {
        return lightSide;
    }

    public long getId() {
        return id;
    }

    public void setSides(int light, int dark) {
        this.lightSide = light;
        this.darkSide = dark;
    }

    public void adjustLightSide(int light) {
        this.lightSide += light;
    }

    public void adjustDarkSide(int dark) {
        this.darkSide += dark;
    }

    public void useLightSide() throws BadArgumentException {
        if(lightSide == 0) {
            throw new BadArgumentException(LIGHT_MSG);
        } else {
            lightSide--;
            darkSide++;
        }
    }

    public void useDarkSide() throws BadArgumentException {
        if(darkSide == 0) {
            throw new BadArgumentException(DARK_MSG);
        } else {
            darkSide--;
            lightSide++;
        }
    }

}
