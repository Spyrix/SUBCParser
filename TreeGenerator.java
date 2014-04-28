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
			if(!nextToken.getType().equals(":")){//the next token should be a colon, for correctness
				error(": in tiny"+"->"+nextToken.getText());
			}
			//getNextToken();
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
			buildTree("program",7);//should be the final tree
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
				if(nextToken.getType().equals(";")){
					break;
				}
				else if(!nextToken.getType().equals(",")){
					error(", in consts"+"->"+nextToken.getText());
				}
				n++;
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
		/*
			So I think that if there are no types than we just build a t
		*/
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
		while(!nextToken.getType().equals(")"));{
			name();
			getNextToken();
			if(!nextToken.getType().equals(",")||!nextToken.getType().equals(")")){
					error(", or ) in litList");
			}
			n++;
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
		if(nextToken.getType().equals("var")){
			do{
				dcln();
				getNextToken();
				n++;
			}while(nextToken.getType().equals("function"));
			if(!nextToken.getType().equals(";")){
				error("; in dclns");//expected a ";"
			}
		}
		buildTree("dclns",n);
	}
	public void dcln(){
		int n = 1;
		//a list of name()s each seperated by a comma followed by a ":" and then another name()
		do{
			name();
			getNextToken();
			System.out.println(nextToken.getType());
			if(!nextToken.getType().equals(":")||!nextToken.getType().equals(",")){
				error(", or : in dcln"+"->"+nextToken.getText());//could also error on a ":"
			}
			n++;
		} while(!nextToken.getType().equals(":"));
		name();
		buildTree("var", n);
	}
	public void subProgs(){
		int n=0;
		//* means zero or more. So we can have zero sub programs
		while(!peekNextToken().getType().equals("function")){
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
				getNextToken();
				n++;
				if(!nextToken.getType().equals(";")){
					error("; in params");//expected a ";"
				}
		}
		buildTree("params", n);
	}
	public void body(){
		//"begin" followed by a list of statements, each seperated by a ;, and finally an "end"
		int n = 0;
		getNextToken();
		if(!nextToken.getType().equals("begin")){
			error("begin in body");
		}
		while(!peekNextToken().getType().equals("end")){
			statement();
			getNextToken();
			if(nextToken.getType().equals(";")){
				error("; in body");
			}
			n++;
		}
		getNextToken();
		if(!nextToken.getType().equals("end")){
			error("end in body");
		}
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
					outExp();
					getNextToken();
					if(!nextToken.getType().equals(",")||!nextToken.getType().equals(")")){
						error(", or ) in statement (output)");
					}
					num++;
				}
				buildTree("output",num);
			}
			else{
				error("( in statement (output)");
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
					if(!nextToken.getType().equals(",")||!nextToken.getType().equals(")")){
						error(", or ) in statement (read)");
					}
					num++;
				}
				buildTree("output",num);
			}			
			else{
				error("( in statement (read)");
			}
		}
		else if(peekNextToken().getType().equals("for")){
			getNextToken();
			getNextToken();
			if(!(nextToken.getType().equals("("))){
				error("( in statement (for)");
			}
			forStat();
			getNextToken();
			if(!(nextToken.getType().equals(";"))){
				error("; in statement (for)");
			}
			forExp();
			getNextToken();
			if(!(nextToken.getType().equals(";"))){
				error("; in statement (for)");
			}
			forStat();
			getNextToken();
			if(!(nextToken.getType().equals(")"))){
				error(") in statement (for)");
			}
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
				if(!peekNextToken().getType().equals("pool")||!peekNextToken().getType().equals(";")){
					error("pool or ; in statement (loop)");
				}
				num++;
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
				if(!nextToken.getType().equals(";")||!peekNextToken().getType().equals("until")){
					error("; or until in statement (repeat)");
				}
				num++;
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
			buildTree("<null>",1);
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
			if(nextToken.getType().equals(":")){
				break;
			}
			else if(!nextToken.getType().equals(",")){
				error(", in caseClause");
			}
			num++;
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
			error(";= or :=; in assignment");
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
				error("expression");
		}
	}
	public void term(){
		factor();
		/*
			Then we expect a +, -, or an or followed by factor();
		*/
		getNextToken();
		switch(nextToken.getType()){
			case"+":
				factor();
				buildTree("+",2);
				break;
			case"-":
				factor();
				buildTree("-",2);
				break;
			case"or":
				factor();
				buildTree("or",2);
				break;
			default:
				error("a value in term");
		}
	}
	public void factor(){
		primary();
		getNextToken();
		switch(nextToken.getType()){
			case"*":
				primary();
				buildTree("*",2);
				break;
			case"/":
				primary();
				buildTree("/",2);
				break;
			case"and":
				primary();
				buildTree("and",2);
				break;
			case"mod":
				primary();
				buildTree("mod",2);
				break;
			default:
				error("a value in factor");
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
					while(!peekNextToken().getType().equals(",")){
						expression();
						//list means expression , expression
						//+means at least once
						//* 0 or more times
						num++;
						getNextToken();
						if(!nextToken.getType().equals(",")||!nextToken.getType().equals(")")){
							error(", or ) in primary of case identifier");
						}
					}
					//getNextToken();
				}
				buildTree("call",num);
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
				break;
			default:
				error("in primary");
		}
	}
	public void buildTree(String s, int n){
		//pops n trees (how many children it should) from the tree stack, builds a node with string s
		//as their parent and pushes the resulting tree to the stack
		Node node = new Node(s);
		if(n!=0){
			for(int i = 0; i<n ; i++){
				node.addSubTree(stackOfTrees.pop());
			}
		}
		stackOfTrees.push(new Tree(node));
	}

	public void getNextToken(){
		while(tokens.get(index).getType().equals("comment")||tokens.get(index).getType().equals("space")){
			index++;
		}
		nextToken=tokens.get(index);
		index++;
	}

	public Token peekNextToken(){
		return tokens.get(index+1);
	}

	public void error(String s){
		// maybe something along the lines of 
		System.out.println("Error, expects "+s);
		System.exit(0);
	}

}
/*
Tiny       -> 'program' Name ':' Consts Types Dclns
                 SubProgs Body Name '.' 			=> "program";

Consts     -> 'const' Const list ',' ';'			=> "consts"
           -> 										=> "consts";

Const      -> Name '=' ConstValue					=> "const";

ConstValue -> '<integer>'
	  	   -> '<char>'
	       -> Name;

Types      -> 'type' (Type ';')+					=> "types"
           -> 										=> "types";

Type       -> Name '=' LitList						=> "type";

LitList    -> '(' Name list ',' ')' 				=> "lit";
	   
SubProgs   -> Fcn*									=> "subprogs";

Fcn        -> 'function' Name '(' Params ')' ':' Name ';'
                 Consts Types Dclns Body Name ';'	=> "fcn";

Params     -> Dcln list ';'							=> "params";

Dclns      -> 'var' (Dcln ';')+						=> "dclns"
           -> 										=> "dclns";

Dcln       -> Name list ',' ':' Name				=> "var";

Body       -> 'begin' Statement list ';' 'end'     	=> "block";

Statement  -> Assignment
           -> 'output' '(' OutExp list ',' ')'    	=> "output"
           -> 'if' Expression 'then' Statement
                            ('else' Statement)?     => "if"
           -> 'while' Expression 'do' Statement     => "while"
	       -> 'repeat' Statement list ';' 'until'
	          Expression        			=> "repeat"
	       -> 'for' '(' ForStat ';' ForExp ';' ForStat ')'
	         Statement				=> "for"
	   -> 'loop' Statement list ';' 'pool'		=> "loop"
           -> 'case' Expression 'of' Caseclauses
                   OtherwiseClause 'end'                => "case"
           -> 'read' '(' Name list ',' ')'		=> "read"
	   -> 'exit'					=> "exit"
	   -> 'return' Expression			=> "return"
           -> Body
	   ->                                        	=> "<null>";

OutExp     -> Expression				=> "integer"
           -> StringNode				=> "string";

StringNode -> '<string>';

Caseclauses-> (Caseclause ';')+;

Caseclause -> CaseExpression list ',' ':' Statement     => "case_clause";

CaseExpression -> ConstValue
               -> ConstValue '..' ConstValue		=> "..";

OtherwiseClause -> 'otherwise' Statement                => "otherwise"
                -> ;

Assignment -> Name ':=' Expression  	        	=> "assign"
           -> Name ':=:' Name                           => "swap"; 


ForStat    -> Assignment
           -> 						=> "<null>";

ForExp     -> Expression
           -> 						=> "true";

Expression -> Term	                         		
	   -> Term '<=' Term				=> "<="
	   -> Term '<' Term				=> "<"
	   -> Term '>=' Term				=> ">="
	   -> Term '>' Term				=> ">"
	   -> Term '=' Term				=> "="
	   -> Term '<>' Term				=> "<>";

Term       -> Factor 
	   -> Term '+' Factor				=> "+"
	   -> Term '-' Factor				=> "-"
	   -> Term 'or' Factor				=> "or";

Factor     -> Factor '*' Primary			=> "*"
	   -> Factor '/' Primary			=> "/"
	   -> Factor 'and' Primary			=> "and"
	   -> Factor 'mod' Primary			=> "mod"
	   -> Primary;

Primary    -> '-' Primary                            	=> "-"
	   -> '+' Primary				
	   -> 'not' Primary				=> "not"
	   -> 'eof'                                     => "eof"
           -> Name
           -> '<integer>'
	   -> '<char>'
	   -> Name '(' Expression list ',' ')'		=> "call"
           -> '(' Expression ')'
	   -> 'succ' '(' Expression ')' 		=> "succ"
	   -> 'pred' '(' Expression ')' 		=> "pred"
	   -> 'chr' '(' Expression ')' 			=> "chr"
	   -> 'ord' '(' Expression ')' 			=> "ord";

Name       -> '<identifier>';
*/
