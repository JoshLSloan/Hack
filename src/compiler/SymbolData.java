package compiler;

public class SymbolData {

	private final Kind kind;
	private final String type;
	private final int index;
	
	public SymbolData(Kind pKind, String pType, int pIndex) {
		this.kind = pKind;
		this.type = pType;
		this.index = pIndex;
	}
	
	public Kind getKind() {
		return kind;
	}

	public String getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}
}
