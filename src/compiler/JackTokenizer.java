package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class JackTokenizer {
	
	public static int KEYWORD = 1;
	public static int SYMBOL = 2;
	public static int IDENTIFIER = 3;
	public static int INT_CONST = 4;
	public static int STRING_CONST = 5;
	
	private static String symbols = "{}()[].,;+-*/&|<>=~";
	private static String key = "cfmsvibtnldewr";
	
	private List<Token> tokens = new ArrayList<>();
	private Scanner scan;
	
	private HashSet<String> keywords;
	
	private int currentToken;

	public JackTokenizer (String fileName) {
		
		List<String> k = new ArrayList<>();
		
		k.add("class");
		k.add("constructor");
		k.add("function");
		k.add("method");
		k.add("field");
		k.add("static");
		k.add("var");
		k.add("int");
		k.add("char");
		k.add("boolean");
		k.add("void");
		k.add("true");
		k.add("false");
		k.add("null");
		k.add("this");
		k.add("let");
		k.add("do");
		k.add("if");
		k.add("else");
		k.add("while");
		k.add("return");
		
		keywords = new HashSet<>(k);
		
		try {
			String t = fileName;
			File file = new File(t);
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		
		tokenize();
	}

	private void tokenize () {
		
		while (scan.hasNextLine()) {
			String s = scan.nextLine().trim();
			StringBuilder sb = new StringBuilder(s);
			
			// Check if line is blank
			if (sb.length() == 0) {
				continue;
			}
			
			// Check if whole line is a comment
			if (sb.charAt(0) == '/' && sb.charAt(1) == '/') {
				continue;
			}
			
			// Remove line comments
			int t;
			if ((t = sb.indexOf("//")) > -1) {
				sb.delete(t, sb.length());
			}
			
			// Block comments
			if ((t = sb.indexOf("/*")) > -1) {
				// We found the start of a block comment
				sb.delete(t, t+2);
				sb = handleBlockComment(sb);
			}
			
			if (sb.length() == 0) {
				continue;
			}
			
			// START PROCESSING TOKENS
			while (sb.length() > 0) {
				Character c = sb.charAt(0);
				
				if (c == ' ') {
					sb.deleteCharAt(0); 
				} else if (symbols.indexOf(c) != -1) {
					// Our current character is a symbol
					tokens.add(new Token(SYMBOL, String.valueOf(c)));
					sb.deleteCharAt(0);
				} else if (Character.isDigit(c)) {
					// Start of an integer constant
					sb = handleIntegerConstant(sb);
				} else if (c == '"') {
					sb.deleteCharAt(0); // Consume " first
					sb = handleStringConstant(sb);
				} else {
					// We have a keyword or identifier
					sb = handleKeywordIdentifier (sb);
				}
			}
			
		}
		
		/*
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new FileWriter(new File("test.xml")));
			w.write("<tokens>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tokens.size(); i ++) {
			Token t = tokens.get(i);
			//System.out.println(t.getType() + ": " + t.getToken());
			try {
				if (t.getType() == KEYWORD) {
					w.write("<keyword> " + t.getToken() + " </keyword>\n");
				} else if (t.getType() == SYMBOL) {
					w.write("<symbol> ");
					String tok = t.getToken();
					if (tok.equals("<")) {
						w.write("&lt;");
					} else if (tok.equals(">")) {
						w.write("&gt;");
					} else if (tok.equals("\"")) {
						w.write("&quot;");
					} else if (tok.equals("&")) {
						w.write("&amp;");
					} else {
						w.write(tok + " </symbol>\n");
					}
				} else if (t.getType() == IDENTIFIER) {
					w.write("<identifier> " + t.getToken() + " </identifier>\n");
				} else if (t.getType() == INT_CONST) {
					w.write("<integerConstant> " + t.getToken() + " </integerConstant>\n");
				} else {
					w.write("<stringConstant> " + t.getToken() + " </stringConstant>\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			w.write("</tokens>");
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	private StringBuilder handleKeywordIdentifier (StringBuilder sb) {
		StringBuilder kiConstant = new StringBuilder();
		
		if (key.indexOf(sb.charAt(0)) == -1) {
			// Must be an identifier
			while (Character.isAlphabetic(sb.charAt(0)) || 
				   Character.isDigit(sb.charAt(0)) ||
				   sb.charAt(0) == '_') {
				
				kiConstant.append(sb.charAt(0));
				sb.deleteCharAt(0);
			}
			tokens.add(new Token(IDENTIFIER, kiConstant.toString()));
			return sb;
		} else {
			// Don't know what it is yet, lets find out
			while (Character.isAlphabetic(sb.charAt(0)) || 
					   Character.isDigit(sb.charAt(0)) ||
					   sb.charAt(0) == '_') {
				
				kiConstant.append(sb.charAt(0));
				sb.deleteCharAt(0);
			}
			
			if (keywords.contains(kiConstant.toString())) {
				tokens.add(new Token(KEYWORD, kiConstant.toString()));
			} else {
				tokens.add(new Token(IDENTIFIER, kiConstant.toString()));
			}
		}
		
		return sb;
	}
	
	private StringBuilder handleStringConstant(StringBuilder sb) {
		StringBuilder stringConstant = new StringBuilder();
		
		while (sb.charAt(0) != '"') {
			stringConstant.append(sb.charAt(0));
			sb.deleteCharAt(0);
		}
		
		tokens.add(new Token(STRING_CONST, stringConstant.toString()));
		sb.deleteCharAt(0); // consume ending "
		return sb;
	}
	
	private StringBuilder handleIntegerConstant(StringBuilder sb) {
		StringBuilder integerConstant = new StringBuilder();
		
		while (Character.isDigit(sb.charAt(0))) {
			integerConstant.append(sb.charAt(0));
			sb.deleteCharAt(0);
		}
		tokens.add(new Token(INT_CONST, integerConstant.toString()));
		return sb;
	}
	
	private StringBuilder handleBlockComment (StringBuilder sb) {
		int t;
		if ((t = sb.indexOf("*/")) > -1) {
			sb.delete(0, t + 2);
			return sb; // The block comment ends on the same line it began
		}
		
		
		while (scan.hasNextLine()) {
			String s = scan.nextLine().trim();
			sb = new StringBuilder(s);
			
			// Check if line is blank
			if (sb.length() == 0) {
				continue;
			}
			
			if ((t = sb.indexOf("*/")) > -1) {
				// We found the end of the block comment
				sb.delete(0, t+2);
				return sb;
			}
		}
		
		return sb;  // Never found the end of the block comment
	}
	
	public boolean advance () {
		return false;
		
	}

	
}
