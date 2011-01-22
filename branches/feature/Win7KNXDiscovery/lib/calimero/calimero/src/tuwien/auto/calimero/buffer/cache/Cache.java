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

/**
 * Cache interface to use for different caching policies.
 * <p>
 * Cache entries are unambiguously identified through a key, as defined by
 * {@link CacheObject}.<br>
 * For comparison of keys, {@link Object#hashCode()} and
 * {@link Object#equals(Object)} are used.
 * 
 * @author B. Malinowsky
 * @see CacheObject
 */
public interface Cache
{
	/**
	 * Offers information about data counting and cache performance.
	 * <p>
	 */
	public interface Statistic
	{
		/**
		 * Returns the total number of successful requests with
		 * {@link Cache#get(Object)} for a {@link CacheObject}.
		 * <p>
		 * 
		 * @return total hits
		 */
		long hits();

		/**
		 * Returns the total number of {@link CacheObject} not found with
		 * {@link Cache#get(Object)}.
		 * <p>
		 * 
		 * @return total misses
		 */
		long misses();

		/**
		 * Returns the ratio between found {@link CacheObject}s to total
		 * requests through {@link Cache#get(Object)}.
		 * <p>
		 * This value is obtained through
		 * <code>ratio = (hits / (hits + misses))</code>.
		 * 
		 * @return the hit ratio in the range [0,1]
		 */
		double hitRatio();
	}
	
	/**
	 * Inserts the {@link CacheObject} <code>obj</code> into the cache.
	 * <p>
	 * If a {@link CacheObject} with an equal key (delivered by
	 * {@link CacheObject#getKey()}) to <code>obj.getKey()</code> is already
	 * in the cache, it will be replaced by <code>obj</code>.<br>
	 * {@link CacheObject#resetTimestamp()} is invoked on <code>obj</code> after
	 * <code>obj</code> was inserted successfully.
	 * 
	 * @param obj CacheObject to put into the cache
	 */
	void put(CacheObject obj);

	/**
	 * Gets the {@link CacheObject} associated with <code>key</code> from the
	 * cache.
	 * <p>
	 * If found, the access count of the CacheObject is incremented by 1.
	 * 
	 * @param key key to search
	 * @return the CacheObject or <code>null</code> if <code>key</code> does
	 *         not exist in the cache
	 */
	CacheObject get(Object key);
	
	/**
	 * Removes the CacheObject associated with <code>key</code> from the cache,
	 * if found.
	 * <p>
	 * 
	 * @param key key of CacheObject to remove
	 */
	void remove(Object key);
	
	/**
	 * Removes all {@link CacheObject}s which are not valid anymore, as defined
	 * by a caching policy, from the cache.
	 * <p>
	 */
	void removeExpired();

	/**
	 * Empties the cache of all {@link CacheObject}s.
	 * <p>
	 */
	void clear();
	
	/**
	 * Returns information collected by this cache since its creation.
	 * <p>
	 * 
	 * @return a {@link Statistic} object
	 */
	Statistic statistic();
}
