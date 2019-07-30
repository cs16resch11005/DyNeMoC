import java.util.Vector;

public class City
{
	int id;		
	Vector<Integer> people;	
	
	City(int id)
	{
		this.id = id;
	}
	
	int self()
	{
		return id;
	}
	
	Vector<Integer> getPopulation()
	{
		return people;
	}	

}
