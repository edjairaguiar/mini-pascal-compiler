package AST;
import Visitor.Visitor;
import compilador.Token;

/**
 *
 * @author edjair
 */

public class Literal extends Fator{
    public Token name;
    
    public void visit(Visitor v){
        v.visitLiteral(this);
    }
}
