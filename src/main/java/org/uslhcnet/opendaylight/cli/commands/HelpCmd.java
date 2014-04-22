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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.uslhcnet.opendaylight.cli.ICliCommand;
import org.uslhcnet.opendaylight.cli.ICliConsole;
import org.uslhcnet.opendaylight.cli.internal.CommandHandler;
import org.uslhcnet.opendaylight.cli.utils.StringTable;

/**
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class HelpCmd implements ICliCommand {
    /** States whether the table should have a separator or not. */
    public static final boolean TABLE_SEPARATOR = false;

    /** The command string. */
    private final String commandString = "help";
    /** The command's arguments. */
    private final String arguments = null;
    /** The command's help text. */
    private final String help = "Prints this help.";
    /** The command hander that handles all console commands. */
    private CommandHandler commander;

    /**
     * Default constructor.
     */
    public HelpCmd() {
        this.commander = CommandHandler.getInstance();
    }

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

        result.append("\n");
        result.append(this.commandsToTable(this.commander.getCommands().values(), arguments));

        // Return.
        return result.toString();
    }

    /**
     * Creates a table of commands and their help text.
     *
     * @param commands
     *            The commands to print.
     * @return <b>String</b> A string representing the table.
     */
    private String commandsToTable(Collection<ICliCommand> commands, String arguments) {
        /* The string table that contains all the device information as strings. */
        StringTable stringTable = new StringTable(TABLE_SEPARATOR)
                .setOffset(StringTable.DEFAULT_OFFSET);

        for (ICliCommand command : commands) {
            if (arguments != null && !arguments.equals("")
                    && !command.getCommandString().equalsIgnoreCase(arguments)) {
                continue;
            }
            if (command.getHelpText() != null && !command.getHelpText().equals("")) {
                // Create a StringTable row.
                List<String> row = new LinkedList<String>();
                // Add columns to row.
                row.add(command.getCommandString() + " ");
                row.add(command.getHelpText());
                // Add row to table.
                stringTable.addRow(row);
            }
        }

        // Return string table as a string.
        return stringTable.toString();
    }
}
