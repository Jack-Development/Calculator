package com.bham.pij.assignments.calculator;

import java.util.Scanner;

public class TestCalculator {
	public static void main(String[] args) {
		Calculator calc = new Calculator();
		String input = "";
		Scanner s = new Scanner(System.in);
		
		// Loops program until the user enters "quit"
		while(!input.equals("quit")) {
			System.out.println("Enter expression to evaluate: (Enter 'quit' to close)");
			input = s.nextLine();
			
			// As long as the last result isn't invalid it will save it in memory, otherwise saves 0
			if(input.equals("m")) {
				if(calc.getCurrentValue() != Float.MIN_VALUE) {
					calc.setMemoryValue(calc.getCurrentValue());
				}
				else {
					calc.setMemoryValue(0);
				}
			}
			// Returns current value in memory
			else if(input.equals("mr")) {
				System.out.println(calc.getMemoryValue());
			}
			// Clears memory to 0
			else if(input.equals("c")) {
				calc.setMemoryValue(0);
			}
			// Outputs the history as the numbers separated by a space
			else if(input.equals("h")) {
				for(int i = 0; i < calc.getHistory().size(); i++) {
					if(i != calc.getHistory().size() - 1) {
						System.out.print(calc.getHistoryValue(i) + " ");
					}
					else {
						System.out.println(calc.getHistoryValue(i));
					}
				}
			}
			// Exits the program
			else if(input.equals("quit")) {
				System.out.println("Exiting...");
			}
			// If none of the following apply it feeds it to the calculator
			else {
				float result = calc.evaluate(input);
				if(result == Float.MIN_VALUE) {
					System.out.println("Invalid Input");
				}
				else {
					System.out.println(result);
				}
			}
		}
		// After the loop finishes it closes the Scanner
		s.close();
	}
	
}
