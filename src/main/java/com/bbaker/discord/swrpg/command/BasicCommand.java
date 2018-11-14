package com.bbaker.discord.swrpg.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bbaker.discord.swrpg.database.DatabaseService;
import com.bbaker.discord.swrpg.parser.text.TextArgumentParser;

public abstract class BasicCommand {
    public static final String ERROR_MSG = "Woops, I ran into an error.";

    protected TextArgumentParser parser = new TextArgumentParser();
    protected DatabaseService dbService;

    public BasicCommand(DatabaseService dbService) {
        this.dbService = dbService;
    }

    protected List<String> getList(String message){
        String[] args = message.split("\\s+");
        List<String> tokens = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
        return new ArrayList<String>(tokens);
    }

}
