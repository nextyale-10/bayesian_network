package bn.inferencer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.base.BayesianNetwork;
import bn.core.Assignment;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;


public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String inferencer =args[0] ;
		String filename="";
		String query="";
		String[] evidence = new String [100];
		int sample=-1;
		
		switch (inferencer) {		//arg[0] is for name of inferencer
									//arg[1] is for the filename
									//arg[2] is for sample size or query 
									//arg[3] is for evidence or query
									//arg[4] and the entries following are for evidence
								
		case ("Enumeration"):
			filename=args[1];
			query = args[2];
			evidence = Arrays.copyOfRange(args, 3, args.length);
			break;
		
		case("RejectionSampling"):
			sample=Integer.parseInt(args[1]);
			filename=args[2];
			query =args[3];
			evidence =Arrays.copyOfRange(args, 4, args.length);
			break;
		
		case("LikelihoodWeighting"):
			
			sample=Integer.parseInt(args[1]);
			filename=args[2];
			query =args[3];
			evidence =Arrays.copyOfRange(args, 4, args.length);
			break;
		
		case("GibbsSampling"):
			sample=Integer.parseInt(args[1]);
			filename=args[2];
			query =args[3];
			evidence =Arrays.copyOfRange(args, 4, args.length);
			break;
		default:
			System.out.println("There is no such inferencer!");
			break;
		}
		
		String type = getType(filename);
		String path = getAddress(filename);
		
		
		bn.core.BayesianNetwork bn = new BayesianNetwork();
		try {
			if(type.equals("bif")) {
				BIFParser bp =new BIFParser (new FileInputStream(path));
				bn=bp.parseNetwork();
			}
			else {
				XMLBIFParser xp= new XMLBIFParser();
				bn=xp.readNetworkFromFile(path);
			}
			RandomVariable X = bn.getVariableByName(query);//x is the query variable
			Assignment A = new bn.base.Assignment();
			RandomVariable[] key= new RandomVariable[evidence.length];
			System.out.println("Query: "+ X);
			int num=1;
            for (int i = 0; i < evidence.length ; i = i+2 ) {    

                System.out.println("evidence"+(num++)+ ":  " + evidence[i] + " is " + evidence[i+1]);
                key[i] = bn.getVariableByName(evidence[i]);
                A.set(key[i], evidence[i+1]);      
            }
            
            
            System.out.println("INFERENCER: " + inferencer + "\n\n");
            if(sample>=0) System.out.println("Sample size: "+ sample);

            if (inferencer.equals("Enumeration")) {
            	long start=System.currentTimeMillis();
                EnumerationInferencer inf = new EnumerationInferencer();  
                Distribution dist = inf.query(X,A,bn);
                long end = System.currentTimeMillis();
                System.out.format("Completed. Time used: %.5f secs\n", (end-start)/1000.0);
                
                System.out.println("\nDistribution:" + dist.toString() + "\n");
            }
            else if (inferencer.equals("GibbsSampling")) {
            	long start =System.currentTimeMillis();
            	Gibbs inf = new Gibbs();
                Distribution dist = inf.query( X, A, bn,sample);
                long end = System.currentTimeMillis();
                System.out.format("Completed. Time used: %.5f secs\n", (end-start)/1000.0);
             
                
                System.out.println("\nDistribution:" + dist.toString()+ "\n");
            }
            else if (inferencer.equals("RejectionSampling")) {
            	long start =System.currentTimeMillis();
            	RS inf = new RS();
                Distribution dist = inf.query( X, A, bn,sample);
                long end = System.currentTimeMillis();
                System.out.format("Completed. Time used: %.5f secs\n", (end-start)/1000.0);
               
                
                System.out.println("\nDistribution:" + dist.toString()+ "\n");
            }
            else if (inferencer.equals("LikelihoodWeighting")) {
            	long start =System.currentTimeMillis();
            	LKW inf = new LKW();            
                Distribution dist = inf.query( X, A, bn,sample);
                long end = System.currentTimeMillis();
                System.out.format("Completed. Time used: %.5f secs\n", (end-start)/1000.0);
           
               
                System.out.println("\nDistribution:" + dist.toString()+ "\n");
            }
            
		}
		catch (IOException e) {
			
		}
		catch (SAXException e) {
			
		}
		catch (ParserConfigurationException e) {
			
		}
		
	}
	public static String getAddress(String name) {
		
		
		String addr="bn/examples/";   //fixme: might be wrong format
		addr+=name;
		
		return addr;
		
	}
	public static String getType(String name) {
		String type;
		if(name.toLowerCase().endsWith(".bif")) {
			type="bif";
		}
		else if (name.toLowerCase().endsWith(".xml")) {
			type="xml";
		}
		else return null;
		
		return type;
	}

}
