/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.broadlink.handler;

import org.eclipse.smarthome.core.thing.Thing;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;

import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Smart power socket handler
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */
public class BroadlinkSocketModel3Handler extends BroadlinkSocketModel2Handler {
    private final Logger logger = LoggerFactory.getLogger(BroadlinkSocketModel3Handler.class);

    public BroadlinkSocketModel3Handler(Thing thing) {
        super(thing);
    }
        
    public void handleCommand(ChannelUID channelUID, Command command) {
    	byte statusPayload[] = getStatusPayloadFromDevice();
        if (channelUID.getId().equals("powerOn")) {
        	int nightLightState = 0;
        	if (deriveNightLightStateFromStatusPayload(statusPayload) == OnOffType.ON) {
        		nightLightState = 2;
        	}
            if (command == OnOffType.ON) {
            	setStatusOnDevice(1+nightLightState);
            } else if (command == OnOffType.OFF) {
            	setStatusOnDevice(0+nightLightState);
            }
        }
        else if (channelUID.getId().equals("nightLight")) {
        	int powerOnState = 0;
        	if (derivePowerOnStateFromStatusPayload(statusPayload) == OnOffType.ON) {
        		powerOnState = 1;
        	}
        	if (command == OnOffType.ON) {
        		setStatusOnDevice(2+powerOnState);
        	} else if (command == OnOffType.OFF) {
        		setStatusOnDevice(0+powerOnState);
        	}
        }
    }

    private void updateDeviceStateFromStatusPayload(byte[] statusPayload) {
		updateState("powerOn", derivePowerOnStateFromStatusPayload(statusPayload));	
		updateState("nightLight", deriveNightLightStateFromStatusPayload(statusPayload));
    }
       
    protected OnOffType deriveNightLightStateFromStatusPayload(byte[] statusPayload) {
		// Credit to the Python Broadlink implementation for this:
		// https://github.com/mjg59/python-broadlink/blob/master/broadlink/__init__.py
    	byte powerByte = statusPayload[4];
    	if (powerByte == 2 || powerByte == 3 || powerByte == 0xFF) {
    		return OnOffType.ON;
    	}
    	return OnOffType.OFF;
    }
}
