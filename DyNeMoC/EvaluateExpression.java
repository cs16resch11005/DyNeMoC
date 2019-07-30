import java.util.Stack;
import java.util.Vector;

public class EvaluateExpression 
{
	int num_operators = 0;
	QueryProcessing qp;
	
	public EvaluateExpression(QueryProcessing _qp)
	{
		qp = _qp;
		num_operators = 0;
	}

	public boolean evaluateSingleAtomicProposition(String expression)
	{
		while(expression.startsWith("("))
		{	
			if(expression.endsWith(")"))
			{
				expression = expression.substring(1, expression.length()-1);
			}
			else
			{	System.out.println("Paranthesis is missing : Error In The Query");
			System.exit(1);
			}				
		}

		if(expression.length() == 1)
		{
			if(expression.equals("1"))
				return true;
			else
				return false;
		}
		else    	 
		{
			return false;
		}

	}/* End of CheckSingleAtomicProposition*/


	public String EvaluatePrefixExpression(String expression, int opId)
	{
		// System.out.println("Expression = " + expression);

		if((expression.isEmpty())   || (expression.equals("0")) || (expression.equals("1")))
		{
			return expression;
		}

		opId = num_operators;
		num_operators = num_operators+1;

		String operator = findOperator(expression);
		expression = expressionAfterOperator(expression);
		// System.out.println("expressionAfterOperator "+ operator + " :"+ expression);

		String firstOperand = findFirstOperand(operator, expression);
		expression = expressionAfterFirstOperand(operator, expression);
		// System.out.println("expressionAfterFirstOperand "+ firstOperand + " : "+ expression);

		String secondOperand = findSecondOperand(expression);
		expression = expressionAfterSecondOperand(expression);		 
		
		//System.out.println("expressionAfterSecondOperand "+ secondOperand + " : "+ expression);		 

		firstOperand = EvaluatePrefixExpression(firstOperand, opId);
		//System.out.println("firstOperand " + firstOperand);

		
		secondOperand = EvaluatePrefixExpression(secondOperand, opId);
		//System.out.println("secondOperand " + secondOperand);
		return formulaEvaluation(operator, opId, firstOperand, secondOperand);
	}

	public String findOperator(String expression) 
	{
		String operator = "";

		while(expression.startsWith("("))
		{	
			if(expression.endsWith(")"))
			{
				expression = expression.substring(1, expression.length()-1);
			}
			else
			{	System.out.println("Paranthesis is missing : Error In The Query");
			System.exit(1);
			}				
		}

		if( expression.equals(null) || expression.isEmpty() || !Character.isAlphabetic(expression.charAt(0)))	
		{			
			System.out.println("Operator Is Missing : Error In The Query");
			System.exit(1);
		}

		for(int i=0; i<expression.length(); i++)
		{
			if(expression.charAt(i) == '(')
			{	
				break;
			}
			else
			{	
				operator = operator + expression.charAt(i);
			}
		}				
		return operator;
	}

	public String expressionAfterOperator(String expression) 
	{
		int index = 0;		

		while(expression.startsWith("("))
			expression = expression.substring(1, expression.length()-1);

		for(int i=0; i<expression.length(); i++)
		{
			if(expression.charAt(i) == '(')
			{
				index = i; 
				break;
			}			
		}
		expression = expression.substring(index, expression.length());		
		return expression;
	}

	public String findFirstOperand(String operator, String expression) 
	{
		String firstOperand = "";
		boolean flag = false;

		while(expression.startsWith("("))
		{	
			if(expression.endsWith(")"))
			{
				expression = expression.substring(1, expression.length()-1);
			}
			else
			{	System.out.println("Paranthesis is missing : Error In The Query");
			System.exit(1);
			}				
		}	

		if(expression.isEmpty() || expression.equals(null))	
		{			
			System.out.println("Operand(s) Are Missing : Error In The Query");
			System.exit(1);
		}

		//System.out.println(expression.charAt(0) + " " +Character.isAlphabetic(expression.charAt(0)));

		if(Character.isAlphabetic(expression.charAt(0)))
		{
			Stack<Character> stack = new Stack<Character>(); 

			for(int i=0; i<expression.length(); i++)
			{
				if(expression.charAt(i) == '(')
				{
					stack.push('(');
				}
				else
				{	
					if(expression.charAt(i) == ')')
					{
						stack.pop();

						if(stack.empty())
						{
							firstOperand = firstOperand + expression.charAt(i);

							if( ((expression.length() > i+1) && (expression.charAt(i+1) == ',')) || ( (operator.equals("ALWAYS")) || (operator.equals("NOT")) || (operator.equals("NEXT")) || (operator.equals("FINALLY"))) )
								flag = true;						
							else
								System.out.println("Comma is missing: Error In The Query");

							break;	
						}						
					}
				}
				firstOperand = firstOperand + expression.charAt(i);
			}		
			if(!flag)
			{
				System.out.println("Paranthesis is Missing In the Expression : Error In The Query");
				System.exit(1);
			}
		}
		else
		{
			if(expression.charAt(0) == '0' || expression.charAt(0) == '1') 
			{
				firstOperand = firstOperand + expression.charAt(0);

				if( (expression.length() > 1) && (expression.charAt(1) != ',') )
				{
					System.out.println("Comma is Missing : Error In The Query");
					System.exit(1);					
				}
				else
				{
					if( !((operator.equals("NOT")) || (operator.equals("NEXT")) || (operator.equals("FINALLY")) || (operator.equals("ALWAYS")))&& (expression.length() == 1) )
					{
						System.out.println("Second Operand is Missing In the Expression: Error In The Query");
						System.exit(1);
					}	
				}
			}  
			else
			{
				System.out.println("First Operand is Neither 0 Nor 1  : Error In The Query");
				System.exit(1);
			}			
		}
		return firstOperand;		
	}

	public String expressionAfterFirstOperand(String operator, String expression) 
	{
		int index = 0;		

		while(expression.startsWith("("))
			expression = expression.substring(1, expression.length()-1);

		if(Character.isAlphabetic(expression.charAt(0)))
		{
			Stack<Character> stack = new Stack<Character>(); 

			for(int i=0; i<expression.length(); i++)
			{
				if(expression.charAt(i) == '(')
				{
					stack.push('(');
				}
				else
				{	
					if(expression.charAt(i) == ')')
					{
						stack.pop();

						if(stack.empty())
						{	
							index = i;
							break;
						}							
					}
				}				
			}	
			if(!((operator.equals("NOT")) || (operator.equals("NEXT")) || (operator.equals("FINALLY"))||(operator.equals("ALWAYS")) ))
				expression = expression.substring(index+2, expression.length());
			else
				expression = "";
		}
		else
		{
			for(int i=0; i<expression.length(); i++)
			{
				if(expression.charAt(i) == ',')
				{
					index = i;
					break;
				}			
			}			
			expression = expression.substring(index+1, expression.length());
		}

		if(expression.length() == 0)
		{
			return "";
		}
		return expression;		
	}

	public String findSecondOperand(String expression) 
	{
		String secondOperand = "";
		boolean flag = false;

		while(expression.startsWith("("))
		{	
			if(expression.endsWith(")"))
			{
				expression = expression.substring(1, expression.length()-1);
			}
			else
			{	System.out.println("Paranthesis is missing : Error In The Query");
			System.exit(1);
			}				
		}	

		if(expression.isEmpty())	
		{			
			return "";
		}


		if(Character.isAlphabetic(expression.charAt(0)))
		{
			Stack<Character> stack = new Stack<Character>(); 

			for(int i=0; i<expression.length(); i++)
			{
				if(expression.charAt(i) == '(')
				{
					stack.push('(');
				}
				else
				{	
					if(expression.charAt(i) == ')')
					{
						stack.pop();

						if(stack.empty())
						{
							secondOperand = secondOperand + expression.charAt(i);
							if(expression.length() == (i+1))
							{
								flag = true;
							}									
							break;	
						}						
					}
				}
				secondOperand = secondOperand + expression.charAt(i);
			}		
			if(!flag)
			{
				System.out.println(expression + "\n Error In The Query: At Most Two Operands Are Accepted OR paranthesis missing");
				System.exit(1);
			}
		}
		else
		{
			if(expression.charAt(0) == '0' || expression.charAt(0) == '1') 
			{
				secondOperand = secondOperand + expression.charAt(0);

				if(expression.length() != 1)
				{
					System.out.println(expression + "\nSomething is Missing OR Extra : Error In The Query");
					System.exit(1);					
				}
			}  
			else
			{
				System.out.println("Second Operand is Neither 0 Nor 1  : Error In The Query");
				System.exit(1);
			}			
		}			
		return expression;
	}

	public String expressionAfterSecondOperand(String expression) 
	{	
		return "";
	}

	public String formulaEvaluation(String operator, int opId, String firstOperand, String secondOperand)
	{
		String result = "0";
		
		if(operator.equals("OR"))
		{
			if( (firstOperand.equals("1")) || (secondOperand.equals("1")) ) 
				result =  "1";
			else
				result =  "0";
		}
		else if(operator.equals("AND"))
		{
			if( (firstOperand.equals("1")) && (secondOperand.equals("1")) ) 
				result= "1";
			else
				result = "0";
		}		
		else if(operator.equals("NOT"))
		{
			if(secondOperand.isEmpty()) 
			{
				if(firstOperand.equals("1")) 
					result =  "0";
				else
					result =  "1";
			}
			else
			{	
				System.out.println("Error In the Expression: NOT Operator Takes One Operand");
				System.exit(1);
			}
		}
		else if(operator.equals("NEXT"))
		{
			if(secondOperand.isEmpty()) 
			{
				if(qp.queryOperators[opId][0] < Simulation.currentTime ) 
				{
					result =  evaluateNEXTOperatorBeforeItsTimeQuatum(opId, firstOperand, secondOperand);
					firstOperand = result;
				}					
				else
				{	
					result = evaluateNEXTOperatorAfterItsTimeQuatum(opId, firstOperand, secondOperand);
					firstOperand = result;
				}
			}
			else
			{	
				System.out.println("Error In the Expression: NEXT Operator Takes One Operand");
				System.exit(1);
			}
		}
		else if(operator.equals("UNTIL"))
		{
			/* if first operand is not false in the last/previous time instance then only */ 
			if( (secondOperand.equals("1")) || ((qp.queryOperators[opId][2] == -1)&&(qp.queryOperators[opId][6] == 1)) )  
			{
				secondOperand = "1";				
				result =  "1";
			}
			else
			{	
				if((qp.queryOperators[opId][5] == 0))
					firstOperand = "0";
				result =  "0";
			}
		}
		else if(operator.equals("ALWAYS"))
		{
			if(secondOperand.isEmpty()) 
			{
				if( firstOperand.equals("1") && (qp.queryOperators[opId][13] != 0) ) 
				{
					qp.queryOperators[opId][13] = 1;
					
					if(ConfigParameters.sim_Time == Simulation.currentTime )
						result =  "1";
					else
						result = "0";
				}
				else
				{
					qp.queryOperators[opId][13] = 0;
					firstOperand = "0";
					result =  "0";
				}					
				
			}
			else
			{	
				System.out.println("Error In the Expression: ALWAYS Operator Takes One Operand");
				System.exit(1);
			}
		}
		else if(operator.equals("FINALLY"))
		{			
			if(secondOperand.isEmpty()) 
			{				
				if(firstOperand.equals("1") || (qp.queryOperators[opId][13] == 1) ) 
				{
					qp.queryOperators[opId][13] = 1;
					firstOperand = "1";
					result =  "1";				
				}
				else
				{	qp.queryOperators[opId][13] = 0;				
					result =  "0";
				}
			}
			else
			{	
				System.out.println("Error In the Expression: FINALLY Operator Takes One Operand");
				System.exit(1);				
			}
		}
		else
		{
			System.out.println("Wrong Operator or NULL Operator");
			System.exit(1);			
		}	

		//if(operator.equals("OR") || operator.equals("AND") || operator.equals("NOT"))
		setOperandValues(opId, firstOperand, secondOperand);		

		if((opId == 0)&&(qp.untilList.size()>0))
			upadateUntilOperatorInformation();

		return result;
	} /* End of formulaEvaluation() */ 

	/*This function get the ancestor who got locked at current instance */
	public int getCurrentGrandParent(int id)
	{
		int parent = qp.queryOperators[id][2];

		while(qp.queryOperators[parent][10] != 1)
		{
			parent = qp.queryOperators[parent][2];

			if(parent == -1)
				break;
		}
		return parent;
	}/*End of the getCurrentGrandParent()*/

	/*This function updates the all until operators data based on their parent information */
	public void upadateUntilOperatorInformation()
	{
		int maxTime = 0;
		int lock[] = new int[qp.num_operands];
		int time[] = new int[qp.num_operands];

		for(int i=0; i<qp.num_operands; i++)
		{
			time[i] = -1;
			lock[i] = -1;
		}		

		for(int j=0; j<qp.untilList.size(); j++)
		{
			int i = qp.untilList.get(j);

			if(qp.queryOperators[i][1] == 4)
			{
				int gp = qp.queryOperators[i][8];/* fetch grand parent information */
				time[i] = qp.queryOperators[i][0];

				if(gp != -1)
				{
					if((qp.queryOperators[gp][5] == 1)&&(time[gp] >= Simulation.currentTime))
					{
						qp.queryOperators[i][5] = 1;

						if( (lock[gp] == 1) )
						{
							int ancestor = getCurrentGrandParent(i);

							qp.queryOperators[i][0] = qp.queryOperators[i][0] + (Simulation.currentTime) - time[ancestor];
							lock[i] = 1;
						}
					}
					else
					{
						if(qp.queryOperators[i][5] == 1) 
						{	
							if((lock[gp] == 1)&&(qp.queryOperators[i][10] != 1)&&(time[gp] >= (Simulation.currentTime)))
							{
								qp.queryOperators[i][0] = qp.queryOperators[i][0] + (Simulation.currentTime) - time[gp];
								lock[i] = 1;
							}							
						}	
						else
						{
							if(qp.queryOperators[i][10] != 1)
							{								
								lock[i] = 1;
								qp.queryOperators[i][0] = Simulation.currentTime;
								qp.queryOperators[i][10] = 1;
							}								
						}
					}					
				}		
				else
				{
					if((qp.queryOperators[i][5] == 0)&&(qp.queryOperators[i][10] != 1)&&(time[i] >= (Simulation.currentTime)))
					{
						//System.out.println("Id : " + i + " Current Time : " + (Simulation.currentTime));
						lock[i] = 1;
						qp.queryOperators[i][0] = Simulation.currentTime;
						qp.queryOperators[i][10] = 1;
					}	
				}

				//System.out.println("\nId : " + i +"  time : "+ qp.queryOperators[i][0]);

				if(maxTime < qp.queryOperators[i][0])
					maxTime = qp.queryOperators[i][0];
			}			
		}	

		if(maxTime > 0)
		{
			ConfigParameters.sim_Time = maxTime;
			//System.out.println("\nsim Time : " + maxTime);
		}

		/* if the left child of until operand has more time quantum than right child, then we have to make both time quantum equal  */
		int secondMax = 0;

		for(int j=0; j<qp.untilList.size(); j++)
		{
			int i = qp.untilList.get(j);

			if(qp.queryOperators[i][1] == 4)
			{
				int lc = qp.queryOperators[i][3];
				int rc = qp.queryOperators[i][4];

				if( (lc != -1) && (rc != -1)  )
				{
					int opLc = qp.queryOperators[lc][1];
					int opRc = qp.queryOperators[rc][1];

					if((opLc == 4)&&(opRc == 4))
					{
						if(qp.queryOperators[lc][0] >  qp.queryOperators[rc][0])
						{
							qp.queryOperators[lc][0] = qp.queryOperators[rc][0];
						}
					}
				}

				if(secondMax < qp.queryOperators[i][0])
					secondMax = qp.queryOperators[i][0];
			}
		}

		if(maxTime > secondMax)
		{
			ConfigParameters.sim_Time = secondMax;
			//System.out.println("\nSecond Max Time : " + secondMax);
		}

	}/* End of the finalUpadateOfUntilOperator()*/


	public void setOperandValues(int opId, String firstOperand, String secondOperand)
	{
		if(firstOperand.equals("0"))
			qp.queryOperators[opId][5] = 0;
		else
			qp.queryOperators[opId][5] = 1;

		if(!secondOperand.isEmpty())
		{	
			if(secondOperand.equals("0"))
				qp.queryOperators[opId][6] = 0;
			else
				qp.queryOperators[opId][6] = 1;
		}		
	}

	public String evaluateNEXTOperatorBeforeItsTimeQuatum(int opId, String firstOperand, String secondOperand)
	{
		int parent = qp.queryOperators[opId][2];

		if(qp.queryOperators[opId][8] != -1)
		{
			if(qp.queryOperators[opId][9] == 1)
			{
				if(qp.queryOperators[opId][10]%2 == 1)
				{	
					return "0";
				}
				else
				{
					return "1";
				}
			}
			else
			{
				if(qp.queryOperators[opId][10]%2 == 1)
				{	
					return "1";
				}
				else
				{
					return "0";
				}
			}
		}
		else 
		{
			if(parent == -1)
			{	
				return "0";
			}			
			else if( (qp.queryOperators[parent][1] == 2) || (qp.queryOperators[parent][1] == 6))
			{
				return "1";
			}
			else
			{
				return "0";
			}
		}
	}	/* End of the evaluateNEXTOperator() */

	public String evaluateNEXTOperatorAfterItsTimeQuatum(int opId, String firstOperand, String secondOperand)
	{

		if((qp.queryOperators[opId][12] == 1)||(qp.queryOperators[opId][8] != -1))
		{
			if(firstOperand.equals("1")) 
				return "1";
			else
				return "0";
		}
		else
		{
			if(qp.queryOperators[opId][0] == (Simulation.currentTime))
			{
				if(firstOperand.equals("1"))								
				{	
					qp.queryOperators[opId][13] = 1;
					return "1";
				}
				else
				{
					qp.queryOperators[opId][13] = 0;
					return "0";
				}
			}
			else
			{
				if(qp.queryOperators[opId][13] == 1)
					return "1";				
				else				
					return "0";					
			}
		}				
	}/*End of the evaluateNEXTOperatorAfterItsTimeQuatum() */


	public boolean checkActualValueIsExpectedValue(int operator, int actual, int expected)
	{
		switch(operator)
		{
		case 1: if(actual == expected){return true;}else{ return false;} 
		case 2: if(actual != expected){return true;}else{ return false;} 
		case 3: if(actual < expected){return true;}else{ return false;} 
		case 4: if(actual <= expected){return true;}else{ return false;} 
		case 5: if(actual > expected){return true;}else{ return false;} 
		case 6: if(actual >= expected){return true;}else{ return false;}		
		default:  System.out.println("Invalid Operator");System.exit(1);
			return false;
		}	
	}/*End of checkActualValueIsExpectedValue() */

	public boolean evaluateAgentAtomicProposition(Vector<Agent> population, int [] atomicExp) 
	{
		int agentId  = atomicExp[2];
		int attrId   = atomicExp[3];
		int attrVal  = atomicExp[4]; 
		int operator = atomicExp[5];

		//System.out.println(" " + agentId );

		if( (agentId < -2) || (agentId >= population.size()) || ((agentId != -1)&&(agentId != -2) && (attrId >= population.get(agentId).attributes.size())) )
		{
			return false;
		}
		else if(agentId == -2)
		{
				int actual  = Simulation.numRecovered;
				attrVal = population.size();	/* CAUTION ---- keep remember change this attrVal for other simulation*/						
				if(checkActualValueIsExpectedValue(operator, actual, attrVal))	
					return true;				
				else				
					return false;
		}
		else
		{
			if(agentId == -1)
			{
				if(atomicExp[4] == 1)				
					return true;
				else
					return false;
			}			
			else
			{
				int actual  = population.get(agentId).attributes.get(attrId);
				//System.out.println("actual : " + actual + " expected :" + attrVal);
				
				if(checkActualValueIsExpectedValue(operator, actual, attrVal))							
					return true;				
				else				
					return false;
			}
		}		
	}/* End of evaluateAgentAtomicExpression() */


	public boolean evaluateCityAtomicProposition(Vector<Agent> population, Vector<City> nodes, int [] atomicExp) 
	{
		int cityId   = atomicExp[2];
		int attrId   = atomicExp[3];
		int attrVal  = atomicExp[4];		
		int operator = atomicExp[5];
		int cumVal   = atomicExp[6];

		if( (cityId > nodes.size()) || (attrId >= population.get(0).attributes.size()) )
		{
			return false;
		}
		else if(cityId == nodes.size())
		{
				int actual  = Simulation.numRecovered;

				if(checkActualValueIsExpectedValue(operator, actual, cumVal))	
				{	System.out.println("Exit Time : "+Simulation.currentTime);
					return true;	
				}
				else				
					return false;
		}
		else
		{
			//City city = nodes.get(cityId);
			int actual = Simulation.countDiffAgents[cityId][attrVal];//findCumulativeValueOfAttribute(attrId, attrVal, population, city);
			//System.out.println(" Actual : " + actual);
			if(checkActualValueIsExpectedValue(operator, actual, cumVal))
			{
				return true;
			}
			else
			{
				return false;
			}
		}

	}/* End of evaluateCityAtomicExpression() */

	// @@override
	public int findCumulativeValueOfAttribute(int attrId, int attrVal, Vector<Agent> population, City city)
	{
		int numAgents = city.people.size();
		int count = 0;

		for(int i=0; i<numAgents; i++)
		{
			int agentId = city.people.get(i);
			Agent agent = population.get(agentId);	

			if(attrVal == 1)
			{
				if( (agent.attributes.get(attrId) == attrVal) || (agent.attributes.get(attrId) == (attrVal+1))) 
					count = count+1;
			}
			else
			{
				if(agent.attributes.get(attrId) == attrVal)
					count = count+1;
			}
		}		
		return count;
	}/*End of findCumulativeValueOfAttribute()*/

	public  String evaluateAtomicPropositions(int numOperands, String modQuery, int [] operandPositions, int [][] queryExpressions, Vector<Agent> population, Vector<City> nodes)
	{
		//EvaluateExpression ee = new EvaluateExpression();

		for(int i=0; i<numOperands; i++)
		{

			int pos = operandPositions[i];	
			
			//System.out.println(i + " : " + pos);

			boolean atomProp = false;

			if(queryExpressions[i][0] == 0)
				atomProp =  evaluateAgentAtomicProposition(population, queryExpressions[i]);
			else
				atomProp =  evaluateCityAtomicProposition(population, nodes, queryExpressions[i]);

			if(atomProp)
				modQuery = modQuery.substring(0, pos) + '1' + modQuery.substring(pos+1);
			else
				modQuery = modQuery.substring(0, pos) + '0' + modQuery.substring(pos+1);		 
		}
		return modQuery;
	}/*End of evaluateAtomicPropositions()*/


	/*public static void main(String args[])
	{
		String query = "NOT(OR(0,AND(1,OR(1,0))))"; //"OR(OR(0,NEXT(1)),AND(1,OR(0,NEXT(1))))"; // "AND(1,OR(AND(1,OR(0,AND(1,1))),0),1)"; //"OR(OR(0,0),AND(1,1))";  	
		EvaluateExpression ee = new EvaluateExpression();			
		String op = ee.Evaluate(query, 0);
		System.out.println("Final Results: " + op);
	}	*/
}