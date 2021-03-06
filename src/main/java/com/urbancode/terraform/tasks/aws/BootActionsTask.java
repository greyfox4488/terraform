/*******************************************************************************
 * Copyright 2012 Urbancode, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.urbancode.terraform.tasks.aws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.urbancode.x2o.tasks.SubTask;


public class BootActionsTask extends SubTask {

    //**********************************************************************************************
    // CLASS
    //**********************************************************************************************
    final static private Logger log = Logger.getLogger(BootActionsTask.class);

    //**********************************************************************************************
    // INSTANCE
    //**********************************************************************************************

    private ContextAWS context;

    private String platform;

    private String shell;
    private String userData;
    private List<BootActionSubTask> actions;

    //----------------------------------------------------------------------------------------------
    public BootActionsTask(ContextAWS context) {
        super(context);
        this.context = context;
        actions = new ArrayList<BootActionSubTask>();
    }

    //----------------------------------------------------------------------------------------------
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    //----------------------------------------------------------------------------------------------
    public void setShell(String shell) {
        this.shell = shell;
    }

    //----------------------------------------------------------------------------------------------
    public void setUserData(String userData) {
        this.userData = userData;
    }

    //----------------------------------------------------------------------------------------------
    public String getUserData() {
        return userData;
    }

    //----------------------------------------------------------------------------------------------
    public List<BootActionSubTask> getScript() {
        return Collections.unmodifiableList(actions);
    }

    //----------------------------------------------------------------------------------------------
    public String getShell() {
        return shell;
    }

    //----------------------------------------------------------------------------------------------
    public ScriptTask createScript() {
        BootActionSubTask scriptTask = new ScriptTask(context);
        actions.add(scriptTask);
        return (ScriptTask)scriptTask;
    }

    //----------------------------------------------------------------------------------------------
    public BootActionSubTask createPuppet() {
        BootActionSubTask puppet = new PuppetTask(context);
        actions.add(puppet);
        return puppet;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void create() {
        String userData = "";

        if (platform.equalsIgnoreCase("windows")) {
            // windows boot actions are not supported
        }
        else {
            userData += "#!" + shell + " \n\n";  // #! is REQUIRED to be first characters)
        }
        setUserData(userData);
        if (getScript() != null) {
            for (BootActionSubTask task : getScript()) {
                task.create();
                setUserData(getUserData() + task.getCmds());
            }
            setUserData(context.resolve(getUserData()));
        }
        log.info("user-data: \n\n" + getUserData());
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void destroy() {
        setUserData(null);
        //already disconnected
    }

}
