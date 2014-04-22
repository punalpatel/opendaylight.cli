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

import org.apache.sshd.ClientSession;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.session.AbstractSession;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.usermanager.IUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uslhcnet.opendaylight.cli.ICliCommand;
import org.uslhcnet.opendaylight.cli.ICliService;
import org.uslhcnet.opendaylight.cli.commands.ExitCmd;
import org.uslhcnet.opendaylight.cli.commands.HelpCmd;
import org.uslhcnet.opendaylight.cli.commands.ShowCmd;
import org.uslhcnet.opendaylight.cli.external.commands.ShowNodesCmd;

/**
 * CLI service implementation.
 *
 * @author Michael Bredel <michael.bredel@cern.ch>
 */
public class CliImpl implements ICliService {
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CliImpl.class);
    /** Default SSH host key location. */
    public static final String DEFAULT_HOSTKEY = "./configuration/ssh_host_dsa_key.pub";
    /** Default SSH host key algorithm */
    public static final String DEFAULT_ALGORITHM = "DSA";

    /** Required ODL bundle: UserManager. */
    private IUserManager userManager;
    /** Required ODL bundle: SwitchManager (only for the show command). */
    private ISwitchManager switchManager;

    /** The SSH-Server instance. */
    private SshServer sshd;
    /** The command hander that handles all console commands. */
    private CommandHandler commander;

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     */
    void init() {
        LOGGER.trace(this.getClass().getName() + ".init()");
        this.commander = CommandHandler.getInstance();
        this.sshd = SshServer.setUpDefaultServer();

        // Register some standard commands.
        this.commander.addCommand(new ExitCmd());
        this.commander.addCommand(new HelpCmd());
        this.commander.addCommand(new ShowCmd());

        // Register some external commands (just for testing).
        this.commander.addCommand(new ShowNodesCmd(this.switchManager));
    }

    /**
     * Function called by dependency manager after "init()" is called and after
     * the services provided by the class are registered in the service registry
     */
    void start() {
        LOGGER.trace(this.getClass().getName() + ".start()");

        this.sshd.setPort(55220);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(DEFAULT_HOSTKEY,
                DEFAULT_ALGORITHM));
        sshd.setPasswordAuthenticator(new UserManagerPasswordAuthenticator(this.userManager));
        // sshd.setPasswordAuthenticator(new SimplePasswordAuthenticator("root",
        // "password"));
        sshd.setShellFactory(new CliShellFactory());
        try {
            sshd.start();
            LOGGER.debug("Starting application-internal SSH server on port {}", sshd.getPort());
        } catch (IOException e) {
            LOGGER.error("Starting application-internal SSH server on port {} failed",
                    sshd.getPort());
        }
    }

    /**
     * Function called by the dependency manager before the services exported by
     * the component are unregistered, this will be followed by a "destroy()"
     * calls
     */
    void stop() {
        LOGGER.trace(this.getClass().getName() + ".stop()");

        // Stop all sessions.
        for (AbstractSession session : sshd.getActiveSessions()) {
            try {
                session.disconnect(ClientSession.CLOSED, "SSH-Server is shutting down.");
            } catch (IOException e) {
                LOGGER.error("Disconnecting Session {} failed.", session);
            }
        }
        // Stop SSH-Server
        try {
            sshd.stop(true);
        } catch (InterruptedException e) {
            LOGGER.error("Stopping the SSH-Server failed.");
        }
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     */
    void destroy() {
        LOGGER.trace(this.getClass().getName() + ".destroy()");
    }

    /**
     * Set the user manager.
     *
     * @param userManager
     *            The user manger.
     */
    void setUserManager(IUserManager userManager) {
        LOGGER.debug("Setting UserManager.");
        this.userManager = userManager;
    }

    /**
     * Remove the user manager.
     *
     * @param userManager
     *            The user manger.
     */
    void unsetUserManager(IUserManager userManager) {
        if (this.userManager == userManager) {
            LOGGER.debug("UNSetting UserManager.");
            this.userManager = null;
        }
    }

    /**
     * Set the switch manager.
     *
     * @param switchManager
     *            The switch manger.
     */
    void setSwitchManager(ISwitchManager switchManager) {
        LOGGER.debug("Setting SwitchManager.");
        this.switchManager = switchManager;
    }

    /**
     * Remove the switch manager.
     *
     * @param switchManager
     *            The switch manger.
     */
    void unsetSwitchManager(ISwitchManager switchManager) {
        if (this.switchManager == switchManager) {
            LOGGER.debug("UNSetting SwitchManager.");
            this.switchManager = null;
        }
    }

    //
    // ICliService
    //

    @Override
    public void registerCommand(ICliCommand command) {
        this.commander.addCommand(command);
    }

    @Override
    public void unregisterCommand(ICliCommand command) {
        this.commander.removeCommand(command);
    }

}
