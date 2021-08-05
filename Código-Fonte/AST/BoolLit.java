package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class BoolLit extends Literal {
    
    @Override
    public void visit(Visitor v){
        v.visitBoolLit(this);
    }
}
