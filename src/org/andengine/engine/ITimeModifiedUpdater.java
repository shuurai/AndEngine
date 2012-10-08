package org.andengine.engine;

import org.andengine.entity.Entity;

/**
 * For use by the {@link Engine} so {@link Entity} can get the time modifer in use.
 * @author Paul Robinson
 * @since 8 Oct 2012 12:39:01
 */
public interface ITimeModifiedUpdater {
	/**
	 * Get the current time Modifier
	 * @return {@link Integer} of Time Modifier
	 */
	public int getTimeModifier();
}
