package translator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) throws IOException {
		
		if (args.length < 1) {
			System.out.println("USAGE: java translator.Driver <filename>");
			System.exit(0);
		}
		
		// Do this in CodeWriter in the future
		BufferedWriter w = null;
		try {
			String[] file = args[0].split("\\.");
			w = new BufferedWriter(new FileWriter(file[0] + ".asm"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Parser parse = new Parser(args[0]);
		CodeWriter code = new CodeWriter(w);

		while (parse.hasMoreCommands()) {
			w.write("// " + parse.getCurrentCommand() + "\n");
			if (parse.commandType() == Parser.C_ARITHMETIC) {
				code.writeArithmetic(parse.com());
			} else if (parse.commandType() == Parser.C_PUSH || parse.commandType() == Parser.C_POP) {
				code.writePushPop(parse.com(), parse.arg1(), parse.arg2());
			}
			
			parse.advance();
		}
		
		code.close();
	}
}
