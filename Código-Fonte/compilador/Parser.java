package compilador;

import AST.Atribuicao;
import AST.BoolLit;
import AST.ComandoComposto;
import AST.Corpo;
import AST.Comando;
import AST.Condicional;
import AST.DeclaracaoDeVariavel;
import AST.Declaracoes;
import AST.Variavel;
import AST.Expressao;
import AST.ExpressaoSimples;
import AST.Fator;
import AST.Iterativo;
import AST.ListaDeComandos;
import AST.ListaDeIds;
import AST.Literal;
import AST.Programa;
import AST.Seletor;
import AST.Termo;
import AST.Tipo;
import AST.TipoAgregado;
import AST.TipoSimples;

/**
 *
 * @author edjair
 */
public class Parser {
    
    private Token currentToken;
    private Token lastToken;
    private Scanner scanner;
    public int erroS = 0;
    
        public Parser(){

        }
    
        // MÉTODOS DA ANÁLISE SINTÁTICA
        public Programa parse(String fileName) throws Exception{
            Programa program;
            scanner = new Scanner(fileName);
            currentToken = this.scanner.scan();
            System.out.println("\n>>> 1. ANALISE SINTATICA <<<");
            program = parsePrograma();
            
            /** if (erroS !=  0){
                Erros.error(1, erroS);
                System.exit(1);
            } 
            else 
            { **/
                System.out.println("  > Análise Sintática concluída;\n  > Total de Erros Sintáticos: 0");
            //}  
            
            return program;
        }
    
        private void accept (byte expectedKind) throws Exception{
		if (currentToken.kind == expectedKind){
                        lastToken = currentToken;
			currentToken = scanner.scan();
                }
                else {
			// erro sintatico, esperava 'expectedKind'
                        Erros.error(11, erroS+1);
                        System.out.println("     | Esperava encontrar: '"+ Token.SPELLINGS[expectedKind] + "'");
                        System.out.println("     | Na linha: " + lastToken.line); 
                        System.out.println("     | Coluna: " + lastToken.col);
                        erroS++;
                        Erros.error(1, erroS);
                }
	}

	private void acceptIt () throws Exception{
		currentToken = scanner.scan();
	}
        
        
        // MÉTODOS PARSE
        private Atribuicao parseAtribuicao() throws Exception{
            //Atribuicao == <Variavel> := <Expressao>
            Atribuicao becomes = new Atribuicao();
            becomes.variable = parseVariavel();
            accept(Token.BECOMES);
            becomes.expression = parseExpressao();
            return becomes;
        }
        
        private BoolLit parseBoolLit() throws Exception{
            //Bool-Lit == True | False
            BoolLit logic = new BoolLit();
            switch(currentToken.kind){
                case Token.TRUE:
                case Token.FALSE:
                    logic.name = currentToken;
                    acceptIt();
                break;
                default:
                    Erros.error(12, erroS+1);
                    System.out.println("     | Na linha: " + currentToken.line); 
                    System.out.println("     | Coluna: " + currentToken.col);
                    erroS++;
                    Erros.error(1, erroS);
            }
            return logic;
        }
        
        private Comando parseComando() throws Exception{
            //Comando == <Atribuiçao> | <Condicional> | <Iterativo> | <Comando-Composto>
            Comando command;
            switch(currentToken.kind){
                    case Token.ID: //<Atribuição>
                        command = parseAtribuicao();
                    break;
                        
                    case Token.IF:  //<Condicional>
                        command = parseCondicional();
                    break;
                        
                    case Token.WHILE: //<Iterativo>
                        command = parseIterativo();
                    break;
                        
                    case Token.BEGIN: //<Comando-Composto>
                        command = parseComandoComposto();
                    break;
                        
                    default:
                        command = null;
                        Erros.error(13, erroS+1);
                        System.out.println("     | Na linha: " + currentToken.line); 
                        System.out.println("     | Coluna: " + currentToken.col);
                        erroS++;
                        Erros.error(1, erroS);
                        
		}
                return command;
        }
        
        private ComandoComposto parseComandoComposto() throws Exception {
		//begin <Lista-de-Comandos> end
                ComandoComposto compositeCommand = new ComandoComposto();
                accept(Token.BEGIN);
                compositeCommand.listOfCommands = parseListaDeComandos();
                accept(Token.END);

                return compositeCommand;
	}
        
        private Condicional parseCondicional() throws Exception{
            //Condicional == if <Expressão> then <Comando> (else <Comando> | <Vazio>)
            Condicional conditional = new Condicional();
            accept(Token.IF);
            conditional.expression = parseExpressao();
            accept(Token.THEN);
            conditional.command = parseComando();
            if(currentToken.kind == Token.ELSE){
                acceptIt();
                conditional.commandElse = parseComando();
                //commandElse: tratamento de caso para ELSE; tratado na AST para evitar else sem nada após
            } else{
                conditional.commandElse = null;
            }
            return conditional;
        }
        
        private Corpo parseCorpo() throws Exception {
		//Corpo == <Declarações> <Comando-Composto>
                Corpo body = new Corpo();
                body.declarations = parseDeclaracoes();
                body.compositeCommand = parseComandoComposto();
                return body;
	}
        
        private DeclaracaoDeVariavel parseDeclaracaoDeVariavel() throws Exception{
            //Declaracao-De-Variavel == var <Lista-de-IDs>: <Tipo>
            DeclaracaoDeVariavel variableDeclaration = new DeclaracaoDeVariavel();
            accept(Token.VAR);
            variableDeclaration.listOfIds = parseListaDeIds();
            accept(Token.COLON);
            variableDeclaration.type = parseTipo();

            return variableDeclaration;
        }
        
        private Declaracoes parseDeclaracoes() throws Exception{
            //Declaracoes == (<Declaracao-De-Variavel>;)* 
            Declaracoes declarations = null;
            while(currentToken.kind == Token.VAR){
                Declaracoes aux = new Declaracoes();
                aux.declarationOfVariable = parseDeclaracaoDeVariavel();
                aux.next = null;
                accept(Token.SEMICOLON);

                if( declarations == null){
                    declarations = aux;
                } else {
                    Declaracoes aux2 = declarations;
                    while(aux2.next != null){
                        aux2 = aux2.next;
                    }
                    aux2.next = aux;
                }
            }
            return declarations;
        }
        
        private Expressao parseExpressao() throws Exception {
		// Expressão == <Expresso-Simples> | <Expresso-Simples> <Op-Rel> <Expresso-Simples>
		// Sendo: <Op-Rel> == < | > | <= | >= | <> | =
                Expressao expression = new Expressao();
		expression.simpleExpression = parseExpressaoSimples();
		if(currentToken.kind == Token.GREATER || currentToken.kind == Token.LESS || currentToken.kind == Token.LESS_EQUAL
				|| currentToken.kind == Token.GREATER_EQUAL || currentToken.kind == Token.DIFF || currentToken.kind == Token.EQUAL){
			expression.operator = currentToken;
                        acceptIt();
			expression.simpleExpressionR = parseExpressaoSimples();
		} else{
                    expression.simpleExpressionR = null;
                    expression.operator = null;
                }
                return expression;
	}
        
        private ExpressaoSimples parseExpressaoSimples() throws Exception {
                //Equivale a: == <Termo> (<Op-Ad><Termo>)* 
                //Sendo <Op-Ad> == + | - | or
            
                ExpressaoSimples simpleExpression = new ExpressaoSimples();
		simpleExpression.term = parseTermo();
                simpleExpression.operator = null;
                simpleExpression.next = null;

		while(currentToken.kind == Token.SUM || currentToken.kind == Token.SUB || currentToken.kind == Token.OR){
			ExpressaoSimples aux = new ExpressaoSimples(); //Instância auxiliar para pôr na árvore
                        aux.operator = currentToken;
                        acceptIt();

			aux.term = parseTermo();
                        aux.next = null;


                        if(simpleExpression.next == null){
                            simpleExpression.next = aux;
                        } else{
                            ExpressaoSimples aux2 = simpleExpression;
                            while(aux2.next != null){
                                aux2 = aux2.next;
                            }
                            aux2.next = aux;
                        }
		}

                return simpleExpression;

	}
        
        private Fator parseFator() throws Exception {
		//Fator == <Variável> | <Literal> | "(" <Expressão> ")"
                Fator factor;
                switch(currentToken.kind){
                    case Token.ID:
                        factor = parseVariavel();
                    break;
                    case Token.TRUE:
                    case Token.FALSE:
                    case Token.INT_LIT:
                    case Token.FLOAT_LIT:
                        factor = parseLiteral();
                    break;
                    case Token.LPAREN:
                        acceptIt();
                        factor = parseExpressao();
                        accept(Token.RPAREN);
                    break;
                    default:
                        factor = null;
                        Erros.error(14, erroS+1);
                        System.out.println("     | Na linha: " + currentToken.line); 
                        System.out.println("     | Coluna: " + currentToken.col);
                        erroS++;
                        Erros.error(1, erroS);
                }
                return factor;
	}
        
        private Iterativo parseIterativo() throws Exception{
            //Iterativo == while <Expressão> do <Comando>
            Iterativo iterative = new Iterativo();
            accept(Token.WHILE);
            iterative.expression = parseExpressao();
            accept(Token.DO);
            iterative.command = parseComando();

            return iterative;
        }
        
        private ListaDeComandos parseListaDeComandos() throws Exception {
		// Lista-de-Comandos> == (<Comando>;)*
                ListaDeComandos listOfCommands = null;
                
		while(currentToken.kind==Token.ID || currentToken.kind==Token.IF || currentToken.kind==Token.WHILE || currentToken.kind==Token.BEGIN){
                    ListaDeComandos aux = new ListaDeComandos();
                    aux.command = parseComando();
                    aux.next = null;
                    accept(Token.SEMICOLON);

                    if(listOfCommands == null){
                        listOfCommands = aux;
                    } else {
                        ListaDeComandos aux2 = listOfCommands;
                        while(aux2.next != null){
                            aux2 = aux2.next;
                        }
                        aux2.next = aux;
                    }
                }
                return listOfCommands;
	}
        
        private ListaDeIds parseListaDeIds() throws Exception {
		//Lista-De-Ids> == <Id>	(, <Id>)*
                ListaDeIds listOfIds = new ListaDeIds();
                listOfIds.id = currentToken;
                listOfIds.next = null;
                accept(Token.ID);
                
		while(currentToken.kind == Token.COMMA){
                        acceptIt();
                        ListaDeIds aux = new ListaDeIds();
			aux.id = currentToken;
                        aux.next = null;
			accept(Token.ID);

                        ListaDeIds aux2;
                        if(listOfIds.next == null){
                            listOfIds.next = aux;
                        } else{
                            aux2 = listOfIds;
                            while(aux2.next != null){
                                aux2 = aux2.next;
                            }
                            aux2.next = aux;
                        }
		}

                return listOfIds;
	}
        
        private Literal parseLiteral() throws Exception {
		//Literal == <Bool-Lit> | <Int-Lit> | <Float-Lit>
                
                Literal literal = new Literal();
                switch(currentToken.kind){
                    case Token.TRUE:
                    case Token.FALSE:
                        literal = parseBoolLit();
                    break;
                    case Token.INT_LIT:
                        literal.name = currentToken;
                        acceptIt();
                    break;
                    case Token.FLOAT_LIT:
                        literal.name = currentToken;
                        acceptIt();
                    break;
                    default:
                        literal = null;
                        Erros.error(15, erroS+1);
                        System.out.println("     | Na linha: " + currentToken.line); 
                        System.out.println("     | Coluna: " + currentToken.col);
                        erroS++;
                        Erros.error(1, erroS);
                }
                return literal;
	}
        
        private Programa parsePrograma() throws Exception {
		//Programa == program <Id> ; <Corpo> . [EOF]
            
                Programa program = new Programa();
                accept(Token.PROGRAM);
                accept(Token.ID);
                accept(Token.SEMICOLON);
                program.body = parseCorpo();
                accept(Token.DOT);
                accept(Token.EOF);
                return program;
	}
        
        private Seletor parseSeletor() throws Exception {
		// Seletor == ("["<Expressão>"]")*
                Seletor selector = null;
		while(currentToken.kind == Token.LBRACKET) {
			acceptIt();
                        Seletor aux = new Seletor();
			aux.expression = parseExpressao();
                        aux.next = null;
			accept(Token.RBRACKET);

                        if(selector == null){
                            selector = aux;
                        } else {
                            Seletor aux2 = selector;
                            while(aux2.next != null){
                                aux2 = aux2.next;
                            }
                            aux2.next = aux;
                        }
		}
                return selector;
	}
        
        private Termo parseTermo() throws Exception {
		// Termo == <Fator> (<Op-Mul><Fator>)*
                Termo term = new Termo();
		term.factor = parseFator();
                term.operator = null;
                term.next = null;
                
		while(currentToken.kind == Token.MULT || currentToken.kind == Token.DIV || currentToken.kind == Token.AND) {
			Termo aux = new Termo();
                        aux.operator = currentToken;
                        acceptIt();

			aux.factor = parseFator();
                        aux.next = null;

                        if(term.next == null){
                            term.next = aux;
                        } else{
                            Termo aux2 = term;
                            while(aux2.next != null){
                                aux2 = aux2.next;
                            }
                            aux2.next = aux;
                        }
		}
                return term;
	}
        
        private Tipo parseTipo() throws Exception {
            //Tipo == integer | real | boolean | array[<Literal>~<Literal>] of <Tipo>
                Tipo typex;
		switch(currentToken.kind){
                    case Token.ARRAY: {
			// TipoAgregado == array [ <Literal>~<Literal> ] of <Tipo>
                            TipoAgregado type = new TipoAgregado();
				acceptIt();
				accept(Token.LBRACKET);
				type.literal1 = parseLiteral();
				accept(Token.TILDE); //Acento til separa Literals de Arrays 
				type.literal2 = parseLiteral();
				accept(Token.RBRACKET);
				accept(Token.OF);
				type.typo = parseTipo();
                                typex = type;
			}
			break;
                    case Token.INTEGER:
                    case Token.REAL:
                    case Token.BOOLEAN: {
			// TipoSimples == integer | real | boolean
                                TipoSimples type = new TipoSimples();
                                type.typo = currentToken;
				acceptIt();
                                typex = type;
			}
			break;
                    default:
                        typex = null;
                        Erros.error(16, erroS+1);
                        System.out.println("     | Na linha: " + currentToken.line); 
                        System.out.println("     | Coluna: " + currentToken.col);
                        erroS++;
                        Erros.error(1, erroS);
		}
                return typex;
	}
        
        private Variavel parseVariavel() throws Exception {
		//Varivel == <Id> <Seletor>
                Variavel variable = new Variavel();
                variable.id = currentToken;
                accept(Token.ID);
		variable.selector = parseSeletor();

                return variable;
	}
}

