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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="http://pidster.org/">pidster</a>
 *
 */
public class Attacher {

    private static final String COM_SUN_TOOLS_ATTACH_VIRTUAL_MACHINE = "com.sun.tools.attach.VirtualMachine";

    private static final Logger LOG = Logger.getLogger(Attacher.class.getName());

    public static void main(String[] args) {

        if (args == null || args.length != 2) {
            usage();
            return;
        }

        String pid = args[0];
        String javaAgentProps = args[1];

        if (!args[1].startsWith("-javaagent=")) {
            LOG.log(Level.SEVERE, "Parameter formatting error: " + javaAgentProps);
            usage();
            return;
        }

        String agent = javaAgentProps.replaceFirst("-javaagent=", "");

        LOG.log(Level.INFO, "user.dir: " + System.getProperty("user.dir"));
        LOG.log(Level.INFO, "PID: " + pid);
        LOG.log(Level.INFO, "Agent+props: " + javaAgentProps);

        Object machine = null;

        try {
            machine = Reflector.invokeStatic(COM_SUN_TOOLS_ATTACH_VIRTUAL_MACHINE, "attach", pid);
            int index = agent.indexOf(".jar=");
            if (index > -1) {
                String iagent = agent.substring(0, index + 4);
                String props = agent.substring(index + 5);

                agent = normalize(iagent);

                LOG.log(Level.INFO, "Agent: " + agent);
                LOG.log(Level.INFO, "Props: " + props);

                validate(props);

                Reflector.invoke(machine, "loadAgent", agent, props);
            }
            else {
                agent = normalize(agent);
                Reflector.invoke(machine, "loadAgent", agent);
            }

            Properties systemProperties = Reflector.invoke(machine, "getSystemProperties");
            systemProperties.setProperty("agent.user.dir", System.getProperty("user.dir"));

        } catch (ReflectorException e) {
            LOG.log(Level.SEVERE, "Unable to load agent: " + agent, e);
        }
        finally {
            if (machine != null)
                try {
                    Reflector.invoke(machine, "detach");
                } catch (ReflectorException e) {
                    LOG.log(Level.WARNING, "Exception when detaching from VirtualMachine", e);
                }
        }
    }

    private static void validate(String props) {
        // TODO Auto-generated method stub
    }

    private static String normalize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            throw new AgentInstallationException("Supplied path does not exist: " + path);
        }
        return f.getAbsolutePath();
    }

    private static void usage() {
        System.out.println("Usage: inject <pid> -javaagent:<jarpath>[=options]");
    }

}
