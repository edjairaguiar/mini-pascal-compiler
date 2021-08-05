package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class Iterativo extends Comando{
    public Expressao expression;
    public Comando command;
    
    public void visit(Visitor v){
        v.visitIterativo(this);
    }
}