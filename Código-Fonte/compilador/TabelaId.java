package compilador;

import AST.DeclaracaoDeVariavel;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author edjair
 */
public class TabelaId {

    public int erroV = 0;
    HashMap table;

    TabelaId() {
        table = new HashMap();
    }

    public void enter(Token id, DeclaracaoDeVariavel declaration) throws IOException {
        if (table.put(id.value, declaration) != null) {
            Erros.error(211, erroV + 1);
            System.out.println("     Identificador " + id.value + " ja declarado.");
            System.out.println("     | Na linha:" + id.line);
            //System.out.println("     | Coluna:" + id.col);
            erroV++;
            Erros.error(2, erroV);
        }
    }

    public DeclaracaoDeVariavel retrieve(Token id) throws IOException {
        if (table.containsKey(id.value) == false) {
            Erros.error(212, erroV + 1);
            System.out.println("     Identificador '" + id.value + "' nao declarado.");
            System.out.println("     | Na linha:" + id.line);
            //System.out.println("     | Coluna:" + id.col);
            erroV++;
            Erros.error(2, erroV);
        } else {
            return (DeclaracaoDeVariavel) table.get(id.value);
        }
        return null;
    }

    public String print() {

        StringBuilder bd = new StringBuilder();

        //System.out.println(">>> Imprimindo Tabela de Identificadores <<<");
        bd.append("\t\n - IMPRESSÃO: TABELA DE IDENTIFICADORES\n");
        //System.out.println("\t-----------------------------------------------------------------");
        bd.append("\n--------------------------------------------------------------------------------------------------");
        //System.out.println("\t|\t" + "VAR" + "\t|\t" + "\t\tADDR\t\t" + "\t|\t");
        bd.append("\n " + "VAR" + "\t|\t" + "\tADDR");
        bd.append("\n--------------------------------------------------------------------------------------------------");
        bd.append("\n--------------------------------------------------------------------------------------------------");
        //System.out.println("\t-----------------------------------------------------------------");
        //System.out.println("\t-----------------------------------------------------------------");

        table.keySet().forEach((name) -> {
            String key = name.toString();
            String value = table.get(name).toString();

            //System.out.println("\t|\t" +key + "\t|\t" + value + "\t|\t");
            bd.append("\n ").append(key).append(" \t|\t").append(value);
            //System.out.println("\t-----------------------------------------------------------------");
            bd.append("\n--------------------------------------------------------------------------------------------------");
        });
        //System.out.println("\t-----------------------------------------------------------------");

        String tableprint = bd.toString();
        return (tableprint);
    }

}
