package compiler;

import java.io.BufferedWriter;
import java.io.IOException;

public class CompilationEngine {
	
	private JackTokenizer tokenizer;
	private BufferedWriter w = null;
	
	private VMWriter writer;
	private SymbolTable table;
	private String className;
	
	private int args;
	
	private static final String op = "+-*/&|<>=";
	
	public CompilationEngine (String pClassName, JackTokenizer tokens, VMWriter pWriter) {
		this.className = pClassName;
		this.tokenizer = tokens;
		this.writer = pWriter;
		this.table = new SymbolTable();
		
		this.compileClass();
	}
	
	private void compileClass () {
		try {
			//w.write("<class>\n");
			
			System.out.println("COMPILING CLASS " + className);
			System.out.println("-----------------------------");
			
			 // class
			tokenizer.advance();
			
			 // className
			tokenizer.advance();
			
			 // {
			tokenizer.advance();
			
			// ClassVarDec
			while (tokenizer.tokenValue().equals("static") || tokenizer.tokenValue().equals("field")) {
				compileClassVarDec();
			}
			
			// ClassVarDec
			while (tokenizer.tokenValue().equals("static") || tokenizer.tokenValue().equals("field")) {
				compileClassVarDec();
			}
			
			table.toString();
			// subroutineDec
			while (tokenizer.tokenValue().equals("constructor") || tokenizer.tokenValue().equals("function") ||
					tokenizer.tokenValue().equals("method")) {
				
				compileSubroutine();
			}
			
			 // {
			tokenizer.advance();
			
			//w.write("</class>\n");
			
			writer.close();
			return;
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failure writing to file!");
			System.exit(0);
		}
	}
	
	private void compileClassVarDec () throws IOException {
		
		Kind classKind;
		String type = null, name;

		if (tokenizer.tokenValue().equals("static")) {
			classKind = Kind.STATIC;
		} else {
			classKind = Kind.FIELD;
		}
		
		// static or field
		tokenizer.advance();
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			type = tokenizer.tokenValue();
			// className
			tokenizer.advance();
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			type = tokenizer.tokenValue();
	        // int char or boolean
			tokenizer.advance();
		}
		
		// varName
		name = tokenizer.tokenValue();
		tokenizer.advance();
		
		table.define(classKind, type, name);
		
		while (tokenizer.tokenValue().contentEquals(",")) {
			 // ,
			tokenizer.advance();
			
			 // varName
			table.define(classKind, type, tokenizer.tokenValue());
			tokenizer.advance();
		}
		
		 // ;
		tokenizer.advance();
		
		//w.write("</classVarDec>\n");
		
		return;
	}
	
	private void compileSubroutine () throws IOException {
		//w.write("<subroutineDec>\n");
		
		String subroutineType, subroutineName;
		
		table.startSubroutine();
		
		subroutineType = tokenizer.tokenValue();
		if (subroutineType.equals("method")) {
			table.define(Kind.ARG, this.className, "this");
		}
		
		 // function constructor or method
		tokenizer.advance();
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			 // type
			tokenizer.advance();
		} else {
			 // void
			tokenizer.advance();
		}
		
		 // subroutineName
		subroutineName = tokenizer.tokenValue();
		tokenizer.advance();
		
		 // (
		tokenizer.advance();
		
		compileParameterList();
		
		 // )
		tokenizer.advance();

		//w.write("<subroutineBody>\n");
		
		 // {
		tokenizer.advance();
		
		// Does the subroutine start with any var decs
		while (tokenizer.tokenValue().equals("var")) {
			compileVarDec();
		}
		
		writer.writeFunction(this.className + "." + subroutineName, table.varCount(Kind.VAR));
		
		// Now we must deal with boilerplate for constructors and methods starts
		if (subroutineType.equals("method")) {
			writer.writePush(SegType.ARG, 0);
			writer.writePop(SegType.POINTER, 0);
		} else if (subroutineType.equals("constructor")) {
			writer.writePush(SegType.CONST, table.varCount(Kind.FIELD)); // Number fields for allocation
			writer.writeCall("Memory.alloc", 1);
			writer.writePop(SegType.POINTER, 0);
		}
		
		compileStatements();
		
		 // }
		tokenizer.advance();
		
		//w.write("</subroutineBody>\n");
		//w.write("</subroutineDec>\n");
		return;
	}
	
	
	private void compileParameterList () throws IOException {
		//w.write("<parameterList>\n");
		
		String argType = null;
		String argName = null;
		
		// No parameters
		if (tokenizer.tokenValue().equals(")")) {
			//w.write("</parameterList>\n");
			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			 // className
			argType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			 // int char or boolean
			argType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		 // varName
		argName = tokenizer.tokenValue();
		tokenizer.advance();
				
		table.define(Kind.ARG, argType, argName);
		
		// Repeats until we don't have any more parameters
		while (tokenizer.tokenValue().equals(",")) {
			 // ,
			tokenizer.advance();
			
			if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
				 // className
				argType = tokenizer.tokenValue();
				tokenizer.advance();
			} else {
				 // int char or boolean
				argType = tokenizer.tokenValue();
				tokenizer.advance();
			}
			
			 // varName
			argName = tokenizer.tokenValue();
			tokenizer.advance();
			
			table.define(Kind.ARG, argType, argName);
		}
		
		//w.write("</parameterList>\n");
		return;
	}
	
	private void compileVarDec () throws IOException {
		//w.write("<varDec>\n");

		String varType = null;
		String varName = null;
		
		 // var
		tokenizer.advance();
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			 // className
			varType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			 // int char or boolean
			varType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		 // varName
		varName = tokenizer.tokenValue();
		tokenizer.advance();
		
		table.define(Kind.VAR, varType, varName);
		
		while (tokenizer.tokenValue().equals(",")) {
			 // ,
			tokenizer.advance();
			
			 // varName
			varType = tokenizer.tokenValue();
			table.define(Kind.VAR, varType, varName);
			
			tokenizer.advance();
		}
		
		 // ;
		tokenizer.advance();
		
		//w.write("</varDec>\n");
		return;
	}
	
	
	private void compileStatements() throws IOException {
		
		//w.write("<statements>\n");
		
		while (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			
			if (tokenizer.tokenValue().equals("let")) {
				compileLet();
			} else if (tokenizer.tokenValue().equals("do")) {
				compileDo();
			} else if (tokenizer.tokenValue().equals("return")) {
				compileReturn();
			} else if (tokenizer.tokenValue().equals("if")) {
				compileIf();
			} else if (tokenizer.tokenValue().equals("while")) {
				compileWhile();
			}
		}
		
		//w.write("</statements>\n");
		return;
	}
	
	private void compileLet() throws IOException {
		
		//w.write("<letStatement>\n");
		
		 // let
		tokenizer.advance();
		
		 // varName
		tokenizer.advance();
		
		// Is this an array value or not
		if (tokenizer.tokenValue().equals("[")) {
			// Array value
			 // [
			tokenizer.advance();
			
			compileExpression();
			
			 // ]
			tokenizer.advance();
		}
		
		 // =
		tokenizer.advance();
		
		compileExpression();
		
		 // ;
		tokenizer.advance();
		
		//w.write("</letStatement>\n");
		
		return;
	}
	
	private void compileDo () throws IOException {
		//w.write("<doStatement>\n");
		
		String subClassName, subName, type = null; 
		boolean thisCall = false; // Will become true if this is a call to subroutine in this class
		args = 0;
		
		 // do
		tokenizer.advance();
		
		 // subroutineName / className / varName
		subClassName = tokenizer.tokenValue(); // 
		tokenizer.advance();
		
		if (tokenizer.tokenValue().equals("(")) {
			
			thisCall = true;
			writer.writePush(SegType.POINTER, 0); // Pushes implicit this onto the stack
			args++;
			subName = subClassName; // subClassName was actually subName for thisCall
			
			 // (
			tokenizer.advance();
			
		} else {
			
			 // .
			tokenizer.advance();
			
			 // subroutineName
			subName = tokenizer.tokenValue();
			tokenizer.advance();
			
			 // (
			tokenizer.advance();
			
			if (table.kindOf(subClassName) != Kind.NONE) {
				// Its a varName and so is a method call
				type = table.typeOf(subClassName);
				SegType segment = getSegType(table.kindOf(type));
				writer.writePush(segment, table.indexOf(subClassName)); // Push the calling object onto the stack
				args++;
			}
		}
		
		compileExpressionList();
		
		// )
		tokenizer.advance();
		
		 // ;
		tokenizer.advance();
		
		if (thisCall) {
			// We are calling a subroutine in this class
			writer.writeCall(className + "." + subName, args);
		} else {
			if (type != null) {
				writer.writeCall(type + "." + subName, args);
			} else {
				writer.writeCall(subClassName + "." + subName, args);
			}
		}
		
		args = 0;

		writer.writePop(SegType.TEMP, 0); // This is a do statement so throw out dummy return value
		
		//w.write("</doStatement>\n");
		return;
	}
	
	
	private void compileReturn() throws IOException {
		//w.write("<returnStatement>\n");
		
		 // return
		tokenizer.advance();
		
		if (!(tokenizer.tokenValue().equals(";"))) {
			// expressions
			compileExpression();
		}  else {
			writer.writePush(SegType.CONST, 0); // dummy void return
		}
		
		 // ;
		tokenizer.advance();
		
		writer.writeReturn();
		//w.write("</returnStatement>\n");
	}
	
	private void compileIf () throws IOException {
		//w.write("<ifStatement>\n");
		
		 // if
		tokenizer.advance();
		
		 // (
		tokenizer.advance();
		
		compileExpression();
		
		 // )
		tokenizer.advance();
		
		 // {
		tokenizer.advance();
		
		compileStatements();
		
		 // }
		tokenizer.advance();
		
		if (tokenizer.tokenValue().equals("else")) {
			// else statement
			
			 // else
			tokenizer.advance();
			
			 // {
			tokenizer.advance();
			
			compileStatements();
			
			 // }
			tokenizer.advance();
		}
		
		//w.write("</ifStatement>\n");
		return;		
	}
	
	private void compileWhile () throws IOException {
		//w.write("<whileStatement>\n");
		
		 // while
		tokenizer.advance();
		
		 // (
		tokenizer.advance();
		
		compileExpression();
		
		 // )
		tokenizer.advance();
		
		 // {
		tokenizer.advance();
		
		compileStatements();
		
		 // }
		tokenizer.advance();
		
		//w.write("</whileStatement>\n");
	}
	
	private void compileExpression() throws IOException { 
		//w.write("<expression>\n");
		
		// First we handle term
		compileTerm();
		
		// Do we have another (op term)* or are we done with the expression
		while (op.indexOf(tokenizer.tokenValue()) > -1) {
			// t.tokenValue is in the string of operators op
			
			String operation = tokenizer.tokenValue();			
						
			 // op
			tokenizer.advance();
			
			compileTerm();

			writeArithmetic(operation);
			
			//tokenizer.advance();
			// We are on another Op or an ending symbol
		}
		
		// We are done with this expression and are now on an ending symbol
		
		//w.write("</expression>\n");
		return;
	}
	
	private void compileTerm() throws IOException {
		
		//w.write("<term>\n");
		
		if (tokenizer.tokenType() == JackTokenizer.INT_CONST) {
			int constant = Integer.parseInt(tokenizer.tokenValue());
			
			if (constant >= 0) {
				writer.writePush(SegType.CONST, constant);
			} else {
				constant = -constant;
				writer.writePush(SegType.CONST, constant);
				writer.writeArithmetic(Arithmetic.NEG);
			}
			// integerConstant
			tokenizer.advance();

			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.STRING_CONST) {
			// stringConstant
			tokenizer.advance();
			//w.write("</term>\n");
			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			// keywordConstant
			tokenizer.advance();
			//w.write("</term>\n");
			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			// varName or varName[expression] or subroutineCall
			 // varName, varName[], subroutineName / className / varName
			tokenizer.advance();
			
			if (tokenizer.tokenValue().equals("(") || tokenizer.tokenValue().equals(".")) {
				// subroutineCall
				
				if (tokenizer.tokenValue().equals("(")) {
					// subroutineName ( expressionList )
					 // (
					tokenizer.advance();
					
					compileExpressionList();
					
					 // )
					tokenizer.advance();
					
					//w.write("</term>\n");
					return;
				} else {
					// (className | varName) . subRoutineName ( expressionList )
					 // .
					tokenizer.advance();
					
					 // subroutineName
					tokenizer.advance();
					
					 // (
					tokenizer.advance();
					
					compileExpressionList();
					
					 // )
					tokenizer.advance();
					
					//w.write("</term>\n");
					return;
				}
				
			} else if (tokenizer.tokenValue().equals("[")) {
				// varName[expression]
				
				 // [
				tokenizer.advance();
				
				compileExpression();
				
				 // ]
				tokenizer.advance();
				
				//w.write("</term>\n");
				return;
			} else {
				// varName (current token is a symbol)
				
				//w.write("</term>\n");
				return;
			}
		}
		
		
		if (tokenizer.tokenValue().equals("(") || tokenizer.tokenValue().equals("~") ||
				tokenizer.tokenValue().equals("-")) {  
			// (expression) or unaryOp + Term
			if (tokenizer.tokenValue().equals("(")) {
				// (expression)
				
				 // (
				tokenizer.advance();
				
				compileExpression();
				
				 // )
				tokenizer.advance();
				
				//w.write("</term>\n");
				return;
			} else {
				// unaryOp Term
				
				 // unaryOp
				tokenizer.advance();
				
				compileTerm();
				
				//w.write("</term>\n");
				return;
			}
		}
		
		//w.write("</term>\n"); // Reachable ?
	}
	
	private void compileExpressionList() throws IOException {
		//w.write("<expressionList>\n");
		
		if (tokenizer.tokenValue().equals(")")) {
			// We have nothing in this expressionList
			//w.write("</expressionList>\n");
			return;
		}
		
		compileExpression();
		args++;
		
		// If the next token is a , then we have another (, expression)
		// Otherwise we are done with this expressionList
		
		while (tokenizer.tokenValue().equals(",")) {
			 // ,
			tokenizer.advance();

			compileExpression();
		}
		
		//w.write("</expressionList>\n");
		return;
	}
	
	private Kind getKind (String pKind) {
		if (pKind.equals("static")) {
			return Kind.STATIC;
		} else if (pKind.equals("field")) {
			return Kind.FIELD;
		} else if (pKind.equals("var")) {
			return Kind.VAR;
		} else {
			return Kind.ARG;
		}
	}
	
	private SegType getSegType (Kind pKind) {
		if (pKind == Kind.ARG) {
			return SegType.ARG;
		} else if (pKind == Kind.VAR) {
			return SegType.LOCAL;
		} else if (pKind == Kind.STATIC) {
			return SegType.STATIC;
		} else {
			return SegType.THIS;
		}
	}
	
	private void writeArithmetic (String pOp) throws IOException {

		if (pOp.equals("+")) {
			writer.writeArithmetic(Arithmetic.ADD);
		} else if (pOp.equals("-")) {
			writer.writeArithmetic(Arithmetic.SUB);
		} else if (pOp.equals("*")) {
			writer.writeCall("Math.multiply", 2);
		} else if (pOp.equals("/")) {
			writer.writeCall("Math.divide", 2);
		} else if (pOp.equals("&")) {
			writer.writeArithmetic(Arithmetic.AND);
		} else if (pOp.equals("|")) {
			writer.writeArithmetic(Arithmetic.OR);
		} else if (pOp.equals("<")) {
			writer.writeArithmetic(Arithmetic.LT);
		} else if (pOp.equals(">")) {
			writer.writeArithmetic(Arithmetic.GT);
		} else if (pOp.equals("=")) {
			writer.writeArithmetic(Arithmetic.EQ);
		}

	}
}
