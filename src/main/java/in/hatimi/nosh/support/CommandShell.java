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

import in.hatimi.nosh.capi.CommandOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author indroneel.das
 *
 */

public class CommandShell implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandShell.class);

    private String                prompt;
    private List<CommandListener> listeners;
    private Thread                loopThrd;

    public CommandShell(String prompt) {
        this.prompt = prompt;
        listeners = new ArrayList<>();
    }

    public void setPrompt(String value) {
        prompt = value;
    }

    public CommandOutput getCommandOutput() {
        return new CommandOutputImpl();
    }

    public void addListener(CommandListener cl) {
        if(!listeners.contains(cl)) {
            listeners.add(cl);
        }
    }

    public void removeListener(CommandListener cl) {
        listeners.remove(cl);
    }

    public void start() {
        loopThrd = new Thread(this, "nosh-command-loop");
        loopThrd.start();
    }

    public void stop() {

    }

    ////////////////////////////////////////////////////////////////////////////
    // Methods of interface Runnable

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.println();
            System.out.print(prompt);
            System.out.flush();

            String cmdLine = null;
            List<String> tokens = null;
            try {
                cmdLine = reader.readLine();
                tokens = parseCmdLine(cmdLine);
            }
            catch(Exception exep) {
                LOGGER.error("error parsing command line", exep);
                continue;
            }
            if(tokens.isEmpty()) {
                continue;
            }
            executeCommand(cmdLine, tokens);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods

    private List<String> parseCmdLine(String cmdLine) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        StreamTokenizer strmtok = new StreamTokenizer(new StringReader(cmdLine));
        strmtok.resetSyntax();
        strmtok.quoteChar('"');
        strmtok.wordChars('#', '~');
        strmtok.wordChars('!', '!');
        //strmtok.wordChars('a', 'z');
        //strmtok.wordChars('A', 'Z');
        //strmtok.wordChars('0', '9');
        //strmtok.wordChars(':', ':');
        //strmtok.wordChars('-', '-');
        //strmtok.wordChars('_', '_');
        //strmtok.wordChars(':', ':');
        strmtok.whitespaceChars(' ', ' ');
        strmtok.whitespaceChars('\t', '\t');
        while(strmtok.nextToken() != StreamTokenizer.TT_EOF) {
            if(strmtok.ttype == StreamTokenizer.TT_WORD) {
                result.add(strmtok.sval);
                continue;
            }
            if(strmtok.ttype == '"') {
                result.add(strmtok.sval);
                continue;
            }
            if(strmtok.ttype == StreamTokenizer.TT_NUMBER) {
                result.add(String.valueOf(strmtok.nval));
                continue;
            }
        }
        return result;
    }

    private void executeCommand(String cmdLine, List<String> tokens) {
        String cmdName = tokens.remove(0);
        for(CommandListener cl : listeners) {
            try {
                cl.execute(cmdLine, cmdName, tokens.toArray(new String[tokens.size()]));
            }
            catch(Exception exep) {
                LOGGER.error("command execution failed", exep);
                continue;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inner class that implements the CommandOutput for this shell

    private class CommandOutputImpl implements CommandOutput {

        @Override
        public void print(Object obj) {
            System.out.print(obj);
        }

        @Override
        public void println(Object obj) {
            System.out.println(obj);
        }

        @Override
        public void printf(String spec, Object... items) {
            System.out.printf(spec, items);
        }
    }
}
