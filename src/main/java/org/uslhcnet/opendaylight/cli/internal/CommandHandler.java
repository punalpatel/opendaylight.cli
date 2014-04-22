package org.uslhcnet.opendaylight.cli.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uslhcnet.opendaylight.cli.ICliCommand;

/**
 * The command handler stores all commands.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class CommandHandler {
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);
    /** The unique command hander that executes all console commands. */
    private static CommandHandler commander;

    /** Map of all commands handled by the command handler. */
    private ConcurrentMap<String, ICliCommand> commands = new ConcurrentHashMap<String, ICliCommand>();

    /**
     * Provides access to the singleton instance of the command handler.
     *
     * @return instance of the command handler.
     */
    public static synchronized CommandHandler getInstance() {
        if (commander == null) {
            commander = new CommandHandler();
        }
        return commander;
    }

    /**
     * Adds a new command to the command handler.
     *
     * @param command
     *            The new command that is added.
     */
    public void addCommand(ICliCommand command) {
        LOGGER.trace("Add command {}", command.getCommandString());
        this.commands.put(command.getCommandString().trim().toLowerCase(), command);
    }

    /**
     * Removes a command from the command handler.
     *
     * @param command
     *            The command that is removed.
     */
    public void removeCommand(ICliCommand command) {
        LOGGER.trace("Remove command {}", command.getCommandString());
        this.commands.remove(command.getCommandString().trim().toLowerCase());
    }

    /**
     * Gets a command form the command handler.
     *
     * @param commandString
     *            The string that identifies the command.
     * @return <b>ICliCommand</b> The command.
     */
    public ICliCommand getCommand(String commandString) {
        return this.commands.get(commandString.trim().toLowerCase());
    }

    /**
     * Returns all the commands registered and handled by the command handler.
     *
     * @return A collection of commands.
     */
    public Map<String, ICliCommand> getCommands() {
        return this.commands;
    }
}
