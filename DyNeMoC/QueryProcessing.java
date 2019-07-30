import java.util.ArrayList;
import java.util.StringTokenizer;

public class QueryProcessing 
{
	public int num_operators = 0;
	public int num_operands  = 0;
	public boolean isQueryOnAllAgents = false;
	public int [][] queryExpressions;
	public int [][] queryOperators  ;	 
	public int []   operandPositions;

	public ArrayList<Integer> untilList = new ArrayList<Integer>();
	
	public EvaluateExpression ee;
	public int maxSimTime = 0;
	public boolean isAFExist = false; /* whether Always or Finally operators are exist in the query */

	public QueryProcessing()
	{
		ee = new EvaluateExpression(null);    	 
	}          

	public void findGrandParentsofUntilOperator()
	{
		for(int i = 0; i<num_operators; i++)
		{
			if(queryOperators[i][1] == 4)
			{
				int parent = queryOperators[i][2];
				int grandParent  = -1;

				while(parent != -1)
				{
					if(queryOperators[parent][1] == 4)
					{
						grandParent = parent;
						break;
					}
					parent   = queryOperators[parent][2];
				} 
				/* some cases one of the ancestor can be until operator, we need this information while evaluating nested until operator*/ 
				queryOperators[i][8] = grandParent;
				// System.out.println("Id : " + i + "  Grand Parent : " + grandParent);
				untilList.add(i);
			}
		}		
	}/* End of the findGrandParentsofUntilOperator() */ 

	/*checking whether there exist any ancestor who is either ALWAYS or FINALLY for each operator */
	public void checkAnyAncestorIsAF()
	{
		for(int i=0; i<num_operators; i++)
		{	
			int parent = queryOperators[i][2];
			
			while(parent != -1)
			{
				if((queryOperators[parent][1] == 5)||(queryOperators[parent][1] == 6))
				{
					queryOperators[i][12] = 1;/*checking whether any of his ancestor is FINALLY or ALWAYS */
					break;
				}
				parent = queryOperators[parent][2];
			}
		}
	}/*End of the checkAnyAncestorIsAF()*/
	
	/* finding time quantum for next operator; some times next can be nested so the time quantum for that next operator will be number of next operator in his ancestor list*/
	public void findTimeQunatumOfNextOperator()
	{
		int tempTime[] = new int[num_operators]; 
		int maxNextTime = 0;
		boolean isUntilExist = false;
		
		for(int i = 0; i<num_operators; i++)
		{
			tempTime[i] = 0;

			if(queryOperators[i][1] == 3)
			{
				int parent = queryOperators[i][2];
				int lc=0;
				int currNode   = i;
				int countNext  = 1;
				int countNot   = 0;
				boolean flag = false;

				while(parent != -1)
				{
					lc = queryOperators[parent][3];

					if(queryOperators[parent][1] == 3)
					{
						countNext = countNext + 1;
					}

					if((queryOperators[parent][1] == 2)&&(flag == false))
					{
						countNot = countNot + 1;
					}
					
					if(queryOperators[parent][1] == 4)
					{
						isUntilExist = true;
						
						if(queryOperators[i][8] == -1)
							queryOperators[i][8] = parent; /* fetching the nearest ancestor who is UNTIL */ 

						if(flag == false)
						{
							if(lc == currNode)
								queryOperators[i][9] = 1; /*checking whether this node is left child or not*/
							else
								queryOperators[i][9] = 0;    						
							flag = true;
						}
					}    				
					currNode = parent;
					parent   = queryOperators[parent][2];
				}
				queryOperators[i][7]  = countNext;  /*time stamp of the next operator(supposed to be true)*/
				queryOperators[i][11] = countNot;  /* number of not operators exist before this operator */
				
				if(maxNextTime < queryOperators[i][7])
					maxNextTime = queryOperators[i][7];
			}			
		}
		
		if((maxNextTime > maxSimTime)&&(isUntilExist == false))		
		{
			maxSimTime = maxNextTime;
			return;
		}
		
		/* finding maximum possible nested next operators */
		for(int i = 0; i<num_operators; i++)
		{
			if(queryOperators[i][1] == 3)
			{
				if((queryOperators[i][8] != -1)&&(queryOperators[i][9] == 0))
				{
					if(tempTime[(queryOperators[i][8])] < queryOperators[i][7])
						tempTime[(queryOperators[i][8])] = queryOperators[i][7]; 				
				}   			
			}   		
		}

		int maxTempTime = 0;
		
		/* updating the time quantum of until operator which contains next operators */
		for(int j=0; j<untilList.size(); j++)
		{
			int i = untilList.get(j);
			queryOperators[i][0] = tempTime[i] + queryOperators[i][0];
			
			if(maxTempTime < queryOperators[i][0])
				maxTempTime = queryOperators[i][0];
		}
		
		if(maxTempTime > maxSimTime)
			maxSimTime = maxTempTime;		

	} /* End of the findTimeQunatumOfNextOperator() */  

    
	public void findParentsofOperators()
	{
		for(int i = 0; i<num_operators; i++)
		{
			int lc = queryOperators[i][3];
			int rc = queryOperators[i][4];

			if(lc != -1) /* if left child is not -1 then his parent is i*/
				queryOperators[lc][2] = i;
			if(rc != -1)/* if right child is not -1 then his parent is i*/
				queryOperators[rc][2] = i;
		}
		queryOperators[0][2] = -1;		
		
	}/*End of the findParentsofOperators()*/		

	public int findtimeQunatumOfUntilOperator(String expression)
	{
		int i=0;
		String temp = "";

		while(expression.charAt(i) == '(')
			i=i+1;

		while(Character.isDigit(expression.charAt(i)))
		{
			temp = temp + expression.charAt(i);
			i=i+1;
		}

		int time = Integer.parseInt(temp);    	
		temp = temp+",";
		expression = expression.replace(temp, "");

		return time;
	}/*End  of the findtimeQunatumOfUntilOperator() */

	public String expressionAfterUntilOperator(String expression)
	{
		int i=0;
		String temp = "";

		while(expression.charAt(i) == '(')
			i=i+1;

		while(Character.isDigit(expression.charAt(i)))
		{
			temp = temp + expression.charAt(i);
			i=i+1;
		}

		temp = temp+",";
		expression = expression.replace(temp, "");
		return expression;    	 
	}

	public int findOperatorName(String operatorName )
	{
		if(operatorName.equals("ALWAYS") || operatorName.equals("FINALLY"))
			 isAFExist = true;
		
		if(operatorName.equals("OR"))
		{
			return 0;
		}
		else if(operatorName.equals("AND"))
		{
			return 1;    	 
		}
		else if(operatorName.equals("NOT"))
		{
			return 2;    	 
		}
		else if(operatorName.equals("NEXT"))
		{	 
			return 3;    	 
		}
		else if(operatorName.equals("UNTIL"))
		{
			return 4;
		}
		else if(operatorName.equals("FINALLY"))
		{
			return 5;    	 
		}
		else if(operatorName.equals("ALWAYS"))
		{
			return 6;    	 
		}
		else 
		{
			System.out.println("Error!! Unknown Operator Name : " + operatorName);
			System.exit(1);
			return -1; 
		}    	   	 
	}

	public String fetchOperatorInformation(String expression, int opId, int opName, int time, int parent, int lc, int rc)
	{
		int timeq = 0;
		// System.out.println("Expression = " + expression);	 
		if(expression.isEmpty()|| (expression.equals("0")) || (expression.equals("1")))
		{			 
			if(!expression.isEmpty())
				num_operands++;			 
			return expression;
		}		 		 
		// System.out.println("Operator Id : " + num_operands + "  num_opt : "+ num_operators);		 
		opId = num_operators;		 
		num_operators = num_operators+1;
		String operator = ee.findOperator(expression);
		expression = ee.expressionAfterOperator(expression);

		opName =  findOperatorName(operator);				 
		if(operator.equals("UNTIL"))
		{
			timeq = findtimeQunatumOfUntilOperator(expression);
			expression = expressionAfterUntilOperator(expression);
		}		 
		//System.out.println("expressionAfterOperator "+ operator + " :"+ expression);		 
		String firstOperand = ee.findFirstOperand(operator, expression);
		expression = ee.expressionAfterFirstOperand(operator, expression);
		// System.out.println("expressionAfterFirstOperand "+ firstOperand + " : "+ expression);		 
		String secondOperand = ee.findSecondOperand(expression);
		expression = ee.expressionAfterSecondOperand(expression);			 
		// System.out.println("expressionAfterSecondOperand "+ secondOperand + " : "+ expression);		 
		if(Character.isAlphabetic(firstOperand.charAt(0)))
			lc = num_operators;
		else
			lc = -1;		 
		firstOperand = fetchOperatorInformation(firstOperand, opId, opName, time+timeq, lc, rc, parent);
		// System.out.println("firstOperand " + firstOperand);
		if(!secondOperand.isEmpty())
		{
			if(Character.isAlphabetic(secondOperand.charAt(0)))
				rc = num_operators;
			else
				rc = -1;
		}
		else
		{
			rc = -1;
		}		 
		secondOperand = fetchOperatorInformation(secondOperand, opId, opName, time+timeq, lc, rc, parent);
		// System.out.println("secondOperand " + secondOperand);		
		if(maxSimTime < (time+timeq))
			maxSimTime = time+timeq;

		queryOperators[opId][0] = time+ timeq;    /*time quantum of the operator */		 
		queryOperators[opId][1] = opName;       /* operator name */
		queryOperators[opId][2] = parent;      /* parent of the current operator */
		queryOperators[opId][3] = lc;         /* left child of the current operator */
		queryOperators[opId][4] = rc;        /* right child of the current operator */		 

		return expression; 
	}

	/*Dynamically Allocating Memory for Query Processing Variables based total atomic propositions presented in it */
	public void allocateMemoryForQueryProcessingVariables(int totalAP)
	{
		queryExpressions = new int [totalAP][30];
		queryOperators   = new int [totalAP][14];	 
		operandPositions = new int[totalAP];    	    
	}

	/*This function finds the total atomic proposition in the Query and returns that value*/	
	public int findTotalExpressionsInQuery(String query)
	{		
		int count=0;	
		int queryLen = query.length();

		for(int i=0; i<queryLen; i++)
		{
			if((i+2 <= queryLen) && (query.substring(i, i+2).equals("AP")))
				count = count + 1;
			else if((i+4 <= queryLen) && query.substring(i,i+4).equals("TRUE"))
				count = count + 1;
			else if((i+5 <= queryLen) && query.substring(i,i+5).equals("FALSE"))
				count = count + 1;
		}
		System.out.println("Total Atomic Propositions : " + (count));
		return count;
	}

	public int findAPRelationalOperation(String operator) 
	{
		if(operator.equals("=="))
		{
			return 1;
		}
		else if(operator.equals("!="))
		{
			return 2;
		}
		else if(operator.equals("<"))
		{
			return 3;
		}
		else if(operator.equals("<="))
		{
			return 4;
		}
		else if(operator.equals(">"))
		{
			return 5;
		}
		else if(operator.equals(">="))
		{
			return 6;
		}
		else
		{
			System.out.println("Invalid Operator");
			System.exit(1);
		}
		return 0;
	}
	
	public int findAgentRelationalOperation(String operator) 
	{
		if(operator.equals("=="))
		{
			return 1;
		}
		else if(operator.equals("!="))
		{
			return 2;
		}
		else if(operator.equals("<"))
		{
			return 3;
		}
		else if(operator.equals("<="))
		{
			return 4;
		}
		else if(operator.equals(">"))
		{
			return 5;
		}
		else if(operator.equals(">="))
		{
			return 6;
		}
		else
		{
			System.out.println("Invalid Operator");
			System.exit(1);
		}
		return 0;
	}

	public int findCityRelationalOperation(String operator) 
	{
		if(operator.equals("=="))
		{
			return 1;
		}
		else if(operator.equals("!="))
		{
			return 2;
		}
		else if(operator.equals("<"))
		{
			return 3;
		}
		else if(operator.equals("<="))
		{
			return 4;
		}
		else if(operator.equals(">"))
		{
			return 5;
		}
		else if(operator.equals(">="))
		{
			return 6;
		}
		else
		{
			System.out.println("Invalid Operator");
			System.exit(1);
		}
		return 0;
	}

	public int parseSetofAttributes(String subexp, int expId, int Id, int subId, int sizeSet, int [][] expressions)
	{   
		int counter = 0;
		
		if(Id == 0)
		{
			subexp = subexp.replace(",", "");
			expressions[expId-1][subId+counter+2] = Integer.parseInt(subexp);/*agent_id or city_id*/
			
			if(expressions[expId-1][subId+counter+2] == -2)
				isQueryOnAllAgents = true;
			
			System.out.println("Index : " + (subId+counter+2) + " value : " + expressions[(expId-1)][subId+counter+2] + "  ");
			counter = counter+1;
		}
		else if(Id == 3)
		{
			subexp = subexp.replace("}", "");
			StringTokenizer st = new StringTokenizer(subexp,",");
			
			while(st.hasMoreTokens()) 
			{
				String temp = st.nextToken();
				
				if(counter == sizeSet )
				{
					expressions[(expId-1)][subId+counter+2] = Integer.parseInt(temp);
				}
				else
				{
					expressions[(expId-1)][subId+counter+2] = findAPRelationalOperation(temp);/* fetching operator values */
				}				
				System.out.println("Index : " + (subId+counter+2) + " value : " + expressions[(expId-1)][subId+counter+2] + "  ");
				counter = counter+1;
			}		
			
			if((counter < sizeSet) && (counter > (sizeSet+1)))
			{
				System.out.println("\nSize of set Attributes and set Values are not equal in Atomic proposition " + expId);
				System.exit(1);
			}	
			else if(counter == sizeSet)
			{
				expressions[(expId-1)][0] = 0; /*agent based Atomic Proposition*/
			}
			else
			{
				expressions[(expId-1)][0] = 1; /*city based Atomic Proposition*/
			}
			
			expressions[(expId-1)][1] = sizeSet; /* size of set-attributes*/
		}
		else
		{
			subexp = subexp.replace("},", "");
			//subexp = subexp.replace(")", "");

			StringTokenizer st = new StringTokenizer(subexp,",");
			
			while(st.hasMoreTokens()) 
			{
				expressions[(expId-1)][subId+counter+2] = Integer.parseInt(st.nextToken()); /* attribute_id  or  attribute_val*/
				System.out.println("Index : " + (subId+counter+2) + " value : " + expressions[(expId-1)][subId+counter+2] + "  ");
				counter = counter+1;				
			}	
			
			if((Id != 1)&&(counter != sizeSet))
			{
				System.out.println("\nSize of set Attributes and set Values are not equal in Atomic proposition " + expId);
				System.exit(1);
			}				
		}
		
		return counter;
		
	}/*End of the parseSetofAttributes()*/
	
	public void parseAPwithSetofAttributes(String exp, int expId, int [][] expressions)
	{
		StringTokenizer st = new StringTokenizer(exp,"{");
		int count1 = 0;
		int count2 = 0;
		int cntTemp = 0;
		int sizeSet = 0;
		
		System.out.println("Total tokens : " + st.countTokens());
		
		while(st.hasMoreTokens()) 
		{			
			String temp = st.nextToken();
			
			if(count1 == 1)
			{
				sizeSet = parseSetofAttributes(temp, expId, count1, count2, sizeSet, expressions);
				count2  = count2 + sizeSet;
			}
			else
			{
				cntTemp = parseSetofAttributes(temp, expId, count1, count2, sizeSet, expressions);
				count2  = count2 + cntTemp;
			}
			
			System.out.println("count : " + count1 + " token : "+ temp);
			count1 = count1+1;
		}
		
		System.exit(1); //UnComment
		
	}/*End of the parseAPwithSetofAttributes()*/
	
	public void parseAtomicProposition(String exp, int expId, int [][] expressions, int [][] operators)
	{
		System.out.println("Atomic Proposition : " + exp +" ID: " + expId);			
		
		exp = exp.replace("(","");
		exp = exp.replace(")","");	
		
		if(exp.contains("{"))
		{
			parseAPwithSetofAttributes(exp, expId, expressions);
			return;
		}
		
		StringTokenizer st = new StringTokenizer(exp,", "); 
		
	    int count = 0;		    
	    if(st.countTokens() == 4) /* if number of arguments in the AP is 4 then consider it as Agent Related */
	    {
	    	while(st.hasMoreTokens()) 
	    	{  
	    		if(count == 3)
	    		{	
	    			expressions[(expId-1)][count+2] = findAPRelationalOperation(st.nextToken());
	    			//expressions[(expId-1)][count] = 0;
	    		}	
	    		else	    			
	    		{
	    			expressions[(expId-1)][count+2] = Integer.parseInt(st.nextToken());
	    		}	    		
	           System.out.print(expressions[(expId-1)][count+2] + "  ");
	           count = count+1;
	    	}
	    	expressions[(expId-1)][0] = 0;
	    	expressions[(expId-1)][1] = 1;

	    }
	    else if(st.countTokens() == 5) /* if number of arguments in the AP is 5 then consider it as City Related */
	    {
	    	while (st.hasMoreTokens()) 
	    	{  
	    		if(count == 3)
	    			expressions[(expId-1)][count+2] = findAPRelationalOperation(st.nextToken());
	    		else    			
	    		    expressions[(expId-1)][count+2] = Integer.parseInt(st.nextToken());
	    		
	             System.out.print(expressions[(expId-1)][count+2] + "  ");
	             count = count+1;
	    	}
	    	System.out.println("");
	    	expressions[(expId-1)][0] = 1;
	    	expressions[(expId-1)][1] = 1;
	    }
	    else 
	    {
	    	System.out.println("Invalid Atomic Proposition... parameter mismatch");
			System.exit(1);
	    }	    
	   // System.out.println();	    
	}/* End of the parseAtomicProposition()*/
	
	
	/*this function fetch the each component of the AP */	
	public void parseExpression(String exp, int expId, int [][] expressions, int [][] operators)
	{
		System.out.println("Atomic Proposition : " + exp +" ID: " + expId);	

		exp = exp.replace("(","");
		exp = exp.replace(")","");		

		StringTokenizer st = new StringTokenizer(exp,", "); 

		int count = 0;		    
		if(st.countTokens() == 4) /* if number of arguments in the AP is 4 then consider it as Agent Related */
		{
			while(st.hasMoreTokens()) 
			{  
				if(count == 3)
				{	
					expressions[(expId-1)][count] = findAgentRelationalOperation(st.nextToken());
					//expressions[(expId-1)][count] = 0;
				}	
				else	    			
				{
					expressions[(expId-1)][count] = Integer.parseInt(st.nextToken());
				}	    		
				//System.out.print(expressions[(expId-1)][count] + "  ");
				count = count+1;
			}
			expressions[(expId-1)][5]=0;
		}
		else if(st.countTokens() == 5) /* if number of arguments in the AP is 5 then consider it as City Related */
		{
			while (st.hasMoreTokens()) 
			{  
				if(count == 4)
					expressions[(expId-1)][count] = findCityRelationalOperation(st.nextToken());
				else    			
					expressions[(expId-1)][count] = Integer.parseInt(st.nextToken());

				//  System.out.print(expressions[(expId-1)][count] + "  ");
				count = count+1;
			}
			expressions[(expId-1)][5]=1;
		}
		else 
		{
			System.out.println("Invalid Expression... parameter mismatch");
			System.exit(1);
		}	    
		// System.out.println();	    
	}	

	/*this function finds the AP(which is tuple which contains either 4 or 5 components)*/
	public int findAtomicProposition(int startIndex, String query, int expId, int [][] expressions, int [][] operators)
	{		
		//System.out.println("Starting Index : " + startIndex);

		String exp = "";
		int endIndex = startIndex;		
		boolean flag = false;

		for(int i=startIndex+2; i<query.length(); i++)
		{
			exp = exp + query.charAt(i);
			if(query.charAt(i) == ')')
			{	
				endIndex = i;
				flag = true;
				break;				
			}			
		}		

		if(flag == true)
		{
			parseAtomicProposition(exp, expId, expressions, operators);
		}
		else
		{
			System.out.println("An Error In Query : ");
			System.exit(1);
		}

		return endIndex;
	}/**/

	/*parse the query(split the query based on the AP--Atomic Propositions, TRUE, FALSE values)*/
	public String parseQuery(String query, int [][] expressions, int [][] atomicPropositions)
	{
		int count=1;		
		String modQuery = "";		
		int queryLen = query.length();			

		for(int i=0; i<queryLen; i++)
		{

			if((i+2 <= queryLen) && (query.substring(i, i+2).equals("AP")))
			{
				//System.out.println( "Sub String : " + query.substring(i, i+3));
				i = findAtomicProposition(i, query, count, expressions, atomicPropositions);	
				//System.out.println(query.charAt(i));
				modQuery = modQuery + count;				
				count = count+1;
			}
			else if((i+4 <= queryLen) &&query.substring(i,i+4).equals("TRUE"))
			{
				System.out.println("Atomic Proposition : TRUE -- ID:"  + count);	
				i=i+3;					
				expressions[(count-1)][4] =  1;/* attribute value */
				expressions[(count-1)][2] = -1;/* agent Id */
				expressions[(count-1)][1] =  1;/* number of attributes*/
				expressions[(count-1)][0] =  0;/* type of AP city or agent */
				modQuery = modQuery + count;				
				count = count+1;
			}
			else if((i+5 <= queryLen) &&query.substring(i,i+5).equals("FALSE"))
			{
				System.out.println("Atomic Proposition : FALSE -- ID:"  + count);	
				i=i+4;
				expressions[(count-1)][4] =  0;/* attribute value */				
				expressions[(count-1)][2] = -1;/* agent Id */
				expressions[(count-1)][1] =  1;/* number of attributes*/
				expressions[(count-1)][0] =  0;/* type of AP city or agent */
				modQuery = modQuery + count;				
				count = count+1;				
			}
			else 
			{
				modQuery = modQuery + query.charAt(i);
				continue;
			}			
		}		
		System.out.println("Modified Query : " + modQuery);		
		return modQuery;
	}/*End of the parseQuery()*/

	/* initialize query operators */
	public void  initializeQueryOperators(int totalAP)
	{
		for(int i=0; i<totalAP; i++)
		{
			queryOperators[i][0]  = -1;/*time quantum of the operator */
			queryOperators[i][1]  = -1;/* operator name */
			queryOperators[i][2]  = -1;/* parent of the  operator */
			queryOperators[i][3]  = -1;/* left child of the  operator */
			queryOperators[i][4]  = -1;/* right child of the  operator */
			queryOperators[i][5]  = -1;/* value of the left child  */
			queryOperators[i][6]  = -1;/* value of the right child */
			queryOperators[i][7] = -1;/* time stamp of the next operator when it has to be true*/
			queryOperators[i][8] = -1;/* grand parent of operator */
			queryOperators[i][9] = -1;/* checking whether this node is left child or not*/
			queryOperators[i][10] = -1;/* flag for until operator evaluation */
			queryOperators[i][11] = -1;/* number of not operators exist before this operator */
			queryOperators[i][12] = -1;/*checking whether any of his ancestor is FINALLY or ALWAYS */
			queryOperators[i][13] = -1;/* its own value in the last/previous instance */
		}
		
		for(int i=0; i<totalAP; i++)
		{
			for(int j=0; j<30; j++)
				queryExpressions[i][j] = -1;
		}
		
	}/*End of the initializeQueryOperators()*/

	public String queryWithOutUntilTimeQuntum(String dummyQuery)
	{
		String temp = "";
		//System.out.println("Dummy Query 1: " + dummyQuery);
		int count= 0;

		for(int i=0; i<dummyQuery.length(); i++)
		{
			if(Character.isDigit(dummyQuery.charAt(i)))
			{
				if((i>=6) && dummyQuery.substring(i-6, i).equals("UNTIL(") )
				{
					while(Character.isDigit(dummyQuery.charAt(i)))
						i=i+1;										
				}
				else
				{
					operandPositions[count] = temp.length();
					count = count +1;
					temp = temp + "0";
					if(Character.isDigit(dummyQuery.charAt(i+1)))
						i=i+1;
				}
			}
			else
			{
				temp = temp + dummyQuery.charAt(i);
			}
		}
		dummyQuery = temp;
		return dummyQuery;
	}/*End of the queryWithOutUntilTimeQuntum()*/

	public String queryWithUntilTimeQuntum(String dummyQuery)
	{
		String temp = "";

		for(int i=0; i<dummyQuery.length(); i++)
		{

			if(Character.isDigit(dummyQuery.charAt(i)))
			{
				if((i>=6) && dummyQuery.substring(i-6, i).equals("UNTIL(") )
				{
					//System.out.println("SubQuery : " + dummyQuery.substring(i-6, i));

					while(Character.isDigit(dummyQuery.charAt(i)))
					{
						temp = temp + dummyQuery.charAt(i);
						i=i+1;
					}
					temp = temp + dummyQuery.charAt(i);
				}
				else
				{
					temp = temp + "0";
					if(Character.isDigit(dummyQuery.charAt(i+1)))
						i=i+1;
				}
			}
			else
			{
				temp = temp + dummyQuery.charAt(i);
			}
		}
		dummyQuery = temp;
		return dummyQuery;
	}/*End of the queryWithUntilTimeQuntum()*/

	public String readAndParseQuery()
	{
		String query = ConfigParameters.query;		
		query = query.replaceAll(" ","");
		query = query.toUpperCase();
		
		query = query.replaceAll("ALL,", "-2,");
		//query = query.replaceAll("ALL)", "-2)");


		int totalAP = findTotalExpressionsInQuery(query);
		allocateMemoryForQueryProcessingVariables(totalAP);		
		initializeQueryOperators(totalAP);	
		String modQuery = parseQuery(query, queryExpressions, queryOperators);
		System.out.println("\nGiven Query : " + modQuery);			
		
		String temp = queryWithUntilTimeQuntum(modQuery);

		fetchOperatorInformation(temp, 0, -1, 0, -1, -1, -1);
		findParentsofOperators();
		findTimeQunatumOfNextOperator();
		findGrandParentsofUntilOperator();
		modQuery = queryWithOutUntilTimeQuntum(temp);		
		
		if(isAFExist == false)
			ConfigParameters.sim_Time = maxSimTime+1;
		//System.out.println("\nFinal Query : " + modQuery);	
		return modQuery;		
	}/*End of the readAndParseQuery()*/
}
