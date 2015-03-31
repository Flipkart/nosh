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

/**
 * @author indroneel.das
 *
 */

@Command(name="echo", description="prints out the command line entered")
public class EchoCommand {

    public void execute(String cmdLine) {
        System.out.println(cmdLine);
    }
}
