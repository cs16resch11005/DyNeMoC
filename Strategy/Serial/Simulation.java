import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Simulation 
{

	public static Vector<Agent> population = new Vector<Agent>();	
	public static Vector<City>  nodes      = new Vector<City>();
	public static Vector<HCU>  hcus        = new Vector<HCU>();
	
	public static int [][] countDiffAgents = new int[(ConfigParameters.num_Nodes)][3]; 
	public static int [] sampleInfect      = new int[ConfigParameters.num_Nodes];
	
	public static int numHCUs               = 0;
	public static int currentTime           = 0;
	
	public static int queryExpressions[][];
	public static int operandPositions[];
		
	public static String modQuery;	
	public static String output; 
	
	public  RecoverInfectedAgents ria; 
	public  ModelEpidemic me;      
	
	public boolean isQuerySatisfied;
	
	Simulation(int _numHCUs)
	{
		numHCUs         = _numHCUs;		
		output          = ConfigParameters.path_output_folder + "Serial.txt";		      
		currentTime     = 0;
		isQuerySatisfied        = false;		
		ria             = new RecoverInfectedAgents();
		me              = new ModelEpidemic();
		population.clear();
		nodes.clear();
		hcus.clear();     
	}
	
	public void preConfiguration() throws IOException 
	{
		//open file for writing simulation output
		File fe1 = new File(output);	    
	    	File fe2 = new File("Total_Nbrs.txt");	
		File fe3 = new File("Infected_Nbrs.txt");	
		File fe4 = new File("Inf_Time_Stamp.txt");
	
		if(fe1.exists())
	      		fe1.delete();
		if(fe2.exists())
	      		fe2.delete();
		if(fe3.exists())
	      		fe3.delete();
		if(fe4.exists())
	      		fe4.delete();

		
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			Agent agent = new Agent(i);		
			
			agent.attributes = new HashMap<Integer, Integer>();	
			ModelEpidemic.initializeAgentAttributes(agent);
			
			agent.nbrList = new ArrayList<Integer>();			
			ModelEpidemic.initializeAgentNeighborList(agent, InitialSetup.hm.get(i));
			
			population.add(agent);
		}	
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{
			City city = new City(i);
			city.people = new Vector<Integer>();
			nodes.add(city);
		}
		
		ModelEpidemic.initialCityPopulation(nodes, InitialSetup.hm);
		
		//numHCUs = 3;
		
		for(int i=0; i<numHCUs; i++)
		{
			HCU hcu = new HCU(i);
			hcus.add(hcu);
		}				
		
		ria.initialPositionsOfHCUs(hcus);	
		
		modQuery = readAndParseQuery();
		
		Run();		
	}/* End of preConfiguration() */	
	
		
	public void preRun() throws IOException
	{
		me.findInfectedAtEachCity(output, numHCUs, population, nodes, countDiffAgents, sampleInfect);
		
		recoverInfectedAgentsWithHCU();
		
		//FileWriter fw1 =  new FileWriter("Total_Nbrs.txt", true);	
		//FileWriter fw2 =  new FileWriter("Infected_Nbrs.txt", true);
		

		//fw1.write("\nHCU Id : "+ numHCUs +" At time t=" + Simulation.currentTime + " Suscepted : " + totalSuscept + " Infected : " + totalInfect  + " Recovered : " + totalRecover+" Curr Infected : "+currInfected);
		
		//fw1.write("\nt="+(currentTime-1) + " ");
		//fw2.write("\nt="+(currentTime-1) + " ");

		for(int i=0; i<population.size(); i++)
		{
			ModelEpidemic.countInfectedNeighborsOfAgent(population.get(i));
			//int total_nbrs = (population.get(i).nbrList.size()-1);
			//fw1.write(total_nbrs+ " ");
			//fw2.write(population.get(i).nbrList.get(total_nbrs) + " ");

		}		
		//fw1.close();
		//fw2.close();

	}/* End of preRun()*/
	
	public void nextPositionsOfHCUs() 
	{		
		ria.determinePositionsOfHCUBasedOnStrategy(hcus);
	}/* End of nextPositionsOfHCUs()*/

	public void recoverInfectedAgentsWithHCU() throws IOException 
	{
		for(int i=0; i<hcus.size(); i++)
		{
			int location = hcus.get(i).currLocation;			
		    ria.clearInfectedAgentsWithHCU(population, nodes.get(location).people, hcus.get(i), location);
		}
	}/* End of recoverInfectedAgentsWithHCU() */

	public void Run() throws IOException
	{
		//ConfigParameters.sim_Time
		for(int i=1; i<ConfigParameters.sim_Time; i++)
		{
			currentTime = i;
			preRun();	
			ModelEpidemic.generateRandomNumbers();		
			nextStateOfAgents();
			nextPositionOfAgents();			
			postRun(i);
			
			if(isQuerySatisfied)
				break;
		}	
		
	}/* End of Run()*/

	public void postRun(int time) throws IOException
	{
				
		clearAllCityInformation();
		
		ModelEpidemic.updateCityPopulation(population, nodes);
		
		clearAllAgentNeighborInformation();		
		
		nextNeighborsListOfAgents();
		
		nextPositionsOfHCUs();	
		
		if(checkTerminationOfSimulation())
		{
		 	//writeInfectionTimeStampofAgents();
			System.out.println("Simulation Has Completed !! ");			
			isQuerySatisfied = true;
		}
		
		/*if(checkQuerySatisfied())
		{
			System.out.println("Query Has Satisfied !! ");
			System.exit(1);
		}*/		
		
	}/* End of postRun()*/
	
	public void writeInfectionTimeStampofAgents() throws IOException
	{
		FileWriter fw3 =  new FileWriter("Inf_Time_Stamp.txt", true);
	
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			fw3.write(i+": "+population.get(i).attributes.get(2)+"\n");
		}
		fw3.close();
	}/*End of writeInfectionTimeStampofAgents()*/
	
	public boolean checkQuerySatisfied()
	{
		/*EvaluateExpression ee = new EvaluateExpression();		
		
		modQuery = ee.evaluateAtomicPropositions(operandPositions.length, modQuery, operandPositions, queryExpressions, population, nodes);	
				
		if(operandPositions.length == 1)
		{
			if(ee.evaluateSingleAtomicProposition(modQuery))
				return true;
			else
				return false;
		}
		
		String finalResults =  ee.EvaluatePrefixExpression(modQuery);		
		
		if(finalResults.equals("0"))
			return false;
		else
			return true;	*/	
		return false;
		
	}/* End of checkQuerySatisfied()*/

	public boolean checkTerminationOfSimulation2()
	{
		int count=0;
		
		//int numAgents = agentSample.size();	
		boolean isValid = true;
		
		for(int i=0; i<3; i++)
		{
			int numAgents = nodes.get(i).people.size();
			
			for(int j=0; j<numAgents; j++)
			{
				int agentId = nodes.get(i).people.get(j);
				
				if(population.get(agentId).attributes.get(1) == 2)
				{
					count = count+1;
				}				
			}	
			
			if(count <= numAgents*0.95)
			{
				isValid = false;
			}				
		}	
		return isValid;
		
	}/*End of checkTerminationOfSimulation()*/
	
	public boolean checkTerminationOfSimulation()
	{
		int count=0;
		
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			if(population.get(i).attributes.get(1) == 1)
				count = count+1;
		}

		if(count == 0)
			return true;
		else
			return false;
	}/*End of checkTerminationOfSimulation()*/

	public void clearAllCityInformation() throws IOException 
	{
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
			nodes.get(i).people.clear();		
		
	}/* End of clearAllCityInformation() */


	public void clearAllAgentNeighborInformation() throws IOException 
	{		
		for(int i=0; i<ConfigParameters.num_People; i++)
			population.get(i).nbrList.clear();
		
	}/*End of clearAllAgentNeighborInformation() */


	public void postConfiguration()
	{
		
	}/* End of postConfiguration()*/	
	
		
	public void parallizeAgentStateUpdation()
	{
		int numCity = nodes.size();
		
		ParallizeStateUpdation [] mt = new ParallizeStateUpdation[numCity];	
		
	    for(int i=0; i<numCity; i++)
	    {
	    	mt[i] = new ParallizeStateUpdation("thread"+i, nodes.get(i));
	    }
	    try
	    {
	     Thread.sleep(0);
	    }
	    catch (InterruptedException e) 
	    {
	     // System.out.println("Main thread Interrupted");
	    }
	   // System.out.println("Main thread exiting.");
	      
	    for (int i = 0; i <numCity; i++) 
	    {
	  	  try 
	  	  {
	  	        mt[i].t.join();
	  	  } 
	  	  catch (InterruptedException e) 
	  	  {
	  	        e.printStackTrace();
	  	  }
	  	}   
	
	}/* End of parallizeAgentState() */
	
	public void nextStateOfAgents() throws IOException
	{

		if(ConfigParameters.compare_Strategy == 1) 
		{
				
			long startTime = System.currentTimeMillis();
			
			ModelEpidemic.readNextStateOfAgents(population);
			
			for(int i=0; i<ConfigParameters.num_People; i++)
			{				
				ModelEpidemic.updationRulesForAgentHealth(population.get(i), ModelEpidemic.stateRandum[i]);
			}
		
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime);  //Total execution time in milli seconds
			
			//FileWriter fw =  new FileWriter("restruct-time.txt",true);
	    		//fw.write("\nExecution Time nextStateOfAgents() :" + duration);
	    		//fw.close();			
		}		
		else
		{
			for(int i=0; i<ConfigParameters.num_People; i++)
			{
				ModelEpidemic.updationRulesForAgentHealth(population.get(i), Math.random());
			}			
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
		}
	}/*End of nextPositionOfAgents() */	
	
	
	public void parallizeAgentNeighborsListUpdation()
	{
		int numCity = nodes.size();
	
		ParallizeNeighborsListUpdation [] mt = new ParallizeNeighborsListUpdation[numCity];
		
	    for(int i=0; i<numCity; i++)
	    {
	    	mt[i] = new ParallizeNeighborsListUpdation("thread"+i, nodes.get(i));
	    }
	    try
	    {
	     Thread.sleep(0);
	    }
	    catch (InterruptedException e) 
	    {
	     // System.out.println("Main thread Interrupted");
	    }
	   // System.out.println("Main thread exiting.");
	      
	    for (int i = 0; i <numCity; i++) 
	    {
	  	  try 
	  	  {
	  	        mt[i].t.join();
	  	  } 
	  	  catch (InterruptedException e) 
	  	  {
	  	        e.printStackTrace();
	  	  }
	  	}   	
	}/* End of parallizeAgentState() */
	
	public void nextNeighborsListOfAgents() throws IOException
	{
		
		if(ConfigParameters.compare_Strategy == 1) 
		{
			
			long startTime = System.currentTimeMillis();
			
			ModelEpidemic.readNeighborsListOfAgents(population, nodes);
			
			for(int i=0; i<ConfigParameters.num_People; i++)
			{
				int loc = population.get(i).attributes.get(0);							
				ModelEpidemic.updationRulesForAgentNeighborsList(population.get(i), nodes.get(loc), ModelEpidemic.agentNbrList[i]);				
			}
			
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime);  //Total execution time in milli seconds
			
			//FileWriter fw =  new FileWriter("restruct-time.txt",true);
	    	//fw.write("\nExecution Time nextNeighborsListOfAgents():" + duration);
	    	//fw.close();						
		}		
		else
		{
			for(int i=0; i<ConfigParameters.num_People; i++)
			{
				int loc = population.get(i).attributes.get(0);							
				ModelEpidemic.updationRulesForAgentNeighborsList(population.get(i), nodes.get(loc));
			}	
			
		}
		
	}/*End of nextNeighborsListOfAgents() */
	
		
	public String readAndParseQuery()
	{
		/*String query = ConfigParameters.query;		
		query = query.replaceAll(" ","");
		query = query.toUpperCase();	
		
		QueryProcessing qp = new QueryProcessing();
		
		int numExp = qp.findTotalExpressionsInQuery(query);
		
		queryExpressions = new int[numExp][6];
	    operandPositions   = new int[numExp];
	    
	    return qp.parseQuery(query, queryExpressions, operandPositions);	*/	
		//System.out.println("Final Results: " + op);
	    return "";
	}/* End of readQuery()*/
	
}
