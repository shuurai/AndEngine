package org.andengine.entity.modifier;

import org.andengine.entity.IEntity;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;
/**
 * Move an entity and set it Z index at a certain location.
 * @author Paul Robinson
 * @since 20 Sep 2012 19:14:32
 */
public class MoveSetZModifier extends DoubleValueSpanEntityModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private int mFromZ = 0;
	private int mToZ = 0;
	/**
	 * When the Z should change over. <br>
	 * Element[0] = X <br>
	 * Element[1] = Y
	 */
	private float[] mChangeOver = {0,0};
	// ===========================================================
	// Constructors
	// ===========================================================
	/**
	 * 
	 * @param pDuration
	 * @param pFromX
	 * @param pToX
	 * @param pFromY
	 * @param pToY
	 * @param pFromZ {@link Integer} of Z index {@link IEntity} should have at start of modifier;
	 * @param pToZ {@link Integer} of Z index {@link IEntity} should have at change over;
	 * @param pXYChangeOver {@link Float} array containing X and Y location of Z switch over. Element[0] = X, Element[1] = Y
	 */
	public MoveSetZModifier(final float pDuration, final float pFromX, final float pToX, final float pFromY, final float pToY, final int pFromZ, final int pToZ, final float[] pXYChangeOver) {
		this(pDuration, pFromX, pToX, pFromY, pToY, pFromZ, pToZ, pXYChangeOver, null, EaseLinear.getInstance());
	}
	/**
	 * 
	 * @param pDuration
	 * @param pFromX
	 * @param pToX
	 * @param pFromY
	 * @param pToY
	 * @param pFromZ {@link Integer} of Z index {@link IEntity} should have at start of modifier;
	 * @param pToZ {@link Integer} of Z index {@link IEntity} should have at change over;
	 * @param pXYChangeOver {@link Float} array containing X and Y location of Z switch over. Element[0] = X, Element[1] = Y
	 * @param pEaseFunction
	 */
	public MoveSetZModifier(final float pDuration, final float pFromX, final float pToX, final float pFromY, final float pToY, final int pFromZ, final int pToZ, final float[] pXYChangeOver, final IEaseFunction pEaseFunction) {
		this(pDuration, pFromX, pToX, pFromY, pToY, pFromZ, pToZ, pXYChangeOver, null, pEaseFunction);
	}
	/**
	 * 
	 * @param pDuration
	 * @param pFromX
	 * @param pToX
	 * @param pFromY
	 * @param pToY
	 * @param pFromZ {@link Integer} of Z index {@link IEntity} should have at start of modifier;
	 * @param pToZ {@link Integer} of Z index {@link IEntity} should have at change over;
	 * @param pXYChangeOver {@link Float} array containing X and Y location of Z switch over. Element[0] = X, Element[1] = Y
	 * @param pEntityModifierListener
	 */
	public MoveSetZModifier(final float pDuration, final float pFromX, final float pToX, final float pFromY, final float pToY, final int pFromZ, final int pToZ, final float[] pXYChangeOver, final IEntityModifierListener pEntityModifierListener) {
		super(pDuration, pFromX, pToX, pFromY, pToY, pEntityModifierListener, EaseLinear.getInstance());
		this.mFromZ = pFromZ;
		this.mToZ = pToZ;
		this.mChangeOver = pXYChangeOver;
	}
	/**
	 * 
	 * @param pDuration
	 * @param pFromX
	 * @param pToX
	 * @param pFromY
	 * @param pToY
	 * @param pFromZ {@link Integer} of Z index {@link IEntity} should have at start of modifier;
	 * @param pToZ {@link Integer} of Z index {@link IEntity} should have at change over;
	 * @param pXYChangeOver {@link Float} array containing X and Y location of Z switch over. Element[0] = X, Element[1] = Y
	 * @param pEntityModifierListener
	 * @param pEaseFunction
	 */
	public MoveSetZModifier(final float pDuration, final float pFromX, final float pToX, final float pFromY, final float pToY, final int pFromZ, final int pToZ, final float[] pXYChangeOver, final IEntityModifierListener pEntityModifierListener, final IEaseFunction pEaseFunction) {
		super(pDuration, pFromX, pToX, pFromY, pToY, pEntityModifierListener, pEaseFunction);
		this.mFromZ = pFromZ;
		this.mToZ = pToZ;
		this.mChangeOver = pXYChangeOver;
	}

	protected MoveSetZModifier(final MoveSetZModifier pMoveModifier) {
		super(pMoveModifier);
	}

	@Override
	public MoveSetZModifier deepCopy(){
		return new MoveSetZModifier(this);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onSetInitialValues(final IEntity pEntity, final float pX, final float pY) {
		pEntity.setPosition(pX, pY);
		pEntity.setZIndex(this.mFromZ);
	}

	@Override
	protected void onSetValues(final IEntity pEntity, final float pPercentageDone, final float pX, final float pY) {
		pEntity.setPosition(pX, pY);
		if(pX == this.mChangeOver[0] && pY == this.mChangeOver[1]){
			pEntity.setZIndex(this.mToZ);
		}
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
