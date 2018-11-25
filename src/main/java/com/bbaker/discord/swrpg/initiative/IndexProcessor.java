package com.bbaker.discord.swrpg.initiative;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bbaker.discord.swrpg.parser.integer.IntegerArgumentEvaluator;

public class IndexProcessor implements IntegerArgumentEvaluator {

    private SortedSet<Integer> foundIndexs = new TreeSet<>();

    @Override
    public boolean evaluate(int token) {
        foundIndexs.add(token);
        return true;
    }

    public SortedSet<Integer> getIndexs(){
        return this.foundIndexs;
    }

}
