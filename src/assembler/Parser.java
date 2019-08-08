package assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
	
	private Scanner scan;
	private List<String> commands = new ArrayList<>();
	private int currentCommand = 0;
	
	public static final int A_COMMAND = 1;
	public static final int C_COMMAND = 2;
	public static final int L_COMMAND = 3;

	public Parser (String fileName) {
		try {
			File file = new File(fileName);
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} 
		
		readCommands();
	}
	
	public boolean hasMoreCommands () {
		return currentCommand < commands.size();
	}
	
	public boolean advance () {
		if (hasMoreCommands()) {
			currentCommand++;
			return true;
		}
		
		return false;
	}
	
	public int commandType () {
		if (commands.get(currentCommand).charAt(0) == '@') {
			return A_COMMAND;
		} else if (commands.get(currentCommand).charAt(0) == '(') {
			return L_COMMAND;
		} else {
			return C_COMMAND;
		}
	}
	 // Currently assuming its not invoked if on c command
	public String symbol () {
		String current = commands.get(currentCommand);
		if (commandType() == A_COMMAND) {
			return current.substring(1);
		} else if (commandType() == L_COMMAND){
			return current.substring(1, current.length()-1);
		} else {
			return "";
		}
	}
	
	public String dest () {
		String current = commands.get(currentCommand);
		if (!current.contains("=")) {
			return null;
		}
		
		int a = current.indexOf('=');
		
		return current.substring(0,a);
	}
		
	public String comp () {
		
		if (commandType() != C_COMMAND) {
			return "";
		}
		
		String current = commands.get(currentCommand);
		int a = 0;
		int b = 0;
		
		if (current.contains("=")) {
			a = current.indexOf('=') + 1;
		}
		
		if (current.contains(";")) {
			b = current.indexOf(';');
		} else {
			b = current.length();
		}
		
		return current.substring(a, b);
		
	}
	

	
	public String jump () {
		String current = commands.get(currentCommand);
		if (!current.contains(";")) {
			return null;
		}
		
		int a = current.indexOf(';') + 1;
		
		return current.substring(a, current.length());
		
	}
	 
	private void readCommands () {
		while (scan.hasNextLine()) {
			String s = scan.nextLine();
			
			s = s.replaceAll("\\s+","");
			
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
			
			// Later I should add more processing to determine if its a valid command.
			// For now I assume at this point it is
			commands.add(s);
		}
	}
}
