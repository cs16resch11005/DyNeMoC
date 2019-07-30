
public class ParallizeLocationUpdation implements Runnable
{
	City city;
	String name;	
	
	ParallizeLocationUpdation(String threadName, City _city)
	{
		city = _city;
		name = threadName;				
		//System.out.println("Thread Name : " + threadName+ " start time : " + System.currentTimeMillis());
	}

	@Override
	public void run()
	{
			ModelEpidemic.parallizeAgentLocationUpdation(city);		
		   // System.out.println(name + " exiting ." +" end time : " + System.currentTimeMillis());
	}	

}
