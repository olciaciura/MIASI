package refactor;

import grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;

public class RefactorVisitor extends PythonParserBaseVisitor<String> {
    private final CharStream input;
    public TokenStreamRewriter rewriter;

    public RefactorVisitor(CharStream input, CommonTokenStream tokens) {
        super();
        this.input = input;
        this.rewriter = new TokenStreamRewriter(tokens);
    }

    private String getText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        if (input == null) throw new RuntimeException("Input stream undefined");
        return input.getText(new Interval(a, b));
    }

    @Override
    public String visitFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        refactorNames(ctx);
        return super.visitFunction_def_raw(ctx);
    }

    public void refactorNames(PythonParser.Function_def_rawContext ctx) {
        String name = ctx.NAME().getText();
        String newName = camelToSnake(name);
        rewriter.replace(ctx.NAME().getSymbol(), newName);
    }

    public String camelToSnake(String name) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return name.replaceAll(regex, replacement).toLowerCase();
    }
}