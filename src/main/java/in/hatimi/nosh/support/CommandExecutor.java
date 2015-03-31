/*
 * Copyright (c) Flipkart Internet Private Limited - All Rights Reserved.
 *
 * Private and confidential
 *
 * This file cannot be copied and/or distributed, via any medium whatsoever,
 * without express permission from Flipkart Internet Private Limited. Failure to
 * comply shall be considered as unauthorized or illegal usage of this file.
 */

package in.hatimi.nosh.support;

import in.hatimi.nosh.Command;
import in.hatimi.nosh.CommandSetup;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

/**
 * @author indroneel.das
 *
 */

public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    private Class<?> cmdCls;
    private String   name;

    public boolean associateWith(Class<?> cls) {
        cmdCls = cls;
        Command cmd = cmdCls.getAnnotation(Command.class);
        name = cmd.value();
        if(StringUtils.isBlank(name)) {
            return false;
        }

        //check for executable method
        Method mthd = findMainMethod();
        if(mthd == null) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void execute(String cmdLine, String[] params, List<CommandSetup> setupList) {
        Class<?> paramCls = getMainParameterType();

        Object paramObj = null;
        if(paramCls.equals(Void.TYPE)) {
            paramObj = null;
        }
        else if(paramCls.equals(String.class)) {
            paramObj = cmdLine;
        }
        else if(paramCls.equals(String[].class)) {
            paramObj = params;
        }
        else {
            try {
                paramObj = paramCls.newInstance();
                new JCommander(paramObj, params);
            }
            catch(Exception exep) {
                LOGGER.error("unable to create parameter instance", exep);
                return;
            }
        }

        Object cmdObj = null;
        try {
            cmdObj = cmdCls.newInstance();
            if(setupList != null) {
                for(CommandSetup cs : setupList) {
                    cs.setup(cmdObj);
                }
            }
            if(paramObj == null) {
                new JCommander(cmdObj, params);
            }
        }
        catch(Exception exep) {
            LOGGER.error("unable to create/setup command", exep);
            return;
        }

        Method mainMthd = findMainMethod();
        try {
            if(paramObj == null) {
                mainMthd.invoke(cmdObj);
            }
            else {
                mainMthd.invoke(cmdObj, paramObj);
            }
        }
        catch(Exception exep) {
            LOGGER.error("execution error", exep);
            return;
        }
    }

    public void showHelp() {
        Class<?> paramCls = getMainParameterType();
        try {
            Object paramObj = paramCls.newInstance();
            new JCommander(paramObj).usage();
        }
        catch(Exception exep) {
            LOGGER.error("unable to create parameter instance", exep);
            return;
        }
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
            return method;
        }
        return null;
    }

    private Class<?> getMainParameterType() {
        Method mthd = findMainMethod();
        Class<?>[] paramTypes = mthd.getParameterTypes();
        if(paramTypes == null || paramTypes.length == 0) {
            return Void.TYPE;
        }
        return paramTypes[0];
    }
}
