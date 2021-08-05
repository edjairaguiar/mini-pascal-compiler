package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class Programa {
    public Corpo body;
    
    public void visit(Visitor v){
        v.visitPrograma(this);
    }
}
