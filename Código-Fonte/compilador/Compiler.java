package compilador;

/**
 *
 * @author edjair
 */
public class Compiler {

    private static String title;

    //public static void ArquivoSelecionado(File afonte) {
    //        String pas = afonte.getAbsolutePath();
    //        testes = pas;
    //} 
    public static void main(String args[]) throws Exception {

        //Erros.error(16);
        //Programa program;
        //String testes = "src/programa.pas";
        ///Parser parser = new Parser();
        ///Printer printer = new Printer();
        ///Checker checker = new Checker();
        //Coder coder = new Coder();
        telaSelecao myFrame = new telaSelecao();
        myFrame.setVisible(true);

        // program = parser.parse(testes); 
        //checker.check (program);
        //int resposta = JOptionPane.showConfirmDialog(null, "Deseja exibir a AST?", title, JOptionPane.YES_NO_OPTION);
        //if (resposta == JOptionPane.YES_OPTION) {
        //  printer.print(program);
        //} else if (resposta == JOptionPane.NO_OPTION) {}
        //////////////////////////////////
        //printer.print(program);
        //checker.table.print();
        ////coder.code(profram);
        //System.out.println("\nMARCUS RAMOS EU TE AMO");
    }
}
