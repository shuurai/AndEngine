package org.andengine.util.texturepack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.SparseArray;

/**
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:34:23 - 15.08.2011
 */
public class TexturePackTextureRegionLibrary {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final SparseArray<TexturePackTextureRegion> mIDMapping;
	private final HashMap<String, TexturePackTextureRegion> mSourceMapping;
	/**
	 * This will store an {@link TexturePackTextureRegion} ID mapped to its
	 * {@link String} filename.<br>
	 * <b>Key:</b> {@link String} <i> of source</i> <br>
	 * <b>Value:</b> {@link Integer} <i> of source ID</i>
	 * 
	 * @author Paul Robinson
	 */
	private final HashMap<String, Integer> mIDToStringMap;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TexturePackTextureRegionLibrary(final int pInitialCapacity) {
		this.mIDMapping = new SparseArray<TexturePackTextureRegion>(pInitialCapacity);
		this.mSourceMapping = new HashMap<String, TexturePackTextureRegion>(pInitialCapacity);
		this.mIDToStringMap = new HashMap<String, Integer>(pInitialCapacity);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public SparseArray<TexturePackTextureRegion> getIDMapping() {
		return this.mIDMapping;
	}

	public HashMap<String, TexturePackTextureRegion> getSourceMapping() {
		return this.mSourceMapping;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void put(final TexturePackTextureRegion pTexturePackTextureRegion) {
		this.throwOnCollision(pTexturePackTextureRegion);

		this.mIDMapping.put(pTexturePackTextureRegion.getID(), pTexturePackTextureRegion);
		this.mSourceMapping.put(pTexturePackTextureRegion.getSource(), pTexturePackTextureRegion);
		this.mIDToStringMap.put(pTexturePackTextureRegion.getSource(), pTexturePackTextureRegion.getID());
	}

	public void remove(final int pID) {
		this.mIDMapping.remove(pID);
	}

	public TexturePackTextureRegion get(final int pID) {
		return this.mIDMapping.get(pID);
	}

	public TexturePackTextureRegion get(final String pSource) {
		return this.mSourceMapping.get(pSource);
	}

	public TexturePackTextureRegion get(final String pSource, final boolean pStripExtension) {
		if (pStripExtension) {
			final int indexOfExtension = pSource.lastIndexOf('.');
			if (indexOfExtension == -1) {
				return this.get(pSource);
			} else {
				final String stripped = pSource.substring(0, indexOfExtension);
				return this.mSourceMapping.get(stripped);
			}
		} else {
			return this.get(pSource);
		}
	}

	/**
	 * Get the source related to a given ID.
	 * 
	 * @param pID
	 *            {@link Integer} of texture ID.
	 * @return {@link String} of source related to texture ID>
	 */
	public String getStringSource(final int pID) {
		if (this.mIDToStringMap.containsValue(pID)) {
			Iterator<Entry<String, Integer>> entries = this.mIDToStringMap.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, Integer> entry = entries.next();
				if (entry.getValue().equals(pID)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	/**
	 * Get the integer related to a given source.
	 * 
	 * @param pSource
	 *            {@link String} of Source.
	 * 
	 * @return {@link Integer} mapped to source
	 */
	public int getIDSource(final String pSource) {
		return this.mIDToStringMap.get(pSource);
	}

	private void throwOnCollision(final TexturePackTextureRegion pTexturePackTextureRegion)
			throws IllegalArgumentException {
		if (this.mIDMapping.get(pTexturePackTextureRegion.getID()) != null) {
			throw new IllegalArgumentException("Collision with ID: '" + pTexturePackTextureRegion.getID() + "'.");
		} else if (this.mSourceMapping.get(pTexturePackTextureRegion.getSource()) != null) {
			throw new IllegalArgumentException("Collision with Source: '" + pTexturePackTextureRegion.getSource()
					+ "'.");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
