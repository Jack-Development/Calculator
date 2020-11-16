package com.bham.pij.assignments.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Arrays;

public class Calculator {

	private float result = Float.MIN_VALUE;
	private float store = 0;
	private List<Float> history;
	
	public Calculator() {
		history = new ArrayList<Float>();
	}
	
	public float evaluate(String expression) {

		// Declines invalid expressions
		if(expression.length() == 1) {
			return Float.MIN_VALUE;
		}
		
		// Converts to usable expressions
		expression = expression.replace('∗', '*');
		expression = expression.replace('−', '-');
		
		// Adds a space before ) and after ( to allow for easier splitting of the operators and operand
		for(int i = 0; i < expression.length(); i++) {
			if(expression.charAt(i) == '(') {
				if(expression.charAt(i) == ' ') {
					return Float.MIN_VALUE;
				}
				else {
					expression = expression.substring(0, i + 1) + " " + expression.substring(i + 1);
				}
			}
			if(expression.charAt(i) == ')' && !(expression.charAt(i - 1) == ' ')) {
				if(expression.charAt(i) == ' ') {
					return Float.MIN_VALUE;
				}
				else {
					expression = expression.substring(0, i) + " " + expression.substring(i);
				}
			}
		}
		
		// Initialises the split of the expression
		List<String> split = new ArrayList<String>();
		split.addAll(Arrays.asList(expression.split(" ")));
		// Separate stacks for operators and values
		Stack<String> operators = new Stack<String>();
		Stack<Float> values = new Stack<Float>();
		
		// If the split is size 1 then it is invalid
		if(split.size() == 1) {
			return Float.MIN_VALUE;
		}
		
		// Checks that for each operator there is a respective number to be used
		int num = 0;
		int expectedNum = 1;
		int expectedRight = 0;
		int expectedLeft = 0;
		// If working with the memory value then it doesn't need the initial number
		if(split.get(0).equals("+") || split.get(0).equals("-") || split.get(0).equals("*") || split.get(0).equals("/")) {
			expectedNum--;
		}
		
		Boolean nextOperator = false;
		// Counts the operands and numbers required by the operands
		for(String s : split) {
			try {
				Float.parseFloat(s);
				if(nextOperator == true) {
					return Float.MIN_VALUE;
				}
				nextOperator = true;
				num++;
			}
			catch(NumberFormatException e) {
				nextOperator = false;
				if(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")) {
					expectedNum++;
				}
				if(s.equals("(")) {
					expectedRight++;
				}
				if(s.equals(")")) {
					expectedLeft++;
				}
			}
		}
		// If there are too much or too little then it returns an error
		if(expectedNum != num) {
			return Float.MIN_VALUE;
		}
		
		// Each bracket should have a opening and closing bracket
		if(expectedRight != expectedLeft) {
			return Float.MIN_VALUE;
		}
		
		// Loops the calculator until all the operators have been dealt with and the split is empty
		while(!split.isEmpty() || !operators.isEmpty()) {

			int count = 0;
			Boolean runOne = false;
			
			for(String s : split) {
				try {
					// Attempts to parse the string into a number, if invalid it catches it
					count++;
					Float.parseFloat(s);
					values.push(Float.valueOf(s));
					// As * and / take priority in this we can process them immediately once we have the second value
					if (!operators.isEmpty() && (operators.peek().equals("*") || operators.peek().equals("/"))) {
						runOne = true;
						break;
					}
				}
				catch(NumberFormatException e) {
					// As the string isn't a number it has to be one of our operators, so if it is then we add it to the stack
					if(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("(") || s.equals(")")) {
						if(s.equals(")")){
							operators.push(s);
							// We need to calculate the bracketed statements first, so we can come back to this after
							break;
						}
						else {
						operators.push(s);
						}
					}
					else {
						return Float.MIN_VALUE;
					}
				}
			}
			
			// We cut out the parts of the operation that we have already processed and leave the rest for the next pass
			List<String> newSplit = new ArrayList<String>();
			for(int i = count; i < split.size(); i++) {
				newSplit.add(split.get(i));
			}
			split = newSplit;
			
			/* As the operators and values are part of a stack and we want to process them from left to right
			 * we will need to flip the stack in order to process them in the right order, but we should only
			 * do it during the final pass, since we don't want to swap all the chars when we're only dealing
			 * with the * or / expressions*/
			if(!operators.contains("*") && !operators.contains("/") || operators.peek().equals(")")) {
				Boolean checkBracket = false;
				// When dealing with just brackets we need to make sure we only flip the operations inside the brackets
				if(operators.peek().equals(")")) {
					operators.pop();
					checkBracket = true;
				}
				// Main flip sequence
				Stack<String> operatorsFlip = new Stack<String>();
				int operatorCount = 0;
				while(!operators.empty()) {
					if(checkBracket && operators.peek().equals("(")) {
						// If we've reached the end of the brackets then we break out the while loop
						break;
					}
					operatorCount++;
					operatorsFlip.push(operators.pop());
				}
				// We add the flipped stack onto the top of the current operators
				operators.addAll(operatorsFlip);
				
				// Repeated for values
				Stack<Float> valuesFlip = new Stack<Float>();
				int valuesCount = 0;
				while(!values.empty()) {
					// We only want to flip the values for the operators we flipped, no more
					if(checkBracket && valuesCount == operatorCount + 1) {
						break;
					}
					valuesCount++;
					valuesFlip.push(values.pop());
				}
				values.addAll(valuesFlip);
			}
			// Calculation loop starts here
			while(!operators.empty() || runOne == true) {	
				// Want to make sure we aren't at the beginning of brackets
				if(!operators.peek().equals("(")) {
					// Grab the top operator and values we want to work with
					String currentOperator = operators.pop();
					Float lastValue = values.pop();
					
					// We then perform the associated calculation based on the operation
					if(currentOperator.equals("*")) {
						if(!values.isEmpty()) {
							result = values.pop() * lastValue;
						}
						else {
							result = store * lastValue;
						}				
					}
					else if(currentOperator.equals("/")) {
						if(lastValue != 0) {
							if(!values.isEmpty()) {
								result = values.pop() / lastValue;
							}
							else {
								result = store / lastValue;
							}			
						}
						else {
							// If we're being asked to divide by 0 then we need to decline
							result = Float.MIN_VALUE;
							return result;
						}
					}
					else if(currentOperator.equals("+")) {
						if(!values.isEmpty()) {
							result = values.pop() + lastValue;
						}
						else {
							result = store + lastValue;
						}			
					}
					else if(currentOperator.equals("-")) {
						if(!values.isEmpty()) {
							// As the values are flipped for subtraction we need to perform it backwards
							result = lastValue - values.pop();
						}
						else {
							result = store - lastValue;
						}			
					}
					else {
						// If the operator is invalid then we return an error
						return Float.MIN_VALUE;
					}
					if(!operators.isEmpty() || runOne == true) {
						// When we are finished we want to store the result on the values stack
						values.push(result);
					}
					if(runOne == true) {
						// Causes runOne to only loop once
						runOne = false;
						break;
					}
				}
				else {
					// Removes the ( and checks to see if we need to * or / the result
					operators.pop();
					if (!operators.isEmpty() && (operators.peek().equals("*") || operators.peek().equals("/"))) {
						runOne = true;
					}
					else {
						break;
					}
				}
			}
		}
		// If the code has run through the loop then the result is valid and can be added to History
		history.add(result);
		// Returns the correct result
		return result;
	}
	
	public float getCurrentValue() {
		return result;
	}
	
	public float getMemoryValue() {
		return store;
	}
	
	public void setMemoryValue(float memval) {
		store = memval;
	}
	
	public void clearMemory() {
		store = 0;
	}
	
	public float getHistoryValue(int index) {
		return history.get(index);
	}
	
	public List<Float> getHistory(){
		return history;
	}
}
