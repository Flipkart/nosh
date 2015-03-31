/*
 * Copyright (c) Flipkart Internet Private Limited - All Rights Reserved.
 *
 * Private and confidential
 *
 * This file cannot be copied and/or distributed, via any medium whatsoever,
 * without express permission from Flipkart Internet Private Limited. Failure to
 * comply shall be considered as unauthorized or illegal usage of this file.
 */

package in.hatimi.nosh.support;

import in.hatimi.nosh.CommandContext;
import in.hatimi.nosh.CommandContextAware;
import in.hatimi.nosh.CommandSetup;

/**
 * @author indroneel.das
 *
 */

class CommandContextAwareSetup implements CommandSetup {

    private CommandContext cmdCtxt;

    public CommandContextAwareSetup(CommandContext ctxt) {
        cmdCtxt = ctxt;
    }

    @Override
    public void setup(Object command) throws Exception {
        // TODO Auto-generated method stub
        if(command instanceof CommandContextAware) {
            ((CommandContextAware) command).setCommandContext(cmdCtxt);
        }
    }
}
