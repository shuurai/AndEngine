package org.andengine.util.algorithm.path.astar.isometric.pool;

import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.algorithm.path.astar.isometric.Node;

public class AStarTileNodePool extends GenericPool<Node> {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	// ===========================================================
	// Constructors
	// ===========================================================
	public AStarTileNodePool(final int pInitialSize, final int pGrowth){
		super(pInitialSize, pGrowth);
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	/**
     * This is called when we've not got any spare objects, so we create one!
     */
	@Override
	protected Node onAllocatePoolItem() {
		return new Node(this);
	}

	 /**
     * Called when a sprite is given back to the pool, ie recycled.
     */
	@Override
	protected void onHandleRecycleItem(Node pItem) {
		pItem.reset();
		super.onHandleRecycleItem(pItem);
	}

	/**
     * This is called before an object is returned to the caller
     */
	@Override
	protected void onHandleObtainItem(Node pItem) {
		super.onHandleObtainItem(pItem);
	}
	
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
