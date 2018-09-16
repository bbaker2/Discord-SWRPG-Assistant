package com.bbaker.discord.swrpg.printer;

import java.util.List;

import com.bbaker.discord.swrpg.initiative.InitCharacter;
import com.bbaker.discord.swrpg.initiative.CharacterType;
import com.bbaker.discord.swrpg.initiative.InitiativeTracker;

public class InitiativePrinter {

    public String printInit(InitiativeTracker initTracker) {
        List<InitCharacter> characteres = initTracker.getInit();
        if(characteres.isEmpty()) {
            return "Not initiative found";
        }

        StringBuilder result = new StringBuilder();
        int round = initTracker.getRound() -1; // since our arrays at 0,
                                               // we subtract 1 to match since rounds start at 1

        InitCharacter c; String suffix;
        for(int i = 0; i < characteres.size(); i++) {
            c = characteres.get(i);

            if(i == round) {
                result.append("[");
                suffix = "]";
            } else {
                suffix = "";
            }

            result.append(findEmojie(c.getType()));
            result.append(c.getLabel());
            result.append(suffix);
        }


        return result.toString();
    }


    private String findEmojie(CharacterType ct) {
        switch(ct) {
            case PC:
                return ":smiley:";
            case NPC:
                return ":smiling_imp:";
            case DPC:
                return ":dizzy_face:";
            case DNPC:
                return ":skull:";
            default:
                return "";
        }
    }

}
