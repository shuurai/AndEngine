package org.andengine.entity.shape;

import org.andengine.input.touch.TouchEvent;

/**
 * When wanting to override a touch area you can set an interface to be called
 * instead of having to override the method on creation. <br> 
 * Useful for when creating entities which are derived from {@link Shape}. <br>
 * You can set the listener by calling
 * {@link Shape#setOnTouchAreaInterface(ITouchAreaListener)} <br>
 * Reason for creating such a thing? because overriding is ugly.
 * 
 * @author Paul Robinson
 * @since 26 Sep 2012 17:55:12
 */
public interface ITouchAreaListener {
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY);
}
