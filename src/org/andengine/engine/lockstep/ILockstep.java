package org.andengine.engine.lockstep;

public interface ILockstep {
	/**
	 * For use by the engine to
	 * 
	 * @param pNanosecondsElapsed
	 */
	public void onUpdate(final float pNanosecondsElapsed);
}
