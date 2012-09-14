package org.andengine.util.algorithm.path.astar.tile;

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
 * @author Paul Robinson
 * @since 7 Sep 2012 19:32:59
 */
public class AStarPathTileModifier extends EntityModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final SequenceModifier<IEntity> mSequenceModifier;

	private IAStarPathTileModifierListener mPathModifierListener;

	private final Path mPath;
	/**
	 * Tile dimensions.
	 * <b>Element[0]</b> Height <b>Element[1]</b> Width
	 */
	private int[] mTileDimensions;
	/**
	 * Map X draw origin.
	 */
	private float mDrawOriginX = 0;
	/**
	 * Map Y Draw Origin
	 */
	private float mDrawOriginY = 0;
	/**
	 * Is the map isometric?
	 */
	private boolean mIsometric = true;
	/**
	 * Offset of sprite to use
	 * <b>Element[0]</b> X <b>Element[1]</b> Y
	 */
	private float[] mOffset;
	/**
	 * The segment length will always be the same on an isometric map, so just store it once.
	 */
	private float mSegmentLength = 0;
	private float modifierCount = 0;
	private MoveModifier[] moveModifiers;
	private int mCurrentIndex = 0;
	// ===========================================================
	// Constructors
	// ===========================================================
	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY, final boolean pIsometric, final float[] pSpriteOffset) {
		this(pDuration, pPath, pTileDimensions, pMapDrawOriginX, pMapDrawOriginY, pIsometric, pSpriteOffset, null, null, EaseLinear.getInstance());
		
	}
	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY, final boolean pIsometric, final float[] pSpriteOffset, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions,pMapDrawOriginX, pMapDrawOriginY,pIsometric, pSpriteOffset, null, null, pEaseFunction);
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY, final boolean pIsometric, final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener) {
		this(pDuration, pPath, pTileDimensions,pMapDrawOriginX, pMapDrawOriginY,pIsometric,  pSpriteOffset,  pEntityModiferListener, null, EaseLinear.getInstance());
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY, final boolean pIsometric, final float[] pSpriteOffset, final IAStarPathTileModifierListener pPathModifierListener) {
		this(pDuration, pPath, pTileDimensions,pMapDrawOriginX, pMapDrawOriginY, pIsometric, pSpriteOffset,  null, pPathModifierListener, EaseLinear.getInstance());
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY, final boolean pIsometric, final float[] pSpriteOffset, final IAStarPathTileModifierListener pPathModifierListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, pMapDrawOriginX, pMapDrawOriginY,pIsometric, pSpriteOffset,  null, pPathModifierListener, pEaseFunction);
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY, final boolean pIsometric, final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, pMapDrawOriginX, pMapDrawOriginY, pIsometric,pSpriteOffset,  pEntityModiferListener, null, pEaseFunction);
	}
	
	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY,final boolean pIsometric,  final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener, final IAStarPathTileModifierListener pPathModifierListener) throws IllegalArgumentException {
		this(pDuration, pPath, pTileDimensions,pMapDrawOriginX, pMapDrawOriginY, pIsometric,pSpriteOffset,  pEntityModiferListener, pPathModifierListener, EaseLinear.getInstance());
	}
	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float pMapDrawOriginX, final float pMapDrawOriginY,final boolean pIsometric, final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener, final IAStarPathTileModifierListener pPathModifierListener, final IEaseFunction pEaseFunction) throws IllegalArgumentException {
		super(pEntityModiferListener);
		final int pathSize = pPath.getLength();

		if (pathSize < 2) {
			throw new IllegalArgumentException("Path needs at least 2 waypoints!");
		}
		this.mTileDimensions = pTileDimensions;
		this.mPath = pPath;
		this.mPathModifierListener = pPathModifierListener;
		this.mDrawOriginX = pMapDrawOriginX;
		this.mDrawOriginY = pMapDrawOriginY;
		this.mIsometric = pIsometric;
		this.mOffset = pSpriteOffset;
		this.moveModifiers = new MoveModifier[pathSize - 1];
		
		float velocity = 0;
		float duration = 0;
		this.modifierCount = moveModifiers.length;
		this.mSegmentLength = this.getSegmentLength(0);
		for(int i = 0; i < this.modifierCount; i++) {
			if(this.mIsometric){
				velocity = (pPath.getLength() * this.mTileDimensions[0]) / pDuration;
				duration = this.mSegmentLength  / velocity;
				duration = pDuration;
				float[] tileCen = this.getTileCentre(i);
				float[] tileCenNeigbour = this.getTileCentre(i +1);
				moveModifiers[i] = new MoveModifier(duration, tileCen[0], tileCenNeigbour[0], tileCen[1], tileCenNeigbour[1], null, pEaseFunction);
			}else{
				velocity = (pPath.getLength() * pTileDimensions[0]) / pDuration;
				duration = getSegmentLength(i) / velocity;
				moveModifiers[i] = new MoveModifier(duration, getXCoordinates(i), getXCoordinates(i + 1), getYCoordinates(i), getYCoordinates(i + 1), null, pEaseFunction);
			}
			
		}

		/* Create a new SequenceModifier and register the listeners that
		 * call through to mEntityModifierListener and mPathModifierListener. */
		this.mSequenceModifier = new SequenceModifier<IEntity>(
				new ISubSequenceModifierListener<IEntity>() {
					@Override
					public void onSubSequenceStarted(final IModifier<IEntity> pModifier, final IEntity pEntity, final int pIndex) {
						mCurrentIndex = pIndex;
						if(pIndex < pathSize)
		                {
		            		switch(pPath.getDirectionToNextStep(pIndex)) {
				            		case UP:
				            			if(mIsometric){
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveUpRight(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}else{
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveUp(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}
				            			break;
				            		case DOWN:
				            			if(mIsometric){
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveDownLeft(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}else{
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveDown(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}
				            			break;
				            		case LEFT:
				            			if(mIsometric){
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveUpLeft(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}else{
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveLeft(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}
				            			break;
				            		case RIGHT:
				            			if(mIsometric){
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveDownRight(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}else{
				            				if(AStarPathTileModifier.this.mPathModifierListener != null) {
			        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveRight(AStarPathTileModifier.this, pEntity, pIndex);
			        						}
				            			}
				            			break;
		                            case UP_LEFT:
		                            	if(AStarPathTileModifier.this.mPathModifierListener != null) {
		        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveUpLeft(AStarPathTileModifier.this, pEntity, pIndex);
		        						}
		                                break;
		                            case UP_RIGHT:
		                            	if(AStarPathTileModifier.this.mPathModifierListener != null) {
		        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveUpRight(AStarPathTileModifier.this, pEntity, pIndex);
		        						}
	                                    break;
		                            case DOWN_LEFT:
		                            	if(AStarPathTileModifier.this.mPathModifierListener != null) {
		        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveDownLeft(AStarPathTileModifier.this, pEntity, pIndex);
		        						}
	                                    break;
		                            case DOWN_RIGHT:
		                            	if(AStarPathTileModifier.this.mPathModifierListener != null) {
		        							AStarPathTileModifier.this.mPathModifierListener.onNextMoveDownRight(AStarPathTileModifier.this, pEntity, pIndex);
		        						}
	                                	break;
		                        	default:
		                    }
		                }
						
						if(AStarPathTileModifier.this.mPathModifierListener != null) {
							AStarPathTileModifier.this.mPathModifierListener.onPathWaypointStarted(AStarPathTileModifier.this, pEntity, pIndex);
						}
					}

					@Override
					public void onSubSequenceFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity, final int pIndex) {
						if(AStarPathTileModifier.this.mPathModifierListener != null) {
							AStarPathTileModifier.this.mPathModifierListener.onPathWaypointFinished(AStarPathTileModifier.this, pEntity, pIndex);
						}
					}
				},
				new IEntityModifierListener() {
					@Override
					public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pEntity) {
						AStarPathTileModifier.this.onModifierStarted(pEntity);
						if(AStarPathTileModifier.this.mPathModifierListener != null) {
							AStarPathTileModifier.this.mPathModifierListener.onPathStarted(AStarPathTileModifier.this, pEntity);
						}
					}

					@Override
					public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
						AStarPathTileModifier.this.onModifierFinished(pEntity);
						if(AStarPathTileModifier.this.mPathModifierListener != null) {
							AStarPathTileModifier.this.mPathModifierListener.onPathFinished(AStarPathTileModifier.this, pEntity);
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

	/**
	 * Get the segment length. Can now calculate isometric paths as well. <br>
	 * For an isometric map this will always be the same.
	 * 
	 * @param pIndex {@link Integer} index of tile in path
	 * @return {@link Float} of segment length.
	 */
	private float getSegmentLength(int pIndex) {
        final int nextSegmentIndex = pIndex + 1;
        float dx = 0;
        float dy = 0;
        if(this.mIsometric){
        	float[] current = this.getTileCentre(pIndex);
        	float[] neighbour = this.getTileCentre(nextSegmentIndex);
        	dx = current[0] - neighbour[0];
            dy = current[1] - neighbour[1];
        }else{
        	dx = getXCoordinates(pIndex) - getXCoordinates(nextSegmentIndex);
            dy = getYCoordinates(pIndex) - getYCoordinates(nextSegmentIndex);
        }
        return FloatMath.sqrt(dx * dx + dy * dy);
	}

	protected AStarPathTileModifier(final AStarPathTileModifier pPathModifier) throws DeepCopyNotSupportedException  {
		this.mPath = new Path(pPathModifier.getPath().getLength());
		for(int i=0;i<pPathModifier.getPath().getLength();i++)
		{
			mPath.set(i, pPathModifier.getPath().getX(i), pPathModifier.getPath().getY(i));
		}
		this.mSequenceModifier = pPathModifier.mSequenceModifier.deepCopy();
	}
	
	@Override
	public AStarPathTileModifier deepCopy() throws DeepCopyNotSupportedException {
		return new AStarPathTileModifier(this);
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

	public IAStarPathTileModifierListener getPathModifierListener() {
		return this.mPathModifierListener;
	}

	public void setPathModifierListener(final IAStarPathTileModifierListener pPathModifierListener) {
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
	/**
	 * Get the tile centre for an isometric map.
	 * @param pIndex {@link Integer} of path index which relates to a tile.
	 * @return result of {@link #getIsoTileCentreAt(int, int)} {@link Float} array <br> <b>Element[0]</b> = X <b>Element[1]</b> = Y
	 */
	private float[] getTileCentre(final int pIndex){
		final int pTileRow = this.mPath.getY(pIndex);
		final int pTileColumn = this.mPath.getX(pIndex);
		return this.getIsoTileCentreAt(pTileColumn, pTileRow);
	}
	/**
	 * Get the X and Y coordinates of a given tile location.
	 * @param pTileColumn {@link Integer} of tile Column, this is {@link Path#getX(int)}
	 * @param pTileRow {@link Integer} of tile row, this is {@link Path#getY(int)}
	 * @return {@link Float} array <br> <b>Element[0]</b> = X <b>Element[1]</b> = Y
	 */
	private float[] getIsoTileCentreAt(final int pTileColumn, final int pTileRow){
		float firstTileXCen = this.mDrawOriginX + (this.mTileDimensions[1] /2);
		float firstTileYCen = this.mDrawOriginY + (this.mTileDimensions[0] /2);
		float isoX = 0;
		float isoY = 0;
		
		isoX = firstTileXCen - (pTileRow * (this.mTileDimensions[1] /2));
		isoY = firstTileYCen + (pTileRow * (this.mTileDimensions[0] /2));
		
		isoX = isoX + (pTileColumn * (this.mTileDimensions[1] /2));
		isoY = isoY + (pTileColumn * (this.mTileDimensions[0] /2));
		isoX += this.mOffset[0];
		isoY += this.mOffset[1];
		return new float[] { isoX, isoY };
	}
	/**
	 * Want to update the speed of the modifiers? This will update the modifiers from the current modifer in use.
	 * @param pSpeed {@link Float} speed to use.
	 * @param pX {@link Float} of entity X
	 * @param pY {@link Float} of entity Y
	 */
	public void updateSpeed(final float pSpeed, final float pX, final float pY){
		for(int i = this.mCurrentIndex; i < this.modifierCount; i++) {
			if(this.mIsometric){
				float pFromValueA = this.moveModifiers[i].getFromValueA();
				float pToValueA = this.moveModifiers[i].getToValueA();
				float pFromValueB = this.moveModifiers[i].getFromValueB();
				float pToValueB = this.moveModifiers[i].getToValueB();
				if(i == this.mCurrentIndex){
					pFromValueA = pX;
					pFromValueB = pY;
				}
				
				this.moveModifiers[i].reset(pSpeed, pFromValueA, pToValueA, pFromValueB, pToValueB);
			}
		}
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IAStarPathTileModifierListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		public void onPathStarted(final AStarPathTileModifier pPathModifier, final IEntity pEntity);
		public void onNextMoveUpRight(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveUpLeft(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveDownRight(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveDownLeft(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveLeft(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveUp(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveRight(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onNextMoveDown(AStarPathTileModifier aStarPathModifier, IEntity pEntity, int pIndex);
		public void onPathWaypointStarted(final AStarPathTileModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex);
		public void onPathWaypointFinished(final AStarPathTileModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex);
		public void onPathFinished(final AStarPathTileModifier pPathModifier, final IEntity pEntity);
	}
}
