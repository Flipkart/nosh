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

import com.beust.jcommander.Parameter;

import in.hatimi.nosh.Command;

/**
 * @author indroneel.das
 *
 */

@Command("test")
public class TestCommand {

    @Parameter(arity=1, names="--t")
    private String tell;

    public void execute() {
        System.out.println(tell);
    }
}
