package org.andengine.util.algorithm.path.astar.isometric.pool;

import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.isometric.Node;

/**
 * Use this manager to obtain an item from its respective pool. <br>
 * All items implement {@link IPool} <br>
 * To return an item to the pool call {@link IPool#destroy()} on the item.
 * 
 * @author Paul Robinson
 * @since 20 Oct 2012 14:34:58
 */
public class AStarPathTilePoolManager {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private AStarTileNodePool mNodePool;
	private AStarTilePathPool mPathPool;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AStarPathTilePoolManager(final int pNodePoolSize, final int pNodePoolGrowthSize, final int pPathPoolSize,
			final int pPathPoolGrowthSize) {
		this.mNodePool = new AStarTileNodePool(pNodePoolSize, pNodePoolGrowthSize);
		this.mPathPool = new AStarTilePathPool(pPathPoolSize, pPathPoolGrowthSize);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	/**
	 * Get a clean {@link Node} from the node pool. Remember to call
	 * {@link Node#setup(int, int, float)}
	 * 
	 * @return {@link Node} from pool
	 */
	public Node getNodeFromPool() {
		return this.mNodePool.obtainPoolItem();
	}

	/**
	 * Get a clean {@link Node} from the node pool. Setup the Node automatically
	 * with given parameters
	 * 
	 * @param pX
	 * @param pY
	 * @param pExpectedRestCost
	 * @return {@link Node} from pool already setup.
	 */
	public Node getNodeFromPool(final int pX, final int pY, final float pExpectedRestCost) {
		Node newNode = this.mNodePool.obtainPoolItem();
		newNode.setup(pX, pY, pExpectedRestCost);
		return newNode;
	}

	/**
	 * Get a clean {@link Path} from the path pool. Remember to call
	 * {@link Path#set(int, int, int)}
	 * 
	 * @return Clean {@link Path} from its pool
	 */
	public Path getPathFromPool() {
		return this.mPathPool.obtainPoolItem();
	}

	/**
	 * Get a clean {@link Path} from the path pool. Setup the {@link Path}
	 * automatically with given parameters
	 * 
	 * @param pLength
	 * @return {@link Path} from pool already setup.
	 */
	public Path getPathFromPool(final int pLength) {
		Path path = this.mPathPool.obtainPoolItem();
		path.setup(pLength);
		return path;
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
