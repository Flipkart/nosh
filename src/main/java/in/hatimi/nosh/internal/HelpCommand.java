/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package in.hatimi.nosh.internal;

import in.hatimi.nosh.capi.Command;
import in.hatimi.nosh.capi.CommandContext;
import in.hatimi.nosh.capi.CommandContextAware;

import java.util.Collections;
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

    public void execute(String[] args) {
        if(args == null || args.length == 0) {
            List<String> cmdList = cmdCtxt.listCommands();
            Collections.sort(cmdList);
            int maxWidth = 0;
            for(String cmdName : cmdList) {
                if(cmdName.length() > maxWidth) {
                    maxWidth = cmdName.length();
                }
            }
            for(String cmdName : cmdList) {
                System.out.printf("%-" + maxWidth +"s : %s\n", cmdName, cmdCtxt.describeCommand(cmdName));
            }
            System.out.println("\ntype help <command> to get help on a specific command");
            return;
        }
        System.out.println(cmdCtxt.describeCommand(args[0]));
        cmdCtxt.printUsage(args[0]);
    }
}
