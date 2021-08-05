package AST;
import Visitor.Visitor;
import compilador.Token;

/**
 *
 * @author edjair
 */

public class Variavel extends Fator{
    public Token id;
    public Seletor selector;
    public DeclaracaoDeVariavel declaration;
    
    
    public void visit(Visitor v){
        v.visitVariavel(this);
    }
}