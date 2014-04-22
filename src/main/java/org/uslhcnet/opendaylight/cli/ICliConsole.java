package org.uslhcnet.opendaylight.cli;

import java.io.IOException;
import java.util.Collection;

import jline.console.completer.Completer;

/**
 * Console interface.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public interface ICliConsole {

    /**
     * Sets the prompt of the current console. Thus, we can adapt the console
     * prompt to specific commands.
     */
    public abstract void setPrompt(String prompt);

    /**
     * Gets the prompt of the current console.
     */
    public abstract String getPrompt();

    /**
     * Gets all the completer attached to the current console.
     */
    public abstract Collection<Completer> getCompleters();

    /**
     * Writes a string to the console prompt.
     *
     * @param string
     *            String to write to the console prompt.
     * @throws IOException
     */
    public abstract void write(String string) throws IOException;

    /**
     * Stops and exits the console.
     */
    public void stop();
}
