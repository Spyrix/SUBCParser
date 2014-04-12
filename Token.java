public class Token{
	private String type;//like if the token is an identifier,keyword,etc

	private String text;//actual text of the token
	public Token(String type, String text){
		this.type=type;
		this.text=text;
	}
	
	public String getType(){
		return type;
	}

	public String getText(){
		return text;
	}
}