package com.bbaker.discord.swrpg.initiative;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bbaker.discord.swrpg.die.Result;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public class InitiativeTracker {

    public static final String WRONG_SIZE_MSG = "Need exactly %d characters. Was given %d.";
    public static final int DNE = -1;
    private static final Comparator<InitCharacter> sorter = new CharacterSort();

    private List<InitCharacter> init;
    private int round;
    private int turn;
    private boolean canRoll;

    public InitiativeTracker(List<InitCharacter> init) {
        this.init = init;
        Collections.sort(this.init, sorter);
        cacheSelf();
    }

    private void cacheSelf() {
        this.round = init.isEmpty() ? 1 : init.get(0).getRound();
        this.turn = 1;
        this.canRoll = true;
        for(InitCharacter c : init) {
            if(c.getRound() <= round) {
                this.turn++;
            }

            if(c.getSuccess() == DNE || c.getAdvantage() == DNE) {
                this.canRoll = false;
            }
        }
    }

    public int getRound() {
        return this.round;
    }

    public int getTurn() {
        return this.turn;
    }

    public List<InitCharacter> getInit() {
        return this.init;
    }

    public void addCharacter(CharacterType type, String label, Result dieResult) {
        InitCharacter c = new InitCharacter(
            label,
            dieResult.getCheck(),
            dieResult.getConsequence(),
            round,
            type
        );

        this.init.add(c);

        Collections.sort(this.init, sorter);

    }

    public void adjustTurn(int adjustment, String label) {
        int max = init.size()+1;
        int unsafeTurn = turn + adjustment;
        int oldTurn = turn;
                
        round += unsafeTurn / max;
        turn = Math.max(1, unsafeTurn % max);

        
        
        
        InitCharacter c;
        for(int i = 0; i < init.size(); i++) {
            c = init.get(i);
            // All characters before the target turn should be updated to the primary round
            if(i < turn-1) { // since turns start at 1 and arrays start at 0, we adjust the turn to match array indexes
                c.setRound(round);
            // All other characters are forced into the previous round
            } else {
                c.setRound(round - 1);
            }

            // old = 1, new = 3
            if(oldTurn >= turn) {
            	if(i > oldTurn - 1 || i <= turn - 1) {
            		c.setLabel(label);
            	}            
            } else {
            	if(i >= turn-1 && i < oldTurn-1) {
            		c.setLabel(label);
            	}
            }
        }
    }

    public void reorder(List<CharacterType> charTypes) throws BadArgumentException {
        if(init.size() != charTypes.size()) {
            throw new BadArgumentException(WRONG_SIZE_MSG, init.size(), charTypes.size());
        }

        InitCharacter c;
        for(int i = 0; i < init.size(); i++) {
            c = init.get(i);
            c.setType(charTypes.get(i));
            c.setLabel(""); // Clear the label

        }
    }

    /**
     * Truncates the old {@link #init}, sets {@link #canRoll} to false.
     * <br/>
     * They will be stored in the database in the order given
     * @param charTypes the characters that truncate the old {@link #init}
     * @throws BadArgumentException
     */
    public void forceSet(List<CharacterType> charTypes) throws BadArgumentException {
        this.init.clear();

        for(int i = 0; i < charTypes.size(); i++) {
            this.init.add(
                new InitCharacter("", round, i, charTypes.get(i))
            );
        }

        this.canRoll = false;
    }

    private static class CharacterSort implements Comparator<InitCharacter> {

        @Override
        public int compare(InitCharacter a, InitCharacter b) {

            if(a.usesOrder() && b.usesOrder()) {
                return b.getOrder() - a.getOrder();
            }

            int diff = b.getSuccess() - a.getSuccess();
            if(diff == 0) {
                return b.getAdvantage() - a.getAdvantage();
            } else {
                return diff;
            }
        }


    }


}
