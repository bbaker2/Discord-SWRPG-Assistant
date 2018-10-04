package com.bbaker.discord.swrpg.initiative;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.die.Result;
import com.bbaker.discord.swrpg.exceptions.BadArgumentException;

public class InitiativeTracker {

    public static final String NEGATIVE_ROUND_MSG = "You cannot go prior to Round 1. No changes made to Initiative";
    public static final String WRONG_SIZE_MSG = "Need exactly %d characters. Was given %d.";
    public static final int DNE = -1;

    private Comparator<InitCharacter> sorter;
    private List<InitCharacter> init;
    private InitTrackerMeta initMeta;

    public InitiativeTracker(List<InitCharacter> init, InitTrackerMeta meta) {
        this.init = init;
        this.initMeta = meta;
        this.sorter = new CharacterSort(meta);
        Collections.sort(this.init, sorter);

    }

    public InitiativeTracker(List<InitCharacter> init, int round, int turn, boolean canRoll) {
        this(init, new InitTrackerMeta(DatabaseService.IS_NEW, round, turn, !canRoll));
    }

    public int getRound() {
        return this.initMeta.round;
    }

    public int getTurn() {
        return this.initMeta.turn;
    }

    public List<InitCharacter> getInit() {
        return this.init;
    }

    public void addCharacter(CharacterType type, String label, Result dieResult) {
        InitCharacter c = new InitCharacter(
            label,
            dieResult.getCheck(),
            dieResult.getConsequence(),
            type
        );

        this.init.add(c);

        Collections.sort(this.init, sorter);

    }

    public void adjustTurn(int adjustment, String label) throws BadArgumentException {
        int direction = adjustment < 0 ? -1 : 1;

        InitCharacter c;
        for(int step = 0; step < Math.abs(adjustment); step++) {
            initMeta.turn += direction;


            if(initMeta.turn <= 0) {
                initMeta.turn = init.size();
                initMeta.round--;
            } else if(initMeta.turn > init.size()) {
                initMeta.turn = 1;
                initMeta.round++;
            }

            c = init.get(initMeta.turn-1);
            c.setLabel(label);
        }

        System.out.println(init);
        if(initMeta.round <= 0) {
            throw new BadArgumentException(NEGATIVE_ROUND_MSG);
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
                new InitCharacter("", initMeta.round, i, charTypes.get(i))
            );
        }

        this.initMeta.usesOrder = !init.isEmpty(); // if everything was wiped out... then you can roll again
    }


    public boolean canRoll() {
        return !this.initMeta.usesOrder;
    }

    @Override
    public String toString() {
        return String.format("Round %2d, Turn %2d, Can Roll: %s\n%s", initMeta.round, initMeta.turn, !initMeta.usesOrder, init);
    }

    private static class CharacterSort implements Comparator<InitCharacter> {
        private InitTrackerMeta itm;
        private CharacterSort(InitTrackerMeta itm) {
            this.itm = itm;
        }

        @Override
        public int compare(InitCharacter a, InitCharacter b) {

            if(itm.usesOrder) {
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
