import java.util.*;
import java.io.*;

public class Scan {
	private File source;
	private Scanner scanner;
	private Token currentToken;
	private Token nextToken;
	private boolean firstIteration;
	private String savedSpot;
	public Scan(File source){
		savedSpot = "";
		if(source.exists())
			this.source = source;
		try {
			scanner = new Scanner(source);
			scanner.useDelimiter("");
		}
		catch (FileNotFoundException e){
			System.out.println("File not found! Program ending!");
			System.exit(0);
		}
	}

	public Scan(){
		//this should never be called
	}
	//scan just one token at a time
	//SCAN JUST ONE TOKEN AT A TIME?
	public Token scanNextToken(){
		//This method scans the file and partions everything into tokens. First it will check the next token for keywords
		//essentially this just stops the end of file

		//BIG PROBLEM. SO ON THE FIRST ITERATION, WE NEED TO GET THE NEXT TOKEN AND CURRENT TOKEN.
		//AUGHGHUGHUGHUGUHGH

		if(scanner.hasNext()==false){
			Token t = new Token("eof", "eof");
			nextToken = t;
			return t;
		}

		String tokenText = "";
		String x = "";

		if(savedSpot.equals("")==false){
			x=savedSpot;
			savedSpot="";
		}
		else
			x = scanner.next();

		tokenText = x;
		//grabs a comment token
		if(x.equals("{")){
				String y = "";
			if(scanner.hasNext())
				y=scanner.next();
			else
				System.out.print("The file shouldn't end here, problem!!");

			if(y.equals("}"))
				tokenText+=y;

			while(y.equals("}")==false&&scanner.hasNext()){
				//System.out.println("loop1");
				tokenText+=y;
				if(scanner.hasNext())
					y = scanner.next();
				if(y.equals("}")==false&&scanner.hasNext()==false)
					System.out.println("Something is wrong... there should be another }");
			}
			tokenText+=y;
		}
		//grabs either "<" or "<=" or "<>"
		else if(x.equals("<")){
			String y = "";
			//System.out.println("Y is:" + y);
			if(scanner.hasNext())
				y=scanner.next();
			else
				System.out.print("The file shouldn't end here, problem!!");

			if(y.equals("=")){
				tokenText+=y;
			}
			else if(y.equals(">")){
				tokenText+=y;
			}
			else
				savedSpot=y;
		}
		//grabs either ">" or ">="
		else if(x.equals(">")){
			String y = "";
			//System.out.println("Y is:" + y);
			if(scanner.hasNext())
				y=scanner.next();
			else
				System.out.print("The file shouldn't end here, problem!!");

			if(y.equals("=")){
				tokenText+=y;
			}
			else
				savedSpot=y;
		}
		//grabs either ":" or ":=" or ":=:"
		else if(x.equals(":")){
			String y = "";
			//System.out.println("Y is:" + y);
			if(scanner.hasNext())
				y=scanner.next();
			else
				System.out.print("The file shouldn't end here, problem!!");

			if(y.equals("=")){
				tokenText+=y;
				String z = "";
				if(scanner.hasNext())
					z=scanner.next();
				else
					System.out.print("The file shouldn't end here, problem!!");
				if(z==":")
					tokenText+=z;
				else
					savedSpot=z;
			}
			else
				savedSpot=y;
		}
		else if(x.equals(".")){
			String y = "";
			if(scanner.hasNext())
				y=scanner.next();
			else
				System.out.print("The file shouldn't end here, problem!!");

			if(y.equals("."))
				tokenText+=y;
			else {
				if(scanner.hasNext()){
					System.out.println("ERROR HERE");
				}
				savedSpot=y;
			}
		}
		//forms a character
		else if(x.equals("'")){
			tokenText+=scanner.next();
			String y = scanner.next();
			if(y.equals("'")==false){
				System.out.println("There is a problem, this isn't a char! ");
			}
			else
				tokenText+=y;
		}
		//forms a string
		else if(x.equals("\"")){

			String y = "";
			if(scanner.hasNext())
				y=scanner.next();
			else
				System.out.print("The file shouldn't end here, problem!!");

			if(y.equals("\""))
				tokenText+=y;

			while(y.equals("\"")==false&&scanner.hasNext()){
			//	System.out.println("loop1");
				tokenText+=y;
				if(scanner.hasNext())
					y = scanner.next();
				if(y.equals("\"")==false&&scanner.hasNext()==false)
					System.out.println("Something is wrong... there should be another \"");
			}
			tokenText+=y;
		}
		//this grabs words! Could be a keyword or identifier
		else if(Character.isLetter(x.charAt(0))||x.equals("_")){
			while(scanner.hasNext()){
			//	System.out.println("loop2");
				String y = scanner.next();
				if(Character.isLetter(y.charAt(0))||Character.isDigit(y.charAt(0))||y.equals("_")){
					tokenText+=y;
				}
				else{
					//the identifier has ended
					savedSpot=y;
					break;
				}
			}
		}
		//forms an integer
		else if(Character.isDigit(x.charAt(0))){
			while(scanner.hasNext()){
			//	System.out.println("loop3");
				String y = scanner.next();
				if(Character.isDigit(y.charAt(0))){
					//the integer has ended
					tokenText+=y;
				}
				else{
					savedSpot=y;
					break;
				}
			}
		}
		//else, it is a single character token
		Token t = new Token(identify(tokenText), tokenText);

		//This block is magic. Somehow I tapped into the aether and through sheer force of will made it work.
		if(currentToken == null)
			currentToken = t;
		if(nextToken == null)
			nextToken = t;
		else if(currentToken != null && nextToken != null)
			currentToken = nextToken;
			nextToken = t;

		//System.out.println(tokenText);
		return t;
	}

	public String identify(String tokenText){
		switch (tokenText) {
			case "\\n":
				return "newLine";
				
			case "program":
				return "program";
				
			case "var":
				return "var";
				
			case "consts":
				return "consts";

			case "type":
				return "type";
				
			case "function":
				return "function";
				
			case "return":
				return "return";
				
			case "begin":
				return "begin";
				
			case "end":
				return "end";
				
			case ":=:":
				return ":=:";
				
			case ":=":
				return ":=";
				
			case "output":
				return "output";
				
			case "if":
				return "if";
				
			case "then":
				return "then";
				
			case "else":
				return "else";
				
			case "while":
				return "while";
				
			case "do":
				return "do";
				
			case "case":
				return "case";
				
			case "of":
				return "of";
				
			case "..":
				return "..";
				
			case "otherwise":
				return "otherwise";
				
			case "repeat":
				return "repeat";
				
			case "for":
				return "for";
				
			case "until":
				return "until";
				
			case "loop":
				return "loop";
				
			case "pool":
				return "pool";
				
			case "exit":
				return "exit";
				
			case "<=":
				return "<=";

			case "<>":
				return "<>";
				
			case "<":
				return "<";
				
			case ">=":
				return ">=";
				
			case ">":
				return ">";
				
			case "=":
				return "=";
				
			case "mod":
				return "mod";
				
			case "and":
				return "and";
				
			case "or":
				return "or";
				
			case "not":
				return "not";
				
			case "read":
				return "read";
				
			case "succ":
				return "succ";
				
			case "pre":
				return "pre";
				
			case "chr":
				return "chr";
				
			case "ord":
				return "ord";
				
			case "eof":
				return "eof";
				
			case "{":
				return "{";
				
			case "}":
				return "}";
				
			case ":":
				return ":";
				
			case ";":
				return ";";
				
			case ".":
				return ".";
				
			case ",":
				return ",";
				
			case "(":
				return "(";
				
			case ")":
				return ")";
				
			case "+":
				return "+";
				
			case "-":
				return "-";
				
			case "*":
				return "*";
				
			case "/":
				return "/";
				
			default:
				break;
				//it wasn't a keyword... must be an identifier, integer, char, or string
		}

		//gets the first char of the token text
		char beginningChar = tokenText.charAt(0);
		//identifies a string
		if(Character.toString(beginningChar).equals("\""))
			return "string";
			//if the string 
		//identifies a char
		else if(Character.toString(beginningChar).equals("'"))
			return "char";
			//if it is longer than three or the character is a single quote, throw an exception!!!!
		//identifies an identifier
		else if (Character.toString(beginningChar).equals("_") || Character.isLetter(beginningChar))
			return "identifier";
		else if (Character.toString(beginningChar).equals("{"))
			return "comment";
		//identifies an integer
		else if (Character.isDigit(beginningChar))
			return "integer";
		else
			return "space";
	}

	public void setNextToken(Token token){
		nextToken = token;
	}

	public Token getNextToken(){
		return nextToken;
	}

	public void setCurrentToken(Token token){
		currentToken = token;
	}

	public Token getCurrentToken(){
		return currentToken;
	}

}