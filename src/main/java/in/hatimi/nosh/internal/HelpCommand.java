/*
 * Copyright (c) Flipkart Internet Private Limited - All Rights Reserved.
 *
 * Private and confidential
 *
 * This file cannot be copied and/or distributed, via any medium whatsoever,
 * without express permission from Flipkart Internet Private Limited. Failure to
 * comply shall be considered as unauthorized or illegal usage of this file.
 */

package in.hatimi.nosh.internal;

import in.hatimi.nosh.Command;
import in.hatimi.nosh.CommandContext;
import in.hatimi.nosh.CommandContextAware;

import java.util.List;

/**
 * @author indroneel.das
 *
 */

@Command(name="help", description="prints this help message")
public class HelpCommand implements CommandContextAware {

    private CommandContext cmdCtxt;

    @Override
    public void setCommandContext(CommandContext ctxt) {
        cmdCtxt = ctxt;
    }

    public void execute() {
        List<String> cmdList = cmdCtxt.listCommands();
        for(String cmdName : cmdList) {
            System.out.printf("%s -- %s\n", cmdName, cmdCtxt.describeCommand(cmdName));
        }
    }
}
