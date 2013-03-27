package org.andengine.util.algorithm.path.astar.isometric.pool;

import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.algorithm.path.Path;

public class AStarTilePathPool extends GenericPool<Path> {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	// ===========================================================
	// Constructors
	// ===========================================================
	public AStarTilePathPool(final int pInitialSize, final int pGrowth){
		super(pInitialSize, pGrowth);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	/**
	 * This is called when we've not got any spare objects, so we create one!
	 */
	@Override
	protected Path onAllocatePoolItem() {
		return new Path(this);
	}

	/**
	 * Called when a sprite is given back to the pool, ie recycled.
	 */
	@Override
	protected void onHandleRecycleItem(Path pItem) {
		pItem.reset();
		super.onHandleRecycleItem(pItem);
	}

	/**
	 * This is called before an object is returned to the caller
	 */
	@Override
	protected void onHandleObtainItem(Path pItem) {
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
