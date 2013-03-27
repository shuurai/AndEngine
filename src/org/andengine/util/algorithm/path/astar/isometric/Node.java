package org.andengine.util.algorithm.path.astar.isometric;

import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.algorithm.path.astar.isometric.pool.IPool;

public class Node implements Comparable<Node>, IPool {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public Node mParent;

	public int mX;
	public int mY;
	public long mID;
	public float mExpectedRestCost;

	public float mCost;
	public float mTotalCost;

	private GenericPool<Node> mPool;
	private boolean mInUse = false;
	// ===========================================================
	// Constructors
	// ===========================================================

	public Node(final GenericPool<Node> pPool) {
		this.mPool = pPool;
	}

	/*
	public Node(final int pX, final int pY, final float pExpectedRestCost) {
		this.setup(pX, pY, pExpectedRestCost);
	}
	*/

	public void setup(final int pX, final int pY, final float pExpectedRestCost) {
		this.mInUse = true;
		this.mX = pX;
		this.mY = pY;
		this.mExpectedRestCost = pExpectedRestCost;

		this.mID = Node.calculateID(pX, pY);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setParent(final Node pParent, final float pCost) {
		this.mParent = pParent;
		this.mCost = pCost;
		this.mTotalCost = pCost + this.mExpectedRestCost;
	}
	
	public boolean inUse(){
		return this.mInUse;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Comparable<Node>
	// ===========================================================

	@Override
	public int compareTo(final Node pNode) {
		final float diff = this.mTotalCost - pNode.mTotalCost;
		if (diff > 0) {
			return 1;
		} else if (diff < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(final Object pOther) {
		if (this == pOther) {
			return true;
		} else if (pOther == null) {
			return false;
		} else if (this.getClass() != pOther.getClass()) {
			return false;
		}
		return this.equals((Node) pOther);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [x=" + this.mX + ", y=" + this.mY + "]";
	}

	// ===========================================================
	// IPool
	// ===========================================================
	@Override
	public void reset() {
		this.mParent = null;
		this.mX = 0;
		this.mY = 0;
		this.mID = 0;
		this.mExpectedRestCost = 0;
		this.mCost = 0;
		this.mTotalCost = 0;
		this.mInUse = false;
	}

	@Override
	public void destroy() {
		this.mPool.recyclePoolItem(this);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public static long calculateID(final int pX, final int pY) {
		return (((long) pX) << 32) | pY;
	}

	public boolean equals(final Node pNode) {
		return this.mID == pNode.mID;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
