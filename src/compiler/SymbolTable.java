package compiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SymbolTable {
	private HashMap<String, SymbolData> classSymbols;
	private HashMap<String, SymbolData> subroutineSymbols;
	
	private int varCount;
	private int fieldCount;
	private int argCount;
	private int staticCount;
	
	public SymbolTable () {
		this.classSymbols = new HashMap<>();
		this.subroutineSymbols = new HashMap<>();
		
		this.varCount = 0;
		this.fieldCount = 0;
		this.argCount = 0;
		this.staticCount = 0;
	}
	
	public void startSubroutine () {
		this.subroutineSymbols.clear();
		this.varCount = 0;
		this.argCount = 0;
	}
	
	public void define (Kind pKind, String pType, String pName) {
		
		//System.out.println("CLASS TABLE: " + pKind);
		
		switch (pKind) {

			case VAR:
				subroutineSymbols.put(pName, new SymbolData(pKind, pType, varCount));
				varCount++;
				return;
			
			case ARG:
				subroutineSymbols.put(pName, new SymbolData(pKind, pType, argCount));
				argCount++;
				return;
				
			case STATIC:
				classSymbols.put(pName, new SymbolData(pKind, pType, staticCount));
				staticCount++;
				return;
				
			case FIELD:
				classSymbols.put(pName, new SymbolData(pKind, pType, fieldCount));
				fieldCount++;
				return;
			
			default:
				break;
		}
	}
	
	public int varCount (Kind pKind) {
		
		switch (pKind) {
			case VAR:
				return varCount;
			
			case ARG:
				return argCount;
				
			case STATIC:
				return staticCount;
				
			case FIELD:
				return fieldCount;

			default:
				return -1;
		}
	}
	
	public Kind kindOf (String pName) {
		if (subroutineSymbols.containsKey(pName)) {
			return subroutineSymbols.get(pName).getKind();
		} else if (classSymbols.containsKey(pName)) {
			return classSymbols.get(pName).getKind();
		} else {
			return Kind.NONE;
		}
	}
	
	public String typeOf (String pName) {
		if (subroutineSymbols.containsKey(pName)) {
			return subroutineSymbols.get(pName).getType();
		} else if (classSymbols.containsKey(pName)) {
			return classSymbols.get(pName).getType();
		} else {
			return null;
		}
	}
	
	public int indexOf (String pName) {
		if (subroutineSymbols.containsKey(pName)) {
			return subroutineSymbols.get(pName).getIndex();
		} else if (classSymbols.containsKey(pName)) {
			return classSymbols.get(pName).getIndex();
		} else {
			return -1;
		}
	}
	
	public String toString () {

		StringBuilder sb = new StringBuilder();

		sb.append("CLASS TABLE\n");
		sb.append("-------------\n");
		classSymbols.forEach((k,v) -> {
		    sb.append(k + " " + v.toString() + "\n");
		});
		
		sb.append("SUBROUTINE TABLE\n");
		sb.append("-------------------\n");
		subroutineSymbols.forEach((k,v) -> {
		    sb.append(k + " " + v.toString() + "\n");
		});
		
		return sb.toString();
		
	}
}
