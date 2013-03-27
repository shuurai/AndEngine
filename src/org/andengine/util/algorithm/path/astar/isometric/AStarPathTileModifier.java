package org.andengine.util.algorithm.path.astar.isometric;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.modifier.SequenceModifier;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import android.util.FloatMath;

/**
 * A Star path modifier for isometric TMX Map. <br>
 * <br>
 * 
 * @author korkd
 * @see <a href="http://code.google.com/p/korkd/">korkd google code</a>
 * @author Paul Robinson
 * @since 7 Sep 2012 19:32:59
 */
public class AStarPathTileModifier extends EntityModifier implements IForcedStop {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final SequenceModifier<IEntity> mSequenceModifier;
	private IAStarPathTileModifierListener mPathModifierListener;
	private AStarPathTileSequenceListener mAStarPathTileSequenceListner;
	private final Path mPath;
	/**
	 * Tile dimensions. <b>Element[0]</b> Height <b>Element[1]</b> Width
	 */
	private int[] mTileDimensions;
	/**
	 * Offset of sprite to use <b>Element[0]</b> X <b>Element[1]</b> Y
	 */
	private float[] mOffset;
	/**
	 * The segment length will always be the same on an isometric map with no
	 * diagonal movement, so just store it once.
	 */
	@SuppressWarnings("unused")
	private float mSegmentLength = 0;
	private float modifierCount = 0;
	private MoveModifier[] moveModifiers;
	private float mStandardSpeedPerModifier = 0;
	private float mCurrentSpeedPerModifier = 0;
	/**
	 * Has this been stopped, i.e. once it sub sequence has finished.
	 */
	private boolean mStopped = false;

	// ===========================================================
	// Constructors
	// ===========================================================
	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions, final float[] pSpriteOffset) {
		this(pDuration, pPath, pTileDimensions, pSpriteOffset, null, null, EaseLinear.getInstance());

	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, pSpriteOffset, null, null, pEaseFunction);
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener) {
		this(pDuration, pPath, pTileDimensions, pSpriteOffset, pEntityModiferListener, null, EaseLinear.getInstance());
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IAStarPathTileModifierListener pPathModifierListener) {
		this(pDuration, pPath, pTileDimensions, pSpriteOffset, null, pPathModifierListener, EaseLinear.getInstance());
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IAStarPathTileModifierListener pPathModifierListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions,pSpriteOffset, null, pPathModifierListener, pEaseFunction);
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener, final IEaseFunction pEaseFunction) {
		this(pDuration, pPath, pTileDimensions, pSpriteOffset, pEntityModiferListener, null, pEaseFunction);
	}

	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener,
			final IAStarPathTileModifierListener pPathModifierListener) throws IllegalArgumentException {
		this(pDuration, pPath, pTileDimensions, pSpriteOffset,  pEntityModiferListener, pPathModifierListener, EaseLinear.getInstance());
	}

	/**
	 * 
	 * @param pDuration
	 *            {@link Float} Duration of each modifier.
	 * @param pPath
	 *            {@link Path} produced by {@link AStarPathFinderTileBased} to
	 *            translate and execute.
	 * @param pTileDimensions
	 *            {@link Integer} array of tile size. <b>Element[0]</b> Height
	 *            <b>Element[1]</b> Width
	 * @param pSpriteOffset
	 *            {@link Float} array of sprite offset (or pass {0,0} if not
	 *            using) helps placing the sprite later on.
	 * @param pEntityModiferListener
	 * @param pPathModifierListener
	 *            {@link IAStarPathTileModifierListener} to help animate the
	 *            sprite.
	 * @param pEaseFunction
	 * @throws IllegalArgumentException
	 */
	public AStarPathTileModifier(final float pDuration, final Path pPath, final int[] pTileDimensions,
			final float[] pSpriteOffset, final IEntityModifierListener pEntityModiferListener,
			final IAStarPathTileModifierListener pPathModifierListener, final IEaseFunction pEaseFunction)
			throws IllegalArgumentException {
		super(pEntityModiferListener);
		final int pathSize = pPath.getLength();
		if (pathSize < 2) {
			throw new IllegalArgumentException("Path needs at least 2 waypoints!");
		}
		this.mTileDimensions = pTileDimensions;
		this.mPath = pPath;
		this.mPathModifierListener = pPathModifierListener;
		this.mOffset = pSpriteOffset;
		this.moveModifiers = new MoveModifier[pathSize - 1];
		float duration = 0;
		this.modifierCount = this.moveModifiers.length;
		this.mSegmentLength = this.getSegmentLength(0);
		for (int i = 0; i < this.modifierCount; i++) {
			duration = pDuration;
			this.mStandardSpeedPerModifier = duration;
			this.mCurrentSpeedPerModifier = this.mStandardSpeedPerModifier;

			float[] tileCen = this.getTileCentre(i);
			float[] tileCenNeigbour = this.getTileCentre(i + 1);

			this.moveModifiers[i] = new MoveModifier(duration, tileCen[0], tileCen[1], tileCenNeigbour[0], tileCenNeigbour[1]);
		}
		EntityModifier pEntityModifier = (EntityModifier)this;
		this.mAStarPathTileSequenceListner = new AStarPathTileSequenceListener(this.mPathModifierListener,
				this.moveModifiers, pPath, pEntityModifier);
		
		this.mSequenceModifier = this.mAStarPathTileSequenceListner.getSequenceModifier();
	}

	/**
	 * Get the segment length. Can now calculate isometric paths as well. <br>
	 * For an isometric map this will always be the same. So call one.
	 * 
	 * @param pIndex
	 *            {@link Integer} index of tile in path
	 * @return {@link Float} of segment length.
	 */
	private float getSegmentLength(int pIndex) {
		final int nextSegmentIndex = pIndex + 1;
		float dx = 0;
		float dy = 0;
		float[] current = this.getTileCentre(pIndex);
		float[] neighbour = this.getTileCentre(nextSegmentIndex);
		dx = current[0] - neighbour[0];
		dy = current[1] - neighbour[1];
		return FloatMath.sqrt(dx * dx + dy * dy);
	}

	protected AStarPathTileModifier(final AStarPathTileModifier pPathModifier) throws DeepCopyNotSupportedException {
		this.mPath = new Path(pPathModifier.getPath().getLength());
		for (int i = 0; i < pPathModifier.getPath().getLength(); i++) {
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
		if (this.mStopped) {
			return 0f;
		} else {
			return this.mSequenceModifier.onUpdate(pSecondsElapsed, pEntity);
		}
	}

	// ===========================================================
	// IForcedStop
	// ===========================================================
	@Override
	public void finished() {
		this.mStopped = true;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	/**
	 * Get the current speed used for each modifier.
	 * 
	 * @return {@link Float} of current speed used for each modifier.
	 */
	public float getCurrentSpeedPerModifier() {
		return this.mCurrentSpeedPerModifier;
	}

	/**
	 * Get the tile centre for an isometric map. If there is an offset and draw
	 * origin it is applied.
	 * 
	 * @param pIndex
	 *            {@link Integer} of path index which relates to a tile.
	 * @return result of {@link #getIsoTileCentreAt(int, int)} {@link Float}
	 *         array <br>
	 *         <b>Element[0]</b> = X <b>Element[1]</b> = Y
	 */
	public float[] getTileCentre(final int pIndex) {
		/*
		 * pTileRow was getY,
		 * pTileColumn was getX
		 */
		final int pTileRow = this.mPath.getX(pIndex);
		final int pTileColumn = this.mPath.getY(pIndex);
		return this.getIsoTileCentreAt(pTileColumn, pTileRow);
	}

	/**
	 * Get the X and Y coordinates of a given tile location. If there is an
	 * offset and draw origin it is applied.
	 * 
	 * @param pTileColumn
	 *            {@link Integer} of tile Column, this is {@link Path#getX(int)}
	 * @param pTileRow
	 *            {@link Integer} of tile row, this is {@link Path#getY(int)}
	 * @return {@link Float} array <br>
	 *         <b>Element[0]</b> = X <b>Element[1]</b> = Y
	 */
	private float[] getIsoTileCentreAt(final int pTileColumn, final int pTileRow) {
		float firstTileXCen = (this.mTileDimensions[1] / 2);
		float firstTileYCen =- (this.mTileDimensions[0] / 2);
		float isoX = 0;
		float isoY = 0;

		isoX = firstTileXCen - (pTileRow * (this.mTileDimensions[1] / 2));
		isoY = firstTileYCen - (pTileRow * (this.mTileDimensions[0] / 2));

		isoX = isoX + (pTileColumn * (this.mTileDimensions[1] / 2));
		isoY = isoY - (pTileColumn * (this.mTileDimensions[0] / 2));
		isoX += this.mOffset[0];
		isoY += this.mOffset[1];
		
		return new float[] { isoX, isoY };
	}

	public void stop() {
		this.mSequenceModifier.stopOnNextModifier();
	}

	/**
	 * Want to update the speed of the modifiers? This will update the modifiers
	 * from the current modifier in use.
	 * 
	 * @param pSpeed
	 *            {@link Float} speed to use.
	 * @param pX
	 *            {@link Float} of entity X
	 * @param pY
	 *            {@link Float} of entity Y
	 */
	public void updateSpeed(final float pSpeed, final float pX, final float pY) {
		this.mCurrentSpeedPerModifier = pSpeed;
		for (int i = this.mAStarPathTileSequenceListner.getCurrentIndex(); i < this.modifierCount; i++) {
			float pFromValueA = this.moveModifiers[i].getFromValueA();
			float pToValueA = this.moveModifiers[i].getToValueA();
			float pFromValueB = this.moveModifiers[i].getFromValueB();
			float pToValueB = this.moveModifiers[i].getToValueB();
			if (i == this.mAStarPathTileSequenceListner.getCurrentIndex()) {
				pFromValueA = pX;
				pFromValueB = pY;
			}
			this.moveModifiers[i].reset(pSpeed, pFromValueA, pToValueA, pFromValueB, pToValueB);
		}
	}

	/**
	 * Get the current path index being executed
	 * 
	 * @return {@link Integer} of path index.
	 */
	public int getCurrentPathIndex() {
		return this.mAStarPathTileSequenceListner.getCurrentIndex();
	}

	public MoveModifier getCurrentModifier() {
		return this.moveModifiers[this.getCurrentPathIndex()];
	}

	public MoveModifier[] getModifers(){
		return this.moveModifiers;
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	/**
	 * Supports Isometric movement.
	 * 
	 * @author Paul Robinson
	 * @since 20 Sep 2012 18:52:19
	 */
	public static interface IAStarPathTileModifierListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		public void onPathStarted(final EntityModifier pPathModifier, final IEntity pEntity);

		public void onNextMoveUpRight(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveUpLeft(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveDownRight(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveDownLeft(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveLeft(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveUp(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveRight(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onNextMoveDown(EntityModifier aStarPathModifier, IEntity pEntity, int pIndex);

		public void onPathWaypointStarted(final EntityModifier pPathModifier, final IEntity pEntity,
				final int pWaypointIndex);

		public void onPathWaypointFinished(final EntityModifier pPathModifier, final IEntity pEntity,
				final int pWaypointIndex);

		public void onPathFinished(final EntityModifier pPathModifier, final IEntity pEntity);
	}

	/**
	 * Get the Z Order for a given location, allows a custom Z numbering system
	 * to be implemented.
	 * 
	 * @author Paul Robinson
	 * @since 20 Sep 2012 18:52:15
	 */
	public static interface IZOrderMethod {
		/**
		 * Get the Z index for a given coordinate
		 * 
		 * @param pXLocation
		 *            {@link Float} X coordinate
		 * @param pYLocation
		 *            {@link Float} Y coordinate
		 * @return {@link Integer} Z index to use.
		 */
		public int getZOrder(final float pXLocation, final float pYLocation);

		/**
		 * Get the Z index for a given tile location.
		 * 
		 * @param pRow
		 *            {@link Integer} Tile Row
		 * @param pColumn
		 *            {@link Integer} Tile Column
		 * @return {@link Integer} Z index to use.
		 */
		public int getZOrder(final int pRow, final int pColumn);
	}

}
