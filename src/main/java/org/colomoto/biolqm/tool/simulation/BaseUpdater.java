package org.colomoto.biolqm.tool.simulation;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.LogicalModelUpdater;

/**
 * Base class for all updaters.
 *
 * @author Aurelien Naldi
 */
abstract public class BaseUpdater implements LogicalModelUpdater {

    protected final LogicalModel model;
    protected final int size;

    public BaseUpdater(LogicalModel model) {
        this.model = model;
        this.size = model.getComponents().size();
    }

    /**
     * Test if a component is ready to change its state.
     *
     * @param state the base state
     * @param index the index of the component
     * @return the state to change to.
     */
    protected int nodeChange(byte[] state, int index) {
        byte curState = state[index];
        byte nextState = model.getTargetValue(index, state);

        // Simply return the next state since we want to update immediately to that state
        return nextState;
    }

    public LogicalModel getModel() {
        return model;
    }

    /**
     * Create or update the next state.
     *
     * @param state the base state
     * @param idx index of the position to update
     * @param change change for this position
     * @param next a previously created next state (null if it is still unchanged)
     *
     * @return an updated next state, cloned from the reference state if needed
     */
    protected byte[] update(byte[] state, int idx, int change, byte[] next) {
        if (next == null) {
            next = state.clone();
        }
        
        byte newState = (byte) (change & 0xFF);
        //System.out.println("t: " + Integer.toString(t));
        next[idx] = newState;

        return next;
    }

}
