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

    public void execute(String[] args) {
        if(args == null || args.length == 0) {
            List<String> cmdList = cmdCtxt.listCommands();
            for(String cmdName : cmdList) {
                System.out.printf("%s -- %s\n", cmdName, cmdCtxt.describeCommand(cmdName));
            }
            return;
        }
        System.out.println(cmdCtxt.describeCommand(args[0]));
        cmdCtxt.printUsage(args[0]);
    }
}
