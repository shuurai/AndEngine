package org.andengine.util.algorithm.path.astar.tile;


/**
 * 
 * @author Paul Robinson
 * @since 6 Sep 2012 15:43:47
 * @param <T>
 */
public interface ITilePathFinderMap<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public boolean isBlocked(final int pRow, final int pCol, final T pEntity);
}
