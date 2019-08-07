package assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
	
	private Scanner scan;
	private List<String> commands = new ArrayList<>();
	
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
		return scan.hasNextLine();
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
