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

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.felix.dm.Component;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.usermanager.IUserManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLI Bundle Activator.
 *
 * @author Michael Bredel <michael.bredel@caltech.edu>
 */
public class Activator extends ComponentActivatorAbstractBase {
    /** The Logger. */
    private static Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) {
        super.start(context);
        LOGGER.info("startCliImpl() passing");
    }

    @Override
    public void stop(BundleContext context) {
        super.stop(context);
        LOGGER.info("stopCliImpl() passing");
    }

    @Override
    protected void configureGlobalInstance(Component c, Object imp) {
        if (imp.equals(CliImpl.class)) {
            // Export the service.
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("salListenerName", "cliasdf");
            Set<String> propSet = new HashSet<String>();
            props.put("cachenames", propSet);

            // Set the interface implemented by this class.
            c.setInterface(new String[] { CliImpl.class.getName(), }, props);

            // Add the plugin dependencies.
            c.add(createServiceDependency().setService(IUserManager.class)
                    .setCallbacks("setUserManager", "unsetUserManager").setRequired(true));

            c.add(createServiceDependency().setService(ISwitchManager.class)
                    .setCallbacks("setSwitchManager", "unsetSwitchManager").setRequired(true));
        }
    }

    @Override
    protected Object[] getGlobalImplementations() {
        final Object[] res = { CliImpl.class };
        return res;
    }
}
