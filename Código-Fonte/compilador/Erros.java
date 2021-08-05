package compilador;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author edjair
 */
public class Erros {

    private static String message;

    public static void error(int protocol, int erroN) throws IOException {
        switch (protocol) {
            // ERROS SINTÁTICOS:
            case 1:

                message = String.format("Compilação derrogada durante a ANÁLISE SINTATICA. \nQuantidade de erros encontrada:  " + erroN + 
                        "\n\nOs erros de compilação são mostrados no terminal.");
                JOptionPane.showMessageDialog(null, message);
                System.out.println("\nPressione ENTER no prompt para encerrar a compilacao.");
                System.in.read();
                System.exit(1);
                break;

            case 11:
                System.out.println(" \n[!][" + erroN + "] ERRO SINTÁTICO (Erro 1.1)");
                System.out.println("     Token inesperado.");
                break;

            case 12:
                System.out.println(" \n[!][" + erroN + "] ERRO SINTATICO (Erro 1.2)");
                System.out.println("     Booleano invalido.");
                System.out.println("     > Atribuição Lógica deve conter 'true' ou 'false'");
                break;

            case 13:
                System.out.println(" \n[!][" + erroN + "] ERRO SINTATICO (Erro 1.3)");
                System.out.println("     Comando inválido.");
                System.out.println("     > Comando deve ser do tipo ATRIBUIÇAO (ID), "
                        + "CONDICIONAL (IF), ITERATIVO (WHILE) ou COMANDO COMPOSTO (BEGIN)");
                break;

            case 14:
                System.out.println(" \n[!][" + erroN + "] ERRO SINTATICO (Erro 1.4)");
                System.out.println("     Fator inválido.");
                System.out.println("     > Fator espera receber ATRIBUIÇAO (ID), "
                        + "LITERAL ou EXPRESSAO ENTRE PARENTESES");
                break;

            case 15:
                System.out.println(" \n[!][" + erroN + "] ERRO SINTATICO (Erro 1.5)");
                System.out.println("     Literal inválido.");
                System.out.println("     > Literal deve ser do tipo BOOLEANO ('true' ou 'false'), "
                        + "INTEIRO ou FLOAT");
                break;

            case 16:
                System.out.println(" \n[!][" + erroN + "] ERRO SINTATICO (Erro 1.6)");
                System.out.println("     Tipo inválido.");
                System.out.println("     > Tipo deve ser TIPO AGREGADO (array) "
                        + "ou TIPO SIMPLES (inteiro, real ou booleano)");
                break;

            //ERROS DE CONTEXTO:    
            case 2:

                message = String.format("Compilação derrogada durante a ANÁLISE DE CONTEXTO. \nQuantidade de erros encontrada:  " + erroN + 
                        "\n\nOs erros de compilação são mostrados no terminal.");
                JOptionPane.showMessageDialog(null, message);
                System.out.println("\nPressione ENTER para encerrar a compilacao.");
                System.in.read();
                System.exit(1);
                break;

            case 211:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.1.1)");
                break;

            case 212:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.1.2)");
                break;

            case 221:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.2.1)");
                System.out.println("     Atribuicao de valores incompatíveis.");
                break;

            case 222:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.2.2)");
                System.out.println("     Esperava-se expressao booleana.");
                break;

            case 223:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.2.3)");
                System.out.println("     Comparacao entre valores incompativeis.");
                break;

            case 224:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.2.4)");
                System.out.println("     Operandos inválidos.");
                break;

            case 225:
                System.out.println(" \n[!][" + erroN + "] ERRO DE CONTEXTO (Erro 2.2.5)");
                System.out.println("     Seletor inválido.");
                break;

        }

    }
;

}
