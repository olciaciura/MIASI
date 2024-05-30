package rewriter_python;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


class PythonListener extends Python3ParserBaseListener {
    private final CommonTokenStream tokStream;
    public TokenStreamRewriter rewriter;
    public PythonListener( CommonTokenStream tokens) throws IOException {
        this.tokStream = tokens;
        this.rewriter = new TokenStreamRewriter(tokens);
    }
    List<String> namesToRename = new ArrayList<String>();


     @Override
     public void enterName(Python3Parser.NameContext ctx) {
         String name = ctx.NAME().getText();
         RuleContext parent = ctx.getParent();
         String modifiedName = " " + name + " ";
         if (parent.getChild(0).getText().equals("def") || namesToRename.contains(name)){
             modifiedName = toSnakeCase(ctx.getText());
             if(!namesToRename.contains(name)) {
                 namesToRename.add(name);
             }
         }

         String leadingText = getLeadingText(ctx.start);
         String trailingText = getTrailingText(ctx.stop);
         System.out.println(leadingText + modifiedName + trailingText);

         rewriter.replace(ctx.start, ctx.stop, leadingText + modifiedName + trailingText);
     }

    private String getLeadingText(Token token) {
        // Capture leading whitespace and newlines
        int tokenIndex = token.getTokenIndex();
        List<Token> tokens = tokStream.getTokens();
        StringBuilder leadingWhitespace = new StringBuilder();

        for (int i = tokenIndex - 1; i >= 0; i--) {
            Token t = tokens.get(i);
            if (t.getType() == Python3Lexer.NEWLINE) {
                leadingWhitespace.insert(0, t.getText());
            } else {
                break;
            }
        }

        return leadingWhitespace.toString();
    }

    private String getTrailingText(Token token) {
        // Capture trailing whitespace and newlines
        int tokenIndex = token.getTokenIndex();
        List<Token> tokens = tokStream.getTokens();
        StringBuilder trailingWhitespace = new StringBuilder();

        for (int i = tokenIndex + 1; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == Python3Lexer.NEWLINE) {
                trailingWhitespace.append(t.getText());
            } else {
                break;
            }
        }

        return trailingWhitespace.toString();
    }


    private String toSnakeCase(String camelCaseString) {
        // Convert CamelCase to snakecase
        StringBuilder snakeCase = new StringBuilder();
        snakeCase.append(" ");
        if (Character.isUpperCase(camelCaseString.charAt(0))){
            snakeCase.append(Character.toLowerCase(camelCaseString.charAt(0)));
        } else {
            snakeCase.append(camelCaseString.charAt(0));
        }
        for (int i = 1; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (Character.isUpperCase(c)) {
                snakeCase.append("_");
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }
        snakeCase.append(" ");
        System.out.println(camelCaseString + " -> " + snakeCase);
        return snakeCase.toString();
    }
}
