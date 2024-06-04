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
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        CharStream inp = null;
        List<String> names = new ArrayList<String>();

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
            names = visitor.names;
            System.out.println(names);
            writer.println(cleanOutput(changeNames(visitor.rewriter.getText(), names)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String cleanOutput(String str) {
        // Remove the special tokens used for indentation.
        return str.replaceAll("<INDENT>", "").replaceAll("<DEDENT>", "").replaceAll("<NEWLINE>", "");
    }
    public static String changeNames(String str, List<String> names) {
        // Rename.
        for(int i  = 0; i < names.size(); i++){
            str = str.replaceAll(names.get(i), camelToSnake(names.get(i)));
        }
        return str;
    }

    public static String camelToSnake(String name) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return name.replaceAll(regex, replacement).toLowerCase();
    }
}