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

import in.hatimi.nosh.capi.Command;
import in.hatimi.nosh.capi.CommandOutput;
import in.hatimi.nosh.capi.CommandOutputAware;
import in.hatimi.nosh.capi.CommandSetup;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author indroneel.das
 *
 */

class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private CommandOutput cmdOut;
    private Class<?>      cmdCls;
    private String[]      names;
    private String        description;
    private String        syntax;

    public CommandExecutor(CommandOutput out) {
        cmdOut = out;
    }

    public boolean associateWith(Class<?> cls) {
        cmdCls = cls;
        Command cmd = cmdCls.getAnnotation(Command.class);
        names = cmd.name();
        description = cmd.description();
        syntax = cmd.syntax();

        if(ArrayUtils.isEmpty(names)) {
            return false;
        }
        for(String name: names) {
            if(StringUtils.isBlank(name)) {
                return false;
            }
        }

        //check for executable method
        Method mthd = findMainMethod();
        if(mthd == null) {
            return false;
        }
        return true;
    }

    public String[] getNames() {
        return names;
    }

    public String getDescription() {
        return description;
    }

    public void execute(String[] params, List<CommandSetup> setupList) {
        Object cmdObj = null;
        CmdLineManager clm = new CmdLineManager(cmdCls);
        try {
            cmdObj = cmdCls.newInstance();
            if(setupList != null) {
                for(CommandSetup cs : setupList) {
                    cs.setup(cmdObj);
                }
            }
            if(!clm.applyCmdLine(cmdObj, params)) {
                clm.printUsage(createSyntax());
                return;
            }
        }
        catch(Exception exep) {
            exep.printStackTrace();
            LOGGER.error("unable to create/setup command", exep);
            clm.printUsage(createSyntax());
            return;
        }

        try {
            if(cmdObj instanceof CommandOutputAware) {
                ((CommandOutputAware) cmdObj).setCommandOutput(cmdOut);
            }
            Method mainMthd = findMainMethod();
            Class<?>[] paramTypes = mainMthd.getParameterTypes();
            if(paramTypes != null && paramTypes.length > 0) {
                Object args = clm.getLeftoverArgs();
                mainMthd.invoke(cmdObj, args);
            }
            else {
                mainMthd.invoke(cmdObj);
            }
        }
        catch(Exception exep) {
            LOGGER.error("execution error", exep);
        }
    }

    public void printUsage() {
        CmdLineManager clm = new CmdLineManager(cmdCls);
        clm.printUsage(createSyntax());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods

    private Method findMainMethod() {
        Method[] methods = cmdCls.getMethods();
        for(Method method : methods) {
            if(!method.getName().equals("execute")) {
                continue; //method name must be 'execute'
            }
            int mod = method.getModifiers();
            if(Modifier.isAbstract(mod)) {
                continue; //method cannot be abstract.
            }
            if(Modifier.isStatic(mod)) {
                continue; //method cannot be static.
            }
            if(Modifier.isSynchronized(mod)) {
                continue; //method cannot be synthetic.
            }
            if(!Modifier.isPublic(mod)) {
                continue; //method must be public.
            }
            if(method.getReturnType() != Void.TYPE) {
                continue; //method must not return a value.
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            if(paramTypes.length > 1) {
                continue; //method can take at most one parameter.
            }
            if(paramTypes.length == 1 && !paramTypes[0].equals(String[].class)) {
                continue; //if single parameter, must be String[]
            }
            return method;
        }
        return null;
    }

    private String createSyntax() {
        StringBuilder sb = new StringBuilder();
        sb.append(names[0]);
        for(int i = 1; i < names.length; i++) {
            sb.append('|').append(names[i]);
        }
        if(StringUtils.isNoneBlank(syntax)) {
            sb.append(' ').append(syntax);
        }
        return sb.toString();
    }
}
