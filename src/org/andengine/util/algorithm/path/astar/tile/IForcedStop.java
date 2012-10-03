package org.andengine.util.algorithm.path.astar.tile;

/**
 * This is to be use between {@link AStarPathTileModifier} and {@link AStarPathTileSequenceListener} <br>
 * It is so the Listener can inform the main modifier, that if stopped, it has finished its sub sequence.
 * @author Paul Robinson
 * @since 3 Oct 2012 14:28:14
 */
public interface IForcedStop{
	/**
	 * Finished in action sub sequence
	 */
	public void finished();
}