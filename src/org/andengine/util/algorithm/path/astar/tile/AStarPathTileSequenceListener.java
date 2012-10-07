package org.andengine.util.algorithm.path.astar.tile;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveSetZModifier;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.tile.AStarPathTileModifier.IAStarPathTileModifierListener;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.SequenceModifier;
import org.andengine.util.modifier.SequenceModifier.ISubSequenceModifierListener;
/**
 * For use from {@link AStarPathTileModifier}
 * Manages listeners for the move modifiers so they can be update.
 * @author Paul Robinson
 * @since 7 Oct 2012 18:32:15
 */
public class AStarPathTileSequenceListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields For the SequenceModifier
	// ===========================================================
	private ISubSequenceModifierListener<IEntity> mSubSequenceModifierListener;
	private IModifierListener<IEntity> mModifierListener;

	// ===========================================================
	// Fields For the AStarPathTileModifier
	// ===========================================================
	private IAStarPathTileModifierListener mPathModifierListener;

	// ===========================================================
	// Fields
	// ===========================================================
	/**
	 * When animating, help keep track of what modifier is currently executing. 
	 * Helps with speeding up entity (Start from current modifier till finish)
	 */
	private int mCurrentIndex = 0;
	private int mPathSize = 0;
	private Path mPath;
	private boolean mIsometric = true;
	private EntityModifier mParent;
	private MoveSetZModifier[] mMoveModifiers;
	private SequenceModifier<IEntity> mSequenceModifier;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AStarPathTileSequenceListener(final IAStarPathTileModifierListener pPathModifierListener,
			final MoveSetZModifier[] pMoveModifiers, final Path pPath, final boolean pIsometric,
			final EntityModifier pParent) {
		this.mPath = pPath;
		if(this.mPath !=null){
			this.mPathSize = this.mPath.getLength();
		}
		this.mIsometric = pIsometric;
		this.mParent = pParent;
		this.mMoveModifiers = pMoveModifiers;
		this.mPathModifierListener = pPathModifierListener;
		this.setupSubSequenceModifierListener();
		this.setupModifierListener();
		this.setupSequenceModifer();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public SequenceModifier<IEntity> getSequenceModifier() {
		return this.mSequenceModifier;
	}

	public int getCurrentIndex() {
		return this.mCurrentIndex;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void animateWithPath(final IModifier<IEntity> pModifier, final IEntity pEntity, final int pIndex) {
		if (pIndex < mPathSize) {
			// We need to translate to isometric view, so move up = move
			// NW = up right
			switch (mPath.getDirectionToNextStep(pIndex)) {
			case UP:
				if (mIsometric) {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveUpRight(mParent, pEntity, pIndex);
					}
				} else {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveUp(mParent, pEntity, pIndex);
					}
				}
				break;
			case DOWN:
				if (mIsometric) {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveDownLeft(mParent, pEntity, pIndex);
					}
				} else {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveDown(mParent, pEntity, pIndex);
					}
				}
				break;
			case LEFT:
				if (mIsometric) {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveUpLeft(mParent, pEntity, pIndex);
					}
				} else {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveLeft(mParent, pEntity, pIndex);
					}
				}
				break;
			case RIGHT:
				if (mIsometric) {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveDownRight(mParent, pEntity, pIndex);
					}
				} else {
					if (mPathModifierListener != null) {
						mPathModifierListener.onNextMoveRight(mParent, pEntity, pIndex);
					}
				}
				break;
			case UP_LEFT:
				if (mPathModifierListener != null) {
					mPathModifierListener.onNextMoveUpLeft(mParent, pEntity, pIndex);
				}
				break;
			case UP_RIGHT:
				if (mPathModifierListener != null) {
					mPathModifierListener.onNextMoveUpRight(mParent, pEntity, pIndex);
				}
				break;
			case DOWN_LEFT:
				if (mPathModifierListener != null) {
					mPathModifierListener.onNextMoveDownLeft(mParent, pEntity, pIndex);
				}
				break;
			case DOWN_RIGHT:
				if (mPathModifierListener != null) {
					mPathModifierListener.onNextMoveDownRight(mParent, pEntity, pIndex);
				}
				break;
			default:
			}
		}
	}

	private void setupSubSequenceModifierListener() {
		this.mSubSequenceModifierListener = new ISubSequenceModifierListener<IEntity>() {
			@Override
			public void onSubSequenceStarted(final IModifier<IEntity> pModifier, final IEntity pEntity, final int pIndex) {
				mCurrentIndex = pIndex;

				if (mPath != null) {
					animateWithPath(pModifier, pEntity, pIndex);
				}

				if (mPathModifierListener != null) {
					mPathModifierListener.onPathWaypointStarted(mParent, pEntity, pIndex);
				}
			}

			@Override
			public void onSubSequenceFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity,
					final int pIndex) {
				if (mPathModifierListener != null) {
					mPathModifierListener.onPathWaypointFinished(mParent, pEntity, pIndex);
				}
			}
		};
	}

	private void setupModifierListener() {
		this.mModifierListener = new IEntityModifierListener() {
			@Override
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pEntity) {
				mParent.onModifierStarted(pEntity);
				if (mPathModifierListener != null) {
					mPathModifierListener.onPathStarted(mParent, pEntity);
				}
			}

			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
				mParent.onModifierFinished(pEntity);
				if (mPathModifierListener != null) {
					mPathModifierListener.onPathFinished(mParent, pEntity);
				}
			}
		};
	}

	private void setupSequenceModifer() {
		this.mSequenceModifier = new SequenceModifier<IEntity>(this.mSubSequenceModifierListener,
				this.mModifierListener, this.mMoveModifiers);
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
