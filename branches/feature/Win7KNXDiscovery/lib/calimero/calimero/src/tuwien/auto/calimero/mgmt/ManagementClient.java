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

package tuwien.auto.calimero.mgmt;

import java.util.List;

import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXInvalidResponseException;
import tuwien.auto.calimero.exception.KNXRemoteException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;

/**
 * Application layer services providing management related tasks in a KNX network for a
 * client.
 * <p>
 * 
 * @author B. Malinowsky
 */
public interface ManagementClient
{
	/**
	 * Sets the response timeout to wait for a KNX response message to arrive to complete
	 * a message exchange.
	 * <p>
	 * 
	 * @param timeout time in seconds
	 */
	void setResponseTimeout(int timeout);

	/**
	 * Returns the response timeout used when waiting for a KNX response message to
	 * arrive.
	 * <p>
	 * 
	 * @return timeout in seconds
	 */
	int getResponseTimeout();

	/**
	 * Sets the KNX message priority for KNX messages to send.
	 * <p>
	 * 
	 * @param p new priority to use
	 */
	void setPriority(Priority p);

	/**
	 * Returns the current used KNX message priority for KNX messages.
	 * <p>
	 * 
	 * @return message Priority
	 */
	Priority getPriority();

	/**
	 * Creates a new destination using the remote KNX address for management
	 * communication.
	 * <p>
	 * A management client will use the transport layer for creating the destination.
	 * 
	 * @param remote destination KNX individual address
	 * @param connectionOriented <code>true</code> for connection oriented mode,
	 *        <code>false</code> for connectionless mode
	 * @return destination representing the logical connection
	 */
	Destination createDestination(IndividualAddress remote, boolean connectionOriented);

	/**
	 * Creates a new destination using the remote KNX address and connection settings for
	 * management communication.
	 * <p>
	 * A management client will use the transport layer for creating the destination.
	 * 
	 * @param remote destination KNX individual address
	 * @param connectionOriented <code>true</code> for connection oriented mode,
	 *        <code>false</code> for connectionless mode
	 * @param keepAlive <code>true</code> to prevent a timing out of the logical
	 *        connection in connection oriented mode, <code>false</code> to use default
	 *        connection timeout
	 * @param verifyMode <code>true</code> to indicate the destination has verify mode
	 *        enabled, <code>false</code> otherwise
	 * @return destination representing the logical connection
	 */
	Destination createDestination(IndividualAddress remote, boolean connectionOriented,
		boolean keepAlive, boolean verifyMode);

	/**
	 * Modifies the individual address of a communication partner in the KNX network.
	 * <p>
	 * This service uses broadcast communication mode.<br>
	 * The communication partner is a device in programming mode.
	 * 
	 * @param newAddress new address
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 */
	void writeAddress(IndividualAddress newAddress) throws KNXTimeoutException,
		KNXLinkClosedException;

	/**
	 * Reads the individual address of a communication partner in the KNX network.
	 * <p>
	 * This service uses broadcast communication mode.<br>
	 * The communication partner is a device in programming mode. In situations necessary
	 * to know whether more than one device is in programming mode,
	 * <code>oneAddressOnly</code> is set to <code>false</code> and the device
	 * addresses are listed in the returned address array. In this case, the whole
	 * response timeout is waited for read responses. If <code>oneAddressOnly</code> is
	 * <code>true</code>, the array size of returned addresses is 1, and the method
	 * returns after receiving the first read response.
	 * 
	 * @param oneAddressOnly <code>true</code> if method should return after receiving
	 *        the first read response, <code>false</code> to wait the whole response
	 *        timeout for read responses
	 * @return array of individual addresses, in the order of reception
	 * @throws KNXTimeoutException on a timeout during send or no address response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read address errors
	 */
	IndividualAddress[] readAddress(boolean oneAddressOnly) throws KNXException;

	/**
	 * Modifies the individual address of a communication partner identified using an
	 * unique serial number in the KNX network.
	 * <p>
	 * This service uses broadcast communication mode.<br>
	 * 
	 * @param serialNo byte array with serial number, <code>serialNo.length</code> = 6
	 * @param newAddress new address
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 */
	void writeAddress(byte[] serialNo, IndividualAddress newAddress)
		throws KNXTimeoutException, KNXLinkClosedException;

	/**
	 * Reads the individual address of a communication partner identified using an unique
	 * serial number in the KNX network.
	 * <p>
	 * This service uses broadcast communication mode.<br>
	 * 
	 * @param serialNo byte array with serial number, <code>serialNo.length</code> = 6
	 * @return the individual address
	 * @throws KNXTimeoutException on a timeout during send or no address response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read address errors
	 */
	IndividualAddress readAddress(byte[] serialNo) throws KNXException;

	/**
	 * Modifies the domain address of a communication partner in the KNX network.
	 * <p>
	 * This service uses system broadcast communication mode.<br>
	 * The communication partner is a device in programming mode.
	 * 
	 * @param domain byte array with domain address, <code>domain.length</code> = 2 (on
	 *        powerline medium) or <code>domain.length</code> = 6 (on RF medium)
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 */
	void writeDomainAddress(byte[] domain) throws KNXTimeoutException,
		KNXLinkClosedException;

	/**
	 * Reads the domain address of a communication partner in the KNX network.
	 * <p>
	 * This service uses system broadcast communication mode.<br>
	 * The communication partner is a device in programming mode. In situations necessary
	 * to read domain addresses from more than one device in programming mode,
	 * <code>oneAddressOnly</code> is set to <code>false</code> and all received
	 * domain addresses are returned in the list. In this case, the whole response timeout
	 * is waited for address responses. If <code>oneAddressOnly</code> is
	 * <code>true</code>, the method returns after receiving the first read response,
	 * and the list contains one domain address.
	 * 
	 * @param oneAddressOnly <code>true</code> if method should return after receiving
	 *        the first read response, <code>false</code> to wait the whole response
	 *        timeout for read responses
	 * @return list of byte arrays with domain addresses, ordered according to time of
	 *         reception
	 * @throws KNXTimeoutException on a timeout during send or no address response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read domain address errors
	 */
	List readDomainAddress(boolean oneAddressOnly) throws KNXException;

	/**
	 * Reads the domain address of a communication partner identified using an address
	 * range.
	 * <p>
	 * This method is used to check existence of a device with the specified domain on a
	 * powerline medium and paying attention to more installations.<br>
	 * This service uses system broadcast communication mode.<br>
	 * <p>
	 * A note on answering behavior when the specified <code>range</code> is &lt; 255:<br>
	 * If an answering device 'A' receives a domain address response from another
	 * answering device 'B', 'A' will terminate the transmission of its response.
	 * 
	 * @param domain byte array with domain address to check for,
	 *        <code>domain.length</code> = 2 (powerline medium only)
	 * @param startAddress start from this individual address, lower bound of checked
	 *        range
	 * @param range address range, specifies upper bound address (startAddress + range)
	 * @return list of byte arrays with domain addresses, ordered according to time of
	 *         reception
	 * @throws KNXTimeoutException on a timeout during send or no address response was
	 *         received
	 * @throws KNXInvalidResponseException on invalid response message
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read domain address errors
	 */
	List readDomainAddress(byte[] domain, IndividualAddress startAddress, int range)
		throws KNXException;

	/**
	 * Reads the device descriptor information of a communication partner its controller.
	 * <p>
	 * This service uses point-to-point connectionless or connection-oriented
	 * communication mode.
	 * <p>
	 * The returned descriptor information format for device descriptor type 0 is as
	 * follows (MSB to LSB):<br>
	 * <code>| mask type (8 bit) | firmware version (8 bit) |</code><br>
	 * with the mask type split up into<br>
	 * <code>| Medium Type (4 bit) | Firmware Type (4 bit)|</code><br>
	 * and the firmware version split up into<br>
	 * <code>| version (4 bit) | sub code (4 bit) |</code><br>
	 * <br>
	 * The returned descriptor information format for device descriptor type 2 is as
	 * follows (MSB to LSB):<br>
	 * <code>| application manufacturer (16 bit) | device type (16 bit) |
	 * version (8 bit) | misc. (2 bit) + LT base (6 bit) | CI 1 (16 bit) |
	 * CI 2 (16 bit) | CI 3 (16 bit) | CI 4 (16 bit) |</code><br>
	 * with <code>CI = channel info</code>
	 * 
	 * @param dst destination to read from
	 * @param descType device descriptor type, 0 for type 0 or 2 for type 2
	 * @return byte array containing device descriptor information
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXInvalidResponseException on invalid read response message
	 * @throws KNXDisconnectException on disconnect in connection oriented mode
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read device descriptor errors
	 */
	byte[] readDeviceDesc(Destination dst, int descType) throws KNXException;

	/**
	 * Initiates a reset of the controller of a communication partner.
	 * <p>
	 * This service uses point-to-point connectionless or connection-oriented
	 * communication mode.<br>
	 * Invoking this method may result in a termination of the transport layer connection
	 * (i.e. state transition into disconnected for the supplied destination).
	 * 
	 * @param dst destination to reset
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 */
	void restart(Destination dst) throws KNXTimeoutException, KNXLinkClosedException;

	/**
	 * Reads the value of a property of an interface object of a communication partner.
	 * <p>
	 * This service uses point-to-point connectionless or connection-oriented
	 * communication mode.<br>
	 * One value element in the returned data byte array consumes<br>
	 * <code>(data.length / elements)</code> bytes.<br>
	 * The byte offset into the returned data to access a property value element with
	 * index <code>i</code> (zero based) is calculated the following way:<br>
	 * <code>offset = (data.length / elements) * i</code>.<br>
	 * Note that interface objects with active access protection are only accessible over
	 * connection oriented communication.
	 * 
	 * @param dst destination to read from
	 * @param objIndex interface object index
	 * @param propID property identifier
	 * @param start start index in the property value to start reading from
	 * @param elements number of elements to read
	 * @return byte array containing the property value data
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException if tried to access a non existing property or forbidden
	 *         property access (not sufficient access rights)
	 * @throws KNXInvalidResponseException if received number of elements differ
	 * @throws KNXDisconnectException on disconnect in connection oriented mode
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read property error
	 */
	byte[] readProperty(Destination dst, int objIndex, int propID, int start, int elements)
		throws KNXException;

	/**
	 * Modifies the value of a property of an interface object of a communication partner.
	 * <p>
	 * This service uses point-to-point connectionless or connection-oriented
	 * communication mode.<br>
	 * The value of the written property is explicitly read back after writing.<br>
	 * Note that interface objects with active access protection are only accessible over
	 * connection oriented communication.
	 * 
	 * @param dst destination to write to
	 * @param objIndex interface object index
	 * @param propID property identifier
	 * @param start start index in the property value to start writing to
	 * @param elements number of elements to write
	 * @param data byte array containing property value data to write
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException if tried to access a non existing property or forbidden
	 *         property access (not sufficient access rights) or erroneous property data
	 *         was written
	 * @throws KNXInvalidResponseException if received number of elements differ or the
	 *         data length read back differs from the written data length
	 * @throws KNXDisconnectException on disconnect in connection oriented mode
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read property error
	 */
	void writeProperty(Destination dst, int objIndex, int propID, int start,
		int elements, byte[] data) throws KNXException;

	/**
	 * Reads the description of a property of an interface object of a communication
	 * partner.
	 * <p>
	 * This service uses point-to-point connectionless or connection-oriented
	 * communication mode.<br>
	 * The property of the object is addressed either with a the <code>propID</code> or
	 * with the <code>propIndex</code>. The property index is only used if the property
	 * identifier is 0, otherwise the index is ignored.<br>
	 * When using the property ID for access, the property index in the returned
	 * description is either the correct property index of the addressed property or 0.
	 * 
	 * @param dst destination to read from
	 * @param objIndex interface object index
	 * @param propID property identifier, specify 0 to use the property index
	 * @param propIndex property index, starts with index 0 for the first property
	 * @return byte array containing the property description, starting with the property
	 *         object index
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException if the response contains no description (e.g. if tried
	 *         to access a non existing property)
	 * @throws KNXDisconnectException on disconnect in connection oriented mode
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read property description error
	 */
	byte[] readPropertyDesc(Destination dst, int objIndex, int propID, int propIndex)
		throws KNXException;

	/**
	 * Reads the value of the A/D converter of a communication partner.
	 * <p>
	 * This service uses point-to-point connection-oriented communication mode.<br>
	 * 
	 * @param dst destination to read from
	 * @param channelNr channel number of the A/D converter
	 * @param repeat number of consecutive converter read operations
	 * @return the calculated A/D conversion value
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException on remote converter read problem (e.g. overflow or wrong
	 *         channel)
	 * @throws KNXDisconnectException on disconnect during read
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read A/D converter error
	 */
	int readADC(Destination dst, int channelNr, int repeat) throws KNXException;

	/**
	 * Reads memory data from the address space of a communication partner its controller.
	 * <p>
	 * This service uses point-to-point connection-oriented communication mode.<br>
	 * Note that a remote application layer shall ignore a memory read if the amount of
	 * read memory does not fit into an APDU of maximum length.
	 * 
	 * @param dst destination to read from
	 * @param startAddr 16 bit start address to read in memory
	 * @param bytes number of data bytes to read (with increasing addresses)
	 * @return byte array containing the data read from the memory
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException on problems of the partner reading (part of) the memory
	 *         (e.g. access to illegal or protected address space, invalid number of
	 *         bytes)
	 * @throws KNXDisconnectException on disconnect during read
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read memory error
	 */
	byte[] readMemory(Destination dst, int startAddr, int bytes) throws KNXException;

	/**
	 * Writes memory data in the address space of a communication partner its controller.
	 * <p>
	 * This service uses point-to-point connection-oriented communication mode.<br>
	 * If verify mode is enabled for the destination, this method will wait for a memory
	 * write response and do an explicit read back of the written memory.<br>
	 * Note that a remote application layer shall ignore a memory write if the amount of
	 * memory does not fit into an APDU of maximum length the remote layer can handle.
	 * 
	 * @param dst destination to write to
	 * @param startAddr 16 bit start address to write in memory
	 * @param data byte array containing the memory data to write
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException in verify mode on problems of the partner writing the
	 *         memory data (e.g. access to illegal or protected address space, invalid
	 *         number of bytes) or erroneous memory data was written
	 * @throws KNXInvalidResponseException in verify mode if the size of memory read back
	 *         differs from the written size of memory
	 * @throws KNXDisconnectException on disconnect during read
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other read memory error
	 */
	void writeMemory(Destination dst, int startAddr, byte[] data) throws KNXException;

	/**
	 * Authorizes at a communication partner using an authorization key to obtain a
	 * certain access level.
	 * <p>
	 * This service uses point-to-point connection-oriented communication mode.<br>
	 * The returned access level is between 0 (maximum access rights) and 3 (i.e. minimum
	 * access rights) or 0 (maximum access rights) and 15 (minimum access rights).<br>
	 * If no authorization is done at all or the supplied key is not valid, the default
	 * access level used is set to minimum. A set access level is valid until disconnected
	 * from the partner or a new authorization request is done.
	 * 
	 * @param dst destination at which to authorize
	 * @param key byte array containing authorization key
	 * @return the granted access level
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXInvalidResponseException if the received access level is out of the
	 *         allowed value range
	 * @throws KNXDisconnectException on disconnect during authorize
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other authorization error
	 */
	byte authorize(Destination dst, byte[] key) throws KNXException;

	/**
	 * Modifies or deletes an authorization key associated to an access level of a
	 * communication partner.
	 * <p>
	 * This service uses point-to-point connection-oriented communication mode.<br>
	 * If the supplied key is 0xFFFFFFFF, the key for the given access <code>level</code>
	 * is removed. The write request has to be done using equal or higher access rights
	 * than the access rights of the <code>level</code> which is to be modified (i.e.
	 * current level &lt;= level to change).
	 * 
	 * @param dst destination to write to
	 * @param level access level to modify
	 * @param key new key for the access level or 0xFFFFFFFF to remove key
	 * @throws KNXTimeoutException on a timeout during send
	 * @throws KNXRemoteException if the current access level > necessary access level for
	 *         writing a key
	 * @throws KNXDisconnectException on disconnect during write
	 * @throws KNXLinkClosedException if network link to KNX network is closed
	 * @throws KNXException on other write key error
	 */
	void writeKey(Destination dst, int level, byte[] key) throws KNXException;

	/**
	 * Returns whether a network link is attached to this management client.
	 * <p>
	 * 
	 * @return <code>true</code> if link attached, <code>false</code> if detached
	 */
	boolean isOpen();

	/**
	 * Detaches the network link from this management client.
	 * <p>
	 * A detach will also detach an internally used transport layer with all its
	 * consequences. If no network link is attached, no action is performed.
	 * <p>
	 * Note that a detach does not trigger a close of the used network link.
	 * 
	 * @return the formerly attached KNX network link, or <code>null</code> if already
	 *         detached
	 * @see TransportLayer#detach()
	 */
	KNXNetworkLink detach();
}
