import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class ModelEpidemic
{
		
	public static double[] stateRandum;// = new double[ConfigParameters.num_People];
	
	
	public static boolean checkInitiallyAgentInfected(Agent agent)
	{
		for(int i=0; i<InitialSetup.init_infected_people.length; i++)
		{	
			if(agent.self() == InitialSetup.init_infected_people[i])
				return true;
		}		
		return false;		
	}	/* End of checkInitiallyAgentInfected() */	
	
	
	public static void initialPositionOfAgents(Vector<Agent> population ) throws IOException
	{
		String fileName = ConfigParameters.path_position_folder + Simulation.currentTime + ".txt";		
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);		
		String readline  = br.readLine();
		
		if(Integer.parseInt(readline) != ConfigParameters.num_People)
		{	
			System.out.println("\n Mismatching with people :" + readline);
			System.exit(1);
		}
		
		int i = 0;		
		while((readline = br.readLine()) != null)
		{
			int location = parsePositionOfAgent(readline);
			population.get(i).attributes.put(0, location);/*current location*/
			population.get(i).attributes.put(3, location);/*home location*/
			i=i+1;
		}		
		fr.close();			
	}
	
	public static void initializeAgentAttributes(Agent agent)
	{
		
		if(checkInitiallyAgentInfected(agent))
		{
			agent.attributes.put(1, 1); /* second attribute is current health state of an agent */
			agent.attributes.put(2, 0);	/* third attribute is infected time stamp   */
		}
		else
		{
			agent.attributes.put(1,  0); 
			agent.attributes.put(2, -1);	
		}		
			
		//System.out.println(agent.attributes.values());
		
	}/* End of initializeAgentAttributes() */	
	
	public static void initializePartitions(Vector<Partition> partitions)
	{
		int numPartitions = partitions.size();
		
		int numofPeople   = ConfigParameters.num_People/numPartitions;
		//int balance = ConfigParameters.num_People%numPartitions;
		
		for(int i=0; i<numPartitions; i++)
		{
			//Partition partition = partitions.get(i);			
			int start =  i*numofPeople;
			int last  = start + numofPeople;
			
			if(i == (numPartitions-1))
			{
				last  = ConfigParameters.num_People;
			}			
			
			for(int j=start; j<last; j++)
			{
				partitions.get(i).listOfAgents.add(j);
			}			
		}
		
		System.out.println("\n People In Each Partition ");
		
		for(int i=0; i<numPartitions; i++)
		{
			System.out.println(partitions.get(i).listOfAgents);						
		}		
	}/* End Of initializePartitions() */
	
			
	public static void updationRulesForAgentLocation(Agent agent, double randum)
	{
		int homeLoc = agent.attributes.get(3);
		
		double sum = 0.0;

		for(int i=0; i<ConfigParameters.num_Nodes; i++) 
		{
		   if(homeLoc == i)
			   sum = sum + ConfigParameters.local_Prob;
		   else
			   sum = sum + (1-ConfigParameters.local_Prob)/(ConfigParameters.num_Nodes-1);
  
		   if(randum <= sum)
		   {
			  if((testDyNeMoC.isTraining)&&(agent.attributes.get(0) != i))
			  { 
				  Simulation.attributeCounter[0] =  Simulation.attributeCounter[0] +1;
			  }			  
			  agent.attributes.replace(0, i);
			  break;
		   }		   
		}			
		
		//System.out.println("Agent " + agent.self() +" Current Location : " + homeLoc + " Random : " + randum + " Sum : " + sum + "  Next Location : " + agent.attributes.get(0));
		
		if(sum > 1.000005)
		{
			System.out.println("Error!! Cummulative Probability is Greater Than ONE : " + sum);
			System.exit(0);
		}		
	}/* End of updationRulesForAgentLocation() */		
	

	public static void updationRulesForAgentHealth(Agent agent, double randum)
	{
		double infProb = 0.0;
		double recProb = 0.0;
		
		//System.out.print("Agent Id: " + agent.self() +" current state : " + agent.attributes.get(1));
		
		if(agent.attributes.get(1) == 0)
		{
			infProb = findInfectionProbOfAgent(agent);			
			if(randum <= infProb)
			{
				agent.attributes.replace(1, 1);
				agent.attributes.replace(2, Simulation.currentTime);
				
				if(testDyNeMoC.isTraining)
				{ 
					Simulation.attributeCounter[1] =  Simulation.attributeCounter[1] +1;
				}			
			}				
		}
		else
		{
			if(agent.attributes.get(1) == 1)
			{
				recProb = findRecoveryProbOfAgent(agent);				
				if(randum <= recProb)
				{
					agent.attributes.replace(1, 2);
					
					if(testDyNeMoC.isTraining)
					{ 
						Simulation.attributeCounter[1] =  Simulation.attributeCounter[1] +1;
					}	
				}
			}	
		}				
		//System.out.print("\nAgent Id : "+agent.self()+" random :"+randum+" inf prob :"+infProb+" rec prob :"+recProb+" Next State : "+agent.attributes.get(1));
	 	
	}/* End of updationRulesForAgentHealth() */		
	
	public static double findRecoveryProbOfAgent(Agent agent)
	{
		int days = Simulation.currentTime - agent.attributes.get(2);		
		return (1 - Math.pow((1-ConfigParameters.delta), (days)));
		
	} /* End of findRecoveryProbOfAgent() */
		
	public static double findInfectionProbOfAgent(Agent agent)
	{
		int loc = agent.attributes.get(0);
		
		int totalNbrs = InitialSetup.num_pop_at_node[loc];
		int infNbrs  = Simulation.countInfected[loc];
		
		if(totalNbrs == 0)
			return 0.0;
		else
			return (double) infNbrs/totalNbrs;
		
	}/*  End of findInfectionProbOfAgent() */	
	
	public static void updateCityPopulation(Vector<Agent> population, Vector<City> nodes)
	{
		//System.out.println("City Population At Time :" + Simulation.currentTime);
		
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			int loc = population.get(i).attributes.get(0);			
			nodes.get(loc).people.add(i);			
		}	
						
	}/* End of updateCityPopulation() */		
	
	
	public static void parallizeAgentLocationUpdation(City city)
	{
		int numPeople = city.people.size();	
		for(int i=0; i< numPeople; i++)
		{
			int agentId = city.people.get(i);
			//System.out.println("agent Id :" + agentId);//stateRandum[agentId]
			ModelEpidemic.updationRulesForAgentLocation(Simulation.population.get(agentId), java.util.concurrent.ThreadLocalRandom.current().nextDouble());
		}
	}/* End of parallizeAgentStateUpdation() */
	
	
	public static void parallizeAgentStateUpdation(City city)
	{
		int numPeople = city.people.size();	
		for(int i=0; i< numPeople; i++)
		{
			int agentId = city.people.get(i);
			//System.out.println("agent Id :" + agentId);//stateRandum[agentId]
			ModelEpidemic.updationRulesForAgentHealth(Simulation.population.get(agentId), java.util.concurrent.ThreadLocalRandom.current().nextDouble());
		}
	}/* End of parallizeAgentStateUpdation() */
	
	
	/* */
	public static int parsePositionOfAgent(String readline)
	{
		String[] token = readline.split(" ");		
		int pos = Integer.parseInt(token[1]);			
		return pos;
	}/* End of parsePositionOfPeople()*/	
	
	public static void readNextPositionsOfAgents(Vector<Agent> population) throws IOException
	{
		String fileName = ConfigParameters.path_position_folder + Simulation.currentTime + ".txt";		
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);		
		String readline  = br.readLine();
		
		if(Integer.parseInt(readline) != ConfigParameters.num_People)
		{	
			System.out.println("\n Mismatching with people :" + readline);
			System.exit(1);
		}
		
		System.out.println("\nFile Name : " + fileName);

		int i = 0;		
		while((readline = br.readLine()) != null)
		{
			int location = parsePositionOfAgent(readline);
			population.get(i).attributes.replace(0, location);
			i=i+1;
		}		
		fr.close();	
		
	}/* End of readNextPositionsOfAgents()*/		
	
	/* parse the each line into id and it's random */
	 public static double parseStateOfAgent(String readline) 
	 {
			String[] token = readline.split(" ");			
			double randum = Double.parseDouble(token[1]);		
			return randum;	
	  }/* End of parse_state_people() */
	
	public static void readNextStateOfAgents(Vector<Agent> population) throws IOException
	{
		if(stateRandum.equals(null))
			stateRandum = new double[ConfigParameters.num_People];
		
		String fileName = ConfigParameters.path_state_folder + Simulation.currentTime + ".txt";			
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);		
		String readline  = br.readLine();
		
		if(Integer.parseInt(readline) != ConfigParameters.num_People)
		{	
			System.out.println("\n Mismatching with people :" + readline);
			System.exit(1);
		}
		
		System.out.println("\nFile Name : " + fileName);
		int i = 0;		
		while((readline = br.readLine()) != null)
		{
			double randum = parseStateOfAgent(readline);
			//ModelEpidemic.updationRulesForAgentHealth(population.get(i), randum);
			stateRandum[i] = randum;
			//System.out.print(randum + " ");
			i=i+1;
		}		
		fr.close();	
		System.out.println();
		
	}/* End of readNextStateOfAgents()*/

	public static void findPeopleAtEachCity(String outputFile, Vector<City> nodes) throws IOException 
	{
		FileWriter fw =  new FileWriter(outputFile, true);		
		fw.write("\n #### At Time : "+(Simulation.currentTime-1)+"####");
		System.out.print("\n #### At Time : "+(Simulation.currentTime-1)+"####");
		
		int numNodes = nodes.size();
		for(int i=0; i<numNodes; i++)
		{
			fw.write("\nPeople at node " +i+" : ");
			//System.out.print("\nPeople at node " +i+" : ");
			int numPeople = nodes.get(i).people.size();
			/*for(int j=0; j<numPeople; j++)
			{
				fw.write(nodes.get(i).people.get(j)+" ");
				System.out.print(nodes.get(i).people.get(j)+" ");
			}*/
			fw.write("-----"+numPeople);
			//System.out.print("-----"+numPeople);
			InitialSetup.num_pop_at_node[i] = numPeople;
		}
		fw.write("\n");
		//System.out.print("\n");
		fw.close();		
	}/* End of findPeopleAtEachCity() */
	
	public static void findInfectedAtEachCity(String outputFile, Vector<City> nodes) throws IOException
	{
		FileWriter fw =  new FileWriter(outputFile, true);		
		//fw.write("\n #### At Time : "+(Simulation.currentTime-1)+"####");
		int currRecovered = 0;		
		int numNodes = nodes.size();
		
		for(int i=0; i<numNodes; i++)
		{
			Simulation.countDiffAgents[i][0] = 0;
			Simulation.countDiffAgents[i][1] = 0;
			Simulation.countDiffAgents[i][2] = 0;
			
			fw.write("\nInfected people at node " +i+" : ");
			//System.out.print("\nInfected people at node " +i+" : ");
			int numPeople = nodes.get(i).people.size();
			int count=0;
			for(int j=0; j<numPeople; j++)
			{
				int id = nodes.get(i).people.get(j);
				if(Simulation.population.get(id).attributes.get(1) == 1)
				{
					//fw.write(nodes.get(i).people.get(j)+" ");
					//System.out.print(nodes.get(i).people.get(j)+" ");
					count = count+1;
					Simulation.countDiffAgents[i][1] = 	Simulation.countDiffAgents[i][1]+1;
				}
				else
				{
					if(Simulation.population.get(id).attributes.get(1) == 2)
					{
						currRecovered = currRecovered + 1;
						Simulation.countDiffAgents[i][2] = 	Simulation.countDiffAgents[i][2]+1;
					}
					else
					{
						Simulation.countDiffAgents[i][0] = 	Simulation.countDiffAgents[i][0]+1;
					}
				}
			}
			fw.write("-----"+ count);
			//System.out.print("-----"+ count);
			Simulation.countInfected[i]= count;
		}
		
		Simulation.numRecovered = currRecovered;
		fw.write("\nTotal Recovered : " + Simulation.numRecovered + "\n");
		//System.out.print("\n");
		fw.close();		
	}/* End of findInfectedAtEachCity()*/
}
