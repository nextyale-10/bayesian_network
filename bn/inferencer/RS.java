package bn.inferencer;


import java.util.HashMap;
import java.util.List;
import java.util.Random;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.core.Value;


public class RS {
	
	
	
	public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network, int sample_num) {
		// TODO Auto-generated method stub
		bn.base.Distribution answer = new bn.base.Distribution(X);
		for(int i=1; i<=sample_num ;i++) {
			HashMap<RandomVariable,bn.core.Value> event=Prior_Sample(network);
			
			if(Is_consistant(event,e)) {
				if(!answer.containsKey(event.get(X))) {
					answer.put(event.get(X), (double)1);
				}else {
					answer.put(event.get(X), answer.get(event.get(X))+1);
				}

			}
		}
		

		answer.normalize();
		return answer;
	}
	
	public HashMap<RandomVariable, Value> Prior_Sample(BayesianNetwork network){
		Assignment ass=new bn.base.Assignment();
		HashMap<RandomVariable,bn.core.Value> event=new HashMap<RandomVariable,bn.core.Value>();
		List<RandomVariable> vars=network.getVariablesSortedTopologically();
		for(RandomVariable var:vars) {
			
			bn.core.Value value=Random_sample(var,ass,network);
			System.out.println(var+"  "+value.toString());
			event.put(var, value);
			ass.put(var, value);
		}
		
		return event;
		
	}
	
	public static bn.core.Value Random_sample(RandomVariable X, Assignment ass, BayesianNetwork network){
		Assignment ass_new=ass.copy();
		HashMap<Value,Double[]> prob_table=new HashMap<Value,Double[]>();
		double sum=0;
		Double interval[]=new Double[2];
		for(Value value:X.getDomain()) {
			interval=new Double[2];
			ass_new.put(X, value);
			
			interval[0]=sum;
			sum+=network.getProbability(X, ass_new);
			interval[1]=sum;
			
			prob_table.put(value, interval);
		}
		
		Random random=new Random();
		double random_prob=random.nextDouble();
		
		
		for(Value value :prob_table.keySet()) {
			
			if(random_prob<=prob_table.get(value)[1]&&random_prob>=prob_table.get(value)[0]) {
			
				return value;
			}
		}
		
		return null;
	}
	
	public boolean Is_consistant(HashMap<RandomVariable,bn.core.Value> event,Assignment e) {
		for(RandomVariable var:e.keySet()) {
			if(!event.get(var).equals(e.get(var))) {
				return false;
			}
		}
		
		return true;
	}

}
