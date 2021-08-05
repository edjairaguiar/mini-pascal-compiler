package compilador;

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
import Visitor.Visitor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edjair
 */
public class Checker implements Visitor {

    TabelaId table;
    public int erroC = 0;

    Checker() {
        table = new TabelaId();
        //table.print();
    }

    public void check(Programa program) throws IOException {

        System.out.println("\n>>> 2. ANALISE DE CONTEXTO <<<");
        program.visit(this);

        if (erroC != 0) {
            Erros.error(2, erroC);
        } else {
            System.out.println("  > Análise de Contexto concluída;\n  > Total de Erros de Contexto: 0");
        }

    }

    //Verificação de Ocorrências:
    @Override
    public void visitAtribuicao(Atribuicao becomes) {
        becomes.variable.visit(this);
        becomes.expression.visit(this);

        //Verificacao de tipos:
        if (becomes.variable.type != null) { //Confere se a atribuição não é vazia
            if (becomes.variable.type.equals(becomes.expression.type)) {
                becomes.type = becomes.variable.type; //Caso o tipo da variável seja o mesmo tipo da expressão, a atribuição recebe a mesma tipagem
            } else if (becomes.variable.type.equals("real") && becomes.expression.type.equals("integer")) {
                becomes.type = becomes.variable.type; //Caso a variável tenha tipo real e a expressão tenha tipo inteiro, a atribuição é do tipo real
            } else {
                try {
                    //Caso contrário, a atribuição tem valores incompatível. É gerada uma mensagem de erro:
                    Erros.error(221, erroC + 1);
                } catch (IOException ex) {
                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("     | Na linha: " + becomes.variable.id.line);
                System.out.println("     | Coluna:" + becomes.variable.id.col);
                erroC++;
            }
        }

    }

    @Override
    public void visitBoolLit(BoolLit boolLit) {
        boolLit.type = "boolean";
    }

    @Override
    public void visitComandoComposto(ComandoComposto compositeCommands) {
        if (compositeCommands.listOfCommands != null) { //Confere se a lista de comandos é vazia; caso não seja, chama a visita
            compositeCommands.listOfCommands.visit(this);
        }
    }

    @Override
    public void visitCondicional(Condicional conditional) {
        if (conditional.expression != null) {
            conditional.expression.visit(this);
            if (!conditional.expression.type.equals("boolean")) {
                try {
                    //O condicional funciona apenas se a expressão retorna o tipo booleano (true ou false);
                    //Caso não retorne, convoca-se mensagem de erro:
                    Erros.error(222, erroC + 1);
                } catch (IOException ex) {
                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("     | Tipo retornado: " + conditional.expression.type);
                System.out.println("     | Na linha: " + conditional.expression.operator.line);
                erroC++;
            }

        }
    }

    @Override
    public void visitCorpo(Corpo body) {
        if (body.declarations != null) {
            body.declarations.visit(this);
        }

        body.compositeCommand.visit(this);

    }

    @Override
    public void visitDeclaracaoDeVariavel(DeclaracaoDeVariavel variableDeclaration) {
        ListaDeIds aux = variableDeclaration.listOfIds;

        while (aux != null) {
            try {
                table.enter(aux.id, variableDeclaration);
            } catch (IOException ex) {
                Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
            }
            aux = aux.next;

        }

        if (variableDeclaration.type instanceof TipoAgregado) {
            ((TipoAgregado) variableDeclaration.type).visit(this);

        } else {
            if (variableDeclaration.type instanceof TipoSimples) {
                ((TipoSimples) variableDeclaration.type).visit(this);
            }
        }
    }

    @Override
    public void visitDeclaracoes(Declaracoes declarations) {
        Declaracoes aux = declarations;
        while (aux != null) {
            aux.declarationOfVariable.visit(this);
            aux = aux.next;
        }
    }

    @Override
    public void visitExpressao(Expressao expression) {
        if (expression.simpleExpression != null) {
            expression.simpleExpression.visit(this);  //Confere se a primeira expressão é vazia;  
            expression.type = expression.simpleExpression.type; //Caso não seja, o tipo da expressão é o mesmo tipo da primeira expressão
        }
        if (expression.simpleExpressionR != null) { //Confere se a expressão seguinte, separada por um operador relativo, é vazia
            expression.simpleExpressionR.visit(this);
            //Caso não seja, o tipo da expressão à direita deve ser real ou inteiro, a fim de fazer a comparação relativa
            if ("real".equals(expression.simpleExpressionR.type) || expression.simpleExpressionR.type.equals("integer")) {
                expression.type = "boolean"; //A expressão então recebe o valor booleano 
            } else {
                try {
                    //Caso contrário, deve-se retornar mensagem de erro:
                    Erros.error(223, erroC + 1);
                } catch (IOException ex) {
                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("     | Na linha: " + expression.operator.line);
                erroC++;
            }

        }

    }

    @Override
    public void visitExpressaoSimples(ExpressaoSimples simpleExpression) {
        ExpressaoSimples aux = simpleExpression; //Expressão-Simples recebe uma sequência de termos separados por um operador adicional (pode receber vazio);
        String place = null;
        while (aux != null) {
            if (aux.term != null) { //Confere se o primeiro termo é vazio
                aux.term.visit(this);
                if (place == null) {
                    place = aux.term.type; //place recebe o tipo do primeiro termo
                }
                if (aux.operator != null) { //Confere se existem termos subsequentes 
                    //Caso haja, vê se os operandos utilizados têm compatibilidade com os tipos dos termos.
                    //Caso não exista compatibilidade, deve-se retornar mensagens de erro:
                    switch (aux.operator.kind) {
                        case Token.SUM:
                        case Token.SUB:
                            switch (place) {
                                //Caso seja uma operação de soma ou subtração, o tipo do primeiro termo deve ser compatível com o tipo do segundo termo.
                                case "integer":
                                    switch (aux.term.type) {
                                        case "integer":
                                            place = "integer";
                                            break;
                                        case "real":
                                            place = "real";
                                            break;
                                        default: {
                                            try {
                                                //Caso os tipos sejam incompatíveis, retorna-se mensagem de erro:
                                                Erros.error(224, erroC + 1);
                                            } catch (IOException ex) {
                                                Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        System.out.println("     | Na linha: " + aux.operator.line);
                                        erroC++;
                                    }
                                    break;
                                case "real":
                                    if (aux.term.type.equals("boolean")) {
                                        try {
                                            Erros.error(224, erroC + 1);
                                        } catch (IOException ex) {
                                            Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        System.out.println("     | Na linha: " + aux.operator.line);
                                        erroC++;
                                    }
                                    place = "real";
                                    break;
                                default: {
                                    try {
                                        Erros.error(224, erroC + 1);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                System.out.println("     | Na linha: " + aux.operator.line);
                                erroC++;

                            }
                            break;
                        case Token.OR:
                            if (!place.equals("boolean") || !aux.term.type.equals("boolean")) {
                                try {
                                    Erros.error(224, erroC + 1);
                                } catch (IOException ex) {
                                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("     | Na linha: " + aux.operator.line);
                                erroC++;
                            }
                            place = "boolean";
                            break;
                    }
                }
            }
            aux = aux.next;
        }

        simpleExpression.type = place;

    }

    @Override
    public void visitIterativo(Iterativo iterative) {
        if (iterative.expression != null) { //Confere se a expressão do iterativo é vazia 
            iterative.expression.visit(this);
        }

        if (!iterative.expression.type.equals("boolean")) {
            try {
                //A expressão do iterativo deve retornar um tipo booleano (true ou false).
                //Caso não retorne, deve-se exibir mensagem de erro:
                Erros.error(222, erroC + 1);
            } catch (IOException ex) {
                Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("     | Tipo retornado: " + iterative.expression.type);
            System.out.println("     | Na linha: " + iterative.expression.operator.line);
            erroC++;
        }

        //Associa o comando do iterativo a atribuição, condicional, iterativo ou comando composto
        if (iterative.command instanceof Atribuicao) {
            ((Atribuicao) iterative.command).visit(this);
        } else if (iterative.command instanceof ComandoComposto) {
            ((ComandoComposto) iterative.command).visit(this);
        } else if (iterative.command instanceof Iterativo) {
            ((Iterativo) iterative.command).visit(this);
        } else if (iterative.command instanceof Condicional) {
            ((Condicional) iterative.command).visit(this);
        }
    }

    @Override
    public void visitListaDeComandos(ListaDeComandos listOfCommands) {
        ListaDeComandos aux = listOfCommands;
        while (aux != null) { //Realiza a chamada de Comando enquanto a lista de comandos não é vazia
            if (aux.command instanceof Atribuicao) {
                ((Atribuicao) aux.command).visit(this);
            } else if (aux.command instanceof ComandoComposto) {
                ((ComandoComposto) aux.command).visit(this);
            } else if (aux.command instanceof Iterativo) {
                ((Iterativo) aux.command).visit(this);
            } else if (aux.command instanceof Condicional) {
                ((Condicional) aux.command).visit(this);
            }
            aux = aux.next;
        }
    }

    @Override
    public void visitListaDeIds(ListaDeIds listOfIds) {

    }

    @Override
    public void visitLiteral(Literal literal) {
        // Associa o literal ao tipo de literal (literal inteiro, float-lit ou booleano) 
        switch (literal.name.kind) {
            case Token.INT_LIT:
                literal.type = "integer";
                break;
            case Token.FLOAT_LIT:
                literal.type = "real";
                break;
            case Token.BOOLEAN:
                literal.type = "boolean";
                break;
        }
    }

    @Override
    public void visitPrograma(Programa program) {
        program.body.visit(this);
    }

    @Override
    public void visitSeletor(Seletor selector) {
        Seletor aux = selector;
        while (aux != null) { //Seletor recebe vários seletores; realiza o processo enquanto não finaliza a lista de seletores
            aux.expression.visit(this);

            if (!aux.expression.type.equals("integer")) {
                try {
                    //O tipo da expressão contida no seletor deve ser inteira. Caso contrário, retorna-se mensagem de erro.
                    Erros.error(225, erroC + 1);
                } catch (IOException ex) {
                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("     | Na linha: " + aux.expression.operator.line);
                erroC++;
            }

            aux = aux.next;
        }

    }

    @Override
    public void visitTermo(Termo term) {
        Termo aux = term;
        String place = null;

        //Termo recebe uma lista de fatores separados por um operador racional. 
        while (aux != null) {
            if (aux.factor != null) { //Confere o primeiro fator

                // Associa fator à sua declaração: deve ser Variavel, Literal ou Expressao
                if (aux.factor instanceof Variavel) {
                    ((Variavel) aux.factor).visit(this);
                    term.type = aux.factor.type;
                } else if (aux.factor instanceof Literal) {
                    ((Literal) aux.factor).visit(this);
                    term.type = aux.factor.type;
                } else if (aux.factor instanceof Expressao) {
                    ((Expressao) aux.factor).visit(this);
                    term.type = aux.factor.type;
                }
//                if(aux.operator != null && aux.operator.value.equals("/")){
//                    term.type = "real";
//                }

                if (place == null) {
                    place = term.type;
                }

                if (aux.operator != null) {
                    //System.out.println(place + " op-mul  " + term.type);
                    switch (aux.operator.kind) {
                        case Token.DIV: //Divisão não pode conter operandos booleanos;
                            if (place.equals("boolean") || term.type.equals("boolean")) {
                                try {
                                    Erros.error(224, erroC + 1);
                                } catch (IOException ex) {
                                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("     | Na linha: " + aux.operator.line);
                                erroC++;
                            }
                            place = "real";
                            break;

                        case Token.MULT:
                            switch (place) {
                                case "integer":
                                    //Confere compatibilidade de tipos;
                                    switch (term.type) {
                                        case "integer":
                                            place = "integer";
                                            break;
                                        case "real":
                                            place = "real";
                                            break;
                                        default: {
                                            try {
                                                Erros.error(224, erroC + 1);
                                            } catch (IOException ex) {
                                                Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        System.out.println("     | Na linha: " + aux.operator.line);
                                        erroC++;
                                    }
                                    break;

                                case "real":
                                    switch (term.type) {

                                        case "integer":
                                        case "real":
                                            place = "real";
                                            break;

                                        default: {
                                            try {
                                                Erros.error(224, erroC + 1);
                                            } catch (IOException ex) {
                                                Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        System.out.println("     | Na linha: " + aux.operator.line);
                                        erroC++;
                                        break;
                                    }

                                    break;

                                default: {
                                    try {
                                        Erros.error(224, erroC + 1);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                System.out.println("     | Na linha: " + aux.operator.line);
                                erroC++;
                                break;
                            }

                            break;

                        case Token.AND:
                            //AND deve ser usado apenas entre booleanos
                            if (!place.equals("boolean") || !term.type.equals("boolean")) {
                                try {
                                    Erros.error(224, erroC + 1);
                                } catch (IOException ex) {
                                    Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("     | Na linha: " + aux.operator.line);
                                erroC++;
                            }
                            place = "boolean";
                            break;
                    }
                }
            }

            aux = aux.next;

        }

    }

    @Override
    public void visitTipoAgregado(TipoAgregado type) {

        //Visualiza se o Tipo incluido na declaração do Tipo-Agregado é Tipo-Agregado ou Tipo-Simples
        if (type.typo instanceof TipoAgregado) {
            ((TipoAgregado) type.typo).visit(this);
        } else {
            //Caso contrário, verifica-se e visita-se a instância do Tipo-Simples
            if (type.typo instanceof TipoSimples) {
                ((TipoSimples) type.typo).visit(this);
            }
        }
        type.type = type.typo.type;
    }

    @Override
    public void visitTipoSimples(TipoSimples type) {
        type.type = type.typo.value;
    }

    @Override
    public void visitVariavel(Variavel variable) {
        try {
            // Dá retrieve no ID da Variável; retornará erro caso a variável não tenha sido declarada;
            variable.declaration = table.retrieve(variable.id);
        } catch (IOException ex) {
            Logger.getLogger(Checker.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (variable.declaration != null) {
            variable.type = variable.declaration.type.type;
        }

        if (variable.selector != null) {
            variable.selector.visit(this);
        }
    }

}
