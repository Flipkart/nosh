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

import in.hatimi.nosh.capi.CmdLineOption;

import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author user
 *
 */

class CmdLineManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdLineManager.class);

    private Class<?> type;
    private Options  options;
    private String[] leftoverArgs;

    public CmdLineManager(Class<?> type) {
        this.type = type;
        options = new Options();
        Field[] fields = type.getDeclaredFields();
        for(Field field : fields) {
            Option opt = optionFromField(field);
            if(opt != null) {
                options.addOption(opt);
            }
        }
    }

    public void printUsage(String syntax) {
        new HelpFormatter().printHelp(syntax, options);
    }

    public boolean applyCmdLine(Object target, String[] arguments) {
        if(!target.getClass().equals(type)) {
            return false;
        }
        GnuParser parser = new GnuParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, arguments);
        }
        catch (ParseException exep) {
            LOGGER.error("failed to parse the command line", exep);
            return false;
        }

        Field[] fields = type.getDeclaredFields();
        for(Field field : fields) {
            if(!injectField(cmdLine, target, field)) {
                LOGGER.error("failed to inject field {}", field.getName());
                return false;
            }
        }
        leftoverArgs = cmdLine.getArgs();
        return true;
    }

    public String[] getLeftoverArgs() {
        return leftoverArgs;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods

    private Option optionFromField(Field field) {
        CmdLineOption clo = field.getAnnotation(CmdLineOption.class);
        if(clo == null) {
            return null;
        }
        Option option = new Option(clo.name(), clo.longName(), clo.argCount() > 0, clo.description());
        option.setArgs(clo.argCount());
        option.setRequired(clo.required());
        option.setOptionalArg(clo.optionalArg());
        option.setValueSeparator(clo.valueSeparator());

        return option;
    }

    private boolean injectField(CommandLine cmdLine, Object target, Field field) {
        CmdLineOption clo = field.getAnnotation(CmdLineOption.class);
        if(clo == null) {
            return true;
        }

        String nameToUse = clo.name();
        if(StringUtils.isBlank(nameToUse)) {
            nameToUse = clo.longName();
        }

        if(clo.argCount() == 1) {
            String value = cmdLine.getOptionValue(nameToUse);
            if(value != null) {
                return injectString(target, field, value);
            }
        }
        else if(clo.valueSeparator() > 0) {
            Properties props = cmdLine.getOptionProperties(nameToUse);
            if(props != null) {
                return injectProperties(target, field, props);
            }
        }
        else if(clo.argCount() > 1) {
            String[] values = cmdLine.getOptionValues(nameToUse);
            if(values != null && values.length > 0) {
                return injectStringArray(target, field, values);
            }
        }
        return true;
    }

    private boolean injectStringArray(Object target, Field field, String[] values) {
        if(!field.getType().isAssignableFrom(String[].class)) {
            return false;
        }
        return injectImpl(target, field, values);
    }

    private boolean injectProperties(Object target, Field field, Properties values) {
        if(!field.getType().isAssignableFrom(Properties.class)) {
            return false;
        }
        return injectImpl(target, field, values);
    }

    private boolean injectString(Object target, Field field, String value) {
        if(field.getType().isAssignableFrom(Boolean.class)) {
            Boolean vobj = new Boolean(value);
            return injectImpl(target, field, vobj);
        }
        if(field.getType().isAssignableFrom(Double.class)) {
            Double vobj = Double.valueOf(value);
            return injectImpl(target, field, vobj);
        }
        if(field.getType().isAssignableFrom(Float.class)) {
            Float vobj = Float.valueOf(value);
            return injectImpl(target, field, vobj);
        }
        if(field.getType().isAssignableFrom(Long.class)) {
            Long vobj = Long.valueOf(value);
            return injectImpl(target, field, vobj);
        }
        if(field.getType().isAssignableFrom(Integer.class)) {
            Integer vobj = Integer.valueOf(value);
            return injectImpl(target, field, vobj);
        }
        if(field.getType().isAssignableFrom(String.class)) {
            return injectImpl(target, field, value);
        }
        return false;
    }

    private boolean injectImpl(Object target, Field field, Object value) {
        try {
            boolean accessible = field.isAccessible();
            if(!accessible) {
                field.setAccessible(true);
            }
            field.set(target, value);
            if(!accessible) {
                field.setAccessible(false);
            }
            return true;
        }
        catch(Exception exep) {
            return false;
        }
    }
}
