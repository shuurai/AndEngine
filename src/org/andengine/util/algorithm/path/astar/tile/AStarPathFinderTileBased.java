package org.andengine.util.algorithm.path.astar.tile;

import org.andengine.util.adt.list.ShiftList;
import org.andengine.util.adt.map.LongSparseArray;
import org.andengine.util.adt.queue.IQueue;
import org.andengine.util.adt.queue.SortedQueue;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.AStarPathFinder;
import org.andengine.util.algorithm.path.astar.tile.pool.AStarPathTilePoolManager;

/**
 * Derived from {@link AStarPathFinder}
 * 
 * @author Paul Robinson
 * @since 6 Sep 2012 15:20:19
 * @param <T>
 */
public class AStarPathFinderTileBased<T> implements ITilePathFinder<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private AStarPathTilePoolManager mPoolManager;

	// ===========================================================
	// Constructors
	// ===========================================================
	public AStarPathFinderTileBased(AStarPathTilePoolManager mPoolManager) {
		this.mPoolManager = mPoolManager;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public Path findPath(ITilePathFinderMap<T> pTilePathFinderMap, int pMaxRows, int pMaxCols, T pEntity, int pFromRow,
			int pFromCol, int pToRow, int pToCol, ITileAStarHeuristic<T> pTileAStarHeuristic,
			ITileCostFunction<T> pCostFunction) {
		return this.findPath(pTilePathFinderMap, pMaxRows, pMaxCols, pEntity, pFromRow, pFromCol, pToRow, pToCol,
				pTileAStarHeuristic, pCostFunction, Float.MAX_VALUE);
	}

	@Override
	public Path findPath(ITilePathFinderMap<T> pTilePathFinderMap, int pMaxRows, int pMaxCols, T pEntity, int pFromRow,
			int pFromCol, int pToRow, int pToCol, ITileAStarHeuristic<T> pTileAStarHeuristic,
			ITileCostFunction<T> pCostFunction, float pMaxCost) {
		return this.findPath(pTilePathFinderMap, pMaxRows, pMaxCols, pEntity, pFromRow, pFromCol, pToRow, pToCol,
				pTileAStarHeuristic, pCostFunction, pMaxCost, null);
	}

	@Override
	public Path findPath(ITilePathFinderMap<T> pTilePathFinderMap, int pMaxRows, int pMaxCols, T pEntity, int pFromRow,
			int pFromCol, int pToRow, int pToCol, ITileAStarHeuristic<T> pTileAStarHeuristic,
			ITileCostFunction<T> pCostFunction, float pMaxCost, ITilePathFinderListener<T> pPathFinderListener) {
		if (((pFromRow == pToRow) && (pFromCol == pToCol)) || pTilePathFinderMap.isBlocked(pFromRow, pFromCol, pEntity)
				|| pTilePathFinderMap.isBlocked(pToRow, pToCol, pEntity)) {
			return null;
		}
		/* Drag some fields to local variables. */
		/*
		final Node fromNode = new Node(pFromRow, pFromCol, pTileAStarHeuristic.getExpectedRestCost(pTilePathFinderMap,
				pEntity, pFromRow, pFromCol, pToRow, pToCol));
		*/
		final Node fromNode = this.mPoolManager.getNodeFromPool(pFromRow, pFromCol, pTileAStarHeuristic
				.getExpectedRestCost(pTilePathFinderMap, pEntity, pFromRow, pFromCol, pToRow, pToCol));
		final boolean allowDiagonalMovement = false;
		final long fromNodeID = fromNode.mID;
		final long toNodeID = Node.calculateID(pToRow, pToCol);

		final LongSparseArray<Node> visitedNodes = new LongSparseArray<Node>();
		final LongSparseArray<Node> openNodes = new LongSparseArray<Node>();
		final IQueue<Node> sortedOpenNodes = new SortedQueue<Node>(new ShiftList<Node>());

		/* Initialize algorithm. */
		openNodes.put(fromNodeID, fromNode);
		sortedOpenNodes.enter(fromNode);

		Node currentNode = null;
		while (openNodes.size() > 0) {
			/* The first Node in the open list is the one with the lowest cost. */
			currentNode = sortedOpenNodes.poll();
			final long currentNodeID = currentNode.mID;
			if (currentNodeID == toNodeID) {
				break;
			}

			visitedNodes.put(currentNodeID, currentNode);

			/* Loop over all neighbors of this position. */
			for (int dX = -1; dX <= 1; dX++) {
				// New it, new Col
				for (int dY = -1; dY <= 1; dY++) {
					// new row
					if ((dX == 0) && (dY == 0)) {
						// We're at a stand still
						continue;
					}

					if (!allowDiagonalMovement && (dX != 0) && (dY != 0)) {
						continue;
					}
					final int neighborNodeX = dX + currentNode.mX; // Col
					final int neighborNodeY = dY + currentNode.mY; // Row
					final long neighborNodeID = Node.calculateID(neighborNodeX, neighborNodeY);

					// Check if tile is within our bounds
					if (neighborNodeX < 0 || neighborNodeX > pMaxCols || neighborNodeY < 0 || neighborNodeY > pMaxRows) {
						// Less than zero and more than rows/cols we've got!
						continue;
					}

					if (pTilePathFinderMap.isBlocked(neighborNodeX, neighborNodeY, pEntity)) {
						// Path is blocked
						continue;
					}

					if (visitedNodes.indexOfKey(neighborNodeID) >= 0) {
						continue;
					}

					Node neighborNode = openNodes.get(neighborNodeID);
					final boolean neighborNodeIsNew;
					/* Check if neighbor exists. */
					if (neighborNode == null) {
						neighborNodeIsNew = true;
						neighborNode = this.mPoolManager.getNodeFromPool();
						neighborNode.setup(neighborNodeX, neighborNodeY, pTileAStarHeuristic.getExpectedRestCost(
								pTilePathFinderMap, pEntity, neighborNodeX, neighborNodeY, pToRow, pToCol));
						/*
						neighborNode = new Node(neighborNodeX, neighborNodeY, pTileAStarHeuristic.getExpectedRestCost(
								pTilePathFinderMap, pEntity, neighborNodeX, neighborNodeY, pToRow, pToCol));
						*/
					} else {
						neighborNodeIsNew = false;
					}

					/* Update cost of neighbor as cost of current plus step from current to neighbor. */
					final float costFromCurrentToNeigbor = pCostFunction.getCost(pTilePathFinderMap, currentNode.mX,
							currentNode.mY, neighborNodeX, neighborNodeY, pEntity);
					final float neighborNodeCost = currentNode.mCost + costFromCurrentToNeigbor;
					if (neighborNodeCost > pMaxCost) {
						/* Too expensive -> remove if isn't a new node. */
						if (!neighborNodeIsNew) {
							openNodes.remove(neighborNodeID);
						}
					} else {
						neighborNode.setParent(currentNode, costFromCurrentToNeigbor);
						if (neighborNodeIsNew) {
							openNodes.put(neighborNodeID, neighborNode);
						} else {
							/* Remove so that re-insertion puts it to the correct spot. */
							sortedOpenNodes.remove(neighborNode);
						}

						sortedOpenNodes.enter(neighborNode);

						if (pPathFinderListener != null) {
							pPathFinderListener.onVisited(pEntity, neighborNodeX, neighborNodeY);
						}
					}
				}
			}
		}

		/* Cleanup. */
		// TODO We could just let the GC do its work.
		/*
		visitedNodes.clear();
		openNodes.clear();
		sortedOpenNodes.clear();
		*/

		/* Check if a path was found. */
		if (currentNode.mID != toNodeID) {
			return null;
		}

		/* Calculate path length. */
		int length = 1;
		Node tmp = currentNode;
		while (tmp.mID != fromNodeID) {
			tmp = tmp.mParent;
			length++;
		}

		/* Traceback path. */
		// final Path path = new Path(length);
		final Path path = this.mPoolManager.getPathFromPool(length);
		int index = length - 1;
		tmp = currentNode;
		while (tmp.mID != fromNodeID) {
			path.set(index, tmp.mX, tmp.mY);
			tmp = tmp.mParent;
			index--;
		}
		path.set(0, pFromRow, pFromCol);

		long[] visitedNodesKeys = visitedNodes.getKeys();
		for (long l : visitedNodesKeys) {
			Node found = visitedNodes.get(l);
			if (found != null) {
				if (found.inUse()) {
					found.destroy();
				}
			}
		}

		long[] openNodesKeys = openNodes.getKeys();
		for (long l : openNodesKeys) {
			Node found = openNodes.get(l);
			if (found != null) {
				if (found.inUse()) {
					found.destroy();
				}
			}
		}

		int sortedSize = sortedOpenNodes.size();
		for (int i = 0; i < sortedSize; i++) {
			Node found = sortedOpenNodes.get(i);
			if (found != null) {
				if (found.inUse()) {
					found.destroy();
				}
			}
		}
		
		visitedNodes.clear();
		openNodes.clear();
		sortedOpenNodes.clear();
		
		return path;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
