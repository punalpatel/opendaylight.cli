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
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.Terminal;
import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;

import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uslhcnet.opendaylight.cli.ICliCommand;
import org.uslhcnet.opendaylight.cli.ICliConsole;
import org.uslhcnet.opendaylight.cli.utils.LfToCrLfFilterOutputStream;

/**
 * The console abstracts the Jline console reader. It reads and writes from and
 * to the command line, handles the command completer, and executes commands.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class CliConsole implements ICliConsole {
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CliConsole.class);
    /** The (optional) application name for the console reader. */
    public static final String APP_NAME = "OpenDaylight";
    /** States whether the bell of the console reader is enabled or not. */
    public static final boolean BELL_ENABLED = false;
    /** States whether the history of the console reader is enabled or not. */
    public static final boolean HISTORY_ENABLED = true;
    /** The default console prompt string. */
    public static final String DEFAULT_PROMPT_STRING = "> ";

    /** The input stream as read from the command line prompt. */
    private InputStream inStream;
    /** The output stream to write to the command line prompt. */
    private PrintStream outStream;
    /** The error stream from the command line. */
    @SuppressWarnings("unused")
    private PrintStream errStream;
    /** An SSH terminal representation. */
    private Terminal terminal;
    /** The Jline console reader. */
    private ConsoleReader reader;
    /** List of command completer. */
    private List<Completer> completors = new LinkedList<Completer>();
    /** Boolean that states if the console is running. */
    private volatile boolean running;
    /** The prompt string of the command line. */
    private String prompt;
    /** The command hander that executes all console commands. */
    private CommandHandler commander;

    /**
     * Constructor.
     */
    public CliConsole(InputStream inStream, OutputStream outStream, OutputStream errStream,
            String encoding, Environment env) throws Exception {
        this.commander = CommandHandler.getInstance();
        this.inStream = inStream;
        this.outStream = new PrintStream(new LfToCrLfFilterOutputStream(outStream), true);
        this.errStream = new PrintStream(new LfToCrLfFilterOutputStream(errStream), true);
        this.terminal = new SshTerminal(env);

        // Create and configure a console reader.
        this.reader = new ConsoleReader(APP_NAME, this.inStream, this.outStream, this.terminal,
                encoding);
        this.reader.setBellEnabled(false);
        this.reader.setHistoryEnabled(true);

        // Generate completer
        this.generateCompleters();
        this.reader.addCompleter(new AggregateCompleter(completors));

        // TODO: Handle command line history in file.
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String getPrompt() {
        if (this.prompt != null)
            return this.prompt;

        return DEFAULT_PROMPT_STRING;
    }

    @Override
    public Collection<Completer> getCompleters() {
        return this.reader.getCompleters();
    }

    @Override
    public void write(String string) throws IOException {
        this.reader.getOutput().write(string + "\n");
    }

    /**
     * Runs the console. Thus, it reads the command line and calls the command
     * handler to execute the commands.
     */
    public void run() {
        /* A local command entry object that is reused. */
        Map.Entry<String, String> commandEntry;
        /* A local command object that is reused. */
        ICliCommand command;
        /* Set the running boolean to true. */
        this.running = true;
        /* The command line string. */
        String line;

        // Print some kind of welcome string.
        this.welcome();

        while (this.running) {
            try {
                // Read command line. Blocking.
                line = this.readCommandLine();

                // Parse line to get the command and its arguments.
                commandEntry = this.parseCommand(line);
                command = this.commander.getCommand(commandEntry.getKey());
                // Execute command.
                if (command != null) {
                    String result = command.execute(this, commandEntry.getValue());
                    if (result != null && !result.trim().equals(""))
                        this.write(result);
                } else if (!line.trim().equals("")) {
                    this.write("Unknown command: " + line);
                }

            } catch (IOException e) {
                LOGGER.warn("Could not read the command line. Exiting CLI.");
                this.running = false;
            }
        }
    }

    /**
     * Stops the execution of this console.
     */
    public synchronized void stop() {
        this.running = false;
    }

    /**
     * Prints a welcome message to the console.
     */
    private void welcome() {
        try {
            this.write("\n   Welcome to the OpenDaylight OpenFlow Controller CLI\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the current command line and generates a list of possible command
     * completions.
     *
     * @return The current command line string.
     * @throws IOException
     */
    private String readCommandLine() throws IOException {
        /* Line read from the CLI by the console reader. */
        String line = this.reader.readLine(getPrompt());
        /* A list of completion candidates. */
        List<CharSequence> candidates = new LinkedList<CharSequence>();

        if (line == null)
            return null;

        for (Completer comp : reader.getCompleters()) {
            if (comp.complete(line, line.length(), candidates) == -1) {
                break;
            }
            candidates = new LinkedList<CharSequence>(new HashSet<CharSequence>(candidates));

            // Completers.complete() automatically adds a space at the end
            // of a command. Thus, we have to handle this case specially.
            if (candidates.contains(line + " ")) {
                if (comp.complete(line + " ", line.length() + 1, candidates) == -1) {
                    break;
                }
                candidates = new LinkedList<CharSequence>(new HashSet<CharSequence>(candidates));
                candidates.remove(line + " ");
            }
        }

        // Return.
        return line;
    }

    /**
     * Generates the completer list by getting all available commands from the
     * command Hander, creates completer and adds them to this consoles
     * completer list.
     */
    private void generateCompleters() {
        for (ICliCommand cmd : this.commander.getCommands().values()) {
            this.addCommand(cmd);
        }
    }

    /**
     * Creates a (Jline) completer and adds a command and its arguments to it.
     *
     * @param cmd
     *            Command that is offered by the command handler.
     */
    private void addCommand(ICliCommand cmd) {
        /* List of StringCompleters for commands and arguments. */
        List<Completer> argCompletorList = new LinkedList<Completer>();
        /* Array of command strings. */
        String[] commands = (cmd.getCommandString()).split(" ");

        // Decompose command string and add strings to StringsCompleter.
        for (String command : commands) {
            argCompletorList.add(new StringsCompleter(command.trim().toLowerCase()));
        }

        // Add argument strings to StringsCompleter.
        if (cmd.getArguments() != null) {
            argCompletorList.add(new StringsCompleter(cmd.getArguments().trim().toUpperCase()));
        }

        // Add NullCompleter to terminate the completer.
        argCompletorList.add(new NullCompleter());

        // Add new ArgumentCompleter to global completer list.
        this.completors.add(new ArgumentCompleter(argCompletorList));
    }

    /**
     * Parses a command string tries to find a corresponding command. To this
     * end, it decomposes the command and it arguments.
     *
     * @param commandString
     *            String that was read from the command line.
     * @return Map.Entry with the command (key) and its arguments (value).
     */
    private Map.Entry<String, String> parseCommand(String commandString) {
        /* All (sub) string elements in a string command. */
        String[] commandElements = commandString.split(" ");
        /* New command string (without arguments). */
        String command = "";
        /* New argument string. */
        String arguments = "";

        for (int i = 0; i < commandElements.length; i++) {
            if (this.commander.getCommands()
                    .get((command + " " + commandElements[i].trim()).trim()) != null) {
                command = (command + " " + commandElements[i].trim()).trim();
            } else {
                arguments = (arguments + " " + commandElements[i].trim()).trim();
            }

        }

        // Return an entry with whatever we found (might be null).
        return new AbstractMap.SimpleEntry<String, String>(command, arguments);
    }

}
