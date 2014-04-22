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

import jline.TerminalSupport;

import org.apache.sshd.server.Environment;

/**
 * Provides an SSH terminal for Jline.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class SshTerminal extends TerminalSupport {
    /** The SSH server environment. */
    private Environment environment;

    /**
     * Constructor.
     *
     * @param environment
     */
    public SshTerminal(Environment environment) {
        super(true);
        setAnsiSupported(true);
        this.environment = environment;
    }

    @Override
    public void init() throws Exception {
        // Do nothing.
    }

    @Override
    public void restore() throws Exception {
        // Do nothing
    }

    @Override
    public int getWidth() {
        int width = 0;
        try {
            width = Integer.valueOf(this.environment.getEnv().get(Environment.ENV_COLUMNS));
        } catch (Throwable t) {
            // Ignore
        }
        return width > 0 ? width : super.getWidth();
    }

    @Override
    public int getHeight() {
        int height = 0;
        try {
            height = Integer.valueOf(this.environment.getEnv().get(Environment.ENV_LINES));
        } catch (Throwable t) {
            // Ignore
        }
        return height > 0 ? height : super.getHeight();
    }

}
