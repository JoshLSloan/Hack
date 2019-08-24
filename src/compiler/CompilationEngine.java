package compiler;

import java.io.BufferedWriter;
import java.io.IOException;

public class CompilationEngine {
	
	private JackTokenizer tokenizer;
	private BufferedWriter w = null;
	
	private VMWriter writer;
	private SymbolTable table;
	private String className;
	
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
			
			writeKeyword(tokenizer.tokenValue()); // class
			tokenizer.advance();
			
			writeIdentifier(tokenizer.tokenValue()); // className
			tokenizer.advance();
			
			writeSymbol(tokenizer.tokenValue()); // {
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
			
			writeSymbol(tokenizer.tokenValue()); // {
			tokenizer.advance();
			
			//w.write("</class>\n");
			
			return;
			
		} catch (IOException e) {
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
			writeSymbol(tokenizer.tokenValue()); // ,
			tokenizer.advance();
			
			writeIdentifier(tokenizer.tokenValue()); // varName
			table.define(classKind, type, tokenizer.tokenValue());
			tokenizer.advance();
		}
		
		writeSymbol(tokenizer.tokenValue()); // ;
		tokenizer.advance();
		
		//w.write("</classVarDec>\n");
		
		return;
	}
	
	private void compileSubroutine () throws IOException {
		//w.write("<subroutineDec>\n");
		
		String subroutineType;
		
		table.startSubroutine();
		
		subroutineType = tokenizer.tokenValue();
		if (subroutineType.equals("method")) {
			table.define(Kind.ARG, this.className, "this");
		}
		
		writeKeyword(tokenizer.tokenValue()); // function constructor or method
		tokenizer.advance();
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			writeIdentifier(tokenizer.tokenValue()); // type
			tokenizer.advance();
		} else {
			writeKeyword(tokenizer.tokenValue()); // void
			tokenizer.advance();
		}
		
		writeIdentifier(tokenizer.tokenValue()); // subroutineName
		tokenizer.advance();
		
		writeSymbol(tokenizer.tokenValue()); // (
		tokenizer.advance();
		
		compileParameterList();
		
		writeSymbol(tokenizer.tokenValue()); // )
		tokenizer.advance();

		//w.write("<subroutineBody>\n");
		
		writeSymbol(tokenizer.tokenValue()); // {
		tokenizer.advance();
		
		// Does the subroutine start with any var decs
		while (tokenizer.tokenValue().equals("var")) {
			compileVarDec();
		}
		
		compileStatements();
		
		writeSymbol(tokenizer.tokenValue()); // }
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
			writeIdentifier(tokenizer.tokenValue()); // className
			argType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			writeKeyword(tokenizer.tokenValue()); // int char or boolean
			argType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		writeIdentifier(tokenizer.tokenValue()); // varName
		argName = tokenizer.tokenValue();
		tokenizer.advance();
				
		table.define(Kind.ARG, argType, argName);
		
		// Repeats until we don't have any more parameters
		while (tokenizer.tokenValue().equals(",")) {
			writeSymbol(tokenizer.tokenValue()); // ,
			tokenizer.advance();
			
			if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
				writeIdentifier(tokenizer.tokenValue()); // className
				argType = tokenizer.tokenValue();
				tokenizer.advance();
			} else {
				writeKeyword(tokenizer.tokenValue()); // int char or boolean
				argType = tokenizer.tokenValue();
				tokenizer.advance();
			}
			
			writeIdentifier(tokenizer.tokenValue()); // varName
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
		
		writeKeyword(tokenizer.tokenValue()); // var
		tokenizer.advance();
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			writeIdentifier(tokenizer.tokenValue()); // className
			varType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			writeKeyword(tokenizer.tokenValue()); // int char or boolean
			varType = tokenizer.tokenValue();
			tokenizer.advance();
		}
		
		writeIdentifier(tokenizer.tokenValue()); // varName
		varName = tokenizer.tokenValue();
		tokenizer.advance();
		
		table.define(Kind.VAR, varType, varName);
		
		while (tokenizer.tokenValue().equals(",")) {
			writeSymbol(tokenizer.tokenValue()); // ,
			tokenizer.advance();
			
			writeIdentifier(tokenizer.tokenValue()); // varName
			varType = tokenizer.tokenValue();
			table.define(Kind.VAR, varType, varName);
			
			tokenizer.advance();
		}
		
		writeSymbol(tokenizer.tokenValue()); // ;
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
		
		writeKeyword(tokenizer.tokenValue()); // let
		tokenizer.advance();
		
		writeIdentifier(tokenizer.tokenValue()); // varName
		tokenizer.advance();
		
		// Is this an array value or not
		if (tokenizer.tokenValue().equals("[")) {
			// Array value
			writeSymbol(tokenizer.tokenValue()); // [
			tokenizer.advance();
			
			compileExpression();
			
			writeSymbol(tokenizer.tokenValue()); // ]
			tokenizer.advance();
		}
		
		writeSymbol(tokenizer.tokenValue()); // =
		tokenizer.advance();
		
		compileExpression();
		
		writeSymbol(tokenizer.tokenValue()); // ;
		tokenizer.advance();
		
		//w.write("</letStatement>\n");
		
		return;
	}
	
	private void compileDo () throws IOException {
		//w.write("<doStatement>\n");
		
		writeKeyword(tokenizer.tokenValue()); // do
		tokenizer.advance();
		
		writeIdentifier(tokenizer.tokenValue()); // subroutineName / className / varName
		tokenizer.advance();
		
		if (tokenizer.tokenValue().equals("(")) {
			writeSymbol(tokenizer.tokenValue()); // (
			tokenizer.advance();
			
			compileExpressionList();
			
			writeSymbol(tokenizer.tokenValue()); // )
			tokenizer.advance();
			
			writeSymbol(tokenizer.tokenValue()); // ;
			tokenizer.advance();
			
		} else {
			
			writeSymbol(tokenizer.tokenValue()); // .
			tokenizer.advance();
			
			writeIdentifier(tokenizer.tokenValue()); // subroutineName
			tokenizer.advance();
			
			writeSymbol(tokenizer.tokenValue()); // (
			tokenizer.advance();
			
			compileExpressionList();
			
			writeSymbol(tokenizer.tokenValue()); // )
			tokenizer.advance();
			
			writeSymbol(tokenizer.tokenValue()); // ;
			tokenizer.advance();
		}
		
		//w.write("</doStatement>\n");
		return;
	}
	
	
	private void compileReturn() throws IOException {
		//w.write("<returnStatement>\n");
		
		writeKeyword(tokenizer.tokenValue()); // return
		tokenizer.advance();
		
		if (!(tokenizer.tokenValue().equals(";"))) {
			// expressions
			compileExpression();
		} 
		
		writeSymbol(tokenizer.tokenValue()); // ;
		tokenizer.advance();
		
		//w.write("</returnStatement>\n");
	}
	
	private void compileIf () throws IOException {
		//w.write("<ifStatement>\n");
		
		writeKeyword(tokenizer.tokenValue()); // if
		tokenizer.advance();
		
		writeSymbol(tokenizer.tokenValue()); // (
		tokenizer.advance();
		
		compileExpression();
		
		writeSymbol(tokenizer.tokenValue()); // )
		tokenizer.advance();
		
		writeSymbol(tokenizer.tokenValue()); // {
		tokenizer.advance();
		
		compileStatements();
		
		writeSymbol(tokenizer.tokenValue()); // }
		tokenizer.advance();
		
		if (tokenizer.tokenValue().equals("else")) {
			// else statement
			
			writeKeyword(tokenizer.tokenValue()); // else
			tokenizer.advance();
			
			writeSymbol(tokenizer.tokenValue()); // {
			tokenizer.advance();
			
			compileStatements();
			
			writeSymbol(tokenizer.tokenValue()); // }
			tokenizer.advance();
		}
		
		//w.write("</ifStatement>\n");
		return;		
	}
	
	private void compileWhile () throws IOException {
		//w.write("<whileStatement>\n");
		
		writeKeyword(tokenizer.tokenValue()); // while
		tokenizer.advance();
		
		writeSymbol(tokenizer.tokenValue()); // (
		tokenizer.advance();
		
		compileExpression();
		
		writeSymbol(tokenizer.tokenValue()); // )
		tokenizer.advance();
		
		writeSymbol(tokenizer.tokenValue()); // {
		tokenizer.advance();
		
		compileStatements();
		
		writeSymbol(tokenizer.tokenValue()); // }
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
			writeSymbol(tokenizer.tokenValue()); // op
			tokenizer.advance();
			
			compileTerm();
		}
		
		// We are done with this expression and are now on an ending symbol
		
		//w.write("</expression>\n");
		return;
	}
	
	private void compileTerm() throws IOException {
		
		//w.write("<term>\n");
		
		if (tokenizer.tokenType() == JackTokenizer.INT_CONST) {
			// integerConstant
			writeIntegerConstant(tokenizer.tokenValue());
			tokenizer.advance();
			//w.write("</term>\n");
			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.STRING_CONST) {
			// stringConstant
			writeStringConstant(tokenizer.tokenValue());
			tokenizer.advance();
			//w.write("</term>\n");
			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.KEYWORD) {
			// keywordConstant
			writeKeyword(tokenizer.tokenValue());
			tokenizer.advance();
			//w.write("</term>\n");
			return;
		}
		
		if (tokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
			// varName or varName[expression] or subroutineCall
			writeIdentifier(tokenizer.tokenValue()); // varName, varName[], subroutineName / className / varName
			tokenizer.advance();
			
			if (tokenizer.tokenValue().equals("(") || tokenizer.tokenValue().equals(".")) {
				// subroutineCall
				
				if (tokenizer.tokenValue().equals("(")) {
					// subroutineName ( expressionList )
					writeSymbol(tokenizer.tokenValue()); // (
					tokenizer.advance();
					
					compileExpressionList();
					
					writeSymbol(tokenizer.tokenValue()); // )
					tokenizer.advance();
					
					//w.write("</term>\n");
					return;
				} else {
					// (className | varName) . subRoutineName ( expressionList )
					writeSymbol(tokenizer.tokenValue()); // .
					tokenizer.advance();
					
					writeIdentifier(tokenizer.tokenValue()); // subroutineName
					tokenizer.advance();
					
					writeSymbol(tokenizer.tokenValue()); // (
					tokenizer.advance();
					
					compileExpressionList();
					
					writeSymbol(tokenizer.tokenValue()); // )
					tokenizer.advance();
					
					//w.write("</term>\n");
					return;
				}
				
			} else if (tokenizer.tokenValue().equals("[")) {
				// varName[expression]
				
				writeSymbol(tokenizer.tokenValue()); // [
				tokenizer.advance();
				
				compileExpression();
				
				writeSymbol(tokenizer.tokenValue()); // ]
				tokenizer.advance();
				
				//w.write("</term>\n");
				return;
			} else {
				// varName (current token is a symbol)
				
				//w.write("</term>\n");
				return;
			}
		}
		
		if (tokenizer.tokenType() == JackTokenizer.SYMBOL) {
			// (expression) or unaryOp + Term
			if (tokenizer.tokenValue().equals("(")) {
				// (expression)
				
				writeSymbol(tokenizer.tokenValue()); // (
				tokenizer.advance();
				
				compileExpression();
				
				writeSymbol(tokenizer.tokenValue()); // )
				tokenizer.advance();
				
				//w.write("</term>\n");
				return;
			} else {
				// unaryOp Term
				
				writeSymbol(tokenizer.tokenValue()); // unaryOp
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
		
		// If the next token is a , then we have another (, expression)
		// Otherwise we are done with this expressionList
		
		while (tokenizer.tokenValue().equals(",")) {
			writeSymbol(tokenizer.tokenValue()); // ,
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
	
	private void writeIdentifier (String token) throws IOException {
		//w.write("<identifier> " + token + " </identifier>\n");
	}
	
	private void writeKeyword (String token) throws IOException {
		//w.write("<keyword> " + token + " </keyword>\n");
	}
	
	private void writeSymbol (String token) throws IOException {
		
		if (token.equals("<")) {
			token = "&lt;";
		} else if (token.equals(">")) {
			token = "&gt;";
		} else if (token.equals("\"")) {
			token = "&quot;";
		} else if (token.equals("&")) {
			token = "&amp;";
		}
		
		//w.write("<symbol> " + token + " </symbol>\n");
	}
	
	private void writeIntegerConstant (String token) throws IOException {
		//w.write("<integerConstant> " + token + " </integerConstant>\n");
	}
	
	private void writeStringConstant (String token) throws IOException {
		//w.write("<stringConstant> " + token + " </stringConstant>\n");
	}
}
