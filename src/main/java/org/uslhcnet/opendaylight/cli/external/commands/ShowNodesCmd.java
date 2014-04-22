package org.uslhcnet.opendaylight.cli.external.commands;

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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opendaylight.controller.sal.core.Description;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.TimeStamp;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.uslhcnet.opendaylight.cli.ICliCommand;
import org.uslhcnet.opendaylight.cli.ICliConsole;
import org.uslhcnet.opendaylight.cli.utils.StringTable;

/**
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class ShowNodesCmd implements ICliCommand {
    /** The command string. */
    private final String commandString = "show nodes";
    /** The command's arguments. */
    private final String arguments = null;
    /** The command's help text. */
    private final String help = "Shows the network nodes connected to the controller.";

    /** Required ODL bundle: SwitchManager (only for the show command). */
    private ISwitchManager switchManager;

    /**
     * Default Constructor.
     *
     * @param sm
     *            The switch manager.
     */
    public ShowNodesCmd(ISwitchManager sm) {
        this.switchManager = sm;
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
        result.append(this.nodesToTable(this.switchManager.getNodes()));

        // Return.
        return result.toString();
    }

    /**
     *
     * @param nodes
     * @return
     */
    private String nodesToTable(Set<Node> nodes) {
        /* The string table that contains all the device information as strings. */
        StringTable stringTable = new StringTable().setOffset(StringTable.DEFAULT_OFFSET);

        // Generate header data.
        List<String> header = new LinkedList<String>();
        header.add("SwitchID");
        header.add("Description");
        header.add("Type");
        header.add("Connected Since");
        stringTable.setHeader(header);

        for (Node node : nodes) {
            Map<String, Property> nodeProps = this.switchManager.getNodeProps(node);
            // Create a StringTable row.
            List<String> row = new LinkedList<String>();
            // Add columns to row.
            row.add(node.getNodeIDString());
            if (nodeProps.containsKey(Description.propertyName)) {
                row.add(nodeProps.get(Description.propertyName).getStringValue());
            } else {
                row.add("");
            }
            row.add(node.getType());
            if (nodeProps.containsKey(TimeStamp.TimeStampPropName)) {
                row.add(nodeProps.get(TimeStamp.TimeStampPropName).getStringValue().substring(16));
            } else {
                row.add("");
            }
            stringTable.addRow(row);
        }

        // Return string table as a string.
        return stringTable.toString();
    }
}
