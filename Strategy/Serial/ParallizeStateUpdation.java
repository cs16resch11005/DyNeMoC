
public class ParallizeStateUpdation implements Runnable
{
	City city;
	String name;
	Thread t;
	
	ParallizeStateUpdation(String threadName, City _city)
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
			ModelEpidemic.parallizeAgentStateUpdation(city);	
		else
			ModelEpidemic.parallizeAgentStateUpdation(city, ModelEpidemic.stateRandum);
		//System.out.println(name + " exiting.");
	}	
}
