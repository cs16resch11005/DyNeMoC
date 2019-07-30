import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class GenInput
{    
	  private File config_File; 
	  
	  private static int num_Nodes;
	  private static int sim_Time;
	  private static int num_People;
	  private static double local_prob;
	  private static double inf_threshold;
	 
 	  private static String  path_position_folder;
	  private static String  path_state_folder;
	  private static String  path_mapping_folder;
		
    /* constructor */
    GenInput(File configFile)
    {
      config_File = configFile;
    }/* End of ESP() */
    
    /* reading the configuration file and assign values to input variables */
    public void read_configFile() throws Exception 
    {
           
       FileReader fr = null;
  	
  	 try 
  	 {
  		             fr  = new FileReader(config_File);
  		Properties props = new Properties();
  		props.load(fr);

  		num_Nodes  = Integer.parseInt(props.getProperty("num_nodes"));
  		sim_Time   = Integer.parseInt(props.getProperty("time_units"));
  		num_People = Integer.parseInt(props.getProperty("num_people"));
  		
  		local_prob	    = Double.parseDouble(props.getProperty("local_prob"));
  		inf_threshold	= Double.parseDouble(props.getProperty("inf_threshold"));;
		
  		path_position_folder = System.getProperty("user.dir")+ "/../" + props.getProperty("location_of_position_folder");
        path_state_folder    = System.getProperty("user.dir")+ "/../" + props.getProperty("location_of_state_folder");
        path_mapping_folder	 = System.getProperty("user.dir")+ "/../" + props.getProperty("location_of_mapping_folder"); 
          
        System.out.println("Num of Nodes             :" + num_Nodes);		
        System.out.println("Simulation Time Units    :" + sim_Time);		
        System.out.println("Total Population         :" + num_People);		
        
        System.out.println("path_position_folder     :" + path_position_folder);		
        System.out.println("path_state_folder        :" + path_state_folder);		
        System.out.println("path_mapping_folder      :" + path_mapping_folder);		
           
  		
  	   }
  	   catch(FileNotFoundException ex)
         {
             System.out.println(" \n Config File is not found, please pass it through constructor!! \n");
  	   }
     	   catch(IOException ex)
         {
              ex.printStackTrace();
    	   }
         catch(Exception e)
         {
  	      System.out.println(" \n Error occured, please try again!! \n ");
  		  return;
  	   }
         finally
         {
                fr.close();                
         }             

     }/* End Of read_configFile() */

    
	public void read_init_pop_at_each_node(String _path_init_pop_at_each_node, int population[]) throws Exception
	{
		   System.out.println("\n read_init_population_people \n");
	       read_input_int_data(_path_init_pop_at_each_node, population);		
	}/* End Of read_init_pop_at_each_node() */   
	
	 public void read_input_int_data(String input_file_name, int [] population) throws Exception 
	 {
	         String lineRead   = null;
	         BufferedReader br = null;

	         try
	         {
	         	      br = new BufferedReader(new FileReader(input_file_name));
	        	lineRead = br.readLine();
	        	int people = Integer.parseInt(lineRead);
	        	
	        	if(people != num_People)
	        	{
	        			System.out.println("\nError ! Missmatch in the population");
	        			System.exit(1);
	        	}
	        		
	        	lineRead = br.readLine();
	        	parse_input_int_data(population, lineRead);
	          }
		      catch(Exception e) 
		      {
	         	e.printStackTrace();
	      	  }         
	          finally
	          {
	                br.close();        
	          }

	}/* End of read_input_int_data() */
	 
	 public void parse_input_int_data(int[] population, String data) 
	 {
		       StringTokenizer st = new StringTokenizer(data,", ");       
		       int count = 0;
		       while (st.hasMoreTokens()) 
		       {  
		    	   population[count] = Integer.parseInt(st.nextToken());   
		          System.out.print(population[count] + "  ");
		         count = count+1;
		       }
		       System.out.println();	
	}/* End of parse_input_int_data()*/
	 
	 public void mapOriginalCityToPerson(int nodes, int[] population, int[] org_node_person)
		{
			int start_index = 0;
			int count = 0;
			
			for(int i=0; i<nodes; i++) 
			{				
				for(int j=start_index; j<(start_index + population[i]); j++) 
				{
					org_node_person[(count)] = i;					
					System.out.println("person id : " + count + "  city :" + org_node_person[(count)]);
					count = count + 1;
				}	
				start_index = start_index + population[i];
			}
		}/* End of mapOriginalCityToPerson()*/

	public static void main(String args[]) throws Exception
	{
		System.setOut(new PrintStream(new OutputStream() {
			  public void write(int b) {
			    // NO-OP
			  }
			})); 		 
		      
		//System.setOut(new PrintStream(new FileOutputStream("output1.txt")));

		File config_File =  new File(System.getProperty("user.dir")+ "/../" +"Config/config.properties");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		GenInput gi = new GenInput(config_File);
		 gi.read_configFile();
		config_File.getAbsolutePath(); 
		 int population []      = new int[num_Nodes];	
		 int org_pos_person[]   = new int[num_People];
		 int curr_pos_person[]  = new int[num_People];
        
		 gi.read_init_pop_at_each_node(System.getProperty("user.dir")+ "/../" +"Input/Network/Resources/initial_population_at_each_node.txt", population);	
		 gi.mapOriginalCityToPerson(num_Nodes, population, org_pos_person);
		 
		 Generate_Input_ESP gie = new Generate_Input_ESP(num_Nodes, sim_Time, num_People, local_prob, inf_threshold, path_position_folder,path_state_folder, path_mapping_folder);
		 gie.start_simulation(org_pos_person, curr_pos_person);
		 
		 System.out.println("\nlocal_prob : " + local_prob + " inf_threshold: " + inf_threshold + " prob: "+ (1-local_prob)/(num_Nodes-1));
		 
	}	
}
