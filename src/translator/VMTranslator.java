package translator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMTranslator {

	public static void main(String[] args) throws IOException {
		
		if (args.length < 1) {
			System.out.println("USAGE: java translator.Driver <filename>");
			System.exit(0); 
		}

		String[] file = args[0].split("\\.");
		List<File> myFiles = new ArrayList<>();
		
		String fileName = null;
		BufferedWriter w = null;
		
		if (file.length == 1) {
			// We have been passed a directory
			File directory = new File(args[0]);
			File[] tmp = directory.listFiles();
			
			for (int i = 0; i < tmp.length; i++) {
				String currentFile = tmp[i].getName();
				if (currentFile.substring(currentFile.length() - 3, 
						currentFile.length()).contentEquals(".vm")) {
					myFiles.add(tmp[i]);
					
				}
			}
			
			
			
			String out = args[0] + "/" + args[0].substring(0,args[0].length()) + ".asm";
			w = new BufferedWriter(new FileWriter(out));
			
		} else {
			myFiles.add(new File(args[0]));
			w = new BufferedWriter(new FileWriter(file[0] + ".asm"));
		}
		
		
		CodeWriter code = new CodeWriter(w);
		
		
		if (file.length == 1) {
			code.setFileName("Sys.vm");
			code.writeInit();
		}
		
		for (int i = 0; i < myFiles.size(); i++) {
			Parser parse = new Parser(myFiles.get(i).getPath() /*+ ".vm"*/); // Need .vm here?
			
			code.setFileName(myFiles.get(i).getName());
			
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
					//currentFunction = "";	
					functionCallCounter = 0;
					
				} else if (parse.commandType() == Parser.C_CALL) {
					
					
					// THIS MIGHT BE WRONG
					String ret = myFiles.get(i).getName() + "." +
							currentFunction + "$ret." + functionCallCounter;
					
					functionCallCounter++;
					
					code.writeCall(ret, parse.arg2(), parse.arg1());
					
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
		}
		code.close();
	}
}
