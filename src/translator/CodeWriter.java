package translator;

import java.io.BufferedWriter;
import java.io.IOException;

public class CodeWriter {
	
	private BufferedWriter w;
	private StringBuilder sb = new StringBuilder();
	
	private static int BOOL_INC = 0;
	
	private static final String BINARY_START = 
			"@SP\n"
			+ "M=M-1\n"
			+ "A=M\n"
			+ "D=M\n"
			+ "@SP\n"
			+ "M=M-1\n"
			+ "A=M\n";
	
	private static final String BINARY_END =
			"@SP\n"
			+ "A=M\n"
			+ "M=D\n"
			+ "@SP\n"
			+ "M=M+1\n";

	private static final String ADD_CMD = BINARY_START + "D=M+D\n" + BINARY_END;
	private static final String SUB_CMD = BINARY_START + "D=M-D\n" + BINARY_END;
	
	private static final String NEG_CMD =
			"@SP\n"
			+ "M=M-1\n"
			+ "A=M\n"
			+ "M=-M\n"
			+ "@SP\n"
			+ "M=M+1\n";
	
	private static final String PUSH_CNST = 
			"D=A\n"
			+ "@SP\n"
			+ "A=M\n"
			+ "M=D\n"
			+ "@SP\n"
			+ "M=M+1\n";
	
	private static final String END = 
			"(END)\n"
			+ "@END\n"
			+ "0;JMP";
	
	public CodeWriter (BufferedWriter pW) {
		w = pW;
	}
	
	public void writeArithmetic (String cmd) {
		
		try {
			if (cmd.equals("add")) {
				w.write(ADD_CMD);
			} else if (cmd.equals("sub")) {
				w.write(SUB_CMD);
			} else if (cmd.equals("neg")) {
				w.write(NEG_CMD);
			} else if (cmd.equals("eq")) {
				String tmp = 
					BINARY_START
					+ "D=M-D\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=-1\n"
					+ "@EQUAL." + BOOL_INC + "\n"
					+ "D;JEQ\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=0\n"
					+ "(EQUAL." + BOOL_INC + ")\n"
					+ "@SP\n"
					+ "M=M+1\n";
				w.write(tmp);
				BOOL_INC++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writePushPop (String pCommand, String pSegment, String pIndex) {
		
		sb.setLength(0);
		
		if (pCommand.equals("push") && pSegment.equals("constant")) {
			sb.append("@");
			sb.append(pIndex);
			sb.append("\n");
			sb.append(PUSH_CNST);
		}
		
		try {
			w.write(sb.toString());
			sb.setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			w.write("// END\n");
			w.write(END);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
