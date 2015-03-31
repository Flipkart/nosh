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

import in.hatimi.nosh.Command;
import in.hatimi.nosh.CommandContext;
import in.hatimi.nosh.CommandSetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author indroneel.das
 *
 */

public class Nosh {

    private static final Logger LOGGER = LoggerFactory.getLogger(Nosh.class);

    private List<String>                 scanPkgNames;
    private Map<String, CommandExecutor> cmdMap;
    private CommandShell                 cmdShell;

    private List<CommandSetup>           cmdSetups;

    public Nosh() {
        cmdMap = new HashMap<>();
        scanPkgNames = new ArrayList<>();
        cmdSetups = new ArrayList<>();
        cmdSetups.add(new CommandContextAwareSetup(new DefaultCommandContext()));

        cmdShell = new CommandShell("nosh$ ");
        cmdShell.addListener(new CommandHandler());
    }

    public Nosh commandPrompt(String value) {
        cmdShell.setPrompt(value);
        return this;
    }

    public Nosh scanPackages(String... pkgs) {
        scanPkgNames.clear();
        scanPkgNames.addAll(Arrays.asList(pkgs));
        return this;
    }

    public Nosh commandSetup(CommandSetup setup) {
        cmdSetups.add(setup);
        return this;
    }

    public void prepare() {
        /*
         * This is a hack around the Reflections package. FilterBuilder does not
         * seem to work with multiple package names, so the list of packages
         * must be provided in a loop
         */
        FilterBuilder fb = new FilterBuilder();
        for(String pkgName : scanPkgNames) {
            fb.includePackage(pkgName);
        }

        ConfigurationBuilder cb = new ConfigurationBuilder()
            .filterInputsBy(fb)
            .setUrls(ClasspathHelper.forClassLoader())
            .addScanners(new SubTypesScanner(), new TypeAnnotationsScanner());
        Reflections reflections = new Reflections(cb);

        Set<Class<?>> clsList = reflections.getTypesAnnotatedWith(Command.class);
        for(Class<?> cls : clsList) {
            CommandExecutor exec = new CommandExecutor();
            if(exec.associateWith(cls)) {
                cmdMap.put(exec.getName(), exec);
            }
        }

        // And start the command shell
        cmdShell.start();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inner class that serves as the command listener for the shell

    private class CommandHandler implements CommandListener {

        @Override
        public void execute(String cmdLine, String cmdName, String[] args) throws Exception {
            if(!cmdMap.containsKey(cmdName)) {
                LOGGER.info("command not found: {}", cmdName);
                return;
            }
            CommandExecutor exec = cmdMap.get(cmdName);
            try {
                exec.execute(cmdLine, args, cmdSetups);
            }
            catch(Exception exep) {
                LOGGER.error(exep.getMessage(), exep);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inner class that serves as the default command context

    private class DefaultCommandContext implements CommandContext {

        @Override
        public List<String> listCommands() {
            return new ArrayList<String>(cmdMap.keySet());
        }

        @Override
        public String describeCommand(String cmdName) {
            if(!cmdMap.containsKey(cmdName)) {
                return null;
            }
            CommandExecutor exec = cmdMap.get(cmdName);
            return exec.getDescription();
        }

        @Override
        public void execute(String cmdName, String[] args) {
            if(!cmdMap.containsKey(cmdName)) {
                LOGGER.info("command not found: {}", cmdName);
                return;
            }
            CommandExecutor exec = cmdMap.get(cmdName);
            try {
                exec.execute(null, args, cmdSetups);
            }
            catch(Exception exep) {
                LOGGER.error(exep.getMessage(), exep);
            }
        }

        @Override
        public void printUsage(String cmdName) {
            if(!cmdMap.containsKey(cmdName)) {
                LOGGER.info("command not found: {}", cmdName);
                return;
            }
            CommandExecutor exec = cmdMap.get(cmdName);
            exec.printUsage();
        }
    }
}
