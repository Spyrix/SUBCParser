import java.util.*;
public class TreeGenerator{
	private Stack<Tree> stackOfTrees;
	private int index;
	private Token nextToken;
	private ArrayList<Token> tokens;
	public TreeGenerator (ArrayList<Token> t){
		stackOfTrees = new Stack<Tree>();
		tokens = t;
		index = 0;
	}
	public TreeGenerator (){

	}

	public Tree generateTree(){
		tiny();
		return stackOfTrees.pop();
	}
	public void tiny(){
		getNextToken();
		//int n=7;how many children it has 
		if(nextToken.getType().equals("program")){
			//read(program)
			name();
			getNextToken();
			//System.out.println(nextToken.getText()+"index is:"+index);
			if(!nextToken.getType().equals(":")){//the next token should be a colon, for correctness
				error(": in tiny"+"->"+nextToken.getText());
			}
			consts();
			types();
			dclns();
			subProgs();
			body();
			name();
			getNextToken();
			if(!nextToken.getType().equals(".")){//The next token should be a period for correctness
				error(". in tiny"+"->"+nextToken.getText());
			}
			buildTree("program",5);//should be the final tree
		}
		else {
			error("program in tiny"+"->"+nextToken.getText());
			//error
		}
	}
	
	public void name(){
		getNextToken();//gets next token which should be an identifier
		if(nextToken.getType().equals("identifier")){
			buildTree(nextToken.getText(), 0);
			buildTree("<identifier>", 1);

		}
		else {
			System.out.println(peekNextToken().getText());
			error("identifier in name"+"->"+nextToken.getText());
			//error
		}
	}
	//all the const stuff is hard to understand, no examples.
	
	public void consts(){
		int n=0;
		///gets next token which should be const, indicating a list of constants
		//list means expression , expression
		if(peekNextToken().getType().equals("const")) {
			getNextToken();
			while(!peekNextToken().getType().equals(";")){
				constRule();
				getNextToken();
				n++;
				if(nextToken.getType().equals(";")){
					break;
				}
				else if(!nextToken.getType().equals(",")){
					error(", in consts"+"->"+nextToken.getText());
				}
			}
			getNextToken();//gets rid of the next token which should be a ";"

		}
		buildTree("consts",n);
	}
	
	public void constRule(){
		/*This is named constRule instead of const() cause it turns out that const is a reserved 
		keyword in java, What does it do? NOTHING!!! It's a hold over from the C++ design.*/
		name();
		getNextToken();
		if(!nextToken.getType().equals("=")){
			error("= in constRule");
		}
		constValue();
		buildTree("const", 2);
	}
	
	public void constValue(){
		if(peekNextToken().getType().equals("char")){
			getNextToken();
			buildTree(nextToken.getText(), 0);
			buildTree("<char>", 1);
		}
		else if(peekNextToken().getType().equals("integer")){
			getNextToken();
			buildTree(nextToken.getText(), 0);
			buildTree("<integer>", 1);
		}
		else if (peekNextToken().getType().equals("identifier")){
			name();
		}
		else {
			error("char, integer, or identifier in constValue");
			//error
		}
	}
	public void types(){
		int n=0;
		//+means at least once
		//'type' followed by at least one type type() ';'
		if(peekNextToken().getType().equals("type")) {
			getNextToken();
			do {
				type();
				getNextToken();
				if(!nextToken.getType().equals(";")){
					error(";");
				}
				n++;
			} while (peekNextToken().getType().equals("identifier"));
		}
		buildTree("types",n);
	}
	public void type(){
		//name() followed by '=' and litList()
		name();
		getNextToken();
		if(!nextToken.getType().equals("=")){
					error("= in type");
		}
		litList();
		buildTree("type", 2);
	}

	public void litList(){
		int n=0;
		//a list of names, seperated 
		getNextToken();
		if(!nextToken.getType().equals("(")){
					error("( in litList");
		}
		while(!nextToken.getType().equals(")")){
			name();
			getNextToken();
			n++;
			if(nextToken.getType().equals(")")){
				break;
			}
			if(!nextToken.getType().equals(",")&&!nextToken.getType().equals(")")){
					error(", or ) in litList");
			}
			
		}
		/*
		getNextToken();//gets rid of the ")"
		getNextToken();//this should be a ";"
		if(!nextToken.getType().equals(";")){
			error(";");
		}*/
		buildTree("lit", n);
	}
	public void dclns(){
		int n=0;
		//first we have a var,
		//then we have at least one dcln; but possibly more
		//+means one or more
		//System.out.println(peekNextToken().getText());
		if(peekNextToken().getType().equals("var")){
			getNextToken();
			do{
				dcln();
				getNextToken();
				n++;
				if(!nextToken.getType().equals(";")){
					error("; in dclns");//expected a ";"
				}
			}while(!peekNextToken().getType().equals("function")&&!peekNextToken().getType().equals("begin"));
		}
		buildTree("dclns",n);
	}
	public void dcln(){
		int n = 1;
		//a list of name()s each seperated by a comma followed by a ":" and then another name()
		do{
			name();
			getNextToken();
			//System.out.println(nextToken.getText()+"HI");
			if(!nextToken.getType().equals(":")&&!nextToken.getType().equals(",")){
				error(", or : in dcln"+"->"+nextToken.getType());//could also error on a ":"
			}
			n++;
		} while(!nextToken.getType().equals(":"));
		name();
		buildTree("var", n);
	}
	public void subProgs(){
		int n=0;
		//* means zero or more. So we can have zero sub programs
		while(peekNextToken().getType().equals("function")){
			fcn();
			n++;
		}
		buildTree("subprogs", n);
	}
	public void fcn(){
		getNextToken();
		if(!nextToken.getType().equals("function")){
			error("function in fcn"+"->"+nextToken.getText());
		}
		name();
		getNextToken();
		if(!nextToken.getType().equals("(")){
			error("( in fcn");
		}
		params();
		getNextToken();
		if(!nextToken.getType().equals(")")){
			error(") in fcn");
		}
		getNextToken();
		if(!nextToken.getType().equals(":")){
			error(": in fcn");
		}
		name();
		getNextToken();
		if(!nextToken.getType().equals(";")){
			error("; in fcn");
		}
		consts();
		types();
		dclns();
		body();
		name();
		getNextToken();
		if(!nextToken.getType().equals(";")){
			error("; in fcn");
		}
		buildTree("fcn", 8);
	}
	public void params(){
		int n = 0;
		//a list of dcln's each seperated by a semi colon
		while(peekNextToken().getType().equals("identifier")){
				dcln();
				n++;
				if(!peekNextToken().getType().equals(";")&&!peekNextToken().getType().equals(")")){
					error("; or ) in params"+"->"+nextToken.getType());//expected a ";"
				}
				else if(peekNextToken().getType().equals(";")){
					getNextToken();
				}
		}
		buildTree("params", n);
	}
	public void body(){
		//"begin" followed by a list of statements, each seperated by a ;, and finally an "end"
		int n = 0;
		getNextToken();
		if(!nextToken.getType().equals("begin")){
			error("begin in body"+"->"+nextToken.getType());
		}
		while(!peekNextToken().getType().equals("end")){
			statement();
			n++;
			getNextToken();
			if(nextToken.getType().equals("end")){
				break;
			}
			else if(!nextToken.getType().equals(";")){
				error("; in body");
			}
			//System.out.println(nextToken.getText());
		}
		//getNextToken();
		/*
		if(!nextToken.getType().equals("end")){
			error("end in body");
		}*/
		
		buildTree("block", n);
	}
	public void statement(){
		int n = 0;// wait I dont think statement actually builds out a tree... or at least, much of one
		if(peekNextToken().getType().equals("output")){
			int num=0;
			getNextToken();
			getNextToken();
			if(nextToken.getType().equals("(")){
				//a list of outExp() each seperated by a "," ending with an ")"
				while(!peekNextToken().getType().equals(")")){
					//System.out.println(peekNextToken().getText());
					outExp();
					getNextToken();
					if(!nextToken.getType().equals(",")&&!nextToken.getType().equals(")")){
						error(", or ) in statement (output)"+"->"+nextToken.getText());
					}
					num++;
					if(nextToken.getType().equals(")")){
						break;
					}
				}
				buildTree("output",num);
			}
			else{
				error("( in statement (output)"+"->"+nextToken.getText());
			}
		}
		else if(peekNextToken().getType().equals("if")){
			int num=2;
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("then")){
				error("then in statement");
			}
			statement();
			//the else appears to be optional
			if(peekNextToken().getType().equals("else")){
				getNextToken();
				statement();
				num++;
			}
			buildTree("if",num);
		}
		else if(peekNextToken().getType().equals("while")){
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("do")){
				error("do in statement (while)");
			}
			statement();
			buildTree("while", 2);
		}
		else if(peekNextToken().getType().equals("read")){
			/*
				"read" followed by "(" followed by a list of name()s, each seperated by a
				"," ending in a ")"
			*/
			int num = 0;
			getNextToken();
			getNextToken();
			if(nextToken.getType().equals("(")){
				while(!peekNextToken().getType().equals(")")){
					if(!peekNextToken().getType().equals("identifier")){
						error("identifier in statement (read)");
					}
					name();
					getNextToken();
					if(!nextToken.getType().equals(",")&&!nextToken.getType().equals(")")){
						error(", or ) in statement (read)"+"->"+nextToken.getText());
					}
					num++;
					if(nextToken.getType().equals(")")){
						break;
					}
					
				}
				buildTree("read",num);
			}			
			else{
				error("( in statement (read)");
			}
		}
		else if(peekNextToken().getType().equals("for")){
			//System.out.println(peekNextToken().getText());
			getNextToken();
			getNextToken();
			if(!(nextToken.getType().equals("("))){
				error("( in statement (for)");
			}
			//System.out.println(peekNextToken().getText());
			forStat();
			//System.out.println(peekNextToken().getText());
			getNextToken();
			if(!(nextToken.getType().equals(";"))){
				error("; in statement (for)");
			}
			forExp();
			//System.out.println(peekNextToken().getText());
			getNextToken();
			if(!(nextToken.getType().equals(";"))){
				error("; in statement (for)");
			}
			//System.out.println(peekNextToken().getText());
			forStat();
			//System.out.println(peekNextToken().getText());
			getNextToken();
			if(!(nextToken.getType().equals(")"))){
				error(") in statement (for)"+"->"+nextToken.getText());
			}
			//System.out.println(peekNextToken().getText());
			statement();
			buildTree("for",4);
		}
		else if(peekNextToken().getType().equals("loop")){
			/*
			"loop" followed by a list of statement()s seperated by ";"s ending with "pool"
			*/
			int num = 0;
			getNextToken();
			while(!peekNextToken().getType().equals("pool")){
				statement();
				getNextToken();
				if(!nextToken.getType().equals("pool")&&!nextToken.getType().equals(";")){
					error("pool or ; in statement (loop)");
				}
				num++;
				if(nextToken.getType().equals("pool")){
					break;
				}
			}
			buildTree("loop",num);
		}
		else if(peekNextToken().getType().equals("case")){
			//unfinished
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("of")){
				error("of in statement (case)");
			}
			int clauses=caseClauses();
			if(peekNextToken().getType().equals("otherwise")){
				otherwiseClause();
			}
			getNextToken();
			if(!nextToken.getType().equals("end")){
				error("end in statement (case)");
			}
			buildTree("case",2+clauses);
		}
		else if(peekNextToken().getType().equals("repeat")){
			/*"repeat" followed by a list of statement()s each seperated by ";", 
			then a single "until" followed by one expression()*/
			getNextToken();
			int num = 1;
			while(!peekNextToken().getType().equals("until")){
				statement();
				getNextToken();
				if(!nextToken.getType().equals(";")&&!nextToken.getType().equals("until")){
					error("; or until in statement (repeat)"+"->"+nextToken.getType());
				}
				num++;
				if(nextToken.getType().equals("until")){
					break;
				}
			}
			expression();
			buildTree("repeat",num);
		}
		else if(peekNextToken().getType().equals("return")){
			getNextToken();//should be a "return"
			expression();
			buildTree("return",1);
		}
		else if(peekNextToken().getType().equals("exit")){
			getNextToken();
			buildTree("exit", 0);
		}
		else if(peekNextToken().getType().equals("begin")){
			body();
		}
		else if(peekNextToken().getType().equals("identifier")){
			assignment();
		}
		else {
			//must be null...?
			buildTree("<null>",0);
		}
	}
	public void outExp(){
		getNextToken();
		if(nextToken.getType().equals("string")){
			stringNode();
			buildTree("string",1);
		}
		else{
			expression();
			buildTree("integer",1);
		}
	}
	public void stringNode(){
		//not sure if I should build a tree here.
		buildTree("<string>", 0);
	}
	public int caseClauses(){
		//one or more (caseClause() ';') so a caseClause() call followed by a ";"
		int num=0;
		do{
			caseClause();
			num++;
		}while(peekNextToken().equals(";"));
		buildTree("case_clause",num);
		return num;
	}
	public void caseClause(){
		int num = 0;
		while(!nextToken.getType().equals(":")){
			caseExpression();
			getNextToken();
			num++;
			if(nextToken.getType().equals(":")){
				break;
			}
			else if(!nextToken.getType().equals(",")){
				error(", in caseClause");
			}
		}
		statement();
		buildTree("case_clause",num);
	}
	public void caseExpression(){
		constValue();
		if(peekNextToken().getType().equals("..")){
			getNextToken();
			constValue();
			buildTree("..", 2);
		}
	}
	public void otherwiseClause(){
		getNextToken();
		if(!nextToken.getType().equals("otherwise")){
			error("otherwise in otherwiseClause");
		}
		statement();
		buildTree("otherwise",1);
	}
	public void assignment(){
		name();
		getNextToken();
		if(nextToken.getType().equals(":=")){
			expression();
			buildTree("assign", 2);
		}
		else if(nextToken.getType().equals(":=:")){
			name();
			buildTree("swap",2);
		}
		else{
			//System.out.println(peekNextToken().getText());
			error(";= or :=; in assignment"+"->"+nextToken.getText());
		}
	}	
	public void forStat(){
		if(peekNextToken().getType().equals("identifier")){
			assignment();
		}
		else{
			buildTree("<null>",0);
		}
	}
	public void forExp(){
		switch(peekNextToken().getType()){
			case "-":
			case "+":
			case "not":
			case "eof":
			case "identifier":
			case "integer":
			case "char":
			case "(":
			case "succ":
			case "pred":
			case "chr":
			case "ord": 
				expression();
				break;
			default:
				//getNextToken();
				buildTree("true",0);
		}
	}
	public void expression(){
		term();
		switch(peekNextToken().getType()){
			case"<=":
				getNextToken();
				term();
				buildTree("<=",2);
				break;
			case"<":
				getNextToken();
				term();
				buildTree("<",2);
				break;
			case">=":
				getNextToken();
				term();
				buildTree(">=",2);
				break;
			case">":
				getNextToken();
				term();
				buildTree(">",2);
				break;
			case"=":
				getNextToken();
				term();
				buildTree("=",2);
				break;
			case"<>":
				getNextToken();
				term();
				buildTree("<>",2);
				break;
			default:
				//error("expression");
		}
	}
	public void term(){
		factor();
		/*
			Then we expect a +, -, or an or followed by factor();
		*/
		while(peekNextToken().getType().equals("+")||peekNextToken().getType().equals("-")||peekNextToken().getType().equals("or")){
			switch(peekNextToken().getType()){
				case"+":
					getNextToken();
					term();
					factor();
					buildTree("+",2);
					break;
				case"-":
					getNextToken();
					term();
					factor();
					buildTree("-",2);
					break;
				case"or":
					getNextToken();
					term();
					factor();
					buildTree("or",2);
					break;
				default:
				//System.out.println(peekNextToken().getType());
				//error("a value in term");
			}
		}
	}

	public void factor(){
		primary();
		while(peekNextToken().getType().equals("*")||peekNextToken().getType().equals("/")||peekNextToken().getType().equals("and")||peekNextToken().getType().equals("mod")){
			switch(peekNextToken().getType()){
			//	System.out.println(peekNextToken().getType());
				case"*":
					getNextToken();
					factor();
					primary();
					buildTree("*",2);
					break;
				case"/":
					getNextToken();
					factor();
					primary();
					buildTree("/",2);
					break;
				case"and":
					getNextToken();
					factor();
					primary();
					buildTree("and",2);
					break;
				case"mod":
					getNextToken();
					factor();
					primary();
					buildTree("mod",2);
					break;
				default:
					//System.out.println(peekNextToken().getText());
					//	error("a value in factor"+"->"+nextToken.getType());
			}
		}
	}
		
	public void primary(){
		switch(peekNextToken().getType()){
			case "-":
				getNextToken();
				primary();
				buildTree("-",1);
				break;
			case "+":
				getNextToken();
				primary();
				break;
			case "not":
				getNextToken();
				primary();
				buildTree("not",1);
				break;
			case "eof":
				getNextToken();
				buildTree("eof", 0);
				break;
			case "identifier":
				name();
				int num = 1;
				if(peekNextToken().getType().equals("(")){
					getNextToken();
					while(!peekNextToken().getType().equals(")")){
						getNextToken();
						expression();
						//list means expression , expression
						//+means at least once
						//* 0 or more times
						num++;
						getNextToken();
						if(!nextToken.getType().equals(",")&&!nextToken.getType().equals(")")){
							error(", or ) in primary of case identifier"+"->"+nextToken.getText());
						}
						if(nextToken.getType().equals(")")){
							break;
						}
					}
					//getNextToken();
					buildTree("call",num);
				}
				
				break;
			case "(":
				getNextToken();
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					error(") in case '(' of primary");
				}
				break;
			case "succ":
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					error("( in case 'succ' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					error(") in case 'succ' of primary");
				}
				buildTree("succ",1);
				break;
			case "pred":
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					error("( in case 'pred' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					error(") in case 'pred' of primary");
				}
				buildTree("pred",1);
				break;
			case "chr":
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					error("( in case 'chr' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					error(") in case 'chr' of primary");
				}
				buildTree("chr",1);
				break;
			case "ord":
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					error("( in case 'ord' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					error(") in case 'ord' of primary");
				}
				buildTree("ord",1);
				break;
			case "char":
				getNextToken();
				buildTree(nextToken.getText(),0);
				buildTree("<char>",1);
				break;
			case "integer":
				getNextToken();
				buildTree(nextToken.getText(),0);
				buildTree("<integer>",1);
				break;
			default:
				//System.out.println(nextToken.getType());
				//error("in primary"+"->"+peekNextToken().getType());
		}
	}
	public void buildTree(String s, int n){
		//pops n trees (how many children it should) from the tree stack, builds a node with string s
		//as their parent and pushes the resulting tree to the stack
		//System.out.println("Stack Size: "+stackOfTrees.size());
	//	System.out.println(s+" "+n);
		Node node = new Node(s);
		if(n!=0){
			for(int i = 0; i<n ; i++){
				node.addSubTree(stackOfTrees.pop());
			}
		}
		stackOfTrees.push(new Tree(node));
	}

	public void getNextToken(){
		nextToken=tokens.get(index);
		index++;
	}

	public Token peekNextToken(){
		return tokens.get(index);
	}

	public void error(String s){
		// maybe something along the lines of 
		System.out.println("Error, expects "+s);
		System.exit(0);
	}

}