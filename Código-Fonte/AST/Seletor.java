package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class Seletor {
    public Expressao expression;
    public Seletor next;
    
    public void visit(Visitor v){
        v.visitSeletor(this);
    }
}
