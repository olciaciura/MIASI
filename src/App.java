import grammar.PythonLexer;
import grammar.PythonParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import refactor.RefactorVisitor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class App {
    public static void main(String[] args) {
        CharStream inp = null;
        try {
            inp = CharStreams.fromFileName("input.py");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PythonLexer lexer = new PythonLexer(inp);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PythonParser parser = new PythonParser(tokens);
        ParseTree tree = parser.file_input();

        RefactorVisitor visitor = new RefactorVisitor(inp, tokens);
        visitor.visit(tree);

        try (PrintWriter writer = new PrintWriter("output.py")) {
            writer.println(cleanOutput(visitor.rewriter.getText()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String cleanOutput(String str) {
        // Remove the special tokens used for indentation.
        return str.replaceAll("<INDENT>", "").replaceAll("<DEDENT>", "").replaceAll("<NEWLINE>", "");
    }
}