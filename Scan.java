import java.util.*;

public class Scan {
	private File source;
	private Scanner scanner;
	private ArrayList tokenList;
	public Scan(File source){
		if(source.exists())
			this.source = source;
	}
	//scan just one token at a time
	//SCAN JUST ONE TOKEN AT A TIME?
	public Token scanFile(){
		//This method scans the file and partions everything into tokens. First it will check the next token for keywords
		
		//case Identifier
	}

}