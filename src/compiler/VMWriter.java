package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

	private BufferedWriter w;
	
	public VMWriter (String pFileName) {
		try  {
			w = new BufferedWriter(new FileWriter(new File(pFileName)));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				w.flush();
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void writePush (SegmentType pSegment, int pIndex) throws IOException {

		w.write("push ");
		
		switch (pSegment) {
		
			case CONST:
				w.write("constant " + pIndex);
				break;
			case ARG:
				w.write("argument " + pIndex);
				break;
			case LOCAL:
				w.write("local " + pIndex);
				break;
			case POINTER:
				w.write("pointer " + pIndex);
				break;
			case STATIC:
				w.write("static " + pIndex);
				break;
			case TEMP:
				w.write("temp " + pIndex);
				break;
			case THAT:
				w.write("that " + pIndex);
				break;
			case THIS:
				w.write("this " + pIndex);
				break;
		}
	}
	
	public void writePop (SegmentType pSegment, int pIndex) throws IOException {
		w.write("pop ");
		
		switch (pSegment) {
		
			case CONST:
				w.write("constant " + pIndex);
				break;
			case ARG:
				w.write("argument " + pIndex);
				break;
			case LOCAL:
				w.write("local " + pIndex);
				break;
			case POINTER:
				w.write("pointer " + pIndex);
				break;
			case STATIC:
				w.write("static " + pIndex);
				break;
			case TEMP:
				w.write("temp " + pIndex);
				break;
			case THAT:
				w.write("that " + pIndex);
				break;
			case THIS:
				w.write("this " + pIndex);
				break;
		}
	}
	
	public void writeArithmetic (Arithmetic pOperation) throws IOException {
		
		switch (pOperation) {
			case ADD:
				w.write("add");
				break;
			case AND:
				w.write("and");
				break;
			case EQ:
				w.write("eq");
				break;
			case GT:
				w.write("gt");
				break;
			case LT:
				w.write("lt");
				break;
			case NEG:
				w.write("neg");
				break;
			case NOT:
				w.write("not");
				break;
			case OR:
				w.write("or");
				break;
			case SUB:
				w.write("sub");
				break;
		}
	}
	
	public void writeLabel (String pLabel) throws IOException {
		w.write("label " + pLabel);
	}
	
	public void writeGoto (String pLabel) throws IOException {
		w.write("goto " + pLabel);
	}
	
	public void writeIf (String pLabel) throws IOException {
		w.write("if-goto " + pLabel);
	}
	
	public void writeCall (String pName, int pArgs) throws IOException {
		w.write("call " + pName + " " + pArgs);
	}
	
	public void writeFunction (String pName, int pLocals) throws IOException {
		w.write("function " + pName + " " + pLocals);
	}
	
	public void writeReturn () throws IOException {
		w.write("return");
	}
	
	public void close () throws IOException {
		w.flush();
		w.close();
	}
}
