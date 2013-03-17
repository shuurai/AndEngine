package org.andengine.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import org.andengine.util.adt.list.IList;
import org.andengine.util.algorithm.sort.InsertionSorter;

/**
 * Used for when the entity is Isometric and has children, this is intended to
 * be for a scene and has not been tested to sorting an entity with children.<br>
 * TODO There is scope for performance improvements.
 * <br>
 * <a href=
 * "http://code.google.com/p/as3isolib/source/browse/trunk/fp10/src/as3isolib/display/renderers/DefaultSceneLayoutRenderer.as"
 * >AS3IsoLib</a> {@link } <br>
 * (c) Nicolas Gramlich 2010 (c) Zynga 2011
 * 
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 12:08:56 - 06.08.2010
 * 
 */
public class ZIsoIndexSorter extends InsertionSorter<IEntity> {
	// ===========================================================
	// Constants
	// ===========================================================

	private static ZIsoIndexSorter INSTANCE;
	/*
	 * line 38 in InsertionSorter is the method we're calling
	 */
	// ===========================================================
	// Fields
	// ===========================================================

	private final Comparator<IEntity> mZIndexComparator = new Comparator<IEntity>() {
		@Override
		public int compare(final IEntity pEntityA, final IEntity pEntityB) {
			return pEntityA.getZIndex() - pEntityB.getZIndex();
		}
	};

	protected int mDepth = 0;
	protected LinkedHashMap<IEntity, Boolean> mVisited;
	protected LinkedHashMap<IEntity, IEntity[]> mDic;
	protected ArrayList<IEntity> behind;

	// ===========================================================
	// Constructors
	// ===========================================================

	private ZIsoIndexSorter() {

	}

	public static ZIsoIndexSorter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ZIsoIndexSorter();
		}
		return INSTANCE;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void sort(final IEntity[] pEntities) {
		this.sort(pEntities, this.mZIndexComparator);
	}

	public void sort(final IEntity[] pEntities, final int pStart, final int pEnd) {
		this.sort(pEntities, pStart, pEnd, this.mZIndexComparator);
	}

	public void sort(final List<IEntity> pEntities) {
		this.sort(pEntities, this.mZIndexComparator);
	}

	public void sort(final List<IEntity> pEntities, final int pStart, final int pEnd) {
		this.sort(pEntities, pStart, pEnd, this.mZIndexComparator);
	}

	public void sort(final IList<IEntity> pEntities) {
		this.sort(pEntities, this.mZIndexComparator);
	}

	public void sort(final IList<IEntity> pEntities, final int pStart, final int pEnd) {
		this.sort(pEntities, pStart, pEnd, this.mZIndexComparator);
	}

	@Override
	public void sort(List<IEntity> pList, int pStart, int pEnd, Comparator<IEntity> pComparator) {
		if (this.mDic == null) {
			this.mDic = new LinkedHashMap<IEntity, IEntity[]>();
		}
		if (this.mVisited == null) {
			this.mVisited = new LinkedHashMap<IEntity, Boolean>();
		}
		if (this.behind == null) {
			this.behind = new ArrayList<IEntity>();
		}
		this.mDic.clear();
		this.mVisited.clear();
		this.behind.clear();

		for (int i = pStart + 1; i < pEnd; i++) {
			behind.clear();
			IEntity pCurrent = pList.get(i);
			if (pCurrent.getSkipSort()) {
				continue;
			}
			final float rightA = pCurrent.get3DX() + pCurrent.get3DWidth();
			final float frontA = pCurrent.get3DY() + pCurrent.get3DLength();
			final float topA = pCurrent.get3DZ() + pCurrent.get3DHeight();
			for (int j = 0; j < pEnd; j++) {
				IEntity pInner = pList.get(j);
				if (pInner.getSkipSort()) {
					continue;
				}
				if (pInner.get3DX() < rightA && pInner.get3DY() < frontA && pInner.get3DZ() < topA && i != j) {
					behind.add(pInner);
				}
			}
			if (behind.size() > 0) {
				final IEntity[] pBehindArray = new IEntity[behind.size()];
				behind.toArray(pBehindArray);
				this.mDic.put(pCurrent, pBehindArray);
			}
		}
		this.mDepth = 100;
		for (IEntity iEntity : pList) {
			if (iEntity.getSkipSort()) {
				continue;
			}
			if (!this.mVisited.containsKey(iEntity)) {
				this.place(iEntity);
			}
		}

		super.sort(pList, pStart, pEnd, pComparator);
	}

	public void place(IEntity pEntity) {
		this.mVisited.put(pEntity, true);
		final IEntity[] pBehind = this.mDic.get(pEntity);
		if (pBehind != null) {
			for (IEntity item : pBehind) {
				if (!this.mVisited.containsKey(item)) {
					this.place(item);
				}
			}
		}
		
		//Do we need this?
		if(this.mDepth != pEntity.getZIndex()){
			pEntity.setZIndexWithoutSort(this.mDepth);
		}
		
		//pEntity.setZIndexWithoutSort(this.mDepth);

		this.mDepth++;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}