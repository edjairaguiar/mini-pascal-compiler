package AST;
import Visitor.Visitor;
import compilador.Token;

/**
 *
 * @author edjair
 */

public class ListaDeIds {
    public Token id;
    public ListaDeIds next;
    
    public void visit(Visitor v){
        v.visitListaDeIds(this);
    }
}
