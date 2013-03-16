package org.andengine.entity;

/**
 * 
 * @author Paul Robinson
 * @since 16 Mar 2013 12:37:00
 */
public interface IIsometricEntity {
	/**
	 * Set the 3D world position. <br>
	 * If you were to place a cube on a tile, the origin point would be the top
	 * tip of the tile. <br>
	 * These variables need to be relation in to the map, If you were on tile
	 * (1,1) then the top point would be (32,32) if you were on tile (2,2) the
	 * top point would be (64,64) <br>
	 * See the map on <a href=
	 * "https://github.com/Niffy/AndEngineTMXTiledMapExtension/wiki/polypoints"
	 * >Niffy Isometric poly points</a> to understand about the the coordinates
	 * 
	 * <br>
	 * Currently Z has only been tested as 0, so a human or a house has an 3DZ
	 * position of 0, if you wanted something such as an overhanging tree to
	 * render over the house, then in theory increasing the tree 3DZ should make
	 * this possible.
	 * 
	 * @param p3DX
	 * @param p3DY
	 * @param p3DZ
	 */
	public void set3DPosition(final float p3DX, final float p3DY, final float p3DZ);

	/**
	 * Set the 3D size
	 * 
	 * @param p3DWidth
	 *            Is the view of the left hand side face, if you were looking at
	 *            a cube
	 * @param p3DLength
	 *            Is the view on the right hand side face, if you were looking
	 *            at a cube
	 * @param p3DHeight
	 *            Is the intended height of the object.
	 */
	public void set3DSize(final int p3DWidth, final int p3DLength, final int p3DHeight);

	/**
	 * Is this entity isometric which has children and needs to be sorted?
	 * 
	 * @return <code>true</code> for yes, <code>false</code> for no
	 */
	public boolean isIsometricSoSort();

	/**
	 * Set if this entity is isometric and has kids that needs to be sorted. <br>
	 * <b>If you have an isometric scene then this should be called!<b>
	 * 
	 * @param pIsometrc
	 *            <code>true</code> for yes, <code>false</code> for no.
	 */
	public void setIsometricToSort(boolean pIsometrc);

	public int get3DWidth();

	public int get3DLength();

	public int get3DHeight();

	public float get3DX();

	public float get3DY();

	public float get3DZ();

	/**
	 * Set to true if this item should be skipped being sorted and use the its
	 * default Z Index, useful if we want to add tiles or grids under the scene
	 * sprites. <br>
	 * <b>When attaching TMXLayers make sure you call this with
	 * <code>true</code></b>
	 * 
	 * @param pSkip
	 *            <code>true</code> to skip, <code>false</code> to sort
	 */
	public void setSkipSort(boolean pSkip);

	/**
	 * Should we skip sorting this object and use it Z index already set?
	 * 
	 * @return <code>true</code> to skip, <code>false</code> to sort
	 */
	public boolean getSkipSort();

	/**
	 * Should this entity recalculate its 3D position when calling
	 * {@link IEntity#setPosition(float, float)}? <br>
	 * this is intended for entities which move. <br>
	 * <b>Remember to call
	 * {@link #setRecalculate3DSpaceXYZ(IIsometricEntity3DSpaceRecalculation)}
	 * </b>
	 * 
	 * @param pBoolean
	 *            <code>true</code> to recalculate, <code>false</code> not to
	 *            recalculate
	 */
	public void recalculate3DSpace(boolean pBoolean);

	/**
	 * Does this entity need to recalculate its 3D Space position when
	 * {@link IEntity#setPosition(float, float)} has been called?
	 * 
	 * @return <code>true</code> for it does, <code>false</code> for it doesn't
	 */
	public boolean doesRecalculate3DSpace();

	/**
	 * When {@link #doesRecalculate3DSpace()} is true, what object should it
	 * call to recalculate its object?
	 * 
	 * @param pIsometricEntity3DSpaceRecalculation
	 *            {@link IIsometricEntity3DSpaceRecalculation} to call to
	 *            recalculate entity 3D space postion.
	 */
	public void setRecalculate3DSpaceXYZ(final IIsometricEntity3DSpaceRecalculation pIsometricEntity3DSpaceRecalculation);

	/**
	 * 
	 * @return {@link IIsometricEntity3DSpaceRecalculation} which is
	 *         recalculating the entity 3D space postion.
	 */
	public IIsometricEntity3DSpaceRecalculation getRecalculate3DSpaceXYZ();
}
