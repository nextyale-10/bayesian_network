package bn.inferencer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import bn.core.Assignment;
import bn.core.BayesianNetwork;

import bn.core.Distribution;

import bn.core.Inferencer;
import bn.core.RandomVariable;
import bn.core.Value;


public class Gibbs {
	
	public Gibbs() {
		
	}
	
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network, int sample_num) {
		bn.base.Distribution answer = new bn.base.Distribution(X);
		List<RandomVariable> vars=network.getVariablesSortedTopologically();
		
		List<RandomVariable> non_evidence_variables=non_evidence_variables(vars,e);
		
		Assignment new_Ass=e.copy();
		Fill_Ass(new_Ass,vars);
		
		for(int i=1; i<=sample_num ;i++) {
			for(RandomVariable non_evi_var:non_evidence_variables) {
				Value value=Gibbs_sample(non_evi_var,new_Ass,network,vars);
				new_Ass.put(non_evi_var, value);
				if(!answer.containsKey(new_Ass.get(X))) {
					answer.put(new_Ass.get(X), (double)1);
				}else {
					answer.put(new_Ass.get(X), answer.get(new_Ass.get(X))+1);
				}
			}
		}
		answer.normalize();
		return answer;
	}
	
	public List<RandomVariable> non_evidence_variables(List<RandomVariable> vars, Assignment e) {
		List<RandomVariable> non_evidence_variables=new ArrayList<RandomVariable>();
		for(RandomVariable var:vars) {
			if(e.containsKey(var)) {
				continue;
			}
			non_evidence_variables.add(var);
		}
		return non_evidence_variables;
	}
	
	public void Fill_Ass(Assignment e,List<RandomVariable> vars) {
		for(RandomVariable var:vars) {
			if(e.containsKey(var)) {
				continue;
			}else {
				Value arr[]=new Value[var.getDomain().size()];
				int sum=0;
				for(bn.core.Value value : var.getDomain()) {
					arr[sum]=value;
					sum++;
				}
				Random random=new Random();
				int random_Ass=random.nextInt(sum);
				e.put(var, arr[random_Ass]);
			}
			
		}
	}
	
	public static bn.core.Value Gibbs_sample(RandomVariable var, Assignment ass, BayesianNetwork network,List<RandomVariable> vars){
		List<RandomVariable> var_children=new ArrayList<RandomVariable>();
		int fromIndex=vars.indexOf(var)+1;
		int toIndex=vars.size()-1;
		Assignment new_Ass=ass.copy();
		
		if(fromIndex>toIndex) { 
		}else {
			var_children=vars.subList(fromIndex, toIndex+1);
		}

		bn.base.Distribution Gibb_Dis = new bn.base.Distribution(var); //Construst P(Zi|mb(Zi)) 
		for(bn.core.Value value : var.getDomain()) {
			new_Ass.put(var, value);
			double prob=1.0;
			
			prob*=network.getProbability(var, new_Ass);
			for(RandomVariable children:var_children) {
				prob*=network.getProbability(children, new_Ass);
			}
			Gibb_Dis.put(value, prob);
		}
		Gibb_Dis.normalize();
		
		double sum=0;
		Double interval[]=new Double[2];
		HashMap<Value,Double[]> prob_table=new HashMap<Value,Double[]>();
		for(bn.core.Value value : var.getDomain()) {
			interval=new Double[2];
			interval[0]=sum;
			sum+=Gibb_Dis.get(value);
			interval[1]=sum;
			
			prob_table.put(value, interval);
		}
		
		Random random=new Random();
		double random_prob=random.nextDouble();
		
		for(Value value :prob_table.keySet()) {
			if(random_prob<=prob_table.get(value)[1]&&random_prob>prob_table.get(value)[0]) {
				return value;
			}
		}
		return null;
	}
}
