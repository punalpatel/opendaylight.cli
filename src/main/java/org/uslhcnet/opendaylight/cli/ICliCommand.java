package org.uslhcnet.opendaylight.cli;

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

/**
 * The command interface that needs to be implemented by every command to be
 * executed by the command handler.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public interface ICliCommand {

    /**
     * Gets the command string.
     *
     * @return The command as a string.
     */
    public String getCommandString();

    /**
     * Gets the command argument string.
     *
     * @return The command's argument as a string.
     */
    public String getArguments();

    /**
     * Gets the command help string
     *
     * @return The command's help text as a string.
     */
    public String getHelpText();

    /**
     * Executes the command and performs its actual action.
     *
     * @param console
     *            The console where the command was initialized.
     * @param arguments
     *            The command's argument.
     * @return A string that might be returned by the command execution.
     */
    public String execute(ICliConsole console, String arguments);

}
