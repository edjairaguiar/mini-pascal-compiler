package AST;
import Visitor.Visitor;
import compilador.Token;

/**
 *
 * @author edjair
 */


public class TipoSimples extends Tipo{
    public Token typo;
    
    public void visit(Visitor v){
        v.visitTipoSimples(this);
    }
}
