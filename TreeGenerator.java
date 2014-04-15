import java.util.*;
public class TreeGenerator{
	private Stack<Tree> stackOfTrees;
	private int index;
	private Token nextToken;
	private ArrayList<Token> tokens;
	public TreeGenerator(ArrayList<Tokens> t){
		this.tree = new Tree();
		stackOfTrees = new Stack();
		tokens = t;
		index = 0;
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
			if(!nextToken.getType().equals(":"))//the next token should be a colon, for correctness
				//error
			consts();
			types();
			dclns();
			subProgs();
			body();
			name();
			getNextToken();
			if(!nextToken.getType().equals("."))/*The next token should be a period for correctness*/
				//error
			buildTree("program",7);
		}
		else
			//error
	}
	public void name(){
		getNextToken();//gets next token which should be an identifier
		if(nextToken.getType().equals("identifier")){
			String s = nextToken.getText();
			buildTree(s, 0);
			buildTree("<identifier>", 1);
		}
		else
			//error
	}
	//all the const stuff is hard to understand
	public void consts(){
		int n=1;
		getNextToken();//gets next token which should be const, indicating a list of constants
		if(nextToken.getType().equals("const")){
			do{
				const();
				getNextToken();
				if(!nextToken.getType().equals(","))
					//error
				n++;
			}while (!peekNextToken().getType().equals(";");
		}
		else
			//error
		getNextToken();//gets rid of the next token which should be a ";"
		buildTree("consts",n);
	}
	public void const(){
		name();
		getNextToken();
		if(!nextToken.getType().equals("="))
			//error
		constValue();
		buildTree("const", 2);
	}
	public void constValue(){
		if(peekNextToken().getType().equals("char"){
			getNextToken();
			buildTree(nextToken.getText(), 0);
			buildTree("<char>", 1);
		}
		else if(peekNextToken().getType().equals("integer")){
			getNextToken();
			buildTree(nextToken.getText(), 0);
			buildTree("<integer>", 1)
		}
		else if (peekNextToken().getType().equals("identifier") {
			name();
		}
		else
			//error

	}
	public void types(){

	}
	public void dclns(){

	}
	public void subProgs(){

	}
	public void body(){

	} 
	public void buildTree(String s, int n){
		/*pops n trees (how many children it should) from the tree stack, builds a node with string s
		as their parent and pushes the resulting tree to the stack*/
		Node n = new Node(s);
		if(n!=0){
			for(int i = 0; i<n;i++){
				n.addSubTree(stackOfTrees.pop());
			}
		}
		stackOfTrees.push(new Tree(n));
	}

	public void getNextToken(){
		nextToken=tokens.get(index);
		index++;
	}
	public token peekNextToken(){
		return tokens.get(index+1);
	}
	public void error(String s){
		/* maybe something along the lines of 
		System.out.println("Error, expects "+s);
		System.exit(0);
		*/
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