import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulation 
{

	int numCores = Runtime.getRuntime().availableProcessors();

	public static Vector<Agent> population        = new Vector<Agent>();	
	public static Vector<City>  nodes             = new Vector<City>();
	//public static Vector<Partition>  partitions   = new Vector<Partition>();
	
	public static int [] countInfected            = new int[(ConfigParameters.num_Nodes)]; 
	public static int numHCUs                     = 0;
	public static int currentTime                 = 0;	

	public static int [][] countDiffAgents = new int[(ConfigParameters.num_Nodes)][3];/* Application Dependent, stores number of agents are in S,I,R at each city*/
	public static int [] attributeCounter  = new int[2]; /* Application Dependent, used to determine whether attribute is dynamic or static */
	public static int [] typeofAttribute   = new int[2]; /* Application Dependent, whether it is dynamic or static*/
	
	public static ArrayList<Integer> list  = new ArrayList<Integer>();
	
	public static String modQuery;	/* Query after parsing */
	public static String output; 
	
	public  ModelEpidemic me;      
	public  QueryProcessing qp;	
	
	public static int numRecovered = 0;	
	public boolean isQuerySatisfied;
	public int[] sampleInfect;
	
	Simulation(int _numHCUs)
	{
		numHCUs         = _numHCUs;		
		output          = ConfigParameters.path_output_folder + "DyNeMoC.txt";		      
		currentTime     = 0;
		isQuerySatisfied        = false;		
		me              = new ModelEpidemic();
		qp = new QueryProcessing();
		population.clear();
		nodes.clear();
		//partitions.clear();		
	}
	
	public void preConfiguration() throws IOException 
	{
		//open file for writing simulation output
		File fe = new File(output);	    
	    
		if(fe.exists())
	           fe.delete();
		
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			Agent agent = new Agent(i);					
			agent.attributes = new HashMap<Integer, Integer>();	
			ModelEpidemic.initializeAgentAttributes(agent);
			population.add(agent);
		}	
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{
			City city = new City(i);
			city.people = new Vector<Integer>();
			nodes.add(city);
		}		
		
		ModelEpidemic.initialPositionOfAgents(population);
		ModelEpidemic.updateCityPopulation(population, nodes);
				
		//if(!testDyNeMoC.isTraining)
		modQuery = qp.readAndParseQuery();		
		Run();		
	}/* End of preConfiguration() */	
	
		
	public void preRun() throws IOException
	{
		ModelEpidemic.findPeopleAtEachCity(output, nodes);
		ModelEpidemic.findInfectedAtEachCity(output, nodes);
		
		if((!testDyNeMoC.isTraining) && checkQuerySatisfied())
		{
			System.out.println("Query Has Satisfied !! ");
			isQuerySatisfied = true;
		}
	}/* End of preRun()*/	
	
	public void Run() throws IOException
	{
		for(int i=1; i<ConfigParameters.sim_Time; i++)
		{
			if(isQuerySatisfied)
				break;
			
			currentTime = i;
			preRun();			
			nextStateOfAgents();
			nextPositionOfAgents();			
			postRun(i);			
		}
		
	}/* End of Run()*/

	public void postRun(int time) throws IOException
	{				
		clearAllCityInformation();		
		ModelEpidemic.updateCityPopulation(population, nodes);		
		
		if(testDyNeMoC.isTraining)
			findTypeOfAttribute();
		
	}/* End of postRun()*/
	
	
	public boolean checkQuerySatisfied()
	{
		EvaluateExpression ee = new EvaluateExpression(qp);						

		modQuery = ee.evaluateAtomicPropositions(qp.operandPositions.length, modQuery, qp.operandPositions, qp.queryExpressions, population, nodes);	
				
		if((qp.operandPositions.length == 1)&&(modQuery.length() <= 5))
		{
			if(ee.evaluateSingleAtomicProposition(modQuery))
				return true;
			else
				return false;
		}
		
		String finalResults =  ee.EvaluatePrefixExpression(modQuery, 0);		
		
		if(finalResults.equals("0"))
			return false;
		else
			return true;	
		
	}/* End of checkQuerySatisfied()*/
	
	public boolean checkTerminationOfSimulation()
	{
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			if(population.get(i).attributes.get(1) == 1)
				return false;			
		}
		return true;
	}/*End of checkTerminationOfSimulation()*/

	public void clearAllCityInformation() throws IOException 
	{
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
			nodes.get(i).people.clear();	
		
	}/* End of clearAllCityInformation() */


	public void postConfiguration()
	{
		
	}/* End of postConfiguration()*/	
	
	public void parallizeAgentLocationUpdation()
	{
	    ExecutorService pool = Executors.newFixedThreadPool(numCores);  
	    
	    int numCity = nodes.size();				 		
	    ParallizeLocationUpdation [] mt = new ParallizeLocationUpdation[numCity];
	    
	    for(int i=0; i<numCity; i++)
	    {
	    	mt[i] = new ParallizeLocationUpdation("thread"+i, nodes.get(i));	    	
	    }
	    for (int i = 0; i <numCity; i++) 
	    {
	    	pool.execute(mt[i]);
	    }	    
	    pool.shutdown(); 	 
	    while (!pool.isTerminated()){}   
	
	}/* End of parallizeAgentState() */	
	
	public void parallizeAgentStateUpdation()
	{
	    ExecutorService pool = Executors.newFixedThreadPool(numCores);  
	    
	    int numCity = nodes.size();				 		
	    ParallizeStateUpdation [] mt = new ParallizeStateUpdation[numCity];
	    
	    for(int i=0; i<numCity; i++)
	    {
	    	mt[i] = new ParallizeStateUpdation("thread"+i, nodes.get(i));	    	
	    }
	    for (int i = 0; i <numCity; i++) 
	    {
	    	pool.execute(mt[i]);
	    }	    
	    pool.shutdown(); 	 
	    while (!pool.isTerminated()){}   
	
	}/* End of parallizeAgentState() */
	
	public void nextStateOfAgents() throws IOException
	{
		if(ConfigParameters.compare_Strategy == 1) 
		{
			ModelEpidemic.readNextStateOfAgents(population);			
			/*for(int i=0; i<ConfigParameters.num_People; i++)
			{				
				ModelEpidemic.updationRulesForAgentHealth(population.get(i), ModelEpidemic.stateRandum[i]);
			}*/
			parallizeAgentStateUpdation();
		}		
		else
		{
			/*for(int i=0; i<ConfigParameters.num_People; i++)
			{
				ModelEpidemic.updationRulesForAgentHealth(population.get(i), Math.random());
			}	*/
			parallizeAgentStateUpdation();
		}
	}/* End of nextStateOfAgents() */
	
	public void nextPositionOfAgents() throws IOException 
	{
		if(ConfigParameters.compare_Strategy == 1) 
		{
			ModelEpidemic.readNextPositionsOfAgents(population);
		}		
		else
		{
			for(int i=0; i<ConfigParameters.num_People; i++)
			{
				ModelEpidemic.updationRulesForAgentLocation(population.get(i), Math.random());
			}
			//parallizeAgentLocationUpdation();
		}
	}/*End of nextPositionOfAgents() */		
	
	public void findTypeOfAttribute()
	{
		int dynamic = (int) (0.001*ConfigParameters.num_People);
		
		for(int i=0; i<attributeCounter.length; i++)
		{
			if((attributeCounter[i] == 0)&&(typeofAttribute[i] != 2))
			{
				typeofAttribute[i] = 0; /* static attribute */
			}
			else 
			{
				if((attributeCounter[i] <= dynamic)&&(typeofAttribute[i] != 2)&&(!qp.isQueryOnAllAgents))
				{
					typeofAttribute[i] = 1; /* semi dynamic attribute */				
				}
				else
				{
					typeofAttribute[i] = 2; /* dynamic attribute */
				}
			}
		}
		
	}/*End of findTypeOfAttribute() */
	
}
