package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class ListaDeComandos {
    public Comando command;
    public ListaDeComandos next;
    
    public void visit(Visitor v){
        v.visitListaDeComandos(this);
    }
}