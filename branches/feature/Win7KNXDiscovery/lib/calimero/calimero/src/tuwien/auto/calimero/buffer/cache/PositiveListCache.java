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

package tuwien.auto.calimero.buffer.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A static {@link Cache} using a positive list with allowed keys for the cache.
 * <p>
 * The positive list contains key objects which are allowed to be cached. On a
 * {@link #put(CacheObject)} operation, {@link CacheObject#getKey()} is checked for a
 * positive match in that list in order to be allowed for caching.
 * <p>
 * This cache does not use a replacement policy (static cache).<br>
 * Nevertheless, a timeout can be given to specify the expiring time of cache values.
 * <p>
 * The usage value of {@link CacheObject#getUsage()} equals the access count,
 * {@link CacheObject#getCount()}.
 * 
 * @author B. Malinowsky
 */
public class PositiveListCache extends ExpiringCache
{
	private Set posList = new HashSet();
	private long hits;
	private long misses;
 
	/**
	 * Creates a new {@link PositiveListCache}.
	 * <p>
	 * Optionally, an expiring time can be specified.
	 * 
	 * @param timeToExpire timespan in seconds for cache objects to stay valid,
	 *        or 0 for no expiring
	 */
	public PositiveListCache(int timeToExpire)
	{
		super(timeToExpire);
	}

	/**
	 * Creates a new {@link PositiveListCache} and inits the positive key list.
	 * <p>
	 * Optionally, an expiring time can be specified.
	 * 
	 * @param positiveList a Collection holding the allowed keys for this cache
	 * @param timeToExpire timespan in seconds for cache objects to stay valid,
	 *        or 0 for no expiring
	 */
	public PositiveListCache(Collection positiveList, int timeToExpire)
	{
		this(timeToExpire);
		setPositiveList(positiveList);
	}

	/**
	 * Sets a new positive list for this cache.
	 * <p>
	 * The old list is discarded. All cache objects will be updated immediately
	 * according to the new list.
	 * 
	 * @param positiveList a Collection holding the allowed keys for this cache
	 */
	public final synchronized void setPositiveList(Collection positiveList)
	{
		if (posList.size() == 0)
			posList.addAll(positiveList);
		else {
			posList = new HashSet(positiveList);
			// remove old keys not in the new list anymore
			for (final Iterator i = map.keySet().iterator(); i.hasNext(); )
				if (!posList.contains(i.next()))
					i.remove();
		}
	}

	/**
	 * Adds a new allowed <code>key</code> to the positive list, if it is not
	 * already present.
	 * <p>
	 * 
	 * @param key the new key object
	 */
	public final synchronized void addToPositiveList(Object key)
	{
		posList.add(key);
	}

	/**
	 * Removes the <code>key</code> from the positive list, if it is present.
	 * <p>
	 * The cache objects will be updated immediately according to the removed
	 * key.
	 * 
	 * @param key key object to remove
	 */
	public final synchronized void removeFromPositiveList(Object key)
	{
		if (posList.remove(key))
			remove(key);
	}

	/**
	 * Returns the positive list currently used by this cache.
	 * 
	 * @return array of all allowed key objects.
	 */
	public final synchronized Object[] getPositiveList()
	{
		return posList.toArray();
	}

	/**
	 * For a {@link CacheObject} to be put into the cache, its key
	 * {@link CacheObject#getKey()} has to be equal to one in the positive list of this
	 * cache.<br>
	 * If expiring of cache objects is set, and the timestamp of a {@link CacheObject} is
	 * renewed externally after it has been put into the cache, a new
	 * {@link #put(CacheObject)} is required for that object to apply the new timestamp
	 * and keep the cache in a consistent state.
	 */
	public synchronized void put(CacheObject obj)
	{
		if (posList.contains(obj.getKey())) {
			startSweeper();
			obj.resetTimestamp();
			// maintain insertion order, if any
			if (map instanceof LinkedHashMap)
				map.remove(obj.getKey());
			map.put(obj.getKey(), obj);
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.buffer.cache.Cache#get(java.lang.Object)
	 */
	public synchronized CacheObject get(Object key)
	{
		final CacheObject o = (CacheObject) map.get(key);
		if (o != null) {
			updateAccess(o);
			++hits;
		}
		else
			++misses;
		return o;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.buffer.cache.Cache#remove(java.lang.Object)
	 */
	public synchronized void remove(Object key)
	{
		map.remove(key);
	}

	/**
	 * {@inheritDoc}<br>
	 * This does not affect the positive list.
	 */
	public synchronized void clear()
	{
		stopSweeper();
		map.clear();
	}
	
	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.buffer.cache.Cache#statistic()
	 */
	public synchronized Statistic statistic()
	{
		return new StatisticImpl(hits, misses);
	}
    
	private static void updateAccess(CacheObject obj)
	{
		obj.incCount();
		obj.setUsage(obj.getCount());
	}
}
