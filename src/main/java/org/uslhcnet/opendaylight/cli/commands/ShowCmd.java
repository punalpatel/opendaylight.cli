package org.uslhcnet.opendaylight.cli.commands;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jline.console.completer.Completer;

import org.uslhcnet.opendaylight.cli.ICliCommand;
import org.uslhcnet.opendaylight.cli.ICliConsole;

/**
 * The show command is used to present the completer of "show", i.e. all
 * commands that start with a "show" string.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class ShowCmd implements ICliCommand {
    /** The command string. */
    private final String commandString = "show";
    /** The command's arguments. */
    private final String arguments = null;
    /** The command's help text. */
    private final String help = null;

    @Override
    public String getCommandString() {
        return commandString;
    }

    @Override
    public String getArguments() {
        return arguments;
    }

    @Override
    public String getHelpText() {
        return help;
    }

    @Override
    public String execute(ICliConsole console, String arguments) {
        /* The String builder that hold the resulting string. */
        StringBuilder result = new StringBuilder();
        /* A list of command completion candidates. */
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        /*
         * Since Completer.complete needs a blank at the end of the command, we
         * need to add one.
         */
        String line = this.commandString + " ";

        // Find possible command completions.
        for (Completer comp : console.getCompleters()) {
            if (comp.complete(line, line.length(), candidates) == -1) {
                break;
            }
        }

        // Make sure the list is unique.
        candidates = new ArrayList<CharSequence>(new HashSet<CharSequence>(candidates));

        // Create the result string.
        result.append("Command not found. Use:");
        for (CharSequence candidate : candidates) {
            result.append("\n  show " + candidate);
        }

        // Return.
        return result.toString();
    }
}
