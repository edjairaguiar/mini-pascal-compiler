package compilador;

import AST.*;
import Visitor.*;

/**
 *
 * @author edjair
 */
public class Coder implements Visitor {

    int mem = 0;
    int ElseCont = 1;
    int varqtd = 0;
    int ElseAux;
    int WhileCont = 1, WhileAux = 0;
    //private static String message;
    StringBuilder bd = new StringBuilder();

    public String encode(Programa program) {

        System.out.println("\n>>> 3. GERACAO DE CODIGO <<<");
        program.visit(this);
        //bd.append("\n0x").append(mem).append("  ").append("END");
        if (ElseAux != 0)
            bd.append("\n\nELSE").append(ElseAux).append(":\n");
        else
            bd.append("\n\nEND").append(":\n");
        
        bd.append("\n0x").append(mem).append(":\t").append("POP \t").append(varqtd);
        mem++;
        bd.append("\n0x").append(mem).append(":\t").append("HALT");

        System.out.println("  > Geracao de Codigo concluída;");
        System.out.println("  \n-------------------------------\n");

        String codgen = bd.toString();

        return (codgen);

    }

    @Override
    public void visitAtribuicao(Atribuicao becomes) {

        if (becomes.expression != null) {
            becomes.expression.visit(this);
        }

        if (becomes.variable != null) {
            bd.append("\n0x").append(mem).append(":\t").append("STORE \t").append(becomes.variable.id.value);
            mem++;
        }

    }

    @Override
    public void visitBoolLit(BoolLit boolLit) {
        boolLit.type = "boolean";
        bd.append("\n0x").append(mem).append(":\t").append("LOADL \t").append(boolLit.name.value);
        mem++;
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
            bd.append("\n0x").append(mem).append(":\t").append("JUMPIF(0)\t").append("ELSE").append(ElseCont);
            mem++;
            ElseAux = ElseCont;
            ElseCont++;
        }
        
        if (conditional.command instanceof Atribuicao) {
            ((Atribuicao) conditional.command).visit(this);
        } else if (conditional.command instanceof ComandoComposto) {
            ((ComandoComposto) conditional.command).visit(this);
        } else if (conditional.command instanceof Iterativo) {
            ((Iterativo) conditional.command).visit(this);
        } else if (conditional.command instanceof Condicional) {
            ((Condicional) conditional.command).visit(this);
        }
        //bd.append("\n0x").append(mem).append(":\t").append("JUMP\t").append("ELSE").append(ElseCont);
        
        
        //bd.append("\n\nELSE").append(ElseCont-1).append(":\n");

        if (conditional.commandElse instanceof Atribuicao) {
            bd.append("\n0x").append(mem).append(":\t").append("JUMP\t").append("ELSE").append(ElseCont);
            mem++;
            bd.append("\n\nELSE").append(ElseCont-1).append(":\n");
            ElseAux++;
            ((Atribuicao) conditional.commandElse).visit(this);
        } else if (conditional.commandElse instanceof ComandoComposto) {
            bd.append("\n0x").append(mem).append(":\t").append("JUMP\t").append("ELSE").append(ElseCont);
            mem++;
            bd.append("\n\nELSE").append(ElseCont-1).append(":\n");
            ElseAux++;
            ((ComandoComposto) conditional.commandElse).visit(this);
        } else if (conditional.commandElse instanceof Iterativo) {
            bd.append("\n0x").append(mem).append(":\t").append("JUMP\t").append("ELSE").append(ElseCont);
            mem++;
            bd.append("\n\nELSE").append(ElseCont-1).append(":\n");
            ElseAux++;
            ((Iterativo) conditional.commandElse).visit(this);
        } else if (conditional.commandElse instanceof Condicional) {
            bd.append("\n0x").append(mem).append(":\t").append("JUMP\t").append("ELSE").append(ElseCont);
            mem++;
            bd.append("\n\nELSE").append(ElseCont-1).append(":\n");
            ElseAux++;
            ((Condicional) conditional.commandElse).visit(this);
        }
        
    }

    @Override
    public void visitCorpo(Corpo body) {

        if (body.declarations != null) {
            body.declarations.visit(this);
        }
        
        bd.append("\n0x").append(mem).append(":\t").append("PUSH \t").append(varqtd);
        mem++;
        body.compositeCommand.visit(this);
    }

    @Override
    public void visitDeclaracaoDeVariavel(DeclaracaoDeVariavel variableDeclaration) {

        ListaDeIds aux = variableDeclaration.listOfIds;
        while (aux != null) {
            aux = aux.next;
            varqtd++;
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
            expression.simpleExpression.visit(this);
        }

        if (expression.simpleExpressionR != null) {
            expression.simpleExpressionR.visit(this);
        }

        if (expression.operator != null) {
            switch (expression.operator.value) {
                case "<":
                    bd.append("\n0x").append(mem).append(":\t").append("CALL\tlt");
                    mem++;
                    break;
                case ">":
                    bd.append("\n0x").append(mem).append(":\t").append("CALL\tgt");
                    mem++;
                    break;
                case ">=":
                    bd.append("\n0x").append(mem).append(":\t").append("CALL\tgtoe");
                    mem++; 
                    break;
                case "<=":
                    bd.append("\n0x").append(mem).append(":\t").append("CALL\tltoe");
                    mem++; 
                    break; 
                case "=":
                    bd.append("\n0x").append(mem).append(":\t").append("CALL\teq");
                    mem++;
                    break;
                case "<>":
                    bd.append("\n0x").append(mem).append(":\t").append("CALL\tneq");
                    mem++; 
                    break;

            }
        }

    }

    @Override
    public void visitExpressaoSimples(ExpressaoSimples simpleExpression) {
        ExpressaoSimples aux = simpleExpression;
        ExpressaoSimples aux2 = aux;
        String place = null;

        while (aux != null) {
            if (aux.term != null) //Confere se o primeiro termo é vazio
            {
                aux.term.visit(this);
            }
            aux = aux.next;
        }

        while (aux2 != null) {
            if (aux2.operator != null) {
                switch (aux2.operator.kind) {
                    case Token.SUM:
                        bd.append("\n0x").append(mem).append(":\t").append("CALL\tadd");
                        mem++; 
                        break;
                    case Token.SUB:
                        bd.append("\n0x").append(mem).append(":\t").append("CALL\tsub");
                        mem++; 
                        break;
                    case Token.OR:
                        bd.append("\n0x").append(mem).append(":\t").append("CALL\tor");
                        mem++;
                        break;
                }
            }
            aux2 = aux2.next;
        }

    }

    @Override
    public void visitIterativo(Iterativo iterative) {
        
        bd.append("\n0x").append(mem).append(":\t").append("JUMP\t").append("EVL").append(WhileCont);
        mem++;
        WhileAux = WhileCont; WhileCont++;
        bd.append("\n\nWHILE").append(WhileAux).append(":\n");
        
        if (iterative.command instanceof Atribuicao) {
            ((Atribuicao) iterative.command).visit(this);
        } else if (iterative.command instanceof ComandoComposto) {
            
            ((ComandoComposto) iterative.command).visit(this);
        } else if (iterative.command instanceof Iterativo) {
            ((Iterativo) iterative.command).visit(this);
        } else if (iterative.command instanceof Condicional) {
            ((Condicional) iterative.command).visit(this);
        }
        
        bd.append("\n\nEVL").append(WhileAux).append(":\n");
        
        if (iterative.expression != null) {
            iterative.expression.visit(this);
            mem++; 
        }
        
        bd.append("\n0x").append(mem).append(":\t").append("JUMPIF(1)\t").append("WHILE").append(WhileAux);
        mem++;
        
        WhileAux--;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visitLiteral(Literal literal) {
        bd.append("\n0x").append(mem).append(":\t").append("LOADL \t").append(literal.name.value);
        mem++;
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
            aux = aux.next;
        }
    }

    @Override
    public void visitTermo(Termo term) {
        Termo aux = term;

        //Termo recebe uma lista de fatores separados por um operador racional. 
        while (aux != null) {
            if (aux.factor != null) { //Confere o primeiro fator

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
                
                
                if (aux.operator != null) 
                    switch (aux.operator.kind) {
                        case Token.DIV: //Divisão não pode conter operandos booleanos;
                            bd.append("\n0x").append(mem).append(":\t").append("CALL\tdiv");
                            mem++; 
                            break;

                        case Token.MULT:
                            bd.append("\n0x").append(mem).append(":\t").append("CALL\tmult");
                            mem++; 
                            break;

                        case Token.AND:
                            bd.append("\n0x").append(mem).append(":\t").append("CALL\tand");
                            mem++; 
                            break;
                    }
                }
                 aux = aux.next; 
            }
    }

    @Override
    public void visitTipoAgregado(TipoAgregado type) {
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

        bd.append("\n0x").append(mem).append(":\t").append("LOAD \t").append(variable.id.value);
        mem++;

        if (variable.selector != null) {
            variable.selector.visit(this);
        }
    }

}
