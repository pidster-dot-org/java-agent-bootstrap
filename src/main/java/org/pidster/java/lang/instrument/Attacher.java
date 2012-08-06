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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * @author <a href="http://pidster.org/">pidster</a>
 *
 */
public class Attacher {

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

        VirtualMachine machine = null;

        try {
            machine = VirtualMachine.attach(pid);
            int index = agent.indexOf(".jar=");
            if (index > -1) {
                String iagent = agent.substring(0, index + 4);
                String props = agent.substring(index + 5);

                agent = normalize(iagent);

                LOG.log(Level.INFO, "Agent: " + agent);
                LOG.log(Level.INFO, "Props: " + props);

                validate(props);

                machine.loadAgent(agent, props);
            }
            else {
                agent = normalize(agent);
                machine.loadAgent(agent);
            }

            machine.getSystemProperties().setProperty("agent.user.dir", System.getProperty("user.dir"));

        } catch (AttachNotSupportedException e) {
            LOG.log(Level.SEVERE, "Unable to load agent: " + agent, e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to load agent: " + agent, e);
        } catch (AgentLoadException e) {
            LOG.log(Level.SEVERE, "Unable to load agent: " + agent, e);
        } catch (AgentInitializationException e) {
            LOG.log(Level.SEVERE, "Unable to load agent: " + agent, e);
        }
        finally {
            if (machine != null)
                try {
                    machine.detach();
                } catch (IOException e) {
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
