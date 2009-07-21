/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openremote.modeler.client.proxy;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.google.gwt.core.client.GWT;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.DeviceMacroRPCServiceAsync;
import org.openremote.modeler.domain.DeviceMacro;

import java.util.List;

public class DeviceMacroBeanModelProxy {
    private final static BeanModelFactory deviceMacroBeanModelFactory = BeanModelLookup.get().getFactory(DeviceMacro.class);
    public static DeviceMacroRPCServiceAsync deviceMacroServiceAsync = GWT.create(DeviceMacro.class);

    public static void loadDeviceMaro(BeanModel beanModel, final AsyncSuccessCallback<List<BeanModel>> callback) {
        if (beanModel == null) {
            deviceMacroServiceAsync.loadAll(new AsyncSuccessCallback<List<DeviceMacro>>() {

                public void onSuccess(List<DeviceMacro> result) {

                    callback.onSuccess(deviceMacroBeanModelFactory.createModel(result));
                }
            });
        } else {
            
        }

    }
}
