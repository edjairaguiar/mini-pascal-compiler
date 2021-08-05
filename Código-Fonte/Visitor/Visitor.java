package Visitor;

import AST.Atribuicao; 
import AST.BoolLit;
import AST.ComandoComposto;
import AST.Condicional;
import AST.Corpo;
import AST.DeclaracaoDeVariavel;
import AST.Declaracoes;
import AST.Expressao;
import AST.ExpressaoSimples;
import AST.Iterativo;
import AST.ListaDeComandos;
import AST.ListaDeIds;
import AST.Literal;
import AST.Programa;
import AST.Seletor;
import AST.Termo;
import AST.TipoAgregado;
import AST.TipoSimples;
import AST.Variavel;

/**
 *
 * @author edjair
 */
public interface Visitor {
    
    public void visitAtribuicao(Atribuicao becomes);
    public void visitBoolLit(BoolLit boolLit);
    public void visitComandoComposto(ComandoComposto compositeCommands);
    public void visitCondicional(Condicional conditional);
    public void visitCorpo(Corpo body);
    public void visitDeclaracaoDeVariavel(DeclaracaoDeVariavel variableDeclaration);
    public void visitDeclaracoes(Declaracoes declarations);
    public void visitExpressao(Expressao expression);
    public void visitExpressaoSimples(ExpressaoSimples simpleExpression);
    public void visitIterativo(Iterativo iterative);
    public void visitListaDeComandos(ListaDeComandos listOfCommands);
    public void visitListaDeIds(ListaDeIds listOfIds);
    public void visitLiteral(Literal literal);
    public void visitPrograma(Programa program);
    public void visitSeletor(Seletor selector);
    public void visitTermo(Termo term);
    public void visitTipoAgregado(TipoAgregado type);
    public void visitTipoSimples(TipoSimples type);
    public void visitVariavel(Variavel variable);
    }

