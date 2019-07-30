import java.util.Vector;

public class Partition
{
	int id;
	Vector<Integer> listOfAgents;
	
	Partition(int _id)
	{
		this.id = _id;		
	}

	int self()
	{
		return id;
	}
	
	Vector<Integer> getListOfAgents()
	{
		return listOfAgents;
	}	
}
