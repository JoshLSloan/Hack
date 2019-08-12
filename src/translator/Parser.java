package translator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {

	private Scanner scan;
	private List<String> commands = new ArrayList<>();
	private int currentCommand = -1;
	private String com, arg1, arg2;
	
	public static final int C_ARITHMETIC = 1;
	public static final int C_PUSH = 2;
	public static final int C_POP = 3;
	public static final int C_LABEL = 4;
	public static final int C_GOTO = 5;
	public static final int C_IF = 6;
	public static final int C_FUNCTION = 7;
	public static final int C_RETURN = 8;
	public static final int C_CALL = 9;
	
	public Parser (String fileName) {
		
		try {
			String t = fileName;
			File file = new File(t);
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		
		readCommands();
		advance();
	}
	
	public boolean hasMoreCommands () {
		return currentCommand < commands.size();
	}
	
	public boolean advance () {
		
		currentCommand++;
		
		if (hasMoreCommands()) {

			String[] temp = commands.get(currentCommand).split("\\s");
			
			com = temp[0];
						
			if (temp.length > 1) {
				arg1 = temp[1];
			}
			
			if (temp.length > 2) {
				arg2 = temp[2];
			}
			
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the type of the current command. 
	 * @return The type of the current command.
	 */
	public int commandType () {
		if (com.equalsIgnoreCase("push")) {
			return C_PUSH;
		} else if (com.equalsIgnoreCase("pop")) {
			return C_POP;
		} else if (com.equalsIgnoreCase("label")) {
			return C_LABEL;
		} else if (com.equalsIgnoreCase("goto")) {
			return C_GOTO;
		} else if (com.equalsIgnoreCase("if-goto")) {
			return C_IF;
		} else if (com.equalsIgnoreCase("function")) {
			return C_FUNCTION;
		} else if (com.equalsIgnoreCase("call")) {
			return C_CALL;
		} else if (com.equalsIgnoreCase("return")) {
			return C_RETURN;
		} else {
			return C_ARITHMETIC;
		}
	}
	
	public String com () {
		return com;
	}
	
	public String arg1 () {
		return arg1;
	}
	
	public String arg2 () {
		return arg2;
	}
	
	public String getCurrentCommand() {
		return commands.get(currentCommand);
	}
	
	private void readCommands () {
		
		while (scan.hasNextLine()) {
			String s = scan.nextLine();
			
			s = s.trim();
			
			// Check if line is blank or entire line is a comment
			if (s.length() == 0 || s.substring(0,2).contentEquals(("//"))) {
				continue;
			}
			
			// Remove any text that is a comment
			for (int i = 0; i < s.length() - 1; i++) {
				if (s.substring(i, i+2).contentEquals("//")) { 
					s = s.substring(0, i);
					break;
				}
			}
			
			commands.add(s);
		}
	}
}
