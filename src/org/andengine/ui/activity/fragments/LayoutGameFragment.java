/**
 * 
 */
package org.andengine.ui.activity.fragments;

import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * <p>Fragment implementation of {@link LayoutGameActivity}</p>
 * 
 * <p>This implementation uses the {@link android.app.Fragment} from Android 3.0 (Honeycomb).
 * To use this class, you must target at least Android 3.0 (API level 11).
 * If you want to use the compatibility library for Fragments, use {@link org.andengine.ui.activity.fragments.compatibility.LayoutGameFragment}.</p>
 *
 * <p>(c) 2011 Nicolas Gramlich<br>(c) 2011 Zynga Inc.</p>
 * 
 * @author Nicolas Gramlich
 * @author Scott Kennedy
 * @since 21:30:00 - 10.08.2010
 * @author Paul Robinson
 *
 */
public abstract class LayoutGameFragment extends BaseGameFragment {
	
	/**
	 * Paul Robinson implemented this again from GLES1, I think most of this 
	 * is taken from the standard {@link BaseGameActivity}
	 * I've credited the Nicholas and Scott since they did the original GLES1
	 * implementation. I' pretty much only did a few tweaks here and there.
	 */
	
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	protected abstract int getLayoutID();
	protected abstract int getRenderSurfaceViewID();

	@Override
	protected void onSetContentView() {
		super.setContentView(this.getLayoutID());
		this.mRenderSurfaceView = (RenderSurfaceView) this.findViewById(this.getRenderSurfaceViewID());

		this.mRenderSurfaceView.setRenderer(this.mEngine, this);
	}


	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
