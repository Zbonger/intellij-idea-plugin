/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jboss.forge.furnace.Furnace;

import java.util.concurrent.Future;

/**
 * IntelliJ service utilities.
 *
 * @author Adam Wyłuda
 */
public class ServiceHelper
{
    private ServiceHelper()
    {
    }

    public static ForgeService getForgeService()
    {
        return ServiceManager.getService(ForgeService.class);
    }

    /**
     * Makes sure that Furnace is loaded and then executes given callback on IntelliJ UI thread.
     */
    public static void loadFurnaceAndRun(Runnable callback)
    {
        if (getForgeService().isLoaded())
        {
            runOnUIThread(callback);
        }
        else
        {
            Future<Furnace> future = getForgeService().startAsync();
            startForgeInitTask(future, callback);
        }
    }

    /**
     * Starts Forge initiation background task.
     */
    private static void startForgeInitTask(final Future<Furnace> future, final Runnable callback)
    {
        new Task.Backgroundable(null, "Starting Forge", true)
        {
            public void run(ProgressIndicator indicator)
            {
                indicator.setText("Loading Furnace");
                indicator.setFraction(0.0);
                try
                {
                    future.get();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                indicator.setFraction(1.0);

                runOnUIThread(callback);
            }
        }.setCancelText("Stop loading").queue();
    }

    /**
     * Runs provided Runnable on Swing dispatch thread.
     */
    private static void runOnUIThread(Runnable runnable)
    {
        if (ApplicationManager.getApplication().isDispatchThread())
        {
            runnable.run();
        }
        else
        {
            ApplicationManager.getApplication().invokeLater(runnable);
        }
    }
}