
public class ParallizeNeighborsListUpdation implements Runnable
{
	
	City city;
	String name;
	Thread t;
	
	ParallizeNeighborsListUpdation(String threadName, City _city)
	{
		city = _city;
		name = threadName;		
		t    = new Thread(this,name);
		//System.out.println("Thread Name : " + threadName);
		t.start();
	}

	@Override
	public void run()
	{
		if(ConfigParameters.compare_Strategy == 0)
			ModelEpidemic.parallizeAgentNeighborsUpdation(city);
		else
			ModelEpidemic.parallizeAgentNeighborsUpdation(city, ModelEpidemic.agentNbrList);
		//System.out.println(name + " exiting.");
	}	

}
