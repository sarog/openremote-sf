/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.ictprotege;

import java.lang.reflect.Method;
import org.jdom.Element;
import org.openremote.controller.ICTProtegeConfiguration;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author Adam Mcnabb
 */
public class MockICTProtegeConfigPolicy implements PowerMockPolicy {

    @Override
    public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
        settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(ProtegeCommandBuilder.class.getName());
    }

    @Override
    public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
        final Method loadConfigurationMethod = Whitebox.getMethod(ProtegeCommandBuilder.class, "loadConfiguration");
        final ICTProtegeConfiguration ictProtegeConfiguration = new ICTProtegeConfiguration();
        settings.addSubtituteReturnValue(loadConfigurationMethod, ictProtegeConfiguration);
    }
    
    
    
}
