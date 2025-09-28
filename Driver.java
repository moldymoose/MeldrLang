// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {

	private static boolean isValid = true;

	public static void main(String[] args) throws Exception {

		// create a CharStream that reads from standard input
		ANTLRInputStream input = new ANTLRInputStream(System.in);

		// create a lexer that feeds off of input CharStream
		LittleLexer lexer = new LittleLexer(input);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		// create a parser that feeds off the tokens buffer
		LittleParser parser = new LittleParser(tokens);
		parser.removeErrorListeners();
		BaseErrorListener errListener = new BaseErrorListener() 
		{

			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
			{
				isValid = false;
			}
		};
		parser.addErrorListener(errListener);
		//start the parsing process (i.e. "program" is the start symbol)
		parser.program();
		
		//add your code below to print "Accepted" or "Not accepted"
		if(isValid)
		{
			System.out.println("Accepted");
		} else
		{
			System.out.println("Not accepted");
		}
    }
}
