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

package in.hatimi.nosh.support;

import in.hatimi.nosh.capi.CommandContext;
import in.hatimi.nosh.capi.CommandContextAware;
import in.hatimi.nosh.capi.CommandSetup;

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
