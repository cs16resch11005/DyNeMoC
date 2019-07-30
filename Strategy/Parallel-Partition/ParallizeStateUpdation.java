
public class ParallizeStateUpdation implements Runnable
{
	Partition partition;
	String name;
	Thread t;
	
	ParallizeStateUpdation(String threadName, Partition _partition)
	{
		partition = _partition;
		name = threadName;		
		t    = new Thread(this,name);
		//System.out.println("Thread Name : " + threadName);
		t.start();
	}

	@Override
	public void run()
	{
			ModelEpidemic.parallizeAgentStateUpdation(partition);	
		    //System.out.println(name + " exiting.");
	}	
}
