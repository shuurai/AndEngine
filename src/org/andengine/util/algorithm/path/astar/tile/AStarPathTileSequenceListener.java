package org.andengine.util.algorithm.path.astar.tile;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveSetZModifier;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.tile.AStarPathTileModifier.IAStarPathTileModifierListener;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.SequenceModifier;
import org.andengine.util.modifier.SequenceModifier.ISubSequenceModifierListener;

import android.util.Log;

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
	@SuppressWarnings("unused")
	/**
	 * When animating, help keep track of what modifier is currently executing. 
	 * Helps with speeding up entity (Start from current modifier till finish)
	 */
	private int mCurrentIndex = 0;
	private int mPathSize = 0;
	private Path mPath;
	private boolean mIsometric = true;
	private AStarPathTileModifier mParent;
	private MoveSetZModifier[] mMoveModifiers;
	private SequenceModifier<IEntity> mSequenceModifier;
	private boolean mStop = false;
	private int mStopCount = 0;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AStarPathTileSequenceListener(final IAStarPathTileModifierListener pPathModifierListener,
			final MoveSetZModifier[] pMoveModifiers, final Path pPath, final boolean pIsometric,
			final AStarPathTileModifier pParent) {
		this.mPath = pPath;
		this.mPathSize = this.mPath.getLength();
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

	public void stop() {
		this.mStop = true;
	}

	private void setupSubSequenceModifierListener() {
		this.mSubSequenceModifierListener = new ISubSequenceModifierListener<IEntity>() {
			@Override
			public void onSubSequenceStarted(final IModifier<IEntity> pModifier, final IEntity pEntity, final int pIndex) {
				mCurrentIndex = pIndex;
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

				if (mPathModifierListener != null) {
					mPathModifierListener.onPathWaypointStarted(mParent, pEntity, pIndex);
				}
			}

			@Override
			public void onSubSequenceFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity,
					final int pIndex) {
				if (mPathModifierListener != null) {
					if (mStop) {
						Log.i("AStarPathTileSequenceListener", "---STOPPED");
						mStopCount++;
						// mPathModifierListener.onPathFinished(mParent,
						// pEntity);
						if (mStopCount == 1) {
							mParent.finished();
							mModifierListener.onModifierFinished(pEntityModifier, pEntity);
							// mPathModifierListener.onPathFinished(mParent,
							// pEntity);
						}
					} else {
						mPathModifierListener.onPathWaypointFinished(mParent, pEntity, pIndex);
					}
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
				Log.i("AStarPathTileSequenceListener", "---onModifierFinished");
				mParent.onModifierFinished(pEntity);
				// mParent.onModifierFinished(pEntity);
				if (mPathModifierListener != null) {
					Log.i("AStarPathTileSequenceListener", "---mPathModifierListener");
					if (mStop) {
						Log.i("AStarPathTileSequenceListener", "---mStop");
						if (mStopCount == 1) {
							Log.i("AStarPathTileSequenceListener", "---mStopCount");
							mPathModifierListener.onPathFinished(mParent, pEntity);
							mPathModifierListener = null;
							mModifierListener = null;
						}
					}else{
						Log.i("AStarPathTileSequenceListener", "---mStop-FALSE");
						mPathModifierListener.onPathFinished(mParent, pEntity);
					}
					
					
					// mPathModifierListener.onPathFinished(mParent, pEntity);
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
