
public class ParallizeStateUpdation implements Runnable
{
	City city;
	String name;
	
	
	ParallizeStateUpdation(String threadName, City _city)
	{
		city = _city;
		name = threadName;					
	}

	@Override
	public void run()
	{
		//System.out.println("Thread Name : " + threadName + " start time : " + System.currentTimeMillis());
			ModelEpidemic.parallizeAgentStateUpdation(city);	
		   // System.out.println(name + " exiting. " + " end time : " + System.currentTimeMillis());
	}	
}
