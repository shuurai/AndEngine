package org.andengine.entity.scene.isometric;

import org.andengine.entity.IEntity;
import org.andengine.util.adt.list.SmartList;

public abstract class IsometricBase {
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	protected int mWidth;
	protected int mHeight;
	protected int mDivider;
	protected SmartList<IEntity> mChildren;
	// ===========================================================
	// Constructors
	// ===========================================================

	public IsometricBase(final int pWidth, final int pHeight, final int pDivider) {
		this.mWidth = pWidth;
		this.mHeight = pHeight;
		this.mDivider = pDivider;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void addEntity(IEntity pEntity){
		
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
