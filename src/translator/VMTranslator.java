package translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMTranslator {

	public static void main(String[] args) throws IOException {
		
		if (args.length < 1) {
			System.out.println("USAGE: java translator.Driver <filename>");
			System.exit(0); 
		}
		

		String[] file = args[0].split("\\.");
		String fileName = null;
		// Do this in CodeWriter in the future
		BufferedWriter w = null;
		try {
			fileName = new File(args[0]).getName();
			w = new BufferedWriter(new FileWriter(file[0] + ".asm"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// Will need to add loop over directory 
		// Remove .vm
		Parser parse = new Parser(args[0]);
		CodeWriter code = new CodeWriter(w);
		code.setFileName(fileName.substring(0,fileName.length()-3));
		
		code.writeInit();

		//String previousFunction = "";// Probably don't need this
		String currentFunction = "";  
		int functionCallCounter = 0;
		int globalCallCounter = 0;
		
		while (parse.hasMoreCommands()) {
			w.write("// " + parse.getCurrentCommand() + "\n");
			if (parse.commandType() == Parser.C_ARITHMETIC) {
				code.writeArithmetic(parse.com());
				
			} else if (parse.commandType() == Parser.C_PUSH || parse.commandType() == Parser.C_POP) {
				code.writePushPop(parse.com(), parse.arg1(), parse.arg2());
				
			} else if (parse.commandType() == Parser.C_LABEL) {
				code.writeLabel(currentFunction + "$" + parse.arg1());
				
			} else if (parse.commandType() == Parser.C_GOTO) {
				code.writeGoto(currentFunction + "$" + parse.arg1());
				
			} else if (parse.commandType() == Parser.C_IF) {
				code.writeIf(currentFunction + "$" + parse.arg1());
			
			} else if (parse.commandType() == Parser.C_FUNCTION) {
				currentFunction = parse.arg1();
				code.writeFunction(currentFunction, Integer.parseInt(parse.arg2()));
				
			} else if (parse.commandType() == Parser.C_RETURN) {
				code.writeReturn();
				currentFunction = "";	
				functionCallCounter = 0;
				
			} else if (parse.commandType() == Parser.C_CALL) {
				
				String ret = fileName.substring(0,fileName.length()-3) + "." +
						currentFunction + "$ret." + functionCallCounter;
				
				code.writeCall(ret, parse.arg2(), parse.arg1());
				
				///////////////////////////////////////////////////////////////////////////////////////
				
				/*
				if (!currentFunction.equals("")) {
					code.writeLabel(currentFunction + "$ret." + functionCallCounter);
					functionCallCounter++;
				} else {
					code.writeLabel("$ret." + globalCallCounter);
					globalCallCounter++;
				}
				*/
			}
			
			parse.advance();
		}
		
		code.close();
	}
}
