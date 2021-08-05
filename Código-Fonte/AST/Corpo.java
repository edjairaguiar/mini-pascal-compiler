package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class Corpo {
    public Declaracoes declarations;
    public ComandoComposto compositeCommand;
    
    public void visit(Visitor v){
        v.visitCorpo(this);
    }
    
}