package bn.inferencer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.CPT;
import bn.core.Distribution;
import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;
import bn.base.BooleanDomain;
import bn.base.BooleanValue;
import bn.base.NamedVariable;
import bn.util.ArraySet;
import bn.inferencer.*;

public class LKW {
	
	
	
	
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network, int sample_num) {
		bn.base.Distribution answer = new bn.base.Distribution(X);
		for(int i=1; i<=sample_num ;i++) {
			HashMap<Assignment,Double> event=Weight_Sample(e,network);
			for(Assignment ass:event.keySet()) {
				if(!answer.containsKey(ass.get(X))) {
					answer.put(ass.get(X),(double)(0+event.get(ass)));
				}else {
					answer.put(ass.get(X), (double)(answer.get(ass.get(X))+event.get(ass)));
				}
			}
		}
		answer.normalize();
		return answer;
	}
	
	public HashMap<Assignment,Double> Weight_Sample(Assignment e, BayesianNetwork network){
		Double Weight=1.0;
		Assignment ass=e.copy();
		HashMap<Assignment,Double> answer=new  HashMap<Assignment,Double>();
		List<RandomVariable> vars=network.getVariablesSortedTopologically();
		for(RandomVariable var:vars) {
			if(ass.containsKey(var)) {
				Weight*=network.getProbability(var, ass);
				System.out.println(var+" "+ass.get(var)+"----- evidence");
			}else {
				Value v=bn.inferencer.RS.Random_sample(var,ass,network);
				ass.put(var, v);
				System.out.println(var+"  "+v.toString());
			}
			
			
		}
		answer.put(ass, Weight);
		return answer;
	}
	
}
