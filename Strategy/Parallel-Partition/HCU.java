import java.util.Vector;

public class HCU
{
     int id;
     int prevLocation;
     int currLocation;
     int timeStamp;    
    
	HCU(int id)
	{
		this.id = id;
	}
	
	int self()
	{
		return id;
	}
	
	int getPrevLocation()
	{
		return prevLocation;
	}
	
	int getCurrLocation()
	{
		return currLocation;
	}
	
	int  getTimeStamp()
	{
		return timeStamp;
	}
	
	public void recoverInfectedAgentsWithHCUs(Vector<Agent> population, Vector<Integer> people)
	{
		for(int i=0; i<people.size(); i++) 
		{
			int id = people.get(i);
			
			if(population.get(id).attributes.get(1) == 1)
			{
				population.get(id).attributes.replace(1, 2);
			}
			
		}
	}/* End of recoverInfectedAgentsWithHCUs() */
	
	
}
