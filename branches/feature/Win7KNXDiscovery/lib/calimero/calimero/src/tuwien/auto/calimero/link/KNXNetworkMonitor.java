/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package tuwien.auto.calimero.link;

import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.event.LinkListener;
import tuwien.auto.calimero.link.event.MonitorFrameEvent;
import tuwien.auto.calimero.link.medium.KNXMediumSettings;
import tuwien.auto.calimero.link.medium.RawFrame;
import tuwien.auto.calimero.log.LogManager;

/**
 * KNX network monitor link.
 * <p>
 * A KNX network monitor is a link to a KNX network to receive KNX bus monitor
 * indications. These indications are provided to users of the monitor link.
 * <p>
 * The link enables transparency of the type of connection protocol used to monitor a KNX
 * network, as well as an abstraction of the particular physical KNX medium used for
 * messaging in the KNX network.
 * <p>
 * A KNX monitor link relies on an underlying intermediate connection technology and
 * protocol (e.g. KNXnet/IP, {@link KNXnetIPConnection}) to access KNX networks, the
 * necessary access options are specified at creation of a dedicated monitor.
 * <p>
 * The name returned by {@link #getName()} is used by a link as name of its log service.
 * 
 * @author B. Malinowsky
 */
public interface KNXNetworkMonitor
{
	/**
	 * Supplies medium information necessary for KNX communication.
	 * <p>
	 * These informations are differing between KNX media and depend on the KNX network
	 * this link is communicating with.<br>
	 * The <code>settings</code> medium type has to match the medium type supplied to
	 * the link in the first place.<br>
	 * The <code>settings</code> object is not copied internally to allow subsequent
	 * changes to medium settings by the user which should take effect immediately.
	 * 
	 * @param settings medium settings to use, the expected subtype is according to the
	 *        KNX network medium
	 */
	void setKNXMedium(KNXMediumSettings settings);

	/**
	 * Returns the KNX medium settings used by this monitor link.
	 * <p>
	 * The returned object is a reference to the one used by this link (not a copy).
	 * 
	 * @return medium settings for KNX network
	 */
	KNXMediumSettings getKNXMedium();

	/**
	 * Adds the specified event listener <code>l</code> to receive events from this
	 * network monitor.
	 * <p>
	 * If <code>l</code> was already added as listener, no action is performed.
	 * 
	 * @param l the listener to add
	 */
	void addMonitorListener(LinkListener l);

	/**
	 * Removes the specified event listener <code>l</code>, so it does no longer
	 * receive events from this network monitor.
	 * <p>
	 * If <code>l</code> was not added in the first place, no action is performed.
	 * 
	 * @param l the listener to remove
	 */
	void removeMonitorListener(LinkListener l);

	/**
	 * Sets whether the monitor should decode the raw frame on medium contained in
	 * received KNX bus monitor messages.
	 * <p>
	 * A decoded raw frame is of type {@link RawFrame} and can be retrieved using
	 * {@link MonitorFrameEvent#getRawFrame()} within a link listener registered for this
	 * monitor.
	 * 
	 * @param decode <code>true</code> to enable decoding, <code>false</code> to skip
	 *        decoding
	 */
	void setDecodeRawFrames(boolean decode);

	/**
	 * Returns the name of the monitor, a short textual representation to identify a
	 * network monitor.
	 * <p>
	 * The name is unique for monitors with different remote endpoints.<br>
	 * The returned name is used by the monitor for the name of its log service. Supply
	 * {@link #getName()} for {@link LogManager#getLogService(String)} for example to get
	 * the associated log service.
	 * <p>
	 * By default, "monitor " + address/ID of the remote endpoint is returned.<br>
	 * After closing the monitor, the returned name might differ, e.g. get reset to some
	 * arbitrary default name.
	 * 
	 * @return monitor name as string
	 */
	String getName();

	/**
	 * Checks for open monitor link.
	 * <p>
	 * After a call to {@link #close()} or after the underlying protocol initiated the end
	 * of the communication, this method always returns <code>false</code>.
	 * 
	 * @return <code>true</code> if this network monitor is open, <code>false</code>
	 *         on closed
	 */
	boolean isOpen();

	/**
	 * Ends monitoring the KNX network and closes the network monitor.
	 * <p>
	 * All registered monitor listeners get notified.<br>
	 * If no communication access was established in the first place, no action is
	 * performed.
	 */
	void close();
}
