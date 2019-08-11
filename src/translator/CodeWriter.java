package translator;

import java.io.BufferedWriter;
import java.io.IOException;

public class CodeWriter {
	
	private BufferedWriter w;
	private StringBuilder sb = new StringBuilder();
	
	private static int EQ_INC = 0;
	private static int GT_INC = 0;
	private static int LT_INC = 0;
	
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
	private static final String AND_CMD = BINARY_START + "D=M&D\n" + BINARY_END;
	private static final String OR_CMD = BINARY_START + "D=M|D\n" + BINARY_END;
	
	private static final String NEG_CMD =
			"@SP\n"
			+ "M=M-1\n"
			+ "A=M\n"
			+ "M=-M\n"
			+ "@SP\n"
			+ "M=M+1\n";
	
	private static final String NOT_CMD =
			"@SP\n"
			+ "M=M-1\n"
			+ "A=M\n"
			+ "M=!M\n"
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
					+ "@EQUAL." + EQ_INC + "\n"
					+ "D;JEQ\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=0\n"
					+ "(EQUAL." + EQ_INC + ")\n"
					+ "@SP\n"
					+ "M=M+1\n";
				w.write(tmp);
				EQ_INC++;
			} else if (cmd.equals("gt")) {
				String tmp =
					BINARY_START
					+ "D=M-D\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=-1\n"
					+ "@GT." + GT_INC + "\n"
					+ "D;JGT\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=0\n"
					+ "(GT." + GT_INC + ")\n"
					+ "@SP\n"
					+ "M=M+1\n";
				w.write(tmp);
				GT_INC++;
			} else if (cmd.equals("lt")) {
				String tmp =
					BINARY_START
					+ "D=M-D\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=-1\n"
					+ "@LT." + LT_INC + "\n"
					+ "D;JLT\n"
					+ "@SP\n"
					+ "A=M\n"
					+ "M=0\n"
					+ "(LT." + LT_INC + ")\n"
					+ "@SP\n"
					+ "M=M+1\n";
				w.write(tmp);
				LT_INC++;
			} else if (cmd.equals("and")) {
				w.write(AND_CMD);
			} else if (cmd.equals("or")) {
				w.write(OR_CMD);
			} else if (cmd.equals("not")) {
				w.write(NOT_CMD);
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
