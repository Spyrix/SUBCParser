import java.util.*;
import java.io.*;
/*
William Frazee
#1355-5441
The main class
*/
public class p1 {
	
	public static void main(String[] args){
		ArrayList<Token> tokenList = new ArrayList<Token>();
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
		This snippet proves that the tokenizer works correctly
		for (int i = 0; i < tokenList.size(); i++){
				System.out.println(tokenList.get(i).getType()+"|||"+tokenList.get(i).getText());
		}*/
		
		TreeGenerator generator = new TreeGenerator(tokenList);
		Tree t = generator.generateTree();
		
		if(printTree==true){
			t.traverseTree();
		}
		//THE SECRET COMMAND FOR TESTING PURPOSES
		//if(args[2]!=null)
			//save to file
	
	}
}