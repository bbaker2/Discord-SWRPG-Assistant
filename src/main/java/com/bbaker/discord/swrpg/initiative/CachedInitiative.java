package com.bbaker.discord.swrpg.initiative;

import java.util.List;

public class CachedInitiative implements Runnable {

    private List<CharacterType> init;

    public CachedInitiative(List<CharacterType> init) {
        this.init = init;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100 * 60 * 10);
        } catch (InterruptedException e) {
            return;
        }

    }

    public List<CharacterType> getInit(){
        return this.init;
    }

}
