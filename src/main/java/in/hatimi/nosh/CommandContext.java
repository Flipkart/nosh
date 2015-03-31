/*
 * Copyright (c) Flipkart Internet Private Limited - All Rights Reserved.
 *
 * Private and confidential
 *
 * This file cannot be copied and/or distributed, via any medium whatsoever,
 * without express permission from Flipkart Internet Private Limited. Failure to
 * comply shall be considered as unauthorized or illegal usage of this file.
 */

package in.hatimi.nosh;

import java.util.List;

/**
 * @author indroneel.das
 *
 */

public interface CommandContext {

    List<String> listCommands();

    String describeCommand(String cmdName);

    void execute(String cmdName, String[] args);

    void printUsage(String cmdName);
}
