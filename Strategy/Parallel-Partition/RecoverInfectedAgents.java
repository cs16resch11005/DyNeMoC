import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class RecoverInfectedAgents 
{
	
	public int randomPositionSelection(double[][] probDist, int pos)
	{
		double rn = Math.random();		
		double sum = 0.0;
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++) 
		{
		   sum = sum +  probDist[(pos)][i];			   
		   if(rn <= sum)
		   {
			   // System.out.print("Random :" + (rn) + "; Matching Prob : " + (sum));
		       return i;
		   }		   
		}
		
		System.out.println("Error!! In Random Position Selection Function : " + (sum) + " " + (rn) );
		System.exit(1);
		return 0;
		
	}/* End of the randomPositionSelection() */
	
	public boolean checkInitiallyHCUsExist(Vector<HCU> hcus, int hcuId, int pos) 
	{
		for(int i=0; i<hcuId; i++)
		{
		   if(hcus.get(i).currLocation == pos)
			   return true;
		}
		return false;
		
	}/* checkInitiallyHCUsExist() */	
	
	public  void initialPositionsOfHCUs(Vector<HCU> hcus)
	{
		boolean first = true;
		
		//System.out.println("\n Initial positionsOf HCUs" );
		
		for(int i=0; i<hcus.size(); i++)
		{
			//System.out.print("\nHCU Id: " + (i) + "; ");
			
			int pos = randomPositionSelection(InitialSetup.init_prob_of_HCUs, i);
			
			if( (pos == 0) && first )
			{
				first = false;
			}			
			else
			{
				while(checkInitiallyHCUsExist(hcus, i, pos))
				{
					pos = randomPositionSelection(InitialSetup.init_prob_of_HCUs, i);
				}
			}					
			hcus.get(i).currLocation = pos;
			hcus.get(i).prevLocation = pos;
			hcus.get(i).timeStamp    = Simulation.currentTime;
			//System.out.print("; Position :" + pos + "; Time Stamp: " + hcus.get(i).timeStamp);
		}
		
	}/* End of the initialPositionsOfHCUs() */
	
	/*	 clear_infected_at_node_with_HCU, Function Parameters : (id, pos, time, curr_pos_people, curr_state_people, infect_prob_people, trans_HCUs_dist); */		    	   
	public void clearInfectedAgentsWithHCU(Vector<Agent> population, Vector<Integer> people, HCU hcu, int location) throws IOException
	{
		
		FileWriter fw =  new FileWriter(Simulation.output, true);	
		
		ArrayList<Integer> sample = new ArrayList<Integer>();		
		int scanCount = 0;
		
		for(int j=0; j<people.size(); j++)
		{
			int id = people.get(j);
								
			if(population.get(id).attributes.get(4) == 0)
			{
				sample.add(id);
				scanCount = scanCount+1;
			}				
			
			if(scanCount >= ConfigParameters.sample_Size)
				break;  	
		}
		
		if(sample.isEmpty())
		{
			fw.write("\npeople recovered at node " + (location) + " : 0");
		}
		else
		{
			int infect_count=0;
			fw.write("\npeople recovered at node " + (location) + " in this sample " + sample + " : ");
			//System.out.print("\npeople recovered at node " + (location) + " :");			
					
			for(int j=0; j < scanCount; j++) 
			{
				int i = sample.get(j);
				
				population.get(i).attributes.replace(4, 1);
			
				if(population.get(i).attributes.get(1) == 1)
				{
					//System.out.print( (i) + " ");
					fw.write((i) + " " );
					population.get(i).attributes.replace(1, 2);
					infect_count = infect_count+1;
				}			
			}				
		//	System.out.println();	
			
			
			if((infect_count >= (ConfigParameters.inf_Threshold * ConfigParameters.sample_Size)) && (scanCount >= ConfigParameters.sample_Size))
			{
				//fw.write("Yes Entered : HUCs " + hcu.self() + " time :" + Simulation.currentTime + "\n");
				//System.out.println();
				hcu.prevLocation = location;
				hcu.currLocation = location; 
				hcu.timeStamp = Simulation.currentTime; 				
			}			
		}	
		
		fw.write("\n");
		
		fw.close();
		
	}/* End of clear_all_infected_with_HCU() */
	
	
	public void determinePositionsOfHCUBasedOnStrategy(Vector<HCU> hcus)
	{
		switch(ConfigParameters.strategy_Name)
		{
			case 0 : nextPositionUsingRandomStrategy(hcus);
				      break;
			case 1 : nextPositionUsingInfectionStrategy(hcus, Simulation.sampleInfect);
			         break;
			case 2 : nextPositionUsingPopulationStrategy(hcus);
			         break;
			default :System.out.println("Invalid Strategy");System.exit(1);
		}
		
	}/* End of recoverInfectedAgentsWithHCUs() */
	
	
	/* checks whether HCU is exist at neighbor before it is moving for next time unit, Function Parameters : */
	public boolean checkHCUExistAtNeighbor(Vector<HCU> hcus, int pos) 
	{
		for(int i=0; i<hcus.size(); i++)
		{
		   if( (hcus.get(i).currLocation == pos) || (hcus.get(i).prevLocation == pos) )
			   return true;
		}
		return false;		
	}/* End Of check_HCU_exist_at_neighbor() */
	
	
	/* selects the position of HCU for next time unit/step randomly based on connectivity, Function Parameters : (HCUs, id, pos, trans_HCUs_dist); */
	public int random_based_HCU_selection(Vector<HCU> hcus, int id, int pos, double[][] trans_HCUs_dist) 
	{
		int count = 0;		
		int pos1 = pos;
		
		pos1 = randomPositionSelection(trans_HCUs_dist, pos);
		
		while(checkHCUExistAtNeighbor(hcus, pos1))
		{
			pos1 = randomPositionSelection(trans_HCUs_dist, pos);
			count = count +1;
			
			if(count == 3*ConfigParameters.num_Nodes)
			{
				System.out.println("Yes It Was repeated : " + (count) );
				pos1 = pos;
				break;
			}
		}		
		
		hcus.get(id).timeStamp = Simulation.currentTime;
		hcus.get(id).prevLocation = pos;
		
		/* Make all people at this position is not scanned*/
		for(int j=0; j<Simulation.nodes.get(pos1).people.size(); j++)
		{
			//(Simulation.population.get(agentId).attributes.get(0) == pos1) && 
			int agentId = Simulation.nodes.get(pos1).people.get(j);			
			if( (Simulation.population.get(agentId).attributes.get(1) != 2) )
				Simulation.population.get(agentId).attributes.replace(4, 0);
		}		
		return pos1;
				
	}/* End of random_based_HCU_selection() */
	
	
	public void nextPositionUsingRandomStrategy(Vector<HCU> hcus)
	{
		   // System.out.println();
		    
			for(int i=0; i<hcus.size(); i++)
			{	
					
				System.out.print("\nHCU Id : " + (i) + "; prev position : " + (hcus.get(i).currLocation) + "; " );
				
				if((hcus.get(i).timeStamp + 1) == Simulation.currentTime)
				{	
					
					hcus.get(i).currLocation = random_based_HCU_selection(hcus, i, hcus.get(i).currLocation, InitialSetup.trans_prob_of_HCUs);
				}	
				else
				{
					System.out.println("Not Entered : " + i);
				}
				System.out.print("; curr position : " + (hcus.get(i).currLocation) + "; time stamp : " + hcus.get(i).timeStamp);
			}	
			
			copy_HCUs_positions_previous_to_current();
	}	
	
	
	public void nextPositionUsingPopulationStrategy(Vector<HCU> hcus) 
	{
		System.out.println();
		
		updateProbMatrixOfHCUsBasedOnPopulation(InitialSetup.trans_prob_of_HCUs);
	    
		for(int i=0; i<hcus.size(); i++)
		{	
				
			System.out.print("\nHCU Id : " + (i) + "; prev position : " + (hcus.get(i).currLocation) + "; " );
			
			if((hcus.get(i).timeStamp + 1) == Simulation.currentTime)
			{	
				hcus.get(i).currLocation = random_based_HCU_selection(hcus, i, hcus.get(i).currLocation, InitialSetup.trans_prob_of_HCUs);
			}				
			System.out.print("; curr position : " + (hcus.get(i).currLocation) + "; time stamp : " + hcus.get(i).timeStamp);
		}	
		
		copy_HCUs_positions_previous_to_current();
		
	}

	public void updateProbMatrixOfHCUsBasedOnPopulation(double[][] trans_prob_of_HCUs)
	{
		int totalPopulation = ConfigParameters.num_Nodes;
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{			
				for(int j=0; j<ConfigParameters.num_Nodes; j++)
				{
					trans_prob_of_HCUs[i][j] = (double) (Simulation.nodes.get(j).people.size())/totalPopulation;					
				} 
		}	
		
		System.out.println("Updated Transition Probabilty Matrix Of HCUs: ");
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{			
				for(int j=0; j<ConfigParameters.num_Nodes; j++)
				{
					System.out.print(trans_prob_of_HCUs[i][j] + " ");				
				} 
				System.out.println();
		}			
	}

	public void nextPositionUsingInfectionStrategy(Vector<HCU> hcus, int [] sampleInfect) 
	{
		System.out.println();
	    
		updateProbMatrixOfHCUsBasedOnInfection(InitialSetup.trans_prob_of_HCUs, sampleInfect );
		
		for(int i=0; i<hcus.size(); i++)
		{	
				
			System.out.print("\nHCU Id : " + (i) + "; prev position : " + (hcus.get(i).currLocation) + "; " );
			
			if((hcus.get(i).timeStamp + 1) == Simulation.currentTime)
			{	
				hcus.get(i).currLocation = random_based_HCU_selection(hcus, i, hcus.get(i).currLocation, InitialSetup.trans_prob_of_HCUs);
			}				
			System.out.print("; curr position : " + (hcus.get(i).currLocation) + "; time stamp : " + hcus.get(i).timeStamp);
		}	
		copy_HCUs_positions_previous_to_current();
		
	}
	
	
	public void updateProbMatrixOfHCUsBasedOnInfection( double[][] trans_prob_of_HCUs, int [] sampleInfect)
	{
		int totalInfected = 0;
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{
			totalInfected = totalInfected + sampleInfect[i];
		}
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{			
				for(int j=0; j<ConfigParameters.num_Nodes; j++)
				{
					trans_prob_of_HCUs[i][j] = (double) (Simulation.nodes.get(j).people.size())/totalInfected;					
				} 
		}	
		
		System.out.println("Updated Transitio Probabilty Matrix Of HCUs: ");
		
		for(int i=0; i<ConfigParameters.num_Nodes; i++)
		{			
				for(int j=0; j<ConfigParameters.num_Nodes; j++)
				{
					System.out.print(trans_prob_of_HCUs[i][j] + " ");				
				} 
				System.out.println();
		}
	}	
			
	 /* update the previous position of HCUs or copies the current position of HCUs to previous position of HCUs, Function parameters : (prev_pos_HCUs, curr_pos_HCUS)*/
	 public void copy_HCUs_positions_previous_to_current()
	 {
			
		// System.out.println("\nPrev and current position of HCUS:\n");			
		    
		 	for(int i=0; i<Simulation.numHCUs; i++)
		 	{	
		 		System.out.println("prev :" + ( Simulation.hcus.get(i).prevLocation) + "  ; curr : " + (Simulation.hcus.get(i).currLocation) );	 		   
		 	}
		 	
			System.out.println();

	 }/*End Of copy_new_state_to_current_state */ 
	
}
