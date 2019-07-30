import java.util.ArrayList;
import java.util.HashMap;

public class Agent
{	
	protected int id;	
	protected HashMap<Integer, Integer> attributes;
	protected ArrayList<Integer> nbrList;	
		
	Agent(int id)
	{
		this.id = id;
	}
	
	int self()
	{
		return id;
	}
	
	HashMap<Integer, Integer> getAtributes()
	{
		return attributes;
	}	
		
	ArrayList<Integer> getNbrList()
	{
		return nbrList;
	}
	
}
