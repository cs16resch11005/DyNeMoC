
public class ParallizeLocationUpdation implements Runnable
{
	City city;
	String name;
	Thread t;
	
	ParallizeLocationUpdation(String threadName, City _city)
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
			ModelEpidemic.parallizeAgentLocationUpdation(city);	
		    //System.out.println(name + " exiting.");
	}	
}
