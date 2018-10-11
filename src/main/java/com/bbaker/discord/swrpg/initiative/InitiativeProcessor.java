package com.bbaker.discord.swrpg.initiative;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import com.bbaker.discord.swrpg.command.ArgumentProcessor;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;
import com.bbaker.discord.swrpg.roller.DiceProcessor;

public class InitiativeProcessor implements ArgumentProcessor {

    private List<CharacterType> characters = new ArrayList<CharacterType>();

    @Override
    public boolean evaluate(String token, OptionalInt left, OptionalInt right) throws BadArgumentException {
        CharacterType ct = findCharacter(token);

        if(ct == null) {
            return false;
        }

        int total = DiceProcessor.getTotal(left, right);
        while(total-- > 0) {
            characters.add(ct);
        }
        return true;
    }

    @Override
    public boolean isToken(String token) {
        return findCharacter(token) != null;
    }

    private static CharacterType findCharacter(String token) {
        switch(token.toLowerCase()) {
            case "p":
            case "pc":
                return CharacterType.PC;
            case "n":
            case "npc":
                return CharacterType.NPC;
            case "dp":
            case "dpc":
            case "deadpc":
                return CharacterType.DPC;
            case "dn":
            case "dnpc":
            case "deadnpc":
                return CharacterType.DNPC;
        }
        return null;
    }

    public List<CharacterType> getCharacters() {
        return this.characters;
    }

}
