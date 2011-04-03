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

import tuwien.auto.calimero.KNXAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.KNXMediumSettings;
import tuwien.auto.calimero.log.LogManager;

/**
 * KNX network link interface to communicate with destinations in a KNX network.
 * <p>
 * A network link enables transparency of the type of connection protocol used to access a
 * KNX network, as well as an abstraction of the particular physical KNX medium used for
 * communication in the KNX network (e.g. TP1).
 * <p>
 * The link provides two forms of information exchange for KNX messages, one is to
 * directly supply necessary information like KNX address, message priority and NSDU, the
 * other to use cEMI as container format.<br>
 * On send, message parts not present or supplied which are necessary for communication
 * will be added using the information provided by
 * {@link KNXNetworkLink#setKNXMedium(KNXMediumSettings)}.
 * <p>
 * A KNX network link relies on an underlying intermediate connection technology and
 * protocol (e.g. IP and KNXnet/IP, {@link KNXnetIPConnection}) to access KNX networks,
 * the necessary access options are specified at creation of a dedicated network link.
 * <p>
 * The name returned by {@link #getName()} is used by a link as name of its log service.
 * 
 * @author B. Malinowsky
 */
public interface KNXNetworkLink
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
	 * Returns the KNX medium settings used by this network link.
	 * <p>
	 * The returned object is a reference to the one used by this link (not a copy).
	 * 
	 * @return medium settings for KNX network
	 */
	KNXMediumSettings getKNXMedium();

	/**
	 * Adds the specified event listener <code>l</code> to receive events from this
	 * link.
	 * <p>
	 * If <code>l</code> was already added as listener, no action is performed.
	 * 
	 * @param l the listener to add
	 */
	void addLinkListener(NetworkLinkListener l);

	/**
	 * Removes the specified event listener <code>l</code>, so it does no longer
	 * receive events from this link.
	 * <p>
	 * If <code>l</code> was not added in the first place, no action is performed.
	 * 
	 * @param l the listener to remove
	 */
	void removeLinkListener(NetworkLinkListener l);

	/**
	 * Sets the hop count used as default in KNX messages.
	 * <p>
	 * It denotes how many sub networks a message is allowed to travel.<br>
	 * A message its hop count is decremented by KNX routers to limit distance and avoid
	 * looping. On hop count value 0, the message is discarded from the network. A hop
	 * count of 7 never gets decremented.<br>
	 * By default, a hop count of 6 is specified.
	 * <p>
	 * 
	 * @param count hop count value, 0 &lt;= value &lt;= 7
	 */
	void setHopCount(int count);

	/**
	 * Returns the hop count used as default for KNX messages.
	 * <p>
	 * 
	 * @return hop count as 3 Bit unsigned value with the range 0 to 7
	 * @see #setHopCount(int)
	 */
	byte getHopCount();

	/**
	 * Sends a link layer request message to the given destination.
	 * <p>
	 * Depending on the address, the request is either point-to-point, multicast or
	 * broadcast. A network link implementation is allowed to interpret a <code>dst</code>
	 * parameter of <code>null</code> as system broadcast, or otherwise uses its default
	 * broadcast behavior.
	 * 
	 * @param dst KNX destination address, or <code>null</code>
	 * @param p priority this KNX message is assigned to
	 * @param nsdu network layer service data unit
	 * @throws KNXTimeoutException on a timeout during send (for example when waiting on
	 *         acknowledge using a reliable sending protocol)
	 * @throws KNXLinkClosedException if the link is closed
	 */
	void sendRequest(KNXAddress dst, Priority p, byte[] nsdu) throws KNXTimeoutException,
		KNXLinkClosedException;

	/**
	 * Sends a link layer request message to the given destination, and waits for the
	 * corresponding link layer confirmation.
	 * <p>
	 * Depending on the address, the request is either point-to-point, multicast or
	 * broadcast. A network link implementation is allowed to interpret a <code>dst</code>
	 * parameter of <code>null</code> as system broadcast, or otherwise uses its default
	 * broadcast behavior.
	 * 
	 * @param dst KNX destination address, or <code>null</code>
	 * @param p priority this message is assigned to
	 * @param nsdu network layer service data unit
	 * @throws KNXTimeoutException on a timeout during send or while waiting for the
	 *         confirmation
	 * @throws KNXLinkClosedException if the link is closed
	 */
	void sendRequestWait(KNXAddress dst, Priority p, byte[] nsdu)
		throws KNXTimeoutException, KNXLinkClosedException;

	/**
	 * Sends a KNX link layer message supplied as type cEMI L-data.
	 * <p>
	 * If the source address of <code>msg</code> is 0.0.0, the device address supplied
	 * in the medium settings is used as message source address.
	 * 
	 * @param msg cEMI L-data message to send
	 * @param waitForCon <code>true</code> to wait for link layer confirmation response,
	 *        <code>false</code> to not wait for the confirmation
	 * @throws KNXTimeoutException on a timeout during send (for example when waiting on
	 *         acknowledge using a reliable sending protocol)
	 * @throws KNXLinkClosedException if the link is closed
	 */
	void send(CEMILData msg, boolean waitForCon) throws KNXTimeoutException,
		KNXLinkClosedException;

	/**
	 * Returns the name of the link, a short textual representation to identify a link.
	 * <p>
	 * The name is unique for links with different remote endpoints.<br>
	 * The returned name is used by this link for the name of its log service. Supply
	 * {@link #getName()} for {@link LogManager#getLogService(String)} for example to get
	 * the log service of this link.
	 * <p>
	 * By default, "link " + address/ID of the remote endpoint is returned (e.g. "Link
	 * 192.168.0.10:3671" for an IP link).<br>
	 * After closing the link, the returned name might differ, e.g. get reset to some
	 * arbitrary default name.
	 * 
	 * @return link name as string
	 */
	String getName();

	/**
	 * Checks for open network link.
	 * <p>
	 * After a call to {@link #close()} or after the underlying protocol initiated the end
	 * of the communication, this method always returns <code>false</code>.
	 * 
	 * @return <code>true</code> if this network link is open, <code>false</code> on
	 *         closed
	 */
	boolean isOpen();

	/**
	 * Ends communication with the KNX network and closes the network link.
	 * <p>
	 * All registered link listeners get notified.<br>
	 * If no communication access was established in the first place, no action is
	 * performed.
	 */
	void close();
}
