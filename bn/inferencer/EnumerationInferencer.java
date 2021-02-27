package bn.inferencer;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.CPT;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.base.BooleanDomain;
import bn.base.BooleanValue;
import bn.base.NamedVariable;
import bn.util.ArraySet;

public class EnumerationInferencer implements bn.core.Inferencer{
	
	public EnumerationInferencer() {
		
	}
	
	@Override
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
		
		bn.base.Distribution answer = new bn.base.Distribution(X);
		
		for(bn.core.Value value : X.getDomain()) {
			e.put(X, value);
			answer.set(value,Enumerate_All(network.getVariablesSortedTopologically(),e,network));
		}
		
		answer.normalize();
		return answer;
	}
	
	public double Enumerate_All(List<RandomVariable> vars, Assignment e, BayesianNetwork network) {
		
		if(vars.isEmpty()) {
			return 1.0;
		}
		
		List<RandomVariable> temp = new ArrayList<RandomVariable>();
		
		for(RandomVariable var:vars) {
			temp.add(var);
		}
		
		RandomVariable Y=temp.remove(0);
		if(e.containsKey(Y)) {
			double answer;
			answer=network.getProbability(Y, e)*Enumerate_All(temp,e,network);
			return answer;
		}else {
			double answer=0;
			for(bn.core.Value value : Y.getDomain()) {
				Assignment new_Assignment=e.copy();
				new_Assignment.put(Y, value);
				answer+=network.getProbability(Y, new_Assignment)*Enumerate_All(temp,new_Assignment,network);
				
			}

			return answer;
		}
		
	}

}
