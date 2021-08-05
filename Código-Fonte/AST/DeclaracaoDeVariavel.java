package AST;
import Visitor.Visitor;

/**
 *
 * @author edjair
 */

public class DeclaracaoDeVariavel {
    public ListaDeIds listOfIds;
    public Tipo type;
    
    public void visit(Visitor v){
        v.visitDeclaracaoDeVariavel(this);
    }
}
