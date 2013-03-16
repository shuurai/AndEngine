package org.andengine.entity;

/**
 * Interface to call when a moving entity has changed its position and its 3D
 * world space coordinates need to be updated.
 * 
 * For use when using an isometric TMX Map.
 * @author Paul Robinson
 * @since 16 Mar 2013 12:32:59
 */
public interface IIsometricEntity3DSpaceRecalculation {
	/**
	 * This will need to call ultimately call {@link IEntity#set3DPosition(float, float, float)}
	 * 
	 * @param pEntity
	 *            {@link IEntity} which needs its 3D world space coordinates
	 *            updated.
	 * @param pDrawX
	 *            {@link Float} new X draw location
	 * @param pDrawY
	 *            {@link Float} new Y draw location
	 */
	public void recalculate(final IEntity pEntity, final float pDrawX, final float pDrawY);
}
