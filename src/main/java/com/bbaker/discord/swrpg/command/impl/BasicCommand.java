package com.bbaker.discord.swrpg.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import com.bbaker.discord.swrpg.command.ArgumentParser;
import com.bbaker.discord.swrpg.database.DatabaseService;

public abstract class BasicCommand {
    public static final String ERROR_MSG = "Woops, I ran into an error.";

    protected ArgumentParser parser = new ArgumentParser();
    protected DatabaseService dbService;

    public BasicCommand(DatabaseService dbService) {
        this.dbService = dbService;
    }

    protected List<String> getList(String message){
        String[] args = message.split("\\s+");
        List<String> tokens = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
        return new ArrayList<String>(tokens);
    }

    protected int getTotal(OptionalInt left, OptionalInt right) {
        int total = 0;
        boolean useDefault = true;

        if(left.isPresent()) {
            total += left.getAsInt();
            useDefault = false;
        }

        if(right.isPresent()) {
            total += right.getAsInt();
            useDefault = false;
        }

        if(useDefault) {
            total = 1;
        }
        return total;
    }

}
