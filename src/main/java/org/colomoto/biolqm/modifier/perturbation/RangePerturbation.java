package org.colomoto.biolqm.modifier.perturbation;

import java.util.List;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;

/**
 * Simple perturbation to restrict the value of a component in a range.
 * 
 * @author Aurelien Naldi
 */
public class RangePerturbation extends AbstractPerturbation {

	public final int min, max;
	public final NodeInfo component;

	/**
	 * Create a range restriction.
	 * Note that the values should verify 0 &gt;= min &lt;= max &lt; nbval.
	 * if min = max, then the component will have a fixed value.
	 * Otherwise, a new function will be computed to ensure that the reached leaf belongs to the [min,max] range.
	 * 
	 * @param target the restricted component
	 * @param min the minimal value after perturbation
	 * @param max the max value after perturbation
	 */
	public RangePerturbation(NodeInfo target, int min, int max) {
		if (min < 0 || max < min || max > target.getMax()) {
			throw new RuntimeException("Invalid perturbation range for "+target+": "+min+","+max);
		}
		this.component = target;
		this.min = min;
		this.max = max;
	}

	@Override
	public void restrictValues(byte[] state, List<NodeInfo> nodeOrder) {
		int index = nodeOrder.indexOf(this.component);
		if (state[index] < this.min) {
			state[index] = (byte) this.min;
		} else if (state[index] > this.max) {
			state[index] = (byte) this.max;
		} // else keep value
	}
	
	@Override
	public void update(LogicalModel model) {
		int idx = -1;
		int[] functions = null;
		
		idx = model.getComponents().indexOf(component);
		if (idx >= 0) {
			functions = model.getLogicalFunctions();
		} else {
			idx = model.getExtraComponents().indexOf(component);
			if (idx >= 0) {
				functions = model.getExtraLogicalFunctions();
			}
		}
		
		if (idx < 0) {
			throw new RuntimeException("Perturbation.update(): Could not find the target component");
		}
		
		if (min == 0 && max == component.getMax()) {
			// no change here
			return;
		}

		int oldValue = functions[idx];
		if (min == max) {
			functions[idx] = min;
		} else {
			RangeRestrictionOperation op = new RangeRestrictionOperation(model.getMDDManager(), min, max);
			functions[idx] = op.restrict(oldValue);
		}
		
		model.getMDDManager().free(oldValue);
	}
	
	@Override
	public String toString() {
		return component.getNodeID() + " ["+min+","+max+"]";
	}

	@Override
	public 	String getStringRepresentation() {
		return component.getNodeID()+ "%" + min + ":" + max;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RangePerturbation) {
			RangePerturbation p = (RangePerturbation)o;
			return p.min == this.min && p.max == this.max && p.component.equals(this.component); 
		}
		return false;
	}

    @Override
    public boolean affectsNode(NodeInfo node) {
        return component.equals(node);
    }

}
