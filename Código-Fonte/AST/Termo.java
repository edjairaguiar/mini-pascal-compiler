package AST;
import Visitor.Visitor;
import compilador.Token;

/**
 *
 * @author edjair
 */

public class Termo {
    public Fator factor;
    public Termo next;
    public Token operator;
    public String type;
    
    public void visit(Visitor v){
        v.visitTermo(this);
    }
}
