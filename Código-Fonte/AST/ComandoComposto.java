package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class ComandoComposto extends Comando{
    public ListaDeComandos listOfCommands;
    
    public void visit(Visitor v){
        v.visitComandoComposto(this);
    }
}
