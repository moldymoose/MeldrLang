package org.meldr.compiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
//YOU ARE NOT REQUIRED TO MODIFY THIS CLASS

public class Driver 
{
    static boolean isSyntaxError = false;
	
	public static void main(String[] args) throws Exception {
        InputStream in;

        if(args.length > 0) {
            in = new FileInputStream(args[0]);
        } else {
            in = System.in;
        }

		// create a CharStream that reads from standard input
		CharStream input = CharStreams.fromStream(in);

		// create a lexer that feeds off of input CharStream
		MeldrLangLexer lexer = new MeldrLangLexer(input);

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// create a parser that feeds off the tokens buffer
		MeldrLangParser parser = new MeldrLangParser(tokens);
        parser.removeErrorListeners();
		BaseErrorListener errListener = new BaseErrorListener()
		{
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
			{
				isSyntaxError = true;
				System.out.println("Syntax error occurred at Line: " + line + "." + " ( " + msg + " )");
			}
		};

		parser.addErrorListener(errListener);
		ParseTree tree = parser.scene(); // begin parsing at scene rule
		if(!isSyntaxError) //throws exceptions if there's syntax errors in our code
		{
			// Create a generic parse tree walker that can trigger callbacks
			ParseTreeWalker walker = new ParseTreeWalker();

			//Class for generating python code needed to render user input (extends BaseListener)
			PythonBuilder stb = new PythonBuilder();

			// Walk the tree created during the parse, trigger callbacks
			walker.walk(stb, tree);

            String filename = stb.getSceneName() + ".py";
            Path outputPath = Path.of("output/" + filename);

            Files.createDirectories(outputPath.getParent());

            try (PrintWriter out = new PrintWriter(outputPath.toFile())) {
                out.println(stb.printOutput());
            }
		}
	}
}