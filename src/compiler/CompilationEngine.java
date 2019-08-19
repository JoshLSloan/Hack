package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
	
	private JackTokenizer t;
	private BufferedWriter w;
	
	private static final String op = "+-*/&|<>=";
	
	public CompilationEngine (String pFileName, JackTokenizer tokens) {
		this.t = tokens;
		try  {
			w = new BufferedWriter(new FileWriter(new File(pFileName)));
			this.compileClass();
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
	
	private boolean more() {
		if (t.hasNextToken()) {
			t.advance();
			return true;
		}
		
		return false;
	}
	
	private void compileClass () {
		try {
			w.write("<class>\n");
			
			writeKeyword(t.tokenValue()); // class
			if (!more()) {return;}
			
			writeIdentifier(t.tokenValue()); // className
			if (!more()) {return;}
			
			writeSymbol(t.tokenValue()); // {
			if (!more()) {return;}
			
			// ClassVarDec
			while (t.tokenValue().equals("static") || t.tokenValue().equals("field")) {
				compileClassVarDec();
			}
			
			
			// subroutineDec
			while (t.tokenValue().equals("constructor") || t.tokenValue().equals("function") ||
					t.tokenValue().equals("method")) {
				
				compileSubroutine();
			}
			
			writeSymbol(t.tokenValue()); // {
			if (!more()) {return;}
			
			w.write("</class>\n");
			
			return;
			
		} catch (IOException e) {
			System.out.println("Failure writing to file!");
			System.exit(0);
		}
	}
	
	private void compileClassVarDec () throws IOException {
	
		w.write("<classVarDec>\n");

		writeKeyword(t.tokenValue()); // static or field
		if (!more()) {return;}
		
		if (t.tokenType() == JackTokenizer.IDENTIFIER) {
			writeIdentifier(t.tokenValue()); // className
			if (!more()) {return;}
		}
		
		if (t.tokenType() == JackTokenizer.KEYWORD) {
			writeKeyword(t.tokenValue()); // int char or boolean
			if (!more()) {return;}
		}
		
		writeIdentifier(t.tokenValue()); // varName
		if (!more()) {return;}
		
		while (t.tokenValue().contentEquals(",")) {
			writeSymbol(t.tokenValue()); // ,
			if (!more()) {return;}
			
			writeIdentifier(t.tokenValue()); // varName
			if (!more()) {return;}
		}
		
		writeSymbol(t.tokenValue()); // ;
		if (!more()) {return;}
		
		w.write("</classVarDec>\n");
		
		return;
	}
	
	private void compileSubroutine () throws IOException {
		w.write("<subroutineDec>\n");
		
		writeKeyword(t.tokenValue()); // function constructor or method
		if (!more()) {return;}
		
		if (t.tokenType() == JackTokenizer.IDENTIFIER) {
			writeIdentifier(t.tokenValue()); // type
			if (!more()) {return;}
		} else {
			writeKeyword(t.tokenValue()); // void
			if (!more()) {return;}
		}
		
		writeIdentifier(t.tokenValue()); // subroutineName
		if (!more()) {return;}
		
		writeSymbol(t.tokenValue()); // (
		if (!more()) {return;}
		
		compileParameterList();
		
		writeSymbol(t.tokenValue()); // )
		if (!more()) {return;}

		w.write("<subroutineBody>\n");
		
		writeSymbol(t.tokenValue()); // {
		if (!more()) {return;}
		
		// Does the subroutine start with any var decs
		while (t.tokenValue().equals("var")) {
			compileVarDec();
		}
		
		compileStatements();
		
		writeSymbol(t.tokenValue()); // }
		if (!more()) {return;}
		
		w.write("</subroutineBody>\n");
		w.write("</subroutineDec>\n");
		return;
	}
	
	
	private void compileParameterList () throws IOException {
		w.write("<parameterList>\n");
				
		// No parameters
		if (t.tokenValue().equals(")")) {
			w.write("</parameterList>\n");
			return;
		}
		
		if (t.tokenType() == JackTokenizer.IDENTIFIER) {
			writeIdentifier(t.tokenValue()); // className
			if (!more()) {return;}
		}
		
		if (t.tokenType() == JackTokenizer.KEYWORD) {
			writeKeyword(t.tokenValue()); // int char or boolean
			if (!more()) {return;}
		}
		
		writeIdentifier(t.tokenValue()); // varName
		if (!more()) {return;}
				
		// Repeats until we don't have any more parameters
		while (t.tokenValue().equals(",")) {
			writeSymbol(t.tokenValue()); // ,
			if (!more()) {return;}
			
			if (t.tokenType() == JackTokenizer.IDENTIFIER) {
				writeIdentifier(t.tokenValue()); // className
				if (!more()) {return;}
			} else {
				writeKeyword(t.tokenValue()); // int char or boolean
				if (!more()) {return;}
			}
			
			writeIdentifier(t.tokenValue()); // varName
			if (!more()) {return;}
		}
		
		w.write("</parameterList>\n");
		return;
	}
	
	private void compileVarDec () throws IOException {
		w.write("<varDec>\n");

		writeKeyword(t.tokenValue()); // var
		if (!more()) {return;}
		
		if (t.tokenType() == JackTokenizer.IDENTIFIER) {
			writeIdentifier(t.tokenValue()); // className
			if (!more()) {return;}
		}
		
		if (t.tokenType() == JackTokenizer.KEYWORD) {
			writeKeyword(t.tokenValue()); // int char or boolean
			if (!more()) {return;}
		}
		
		writeIdentifier(t.tokenValue()); // varName
		if (!more()) {return;}
		
		while (t.tokenValue().equals(",")) {
			writeSymbol(t.tokenValue()); // ,
			if (!more()) {return;}
			
			writeIdentifier(t.tokenValue()); // varName
			if (!more()) {return;}
		}
		
		writeSymbol(t.tokenValue()); // ;
		if (!more()) {return;}
		
		w.write("</varDec>\n");
		return;
	}
	
	
	private void compileStatements() throws IOException {
		
		w.write("<statements>\n");
		
		while (t.tokenType() == JackTokenizer.KEYWORD) {
			
			if (t.tokenValue().equals("let")) {
				compileLet();
			} else if (t.tokenValue().equals("do")) {
				compileDo();
			} else if (t.tokenValue().equals("return")) {
				compileReturn();
			} else if (t.tokenValue().equals("if")) {
				compileIf();
			} else if (t.tokenValue().equals("while")) {
				compileWhile();
			}
		}
		
		w.write("</statements>\n");
		return;
	}
	
	private void compileLet() throws IOException {
		
		w.write("<letStatement>\n");
		
		writeKeyword(t.tokenValue()); // let
		if (!more()) {return;}
		
		writeIdentifier(t.tokenValue()); // varName
		if (!more()) {return;}
		
		// Is this an array value or not
		if (t.tokenValue().equals("[")) {
			// Array value
			writeSymbol(t.tokenValue()); // [
			if (!more()) {return;}
			
			compileExpression();
			
			writeSymbol(t.tokenValue()); // ]
			if (!more()) {return;}
		}
		
		writeSymbol(t.tokenValue()); // =
		if (!more()) {return;}
		
		compileExpression();
		
		writeSymbol(t.tokenValue()); // ;
		if (!more()) {return;}
		
		w.write("</letStatement>\n");
		
		return;
	}
	
	private void compileDo () throws IOException {
		w.write("<doStatement>\n");
		
		writeKeyword(t.tokenValue()); // do
		if (!more()) {return;}
		
		writeIdentifier(t.tokenValue()); // subroutineName / className / varName
		if (!more()) {return;}
		
		if (t.tokenValue().equals("(")) {
			writeSymbol(t.tokenValue()); // (
			if (!more()) {return;}
			
			compileExpressionList();
			
			writeSymbol(t.tokenValue()); // )
			if (!more()) {return;}
			
			writeSymbol(t.tokenValue()); // ;
			if (!more()) {return;}
			
		} else {
			
			writeSymbol(t.tokenValue()); // .
			if (!more()) {return;}
			
			writeIdentifier(t.tokenValue()); // subroutineName
			if (!more()) {return;}
			
			writeSymbol(t.tokenValue()); // (
			if (!more()) {return;}
			
			compileExpressionList();
			
			writeSymbol(t.tokenValue()); // )
			if (!more()) {return;}
			
			writeSymbol(t.tokenValue()); // ;
			if (!more()) {return;}
		}
		
		w.write("</doStatement>\n");
		return;
	}
	
	
	private void compileReturn() throws IOException {
		w.write("<returnStatement>\n");
		
		writeKeyword(t.tokenValue()); // return
		if (!more()) {return;}
		
		if (!(t.tokenValue().equals(";"))) {
			// expressions
			compileExpression();
		} 
		
		writeSymbol(t.tokenValue()); // ;
		if (!more()) {return;}
		
		w.write("</returnStatement>\n");
	}
	
	private void compileIf () throws IOException {
		w.write("<ifStatement>\n");
		
		writeKeyword(t.tokenValue()); // if
		if (!more()) {return;}
		
		writeSymbol(t.tokenValue()); // (
		if (!more()) {return;}
		
		compileExpression();
		
		writeSymbol(t.tokenValue()); // )
		if (!more()) {return;}
		
		writeSymbol(t.tokenValue()); // {
		if (!more()) {return;}
		
		compileStatements();
		
		writeSymbol(t.tokenValue()); // }
		if (!more()) {return;}
		
		if (t.tokenValue().equals("else")) {
			// else statement
			
			writeKeyword(t.tokenValue()); // else
			if (!more()) {return;}
			
			writeSymbol(t.tokenValue()); // {
			if (!more()) {return;}
			
			compileStatements();
			
			writeSymbol(t.tokenValue()); // }
			if (!more()) {return;}
		}
		
		w.write("</ifStatement>\n");
		return;		
	}
	
	private void compileWhile () throws IOException {
		w.write("<whileStatement>\n");
		
		writeKeyword(t.tokenValue()); // while
		if (!more()) {return;}
		
		writeSymbol(t.tokenValue()); // (
		if (!more()) {return;}
		
		compileExpression();
		
		writeSymbol(t.tokenValue()); // )
		if (!more()) {return;}
		
		writeSymbol(t.tokenValue()); // {
		if (!more()) {return;}
		
		compileStatements();
		
		writeSymbol(t.tokenValue()); // }
		if (!more()) {return;}
		
		w.write("</whileStatement>\n");
	}
	
	private void compileExpression() throws IOException { 
		
		w.write("<expression>\n");
		
		// First we handle term
		compileTerm();
		
		// Do we have another (op term)* or are we done with the expression
		while (op.indexOf(t.tokenValue()) > -1) {
			// t.tokenValue is in the string of operators op
			writeSymbol(t.tokenValue()); // op
			if (!more()) {return;}
			
			compileTerm();
		}
		
		// We are done with this expression and are now on an ending symbol
		
		w.write("</expression>\n");
		return;
	}
	
	private void compileTerm() throws IOException {
		
		w.write("<term>\n");
		
		if (t.tokenType() == JackTokenizer.INT_CONST) {
			// integerConstant
			writeIntegerConstant(t.tokenValue());
			if (!more()) {return;}
			w.write("</term>\n");
			return;
		}
		
		if (t.tokenType() == JackTokenizer.STRING_CONST) {
			// stringConstant
			writeStringConstant(t.tokenValue());
			if (!more()) {return;}
			w.write("</term>\n");
			return;
		}
		
		if (t.tokenType() == JackTokenizer.KEYWORD) {
			// keywordConstant
			writeKeyword(t.tokenValue());
			if (!more()) {return;}
			w.write("</term>\n");
			return;
		}
		
		if (t.tokenType() == JackTokenizer.IDENTIFIER) {
			// varName or varName[expression] or subroutineCall
			writeIdentifier(t.tokenValue()); // varName, varName[], subroutineName / className / varName
			if (!more()) {return;}
			
			if (t.tokenValue().equals("(") || t.tokenValue().equals(".")) {
				// subroutineCall
				
				if (t.tokenValue().equals("(")) {
					// subroutineName ( expressionList )
					writeSymbol(t.tokenValue()); // (
					if (!more()) {return;}
					
					compileExpressionList();
					
					writeSymbol(t.tokenValue()); // )
					if (!more()) {return;}
					
					w.write("</term>\n");
					return;
				} else {
					// (className | varName) . subRoutineName ( expressionList )
					writeSymbol(t.tokenValue()); // .
					if (!more()) {return;}
					
					writeIdentifier(t.tokenValue()); // subroutineName
					if (!more()) {return;}
					
					writeSymbol(t.tokenValue()); // (
					if (!more()) {return;}
					
					compileExpressionList();
					
					writeSymbol(t.tokenValue()); // )
					if (!more()) {return;}
					
					w.write("</term>\n");
					return;
				}
				
			} else if (t.tokenValue().equals("[")) {
				// varName[expression]
				
				writeSymbol(t.tokenValue()); // [
				if (!more()) {return;}
				
				compileExpression();
				
				writeSymbol(t.tokenValue()); // ]
				if (!more()) {return;}
				
				w.write("</term>\n");
				return;
			} else {
				// varName (current token is a symbol)
				
				w.write("</term>\n");
				return;
			}
		}
		
		if (t.tokenType() == JackTokenizer.SYMBOL) {
			// (expression) or unaryOp + Term
			if (t.tokenValue().equals("(")) {
				// (expression)
				
				writeSymbol(t.tokenValue()); // (
				if (!more()) {return;}
				
				compileExpression();
				
				writeSymbol(t.tokenValue()); // )
				if (!more()) {return;}
				
				w.write("</term>\n");
				return;
			} else {
				// unaryOp Term
				
				writeSymbol(t.tokenValue()); // unaryOp
				if (!more()) {return;}
				
				compileTerm();
				
				w.write("</term>\n");
				return;
			}
		}
		
		w.write("</term>\n"); // Reachable ?
	}
	
	private void compileExpressionList() throws IOException {
		w.write("<expressionList>\n");
		
		if (t.tokenValue().equals(")")) {
			// We have nothing in this expressionList
			w.write("</expressionList>\n");
			return;
		}
		
		compileExpression();
		
		// If the next token is a , then we have another (, expression)
		// Otherwise we are done with this expressionList
		
		while (t.tokenValue().equals(",")) {
			writeSymbol(t.tokenValue()); // ,
			if (!more()) {return;}
			
			compileExpression();
		}
		
		w.write("</expressionList>\n");
		return;
	}
	
	private void writeIdentifier (String token) throws IOException {
		w.write("<identifier> " + token + " </identifier>\n");
	}
	
	private void writeKeyword (String token) throws IOException {
		w.write("<keyword> " + token + " </keyword>\n");
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
		
		w.write("<symbol> " + token + " </symbol>\n");
	}
	
	private void writeIntegerConstant (String token) throws IOException {
		w.write("<integerConstant> " + token + " </integerConstant>\n");
	}
	
	private void writeStringConstant (String token) throws IOException {
		w.write("<stringConstant> " + token + " </stringConstant>\n");
	}
}
