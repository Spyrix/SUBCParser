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
				error(":");
			}
			consts();
			types();
			dclns();
			subProgs();
			body();
			name();
			getNextToken();
			if(!nextToken.getType().equals(".")){//The next token should be a period for correctness
				error(".");
			}
			buildTree("program",7);//should be the final tree
		}
		else {
			error("program");
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
			error("identifier");
			//error
		}
	}
	//all the const stuff is hard to understand, no examples.
	
	public void consts(){
		int n=0;
		getNextToken();//gets next token which should be const, indicating a list of constants
		//list means expression , expression
		if(nextToken.getType().equals("const")) {
			do {
				constRule();
				getNextToken();
				if(!nextToken.getType().equals(",")){
					error(",");
				}
				n++;
			} while (!peekNextToken().getType().equals(";"));
			getNextToken();//gets rid of the next token which should be a ";"
		}
		buildTree("consts",n);
	}
	
	public void constRule(){
		getNextToken();
		if(!nextToken.getType().equals("=")){
			error("=");
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
			error("value");
			//error
		}
	}
	public void types(){
		int n=0;
		//+means at least once
		getNextToken();
		if(nextToken.getType().equals("type")) {
			do {
				type();
				getNextToken();
				if(!nextToken.getType().equals(";")){
					error(";");
				}
				n++;
			} while (!peekNextToken().getType().equals("identifier"));
		}
		buildTree("types",n);
	}
	public void type(){
		getNextToken();
		name();
		getNextToken();
		if(!nextToken.getType().equals("=")){
					error("=");
		}
		litList();
		buildTree("type", 2);
	}

	public void litList(){
		int n=0;
		//a list of names, seperated 
		getNextToken();
		if(!nextToken.getType().equals("(")){
					error("(");
		}
		do {
			name();
			getNextToken();
			if(!nextToken.getType().equals(",")||!nextToken.getType().equals(")")){
					error(",");
			}
			n++;
		} while(!nextToken.getType().equals(")"));
		getNextToken();//gets rid of the ")"
		getNextToken();//this should be a ";"
		if(!nextToken.getType().equals(";")){
			error(";");
		}
		buildTree("lit", n);
	}
	public void dclns(){
		getNextToken();
		int n=0;
		//first we have a var,
		//then we have at least one dcln; but possibly more
		//+means one or more
		if(nextToken.getType().equals("var")){
			do{
				dcln();
				getNextToken();
				n++;
			}while(!nextToken.getType().equals(";"));
			if(!nextToken.getType().equals(";")){
				error(";");//expected a ";"
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
			if(!nextToken.getType().equals(":")||!nextToken.getType().equals(",")){
				error(",");//could also error on a ":"
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
			error("function");
		}
		name();
		getNextToken();
		if(!nextToken.getType().equals("(")){
			error("(");
		}
		params();
		getNextToken();
		if(!nextToken.getType().equals(")")){
			error(")");
		}
		getNextToken();
		if(!nextToken.getType().equals(":")){
			error(":");
		}
		name();
		getNextToken();
		if(!nextToken.getType().equals(";")){
			error(";");
		}
		consts();
		types();
		dclns();
		body();
		name();
		getNextToken();
		if(!nextToken.getType().equals(";")){
			error(";");
		}
		buildTree("fcn", 8);
	}
	public void params(){
		int n = 0;
		//a list of dcln's each seperated by a semi colon
		do{
				dcln();
				getNextToken();
				n++;
				if(!nextToken.getType().equals(";")){
					error(";");//expected a ";"
				}
		}while(peekNextToken.getType().equals("identifier"));
		buildTree("params", n);
	}
	public void body(){
		//"begin" followed by a list of statements, each seperated by a ;, and finally an "end"
		int n = 0;
		getNextToken();
		if(!nextToken.getType().equals("begin")){
			error("begin");
		}
		while(!peekNextToken().getType().equals("end")){
			statement();
			getNextToken();
			if(nextToken.getType().equals(";")){
				error(";");
			}
			n++;
		}
		getNextToken();
		if(!nextToken.getType().equals("end")){
			error("end");
		}
		buildTree("block", n);
	}
	public void statement(){
		int n = 0;// wait I dont think statement actually builds out a tree... or at least, much of one
		if(peekNextToken().getType().equals("output")){
			getNextToken();
			getNextToken();
			if(nextToken.getType().equals("(")){
				//a list of outExp() each seperated by a "," ending with an ")"
			}
			else{
				error("(");
			}
		}
		else if(peekNextToken().getType().equals("if")){
			getNextToken();
			expression();

		}
		else if(peekNextToken().getType().equals("while")){
			getNextToken();
			expression();
			getNextToken();
			if(!nextToken.getType().equals("then")){
				error("then");
			}
			statement();
			//It seems like the else is an optional part, this might break
			if(peekNextToken().getType().equals("else")){
				getNextToken();
				statement();
			}
			
		}
		else if(peekNextToken().getType().equals("read")){
			/*
				"read" followed by "(" followed by a list of name()s, each seperated by a
				"," ending in a ")"
			*/
		}
		else if(peekNextToken().getType().equals("for")){
			
		}
		else if(peekNextToken().getType().equals("loop")){
			/*
			"loop" followed by a list of statement()s seperated by ";"s ending with "pool"
			*/
		}
		else if(peekNextToken().getType().equals("case")){
			
		}
		else if(peekNextToken().getType().equals("repeat")){
			/*"repeat" followed by a list of statement()s each seperated by ";", 
			then a single "until" followed by one expression()*/
		}
		else if(peekNextToken().getType().equals("return")){
			/*
			getNextToken()//should be a "return"
			expression();
			*/
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
		if(peekNextToken().getType().equals("string")){
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
	public void caseClauses(){
		//one or more (caseClause() ';') so a caseClause() call followed by a ";"
	}
	public void caseExpression(){
		constValue();
		if(peekNextToken().getType().equals("..")){
			getNextToken();
			constValue();
			buildTree("..", 2);
		}
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
			error(";= or :=;");
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
				getNextToken();
				buildTree("<null>",0);
		}
	}
	public void expression(){
		term();
		switch(peekNextToken()){
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
			default
				error("expression");
		}
	}
	public void term(){
		
	}
	public void factor(){

	}
	public void primary(){
		switch(peekNextToken().getType()){
			case "-":
				getNextToken();
				primary();
				buildTree("-",1);
				break;
			case "+":

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
				if(peekNextToken().getType().equals("(")){
					int n=0;
					getNextToken();
					while(!peekNextToken().getType().equals(",")){
						expression();
						//list means expression , expression
						//+means at least once
						//* 0 or more times
						n++
					}
					getNextToken();
				}
				break;
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
		nextToken=tokens.get(index);
		index++;
	}

	public Token peekNextToken(){
		return tokens.get(index+1);
	}

	public void error(String s){
		// maybe something along the lines of 
		//System.out.println("Error, expects "+s);
		//System.exit(0);
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
