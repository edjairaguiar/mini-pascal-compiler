package AST;
import Visitor.Visitor;
import compilador.Token;

/**
 *
 * @author edjair
 */

public class Expressao extends Fator{
    public ExpressaoSimples simpleExpression;
    public ExpressaoSimples simpleExpressionR;
    public Token operator;
    
    public void visit(Visitor v){
        v.visitExpressao(this);
    }
}
