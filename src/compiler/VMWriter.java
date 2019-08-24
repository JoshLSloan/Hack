package compiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {
private BufferedWriter w;
	
	public VMWriter (String pFileName) {
		try  {
			w = new BufferedWriter(new FileWriter(pFileName));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void writePush (SegType pSegment, int pIndex) throws IOException {

		w.write("push ");
		
		switch (pSegment) {
		
			case CONST:
				w.write("constant " + pIndex + "\n");
				break;
			case ARG:
				w.write("argument " + pIndex + "\n");
				break;
			case LOCAL:
				w.write("local " + pIndex + "\n");
				break;
			case POINTER:
				w.write("pointer " + pIndex + "\n");
				break;
			case STATIC:
				w.write("static " + pIndex + "\n");
				break;
			case TEMP:
				w.write("temp " + pIndex + "\n");
				break;
			case THAT:
				w.write("that " + pIndex + "\n");
				break;
			case THIS:
				w.write("this " + pIndex + "\n");
				break;
		}
	}
	
	public void writePop (SegType pSegment, int pIndex) throws IOException {
		w.write("pop ");
		
		switch (pSegment) {
		
			case CONST:
				w.write("constant " + pIndex + "\n");
				break;
			case ARG:
				w.write("argument " + pIndex + "\n");
				break;
			case LOCAL:
				w.write("local " + pIndex + "\n");
				break;
			case POINTER:
				w.write("pointer " + pIndex + "\n");
				break;
			case STATIC:
				w.write("static " + pIndex + "\n");
				break;
			case TEMP:
				w.write("temp " + pIndex + "\n");
				break;
			case THAT:
				w.write("that " + pIndex + "\n");
				break;
			case THIS:
				w.write("this " + pIndex + "\n");
				break;
		}
	}
	
	public void writeArithmetic (Arithmetic pOperation) throws IOException {
		
		switch (pOperation) {
			case ADD:
				w.write("add\n");
				break;
			case AND:
				w.write("and\n");
				break;
			case EQ:
				w.write("eq\n");
				break;
			case GT:
				w.write("gt\n");
				break;
			case LT:
				w.write("lt\n");
				break;
			case NEG:
				w.write("neg\n");
				break;
			case NOT:
				w.write("not\n");
				break;
			case OR:
				w.write("or\n");
				break;
			case SUB:
				w.write("sub\n");
				break;
		}
	}
	
	public void writeLabel (String pLabel) throws IOException {
		w.write("label " + pLabel + "\n");
	}
	
	public void writeGoto (String pLabel) throws IOException {
		w.write("goto " + pLabel + "\n");
	}
	
	public void writeIf (String pLabel) throws IOException {
		w.write("if-goto " + pLabel + "\n");
	}
	
	public void writeCall (String pName, int pArgs) throws IOException {
		w.write("call " + pName + " " + pArgs + "\n");
	}
	
	public void writeFunction (String pName, int pLocals) throws IOException {
		w.write("function " + pName + " " + pLocals + "\n");
	}
	
	public void writeReturn () throws IOException {
		w.write("return\n");
	}
	
	public void close () throws IOException {
		w.flush();
		w.close();
	}
}
