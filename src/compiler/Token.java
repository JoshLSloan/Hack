package compiler;

public class Token {

	private final int type;
	private final String token;
	
	public Token(int pType, String pToken) {
		this.type = pType;
		this.token = pToken;
	}
	
	public int getType () {return type;}
	public String getToken () {return token;}
}
