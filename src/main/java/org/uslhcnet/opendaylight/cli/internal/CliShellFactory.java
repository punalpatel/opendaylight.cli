package org.uslhcnet.opendaylight.cli.internal;

/*
 * Copyright (c) 2014, California Institute of Technology
 * ALL RIGHTS RESERVED.
 * Based on Government Sponsored Research DE-SC0007346
 * Author Michael Bredel <michael.bredel@cern.ch>
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Neither the name of the California Institute of Technology
 * (Caltech) nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior
 * written permission.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

/**
 * A {@link Factory} of {@link Command} that will create a new shell process and
 * bridge the streams.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class CliShellFactory implements Factory<Command> {

    @Override
    public Command create() {
        return new CliShell();
    }

    /**
     * The OpenDaylight shell that is created (as a thread) whenever a new
     * connection to the SSH-Server is established.
     *
     * @author Michael Bredel <michael.bredel@cern.ch>
     */
    public static class CliShell implements Command, Runnable {
        /** The name of the shell thread. */
        public static final String SHELL_THREAD_NAME = "OpenDaylightShell";

        /** The input stream as read from the shell's command line prompt. */
        private InputStream inStream;
        /** The output stream to write to the shell's command line prompt. */
        private OutputStream outStream;
        /** The error stream from the shell's command line. */
        private OutputStream errStream;
        /** Environment to get some user data, like the console encoding, from. */
        private Environment environment;
        /** The callback function that is executed when the shell is terminated. */
        private ExitCallback callback;
        /** The thread that runs this shell. */
        private Thread thread;
        /** The console that handles inputs, outputs, and command execution. */
        private CliConsole console;

        @Override
        public synchronized void destroy() {
            this.console.stop();
            thread.interrupt();
        }

        @Override
        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        @Override
        public void setInputStream(InputStream inStream) {
            this.inStream = inStream;
        }

        @Override
        public void setOutputStream(OutputStream outStream) {
            this.outStream = outStream;
        }

        @Override
        public void setErrorStream(OutputStream errStream) {
            this.errStream = errStream;
        }

        @Override
        public void start(Environment environment) throws IOException {
            this.environment = environment;
            this.thread = new Thread(this, SHELL_THREAD_NAME);
            this.thread.start();
        }

        @Override
        public void run() {
            String encoding = this.environment.getEnv().get("LC_CTYPE");
            if (encoding != null && encoding.indexOf('.') > 0) {
                encoding = encoding.substring(encoding.indexOf('.') + 1);
            }

            try {
                // Create a new console that actually handles the user inputs.
                this.console = new CliConsole(inStream, outStream, errStream, encoding, environment);
                // Because the shell runs as a thread, this is a blocking call.
                this.console.run();
            } catch (Exception e) {
                this.console.stop();
            } finally {
                // End this thread.
                if (this.callback != null)
                    this.callback.onExit(0);
            }
        }
    }

}
