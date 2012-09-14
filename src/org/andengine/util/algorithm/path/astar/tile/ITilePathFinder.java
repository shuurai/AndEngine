package org.andengine.util.algorithm.path.astar.tile;

import org.andengine.util.algorithm.path.IPathFinder;
import org.andengine.util.algorithm.path.Path;

/**
 * Derived from {@link IPathFinder}
 * @author Paul Robinson
 * @since 6 Sep 2012 15:25:44
 * @param <T>
 */
public interface ITilePathFinder<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public Path findPath(final ITilePathFinderMap<T> pTilePathFinderMap, final int pMaxRows, final int pMaxCols, final T pEntity, final int pFromRow, final int pFromCol, final int pToRow, final int pToCol,final ITileAStarHeuristic<T> pTileAStarHeuristic, final ITileCostFunction<T> pTileCostFunction);
	public Path findPath(final ITilePathFinderMap<T> pTilePathFinderMap, final int pMaxRows, final int pMaxCols, final T pEntity, final int pFromRow, final int pFromCol, final int pToRow, final int pToCol, final ITileAStarHeuristic<T> pTileAStarHeuristic, final ITileCostFunction<T> pTileCostFunction, final float pMaxCost);
	public Path findPath(final ITilePathFinderMap<T> pTilePathFinderMap, final int pMaxRows, final int pMaxCols, final T pEntity, final int pFromRow, final int pFromCol, final int pToRow, final int pToCol, final ITileAStarHeuristic<T> pTileAStarHeuristic, final ITileCostFunction<T> pTileCostFunction, final float pMaxCost, final ITilePathFinderListener<T> pPathFinderListener);

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
	
	public interface ITilePathFinderListener<T> {
		// ===========================================================
		// Constants
		// ===========================================================
	
		// ===========================================================
		// Fields
		// ===========================================================
	
		public void onVisited(final T pEntity, final int pRow, final int pCol);
	}
}
