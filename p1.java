import java.util.*;
import java.io.*;

public class p1 {
	
	public static void main(String[] args){
		ArrayList<Token> tokenList = new ArrayList<Token>();//not sure if I realllllly need this...
		Scan scan;
		File source;
		//Scanner scanner;
		//ErrorChecker errorC;
		boolean printTree = false;
		//maybe we should start scanning the file here
	
		//first we get the file path.
		if(args.length > 1) {
			source = new File(args[1]);
			if(args[0].equals("-ast"))
				printTree = true;
			else
				System.out.println("Unknown tag, please retry");
		}

		else
			source = new File(args[0]);

		scan = new Scan(source);
		//Okay so now we start scanning for tokens. For testing purposes I will put them into the arraylist of tokens
		do {
			tokenList.add(scan.scanNextToken());
		} while (scan.getNextToken().getText() != "eof");
		//remove the comments and spaces
		for(int i = 0; i < tokenList.size();i++){
			if(tokenList.get(i).getType().equals("space")||tokenList.get(i).getType().equals("comment")){
				tokenList.remove(i);
				i--;
			}
		}
		/*
		for (int i = 0; i < tokenList.size(); i++){
				System.out.println(tokenList.get(i).getType()+"|||"+tokenList.get(i).getText());
		}*/
		
		TreeGenerator generator = new TreeGenerator(tokenList);
		Tree t = generator.generateTree();
		
		if(printTree==true){
			t.traverseTree();
		}
		//THE SECRET COMMAND FOR FILE PRINTING PURPOSES
		//if(args[2]!=null)
			//save to file
	
	}
}
	/*
			So there are two possible input in args. Either there is one input which is the location of the file to analyze

			If there are two inputs, then the first one is -ast, which means we need to print out the abstract syntax tree.
			The second one is the location of the file.
		*/
		/*
			So this is how I imagine it to work. First we either parse through the file all at once and get an arraylist of tokens
			or we parse one token at a time. Is it possible to use the scanner class in either case? Scanner will get each token deliminated with a white space.
			
			If we do it one at a time, we use the grammar to begin the recursive bulding of the syntax tree.

			Oh oh! The way to do it one at a time! The scan class could have a scanner built in which keeps track of which token we are on. And it goes one character at a time to get the tokens.
			And then we instansiate the scan class in p1!
		*/

/*
Token Definitions

One of the first steps to start with the project is to write a lexical analyzer. Your lexical analyzer is supposed to convert a sequence of characters from the input file into a sequence of tokens and verify if they are valid tokens or not. 

Definition of some of the token is given below.
Identifier: Any sequence of characters which may contain alphabets, digits from 0 to 9 or an underscore. The sequence must start with an alphabet or an underscore. 
Integer: A sequence of characters which may contain any combination of digits from 0 to 9.
White Space: Any sequence of characters containing any combination of single space, form feed, horizontal tab and vertical tab.
Char: A sequence of three characters which contains a single character between two single quotes. The single character between the two quotes can be any character except a single quote itself. For example, ''' is an invalid character.
String: A sequence of characters which contains any number of characters between two double quotes. The character sequence between the two double quotes can be any character except a double quote itself. For example, "Hello"How" is an invalid string.
Comment: A sequence of characters which starts with {  and ends with a  }.  This type of comment can cross line boundaries.
 
Some of the pre-defined tokens in the language are given below:
No.	Token 	Description

1 	\n 		Newline

2 	program Start of Program

3 	var		Variable

4 	const	Constant 

5 	type	To define a data type

6	function To define a function

7	return	return from function

8	begin	start of a block

9	end end of a block

10	:=:	swap 

11	:=	assignment operator

12	output	output an expression or string

13	if 	keyword

14	then	keyword

15	else	keyword

16	while	keyword for loop

17	do 	keyword for loop

18	case	keyword

19	of	keyword

20 	..	dots for case expression 

21	otherwise	keyword

22	repeat 	keyword for repeat-until loop

23	for 	keyword for loop

24	until 	keyword for repeat-until loop

25	loop 	keyword for loop-pool loop

26	pool 	keyword for loop-pool loop

27	exit	keyword

28	<=	less than equal to binary operator

29	<>	not equal to binary operator

30	<	less than binary operator

31	>=	greater than equal to binary operator

32	>	greater than binary operator

33	=	equal to binary operator

34	mod	modulus binary operator

35	and	and binary operator

36	or 	or binary operator

37	not	not unary operator

38	read read an identifier

39	succ successor of an ordinal value

40	pre predecessor of a ordinal value

41	chr keyword for character function

42	ord keyword for ordinal function

43	eof	keyword for end of file 

44	{	begin comment

45	}	end comment

46	:	colon

47	;	semi colon

48	.	single dot

49	,	comma

50	(	Left parenthesis

51	)	Right parenthesis

52	+	plus

53	-	minus

54	*	multiply

55	/	divide

*/
