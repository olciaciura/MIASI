package rewriter_python;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class StartModifier {
    public static void main(String[] args) throws Exception {
        CharStream inp = null;
        String out = null;

        try {
            inp = CharStreams.fromFileName("src/rewriter_python/input.py");
            out = "src/rewriter_python/output_v3.py";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Python3Lexer lexer = new Python3Lexer(inp);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        Python3Parser parser = new Python3Parser(tokens);

         ParseTree tree = parser.file_input();

         ParseTreeWalker walker = new ParseTreeWalker();
         PythonListener listener = new PythonListener(tokens);
         walker.walk(listener, tree);

         try {
             var wr = new FileWriter("output8.py");
             wr.write(listener.rewriter.getText()); //ta linijka tez est wazna
             wr.close();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }
}
