import java.util.*;
/*
William Frazee
#1355-5441
This class generates the tree
*/
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
		/*This procedure expects 'program' name() ';' consts() types() dclns() supProgs() body() name() '.'*/
		getNextToken();//this starts the process, getting the first token
		if(nextToken.getType().equals("program")){
			name();
			getNextToken();
			//System.out.println(nextToken.getText()+"index is:"+index);
			if(!nextToken.getType().equals(":")){//the next token should be a colon, for correctness
				//error(": in tiny"+"->"+nextToken.getText());
			}
			consts();
			types();
			dclns();
			subProgs();
			body();
			name();
			getNextToken();
			if(!nextToken.getType().equals(".")){//The next token should be a period for correctness
				//error(". in tiny"+"->"+nextToken.getText());
			}
			buildTree("program",4);//should be the final tree
		}
		//else 
			//error("program in tiny"+"->"+nextToken.getText());
		
	}
	
	public void name(){
		/*This procedure creates an <identifier> node with a subnode of the name of the identifier*/
		getNextToken();//gets next token which should be an identifier
		if(nextToken.getType().equals("identifier")){
			buildTree(nextToken.getText(), 0);
			buildTree("<identifier>", 1);

		}
		
		//else 
			//System.out.println(peekNextToken().getText());
			//error("identifier in name"+"->"+nextToken.getText());

		
		isReservedKeyword(nextToken.getText());

	}
	
	public void consts(){
		/*
			'const' list of constRule() calls seperated with ',' ';'
			If none, it builds a tree with zero children 
		*/
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
				/*else if(!nextToken.getType().equals(",")){
					//error(", in consts"+"->"+nextToken.getText());
				}*/
			}
			getNextToken();//gets rid of the next token which should be a ";"

		}
		buildTree("consts",n);
	}
	
	public void constRule(){
		/*This is named constRule instead of const() because it turns out that const is a reserved 
		keyword in java, What does it do? NOTHING!!! It's a hold over from the C++ design.*/
		/*name() '=' constValue()*/
		name();
		getNextToken();
		if(!nextToken.getType().equals("=")){
			//error("= in constRule");
		}
		constValue();
		buildTree("const", 2);
	}
	
	public void constValue(){
		/*builds either an <integer> or <char> node on the tree with a subnode contaning the text, or calls name()*/
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
		//else 
			//error("char, integer, or identifier in constValue");
			
		
	}
	public void types(){
		int n=0;
		//+means at least once

		/*'type' followed by at least one type type() ';' possibly more. Only happens if 'type' is there. If none, it builds a tree with zero children */
		if(peekNextToken().getType().equals("type")) {
			getNextToken();
			do {
				type();
				getNextToken();
				if(!nextToken.getType().equals(";")){
					//error(";");
				}
				n++;
			} while (peekNextToken().getType().equals("identifier"));
		}
		buildTree("types",n);
	}
	public void type(){
		//name() '=' litList()
		name();
		getNextToken();
		if(!nextToken.getType().equals("=")){
					//error("= in type");
		}
		litList();
		buildTree("type", 2);
	}

	public void litList(){
		int n=0;
		/*
			'(' list of name() seperated by ',' ending with ')'
		*/
		getNextToken();
		if(!nextToken.getType().equals("(")){
					//error("( in litList");
		}
		while(!nextToken.getType().equals(")")){
			name();
			getNextToken();
			n++;
			if(nextToken.getType().equals(")")){
				break;
			}
			if(!nextToken.getType().equals(",")&&!nextToken.getType().equals(")")){
					//error(", or ) in litList");
			}
			
		}
		/*
		getNextToken();//gets rid of the ")"
		getNextToken();//this should be a ";"
		if(!nextToken.getType().equals(";")){
			//error(";");
		}*/
		buildTree("lit", n);
	}
	public void dclns(){
		int n=0;
		/* 'var' then one or more calls to dcln() and then a ';' after each call. If none, it builds a tree with zero children */
		//+means one or more
		//System.out.println(peekNextToken().getText());
		if(peekNextToken().getType().equals("var")){
			getNextToken();
			do{
				dcln();
				getNextToken();
				n++;
				if(!nextToken.getType().equals(";")){
					//error("; in dclns");//expected a ";"
				}
			}while(!peekNextToken().getType().equals("function")&&!peekNextToken().getType().equals("begin"));
		}
		buildTree("dclns",n);
	}
	public void dcln(){
		/* list of name() calls seperated by ',' then ':' 'end'*/
		int n = 1;
		//a list of name()s each seperated by a comma followed by a ":" and then another name()
		do{
			name();
			getNextToken();
			//System.out.println(nextToken.getText()+"HI");
			if(!nextToken.getType().equals(":")&&!nextToken.getType().equals(",")){
				//error(", or : in dcln"+"->"+nextToken.getType());//could also error on a ":"
			}
			n++;
		} while(!nextToken.getType().equals(":"));
		name();
		buildTree("var", n);
	}
	public void subProgs(){
		int n=0;
		/* Zero or more fcn() calls*/
		while(peekNextToken().getType().equals("function")){
			fcn();
			n++;
		}
		buildTree("subprogs", n);
	}
	public void fcn(){
		/*
		'function' name() '(' params() ')' ':' name() ';' consts() types() dclns() body() name() ';'
		*/
		getNextToken();
		if(!nextToken.getType().equals("function")){
			//error("function in fcn"+"->"+nextToken.getText());
		}
		name();
		getNextToken();
		if(!nextToken.getType().equals("(")){
			//error("( in fcn");
		}
		params();
		getNextToken();
		if(!nextToken.getType().equals(")")){
			//error(") in fcn");
		}
		getNextToken();
		if(!nextToken.getType().equals(":")){
			//error(": in fcn");
		}
		name();
		getNextToken();
		if(!nextToken.getType().equals(";")){
			//error("; in fcn");
		}
		consts();
		types();
		dclns();
		body();
		name();
		getNextToken();
		if(!nextToken.getType().equals(";")){
			//error("; in fcn");
		}
		buildTree("fcn", 8);
	}
	public void params(){
		int n = 0;
		/* list of dcln() seperated by ';'*/
		while(peekNextToken().getType().equals("identifier")){
				dcln();
				n++;
				if(!peekNextToken().getType().equals(";")&&!peekNextToken().getType().equals(")")){
					//error("; or ) in params"+"->"+nextToken.getType());//expected a ";"
				}
				else if(peekNextToken().getType().equals(";")){
					getNextToken();
				}
		}
		buildTree("params", n);
	}
	public void body(){
		/*"begin" followed by a list of statements, each seperated by a ';' then 'end'*/
		int n = 0;
		getNextToken();
		if(!nextToken.getType().equals("begin")){
			//error("begin in body"+"->"+nextToken.getType());
		}
		while(!peekNextToken().getType().equals("end")){
			statement();
			n++;
			getNextToken();
			if(nextToken.getType().equals("end")){
				break;
			}
			else if(!nextToken.getType().equals(";")){
				//error("; in body");
			}
			//System.out.println(nextToken.getText());
		}
		//getNextToken();
		/*
		if(!nextToken.getType().equals("end")){
			//error("end in body");
		}*/
		
		buildTree("block", n);
	}
	public void statement(){
		/*this can be a whole mess of things*/
		int n = 0;// wait I dont think statement actually builds out a tree... or at least, much of one
		if(peekNextToken().getType().equals("output")){
			/* 'output' '(' list of outExp() seperated by ',' ')'*/
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
						//error(", or ) in statement (output)"+"->"+nextToken.getText());
					}
					num++;
					if(nextToken.getType().equals(")")){
						break;
					}
				}
				buildTree("output",num);
			}
			//else
				//error("( in statement (output)"+"->"+nextToken.getText());
			
		}
		else if(peekNextToken().getType().equals("if")){
			/* 'if' expression() 'then' statement() followed by an optional 'else' statement()*/
			int num=2;
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("then")){
				//error("then in statement");
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
			/*'while' expression() 'do' statement()*/
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("do")){
				//error("do in statement (while)");
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
						//error("identifier in statement (read)");
					}
					name();
					getNextToken();
					if(!nextToken.getType().equals(",")&&!nextToken.getType().equals(")")){
						//error(", or ) in statement (read)"+"->"+nextToken.getText());
					}
					num++;
					if(nextToken.getType().equals(")")){
						break;
					}
					
				}
				buildTree("read",num);
			}			
			else{
				//error("( in statement (read)");
			}
		}
		else if(peekNextToken().getType().equals("for")){
			/*'for' '(' forStat() ';' forExp() ';' forStat() ')' statement()*/
			//System.out.println(peekNextToken().getText());
			getNextToken();
			getNextToken();
			if(!(nextToken.getType().equals("("))){
				//error("( in statement (for)");
			}
			//System.out.println(peekNextToken().getText());
			forStat();
			//System.out.println(peekNextToken().getText());
			getNextToken();
			if(!(nextToken.getType().equals(";"))){
				//error("; in statement (for)");
			}
			forExp();
			//System.out.println(peekNextToken().getText());
			getNextToken();
			if(!(nextToken.getType().equals(";"))){
				//error("; in statement (for)");
			}
			//System.out.println(peekNextToken().getText());
			forStat();
			//System.out.println(peekNextToken().getText());
			getNextToken();
			if(!(nextToken.getType().equals(")"))){
				//error(") in statement (for)"+"->"+nextToken.getText());
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
					//error("pool or ; in statement (loop)");
				}
				num++;
				if(nextToken.getType().equals("pool")){
					break;
				}
			}
			buildTree("loop",num);
		}
		else if(peekNextToken().getType().equals("case")){
			/*'case' expression() 'of' caseClauses() otherwiseClause() 'end'

			I think the otherwiseClause seems to be optional*/
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("of")){
				//error("of in statement (case)");
			}
			int clauses=caseClauses();
			//System.out.println("hi");
			if(peekNextToken().getType().equals("otherwise")){
				otherwiseClause();
			}
			getNextToken();
			if(!nextToken.getType().equals("end")){
				//error("end in statement (case)"+"->"+nextToken.getText());
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
					//error("; or until in statement (repeat)"+"->"+nextToken.getType());
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
			/*'return' expression()*/
			getNextToken();//should be a "return"
			expression();
			buildTree("return",1);
		}
		else if(peekNextToken().getType().equals("exit")){
			/*'exit'*/
			getNextToken();
			buildTree("exit", 0);
		}
		else if(peekNextToken().getType().equals("begin")){
			/*body() always begins with a 'begin' thus it would make sense to have this check for a statement() to turn into a body()*/
			body();
		}
		else if(peekNextToken().getType().equals("identifier")){
			/*assignment() always begin with identifiers thus it would make sense to have this check for a statement() to turn into a assignment()*/
			assignment();
		}
		else {
			/*must be null because it's nothing else*/
			buildTree("<null>",0);
		}
	}
	public void outExp(){
		/*Two possibilities

		Either we have a string in which case stringNode() should be called

		If not that then it must be an expression*/
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
		buildTree("<string>", 0);
	}
	public int caseClauses(){
		//one or more caseClause() with a ';' following. Interestingly enough, this procedure does not build a node.
		int num=0;
		do{
			//System.out.println(peekNextToken().getType());
			caseClause();
			num++;
			//System.out.println(nextToken.getType());
			//System.out.println(peekNextToken().getType());
			getNextToken();
			if(!nextToken.getType().equals(";")){
				//error("; in caseClauses"+"->"+nextToken.getText());
			}
		}while(peekNextToken().getType().equals("identifier")||peekNextToken().getType().equals("char")||peekNextToken().getType().equals("integer"));
		//buildTree("case_clause",num);
		return num;
	}
	public void caseClause(){
		/*A list of caseExpression() seperated by ',' then a ":" ending with statement()*/
		int num = 1;
		while(!nextToken.getType().equals(":")){
			caseExpression();
			getNextToken();
			num++;
			if(nextToken.getType().equals(":")){
				break;
			}
			/*else if(!nextToken.getType().equals(",")){
				//error(", in caseClause");
			}*/
		}
		//System.out.println(peekNextToken().getType());
		//System.out.println(num);
		statement();
		buildTree("case_clause",num);
	}
	public void caseExpression(){
		/*
		Two possibilities
		Either we have a single constValue()
		or we have constValue() '..' constValue()
		*/
		constValue();
		if(peekNextToken().getType().equals("..")){
			getNextToken();
			constValue();
			buildTree("..", 2);
		}
	}
	public void otherwiseClause(){
		/*
			if we have 'otherwise' then we call statement()
			else do nothing

			the optionality of this is handled in the statement() that the otherwiseClause() call originated from
		*/
		getNextToken();
		if(!nextToken.getType().equals("otherwise")){
			//error("otherwise in otherwiseClause");
		}
		statement();
		buildTree("otherwise",1);
	}
	public void assignment(){
		/*
			Two possibilities

			In both cases we have a name()
			But then we call name() or expression() dependinging on the next operator

		*/
		name();
		//System.out.println(peekNextToken().getText());
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
			//error(";= or :=; in assignment"+"->"+nextToken.getText());
		}
	}	
	public void forStat(){
		/*
		either it called assignment() or builds a null
		*/
		if(peekNextToken().getType().equals("identifier")){
			assignment();
		}
		else{
			buildTree("<null>",0);
		}
	}
	public void forExp(){
		/*Either it calls expression() or builds null, depending on the operator*/
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
		/*
			always calls term() then we may get another term() call depending on what's next
		*/
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
				////error("expression");
		}
	}
	public void term(){
		/*
			term() ALWAYS goes to factor()
		*/
		factor();
		/*
			Then we MAY get a '+', '-', or an 'or' followed by factor();. This repeats as many times as is necessary
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
				////error("a value in term");
			}
		}
	}

	public void factor(){
		/*
			factor() always goes to a primary()
			Then we MAY get a '*', '/', 'and', or 'mod' followed by primary();. This repeats as many times as is necessary
		*/
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
					//	//error("a value in factor"+"->"+nextToken.getType());
			}
		}
	}
		
	public void primary(){
		switch(peekNextToken().getType()){
			/*
				much like statement(), this varies depending on what comes next
			*/
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
			/*
				in this case we always get a name() but we may get a list of expression() seperated by ',' depending on if we get a parens next
			*/
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
							//error(", or ) in primary of case identifier"+"->"+nextToken.getText());
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
				/*
					a single expression() enclosed by '(' and ')'
				*/
				getNextToken();
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					//error(") in case '(' of primary");
				}
				break;
			case "succ":
				/*
					'succ' followed by a single expression() enclosed by '(' and ')'
				*/
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					//error("( in case 'succ' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					//error(") in case 'succ' of primary");
				}
				buildTree("succ",1);
				break;
			case "pred":
				/*
					'pred' followed by a single expression() enclosed by '(' and ')'
				*/
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					//error("( in case 'pred' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					//error(") in case 'pred' of primary");
				}
				buildTree("pred",1);
				break;
			case "chr":
				/*
					'chr' followed by a single expression() enclosed by '(' and ')'
				*/
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					//error("( in case 'chr' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					//error(") in case 'chr' of primary");
				}
				buildTree("chr",1);
				break;
			case "ord":
				/*
					'ord' followed by a single expression() enclosed by '(' and ')'
				*/
				getNextToken();
				getNextToken();
				if(!nextToken.getType().equals("(")){
					//error("( in case 'ord' of primary");
				}
				expression();
				getNextToken();
				if(!nextToken.getType().equals(")")){
					//error(") in case 'ord' of primary");
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
				////error("in primary"+"->"+peekNextToken().getType());
		}
	}
	public void buildTree(String s, int n){
		/*pops n trees (how many children it should have) from the tree stack, builds a node with string s
		as their parent and pushes the resulting tree to the stack*/
		
		System.out.println("Stack Size: "+stackOfTrees.size());
		//System.out.println(s+" "+n);
		Node node = new Node(s);
		if(n!=0){
			for(int i = 0; i<n ; i++){
				node.addSubTree(stackOfTrees.pop());
			}
		}
		stackOfTrees.push(new Tree(node));
	}

	public void getNextToken(){
		/*This makes the global nextToken variable the next token in the list of tokens parsed from the input file.*/
		nextToken=tokens.get(index);
		index++;
	}

	public Token peekNextToken(){
		/*This returns the token that comes next in the list without incrementing the list  */
		return tokens.get(index);
	}

	public void error(String s){
		/*
			This function was used during debugging to see if there were any errors in the syntax. Usually if this function was called,
			it meant an error in the logic of the code.
		*/
		System.out.println("Error, expects "+s);
		System.exit(0);
	}

	public void isReservedKeyword(String s){
		//Checks to see if an identifier is on the reserved list of keywords.
		switch (s) {
			case "\\n":
				
			case "program":
				
			case "var":
				
			case "consts":
				
			case "type":
				
			case "function":
				
			case "return":
				
			case "begin":
				
			case "end":
				
			case ":=:":
				
			case ":=":
				
			case "output":
				
			case "if":
				
			case "then":
				
			case "else":
				
			case "while":
				
			case "do":
				
			case "case":
				
			case "of":
				
			case "..":
				
			case "otherwise":
				
			case "repeat":
				
			case "for":
				
			case "until":
				
			case "loop":
				
			case "pool":
			
			case "exit":
			
			case "<=":
			
			case "<>":
			
			case "<":
			
			case ">=":
			
			case ">":
			
			case "=":

			case "mod":
				
			case "and":
				
			case "or":

			case "not":

			case "read":
			
			case "succ":
			
			case "pre":
			
			case "chr":
	
			case "ord":
		
			case "eof":

			case "{":
	
			case "}":
			
			case ":":
		
			case ";":
		
			case ".":
			
			case ",":
			
			case "(":
			
			case ")":
				
			case "+":

			case "-":
				
			case "*":

			case "/":
				//error("non-reserved keyword ("+s+")");
		}

	}

}