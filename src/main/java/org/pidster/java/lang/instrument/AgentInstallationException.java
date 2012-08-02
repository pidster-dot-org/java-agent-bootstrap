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

/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class AgentInstallationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public AgentInstallationException(String message) {
        super(message);
    }

    /**
     * @param thrown
     */
    public AgentInstallationException(Throwable thrown) {
        super(thrown);
    }

    /**
     * @param message
     * @param thrown
     */
    public AgentInstallationException(String message, Throwable thrown) {
        super(message, thrown);
    }

}
