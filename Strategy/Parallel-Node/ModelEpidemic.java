import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

public class ModelEpidemic
{
		
	public static double[] stateRandum = new double[ConfigParameters.num_People];
	//public static HashMap<Integer, ArrayList<Integer>> agentNbrList = new HashMap<Integer, ArrayList<Integer>>();
	public static int[][] agentNbrList = new int[ConfigParameters.num_People][5];
	
	public static boolean checkInitiallyAgentInfected(Agent agent)
	{
		for(int i=0; i<InitialSetup.init_infected_people[0].length; i++)
		{	
			if(agent.self() == InitialSetup.init_infected_people[0][i])
				return true;
		}		
		return false;		
	}	/* End of checkInitiallyAgentInfected() */	
	
	public static void initializeAgentAttributes(Agent agent)
	{
		//System.out.println("Attribute List of Agent :" + agent.self());
		
		agent.attributes.put(0, InitialSetup.hm.get(agent.self()).get(0)); /* first attribute is current location of an agent */
		
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
		agent.attributes.put(3, InitialSetup.hm.get(agent.self()).get(0));	/* fourth attribute is home location of an agent  */	
		agent.attributes.put(4, 0); /* fifth attribute is scan status of an agent */
		
		//System.out.println(agent.attributes.values());
		
	}/* End of initializeAgentAttributes() */	

	
	public static void initializeAgentNeighborList(Agent agent, ArrayList<Integer> nbrList)
	{
		//System.out.println("Neighbor List of Agent :" + agent.self());
		
		for(int i=3; i < nbrList.size(); i++)
		{	
			agent.nbrList.add(nbrList.get(i)); /* except last element all are neighbors where last element is number of infected among these neighbors */
			//System.out.print(agent.nbrList.get(i-3) + "  ");
		}			
		//System.out.println();	
		
	}/* End of initializeAgentNeighborList() */	
	
	public static void countInfectedNeighborsOfAgent(Agent agent) 
	{
		int count=0;		
		
		if(agent.attributes.get(1) != 0)
		{
			agent.nbrList.add(count);
		}
		else
		{
			for(int i=0; i<agent.nbrList.size(); i++)
			{
				int nbrId = agent.nbrList.get(i);			
				if(Simulation.population.get(nbrId).attributes.get(1) == 1 )
				{
					count = count+1;
				}
			}		
			agent.nbrList.add(count); /* add last element as number of infected neighbors  */
		}
		//System.out.println("Agent Id : " + agent.self() + " Neighbors : " + agent.nbrList);
		//System.out.println("Agent Id : " + agent.self() + " infected : " + count + " total: " + (agent.nbrList.size()-1));
		
	}/* End of countInfectedNeighborsOfAgent() */	
	
	public static void initialCityPopulation(Vector<City> nodes, HashMap<Integer, ArrayList<Integer>> hm)
	{
		//System.out.println("City Population At Time :" + Simulation.currentTime);
		
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			int loc = hm.get(i).get(0);			
			nodes.get(loc).people.add(i);			
		}
		
		for(int j=0; j<ConfigParameters.num_Nodes; j++)
		{
			;//System.out.println("Population of City : "  + j + "  " + nodes.get(j).people + " --- " + nodes.get(j).people.size());
		}		
		
	}/* End of initialCityPopulation() */


	public static void generateRandomNumbers()
	{
		for(int i=0; i<ConfigParameters.num_People; i++)
		{
			stateRandum[i] = Math.random();
		}	
	}
		
	public static void updationRulesForAgentAttributes(Agent agent)
	{
		//updationRulesForAgentHealth(agent, 0.0);
		//updationRulesForAgentLocation(agent, 0.0);
		
	}/* End of updationRulesForAgentAttributes() */
	
	public static void parallizeAgentLocationUpdation(City city)
	{
		int numPeople = city.people.size();	
		for(int i=0; i< numPeople; i++)
		{
			int agentId = city.people.get(i);	
			//System.out.println("agent Id :" + agentId);//Math.random()
			//ModelEpidemic.updationRulesForAgentLocation(Simulation.population.get(agentId), java.util.concurrent.ThreadLocalRandom.current().nextDouble());
			ModelEpidemic.updationRulesForAgentLocation(Simulation.population.get(agentId), stateRandum[agentId]);
		}		
	}/*End of the parallizeAgentLocationUpdation() */
	
	public static void parallizeAgentStateUpdation(City city)
	{
		int numPeople = city.people.size();	
		//System.out.println(" parallizeAgentStateUpdation(city) city Id : " + city.self() + " num people : " + numPeople);
		if(ConfigParameters.compare_Strategy == 0)
		{	
			for(int i=0; i< numPeople; i++)
			{
				int agentId = city.people.get(i);	
				//System.out.println("agent Id :" + agentId);//Math.random()
				//ModelEpidemic.updationRulesForAgentHealth(Simulation.population.get(agentId), java.util.concurrent.ThreadLocalRandom.current().nextDouble() );
				ModelEpidemic.updationRulesForAgentHealth(Simulation.population.get(agentId), stateRandum[agentId]);
			}	
		}
		else
		{
			for(int i=0; i< numPeople; i++)
			{
				int agentId = city.people.get(i);
				//System.out.println("agent Id :" + agentId);
				ModelEpidemic.updationRulesForAgentHealth(Simulation.population.get(agentId), stateRandum[agentId]);
			}	
		}
	}/* End of parallizeAgentStateUpdation() */
	
	public static void parallizeAgentStateUpdation(City city, double[] randum)
	{
		int numPeople = city.people.size();	
		//System.out.println(" parallizeAgentStateUpdation(city, randum) city Id : " + city.self() + " num people : " + numPeople);
		
		for(int i=0; i< numPeople; i++)
		{
			int agentId = city.people.get(i);
			//System.out.println("agent Id :" + agentId);
			ModelEpidemic.updationRulesForAgentHealth(Simulation.population.get(agentId), randum[agentId]);
		}		
	}/* End of parallizeAgentStateUpdation() */
	
	public static void parallizeAgentNeighborsUpdation(City city)
	{
		int numPeople = city.people.size();	
		//System.out.println(" parallizeAgentNeighborsUpdation(city) city id : " + city.self() + " num people : " + numPeople);
		if(ConfigParameters.compare_Strategy == 0)
		{
			for(int i=0; i< numPeople; i++)
			{
				int agentId = city.people.get(i);
				//System.out.println("agent Id :" + agentId);
				ModelEpidemic.updationRulesForAgentNeighborsList(Simulation.population.get(agentId), city);
			}	
		}
		else
		{
			for(int i=0; i< numPeople; i++)
			{
				int agentId = city.people.get(i);
				//System.out.println("agent Id :" + agentId);
				ModelEpidemic.updationRulesForAgentNeighborsList(Simulation.population.get(agentId), city, agentNbrList[agentId]);
			}	
		}
	}/* End of parallizeAgentStateUpdation() */
	
	public static void parallizeAgentNeighborsUpdation(City city,  int [][] agentNbrList)
	{
		int numPeople = city.people.size();	
		System.out.println(" parallizeAgentNeighborsUpdation(city, hmNbrList ) city id : " + city.self() + " num people : " + numPeople);
		for(int i=0; i< numPeople; i++)
		{
			int agentId = city.people.get(i);
			//System.out.println("agent Id :" + agentId);
			ModelEpidemic.updationRulesForAgentNeighborsList(Simulation.population.get(agentId), city, agentNbrList[agentId]);
		}		
	}/* End of parallizeAgentStateUpdation() */
	
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
			}				
		}
		else
		{
			if(agent.attributes.get(1) == 1)
			{
				recProb = findRecoveryProbOfAgent(agent);				
				if(randum <= recProb)
					agent.attributes.replace(1, 2);
			}	
		}				
	 // System.out.print(" random :" + randum + " inf prob :" + infProb + " rec prob :" + recProb + " Next State : " +agent.attributes.get(1) + "\n" );		
	}/* End of updationRulesForAgentHealth() */		
	
	public static double findRecoveryProbOfAgent(Agent agent)
	{
		int days = Simulation.currentTime - agent.attributes.get(2);		
		return (1 - Math.pow((1-ConfigParameters.delta), (days)));
		
	} /* End of findRecoveryProbOfAgent() */
		
	public static double findInfectionProbOfAgent(Agent agent)
	{
		int totalNbrs = agent.nbrList.size()-1;
		int infNbrs  = agent.nbrList.get(totalNbrs);
		
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
		
		for(int j=0; j<ConfigParameters.num_Nodes; j++)
		{
			;//System.out.println("Population of City : "  + j + "  " + nodes.get(j).people + " --- " + nodes.get(j).people.size());
		}				
	}/* End of updateCityPopulation() */		
	
	public static void mapAccordingToNeighborExistance(Agent agent, ArrayList<Integer> orgNbrs)
	{
		for(int i=3; i<orgNbrs.size(); i++)
		{
			Agent nbr = Simulation.population.get(orgNbrs.get(i));			
			if(nbr.attributes.get(0) == nbr.attributes.get(3))
				agent.nbrList.add(orgNbrs.get(i));
		}
		
	}/* End of mapAccordingToNeighborExistance() */		
	
	public static int [] generateRandomNeighbors(int numPeople)
	{
		Random rand = new Random();
		int num = rand.nextInt(3) + 1;
		int count = 1;
		
		ArrayList<Integer> nbrList = new ArrayList<Integer>();
		
		int nbrArr[] = new int[5];
		
		while(count <= num)
		{
			int randum = rand.nextInt(numPeople);
			
			if(!nbrList.contains(randum))
			{
				nbrList.add(randum);
				nbrArr[count] = randum;
				count=count+1;
			}			
		}		
		return nbrArr;	
		
	}/* End of generateRandomNeighbors()*/		
	
	public static boolean checkNeighborAlreadyExist(Agent agent, int nbrId)
	{
		for(int i=0; i<agent.nbrList.size(); i++)
		{
			if(nbrId == agent.nbrList.get(i))
				return true;
		}
		return false;
		
	}/* End of checkNeighborAlreadyExist() */	
	
	public static void mapWithPeopleInOtherCity(Agent agent, Vector<Integer> people, int[] nbrList)
	{
		
		//System.out.println("Agent Id " + agent.self() + " : " + ranNbrs);
		
		for(int i=0; i<5; i++)
		{
			if(nbrList[i] == -1)
				continue;
			
			
			if(nbrList[i] >= people.size())
			{
				System.out.println("Error : " + agent.id + " city people:  " + people.toString() + " nbrs : " + nbrList.toString() );
				System.exit(1);
			}
			
			int nbrId = people.get((nbrList[i]));
			
			if((nbrId != agent.self()) &&(!checkNeighborAlreadyExist(agent, nbrId)))		
			{
				agent.nbrList.add(nbrId);
				Simulation.population.get(nbrId).nbrList.add(agent.self());
			}			
		}
		
	}/* End of  mapWithPeopleInOtherCity()  */		
	
	/* */
	public static void updationRulesForAgentNeighborsList(Agent agent, City city)
	{
				
		if(agent.attributes.get(0) == agent.attributes.get(3))
		{
			mapAccordingToNeighborExistance(agent, InitialSetup.hm.get(agent.self()));			
		}
		else
		{
			int[] nbrList = generateRandomNeighbors(city.people.size());
			//System.out.println("Agent Id : " + agent.self() +" Gen Nbrs : "+ nbrList);
			mapWithPeopleInOtherCity(agent, city.people, nbrList);
		}		
		//System.out.println("Neighbor List of Agent " + agent.self() + " : " + agent.nbrList);
		
	}/* End of updationRulesForAgentNeighborsList() */	
	
	/* */
	public static void updationRulesForAgentNeighborsList(Agent agent, City city, int[] nbrList)
	{
				
		if(agent.attributes.get(0) == agent.attributes.get(3))
		{
			mapAccordingToNeighborExistance(agent, InitialSetup.hm.get(agent.self()));			
		}
		else
		{
			//System.out.println("Agent Id : " + agent.self() +" Gen Nbrs : "+ nbrList);
			mapWithPeopleInOtherCity(agent, city.people, nbrList);
		}		
		//System.out.println("Neighbor List of Agent " + agent.self() + " : " + agent.nbrList);
		
	}/* End of updationRulesForAgentNeighborsList() */	
	
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
		
		//System.out.println();
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
		//System.out.println();
		
	}/* End of readNextStateOfAgents()*/

	public static ArrayList<Integer> parseNeighborsOfAgent(String readline) 
	{
		ArrayList<Integer> genNbrs = new ArrayList<Integer>();
		String[] nbrs = readline.split(" ");		
		for(String a: nbrs) 
		{
			genNbrs.add(Integer.parseInt(a));
		}		
		genNbrs.remove(0);		
		return genNbrs;
	}/*End Of parseNeighborsOfAgent() */
	
	public static void parseNeighborsOfAgent(String readline, int agentId) 
	{
		String[] nbrs = readline.split(" ");	
		int count=0;
		for(String a: nbrs) 
		{
			if(count != 0)
				agentNbrList[agentId][count-1] = Integer.parseInt(a);
			count = count+1;
		}		
		
		for(int i=count-1; i<5; i++)
			agentNbrList[agentId][i] = -1;
		
	}/*End Of parseNeighborsOfAgent() */
	
	public static void readNeighborsListOfAgents(Vector<Agent> population, Vector<City> nodes) throws IOException 
	{
		String fileName = ConfigParameters.path_mapping_folder + Simulation.currentTime + ".txt";		
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);		
		String readline  = br.readLine();
		
		if(Integer.parseInt(readline) != ConfigParameters.num_People)
		{	
			System.out.println("\n Mismatching with people :" + readline);
			System.exit(1);
		}
		
		/*if(agentNbrList != null)
		     agentNbrList.clear();*/
		
		int i = 0;		
		while((readline = br.readLine()) != null)
		{
			parseNeighborsOfAgent(readline, i);
			
			/*ArrayList<Integer> genNbrList= parseNeighborsOfAgent(readline);
			agentNbrList.put(i, genNbrList);
			*/
			/*
			if(population.get(i).attributes.get(0) == population.get(i).attributes.get(3))
			{
				mapAccordingToNeighborExistance(population.get(i), InitialSetup.hm.get(i));				
			}
			else
			{	
				int loc = population.get(i).attributes.get(0);
				ArrayList<Integer> genNbrList= parseNeighborsOfAgent(readline);
				mapWithPeopleInOtherCity(population.get(i), nodes.get(loc).people, genNbrList);
			}	
			*/			
			i=i+1;
		}		
		fr.close();			
	}/* End of readNeighborsListOfAgents() */
	
	
	public void findPeopleAtEachCity(String outputFile, Vector<City> nodes) throws IOException 
	{
		FileWriter fw =  new FileWriter(outputFile, true);		
		fw.write("\n At Time : "+(Simulation.currentTime-1));
		
		for(int i=0; i<nodes.size(); i++)
		{
			//fw.write("\n people at node " + i + ": " + nodes.get(i).people + " --- " + nodes.get(i).people.size());
			fw.write("\n people at node " + i + ": " + 0 + " --- " + nodes.get(i).people.size());
		}
		fw.write("\n");
		fw.close();		
	}
	
	/* finds and writes the infected people at each node at current instance of time to output file, Function Parameters: (outputFile, num_infect_node, curr_pos_people) */
	public void findInfectedAtEachCity(String outputFile, int numHCUs, Vector<Agent> population, Vector<City> nodes, int [][] countDiffAgents, int [] sampleInfect) throws IOException  
	{
		findPeopleAtEachCity(outputFile, nodes);
		
		FileWriter fw =  new FileWriter(outputFile, true);		
		fw.write("\n");
		
		int totalRecover = 0;  //counts the number of recovered people with in sample 
		int totalInfect  = 0;  //counts the number of infected people with in sample 
		int totalSuscept = 0;
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{
			fw.write("infected people at node " + (i)+ " : ");
			
			int countRecover = 0;  //counts the number of recovered people with in sample 
			int countInfect  = 0;  //counts the number of infected people with in sample 
			int countSuscept = 0;  // counts the number of suscept people with in sample 			
			int count_sample = 0; // counts the number of infected people with in sample 
			int sample_size  = 0; // for sample size;
			
			for(int j=0; j<nodes.get(i).people.size();j++)
			{
				int k = nodes.get(i).people.get(j);									
				
				double rn = Math.random();				
				if(rn < 0.5)
					sample_size = sample_size + 1;
					
				if(population.get(k).attributes.get(1) == 1)
				{
					//fw.write((k) + "  ");
					countInfect = countInfect+1;				
					
					if(sample_size < ConfigParameters.sample_Size)
						 count_sample = count_sample + 1;
					
				}	
				else if(population.get(k).attributes.get(1)  == 2)
				{
					countRecover = countRecover + 1;
			    }
				else
				{
					countSuscept = countSuscept +1;
				}				
			}			
			
			countDiffAgents[i][0] = countSuscept;
			countDiffAgents[i][1] = countInfect;
			countDiffAgents[i][2] = countRecover;		
			sampleInfect[i]       = count_sample; 
			
			fw.write( " -- " + countDiffAgents[i][1] + "\n");// + " sample infected: " +count_sample + "\n");
			
			totalSuscept = totalSuscept + countDiffAgents[i][0];
			totalInfect  = totalInfect  + countDiffAgents[i][1];
			totalRecover = totalRecover + countDiffAgents[i][2];			
		 }		
		
		fw.close();		
		
		//FileWriter fw1 =  new FileWriter("outputFile_1.txt", true);		
		//fw1.write("\nHCU Id : "+ numHCUs +" At time t=" + Simulation.currentTime + " Suscepted : " + totalSuscept + " Infected : " + totalInfect  + " Recovered : " + totalRecover);
		//fw1.close();
		
	}/* End of find_infected_at_each_node() */

	public static void printAgentNeighborList(Vector<Agent> population) 
	{
		int people = population.size();
		System.out.println();
		for(int i =0; i<people; i++)
			System.out.println("Neighbor List of Agent " + i + " : "+ population.get(i).nbrList);
	}/* End of printAgentNeighborList() */
	
}
