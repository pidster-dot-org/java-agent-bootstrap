/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pidster.java.lang.instrument;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="http://pidster.org/">pidster</a>
 *
 */
public class Agent {

    private static final String AGENT_PARAM_LIBS = "libs";

    private static final String AGENT_PARAM_THREAD = "thread";

    private static final Logger LOG = Logger.getLogger(Agent.class.getName());

    /**
     * @param agentArgs
     * @param instrumentation
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        internal(agentArgs, instrumentation, false);
    }

    /**
     * @param agentArgs
     * @param instrumentation
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        internal(agentArgs, instrumentation, true);
    }

    /**
     * @param agentArgs
     * @param instrumentation
     * @param preStarted
     */
    private static void internal(String agentArgs, Instrumentation instrumentation, boolean preStarted) {

        Map<String, String> args = new HashMap<String, String>();

        LOG.log(Level.FINE, "Parsing agentArgs:" + agentArgs);

        String[] pairs = agentArgs.split("\\,");
        for (String pair : pairs) {
            String[] nameValue = pair.split("=");
            LOG.log(Level.FINE, "Adding param:" + nameValue);
            args.put(nameValue[0], nameValue[1]);
        }

        try {
            
            String threadClass = args.get(AGENT_PARAM_THREAD);

            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            Thread t = instantiateThreadClass(args, threadClass, tccl);
            // Restore original ClassLoader
            Thread.currentThread().setContextClassLoader(tccl);

            if (t instanceof AgentBootstrap) {
                AgentBootstrap ab = (AgentBootstrap) t;
                ab.setInstrumentation(instrumentation);
                ab.setPreStarted(preStarted);
                ab.start();
            }
            else {
                t.setName("agent-bootstrap");
                t.start();
            }

            System.out.printf("Injected class %s via javaagent %s with arguments %s %n", threadClass, Agent.class.getName(), agentArgs);

        } catch (Exception e) {
            throw new AgentInstallationException(e);
        }
    }

    private static Thread instantiateThreadClass(Map<String, String> args, String threadClass, ClassLoader tccl) {

        try {
            URL[] urls = getClassLoaderURLs(args);

            ClassLoader cl = new URLClassLoader(urls, tccl);
            Thread.currentThread().setContextClassLoader(cl);

            Class<?> c = cl.loadClass(threadClass);
            Class<? extends Thread> tc = c.asSubclass(Thread.class);

            Thread t = tc.newInstance();
            t.setContextClassLoader(cl);
            // NB If this isn't a daemon, we can hold up shutdown.
            t.setDaemon(true);

            return t;

        } catch (ClassNotFoundException e) {
            throw new TargetClassInstantiationException(e);
        } catch (InstantiationException e) {
            throw new TargetClassInstantiationException(e);
        } catch (IllegalAccessException e) {
            throw new TargetClassInstantiationException(e);
        }
    }

    /**
     * @param args
     * @return URLs
     */
    private static URL[] getClassLoaderURLs(Map<String, String> args) {
        Set<URL> urlSet = new HashSet<URL>();

        if (args.containsKey(AGENT_PARAM_LIBS)) {
            String libs = args.get(AGENT_PARAM_LIBS);

            File libdir = new File(libs);
            LOG.log(Level.FINE, "Searching: " + libdir.getAbsolutePath());

            File[] files = libdir.listFiles();
            for (File file : files) {
                try {
                    LOG.log(Level.FINE, "Adding: " + file.getAbsolutePath());
                    URL url = file.toURI().toURL();
                    urlSet.add(url);
                } catch (MalformedURLException e) {
                    LOG.log(Level.SEVERE, "Unable to parse: " + file.getAbsolutePath(), e);
                }
            }
        }

        URL[] urls = new URL[urlSet.size()];
        return urlSet.toArray(urls);
    }

}
