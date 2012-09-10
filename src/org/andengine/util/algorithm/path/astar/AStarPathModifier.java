package org.andengine.util.algorithm.path.astar;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.SequenceModifier;
import org.andengine.util.modifier.SequenceModifier.ISubSequenceModifierListener;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import android.util.FloatMath;
/**
 * 
 * @author korkd
 * @see <a href="http://code.google.com/p/korkd/">korkd google code</a>
 */
public class AStarPathModifier extends EntityModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final SequenceModifier<IEntity> mSequenceModifier;

	private IAStarPathModifierListener mPathModifierListener;

	private final Path mPath;

	private int[] mTileDimensions;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions) {
		this(pDuration, pPath, pTileDimensions, null, null, EaseLinear.getInstance());
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, null, null, pEaseFunction);
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IEntityModifierListener pEntityModiferListener) {
		this(pDuration, pPath, pTileDimensions, pEntityModiferListener, null, EaseLinear.getInstance());
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IAStarPathModifierListener pPathModifierListener) {
		this(pDuration, pPath, pTileDimensions, null, pPathModifierListener, EaseLinear.getInstance());
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IAStarPathModifierListener pPathModifierListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, null, pPathModifierListener, pEaseFunction);
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IEntityModifierListener pEntityModiferListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, pEntityModiferListener, null, pEaseFunction);
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IEntityModifierListener pEntityModiferListener, final IAStarPathModifierListener pPathModifierListener) throws IllegalArgumentException {
		this(pDuration, pPath, pTileDimensions, pEntityModiferListener, pPathModifierListener, EaseLinear.getInstance());
	}

	public AStarPathModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final IEntityModifierListener pEntityModiferListener, final IAStarPathModifierListener pPathModifierListener, final IEaseFunction pEaseFunction) throws IllegalArgumentException {
		super(pEntityModiferListener);
		final int pathSize = pPath.getLength();

		if (pathSize < 2) {
			throw new IllegalArgumentException("Path needs at least 2 waypoints!");
		}
		mTileDimensions = pTileDimensions;
		this.mPath = pPath;
		this.mPathModifierListener = pPathModifierListener;

		final MoveModifier[] moveModifiers = new MoveModifier[pathSize - 1];

		final float velocity = (pPath.getLength() * pTileDimensions[0]) / pDuration;

		final int modifierCount = moveModifiers.length;
		for(int i = 0; i < modifierCount; i++) {
			final float duration = getSegmentLength(i) / velocity;
			moveModifiers[i] = new MoveModifier(duration, getXCoordinates(i), getXCoordinates(i + 1), getYCoordinates(i), getYCoordinates(i + 1), null, pEaseFunction);
		}

		/* Create a new SequenceModifier and register the listeners that
		 * call through to mEntityModifierListener and mPathModifierListener. */
		this.mSequenceModifier = new SequenceModifier<IEntity>(
				new ISubSequenceModifierListener<IEntity>() {
					@Override
					public void onSubSequenceStarted(final IModifier<IEntity> pModifier, final IEntity pEntity, final int pIndex) {
						if(pIndex < pathSize)
		                {
		            		switch(pPath.getDirectionToNextStep(pIndex)) {
		                            case DOWN:
		                            	if(AStarPathModifier.this.mPathModifierListener != null) {
		        							AStarPathModifier.this.mPathModifierListener.onNextMoveDown(AStarPathModifier.this, pEntity, pIndex);
		        						}
		                                break;
		                            case RIGHT:
		                            	if(AStarPathModifier.this.mPathModifierListener != null) {
		        							AStarPathModifier.this.mPathModifierListener.onNextMoveRight(AStarPathModifier.this, pEntity, pIndex);
		        						}
	                                    break;
		                            case UP:
		                            	if(AStarPathModifier.this.mPathModifierListener != null) {
		        							AStarPathModifier.this.mPathModifierListener.onNextMoveUp(AStarPathModifier.this, pEntity, pIndex);
		        						}
	                                    break;
		                            case LEFT:
		                            	if(AStarPathModifier.this.mPathModifierListener != null) {
		        							AStarPathModifier.this.mPathModifierListener.onNextMoveLeft(AStarPathModifier.this, pEntity, pIndex);
		        						}
	                                	break;
		                        	default:
		                        			
		                    }
		                }
						
						if(AStarPathModifier.this.mPathModifierListener != null) {
							AStarPathModifier.this.mPathModifierListener.onPathWaypointStarted(AStarPathModifier.this, pEntity, pIndex);
						}
					}

					@Override
					public void onSubSequenceFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity, final int pIndex) {
						if(AStarPathModifier.this.mPathModifierListener != null) {
							AStarPathModifier.this.mPathModifierListener.onPathWaypointFinished(AStarPathModifier.this, pEntity, pIndex);
						}
					}
				},
				new IEntityModifierListener() {
					@Override
					public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pEntity) {
						AStarPathModifier.this.onModifierStarted(pEntity);
						if(AStarPathModifier.this.mPathModifierListener != null) {
							AStarPathModifier.this.mPathModifierListener.onPathStarted(AStarPathModifier.this, pEntity);
						}
					}

					@Override
					public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
						AStarPathModifier.this.onModifierFinished(pEntity);
						if(AStarPathModifier.this.mPathModifierListener != null) {
							AStarPathModifier.this.mPathModifierListener.onPathFinished(AStarPathModifier.this, pEntity);
						}
					}
				},
				moveModifiers
		);
	}
	
	private float getXCoordinates(int pIndex) {
		return (mPath.getX(pIndex) * mTileDimensions[0]) + 4;
	}
	
	private float getYCoordinates(int pIndex) {
		return (mPath.getY(pIndex) * mTileDimensions[1]) + 4;
	}

	private float getSegmentLength(int pIndex) {

        final int nextSegmentIndex = pIndex + 1;

        final float dx = getXCoordinates(pIndex) - getXCoordinates(nextSegmentIndex);
        final float dy = getYCoordinates(pIndex) - getYCoordinates(nextSegmentIndex);

        return FloatMath.sqrt(dx * dx + dy * dy);
	}

	protected AStarPathModifier(final AStarPathModifier pPathModifier) throws DeepCopyNotSupportedException  {
		this.mPath = new Path(pPathModifier.getPath().getLength());
		for(int i=0;i<pPathModifier.getPath().getLength();i++)
		{
			mPath.set(i, pPathModifier.getPath().getX(i), pPathModifier.getPath().getY(i));
		}
		this.mSequenceModifier = pPathModifier.mSequenceModifier.deepCopy();
	}
	
	@Override
	public AStarPathModifier deepCopy() throws DeepCopyNotSupportedException {
		return new AStarPathModifier(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Path getPath() {
		return this.mPath;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean isFinished() {
		return this.mSequenceModifier.isFinished();
	}

	@Override
	public float getSecondsElapsed() {
		return this.mSequenceModifier.getSecondsElapsed();
	}

	@Override
	public float getDuration() {
		return this.mSequenceModifier.getDuration();
	}

	public IAStarPathModifierListener getPathModifierListener() {
		return this.mPathModifierListener;
	}

	public void setPathModifierListener(final IAStarPathModifierListener pPathModifierListener) {
		this.mPathModifierListener = pPathModifierListener;
	}

	@Override
	public void reset() {
		this.mSequenceModifier.reset();
	}

	@Override
	public float onUpdate(final float pSecondsElapsed, final IEntity pEntity) {
		return this.mSequenceModifier.onUpdate(pSecondsElapsed, pEntity);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IAStarPathModifierListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		public void onPathStarted(final AStarPathModifier pPathModifier, final IEntity pEntity);
		public void onNextMoveLeft(AStarPathModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveUp(AStarPathModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveRight(AStarPathModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveDown(AStarPathModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onPathWaypointStarted(final AStarPathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex);
		public void onPathWaypointFinished(final AStarPathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex);
		public void onPathFinished(final AStarPathModifier pPathModifier, final IEntity pEntity);
	}
}
