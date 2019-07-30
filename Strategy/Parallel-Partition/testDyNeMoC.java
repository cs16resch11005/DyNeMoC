import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintStream;

public class testDyNeMoC
{
	public static boolean isTraining = false;

	/* calculate number of times simulation to be run using estimation method */
	public int calNumOfSimulations(double ci, double epsilon)
	{
		int num_sims = 0;
		epsilon = 2*epsilon*epsilon;
		ci = Math.log(2/ci);
		num_sims = (int)Math.ceil(ci/epsilon);
		return num_sims;
	}

	public static void main(String args[]) throws Exception
	{
		System.setOut(new PrintStream(new OutputStream() {
			public void write(int b){ /* NO-OP */ }
		}));		 

		//System.setOut(new PrintStream(new FileOutputStream("output2.txt")));		

		File fe = new File("results.txt");    
		if(fe.exists())
			fe.delete();

		File configFile =  new File(System.getProperty("user.dir")+ "/../" +"Config/config.properties");
		ConfigParameters cp = new ConfigParameters(configFile);
		cp.read_configFile();		
		InitialSetup is = new InitialSetup();
		is.read_Network_Information();

		//System.out.println("num_HCUS" + ConfigParameters.num_HCUs);
		//ConfigParameters.num_HCUs;		

		long startTime = System.currentTimeMillis();

		testDyNeMoC th = new testDyNeMoC();
		int numRuns   = th.calNumOfSimulations(ConfigParameters.val_Ci, ConfigParameters.val_Epsilon);
		int numSucess  = 0;
		int numFailure = 0;
		
		//numRuns = 10;

		System.out.println("\nNum of Runs : " + numRuns);

		for(int j=0; j<numRuns; j++)
		{
			
			Simulation sm = new Simulation(0);
			sm.preConfiguration();

			FileWriter fw =  new FileWriter("results.txt", true);
		
			if(sm.isQuerySatisfied)
			{
				numSucess  = numSucess + 1;
				fw.write("\nIteration Id : " + j +"\tSuccess");
			}
			else
			{
				numFailure = numFailure + 1;
				fw.write("\nIteration Id : " + j +"\tFailure");
			}
			fw.close();
		}

		int totalRuns = numFailure+numSucess;

		FileWriter fw =  new FileWriter("results.txt", true);
		fw.write("\nnum Sucess  : " + numSucess +"\tnum Failure : " + numFailure);
		fw.write("\nProbability of given query has been satisfied : " + (double)numSucess/totalRuns+"\n");
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);  //Total execution time in milli seconds

		fw.write("\nExecution Time testDyNeMoC:" + duration);
		fw.close();
		//System.out.println(duration);

	}
}
