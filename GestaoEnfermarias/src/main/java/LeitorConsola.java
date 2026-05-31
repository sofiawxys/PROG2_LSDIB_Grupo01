import java.util.Scanner;

/**
 * Classe utilitária responsável por centralizar e validar a leitura de dados inseridos
 * pelo utilizador através da consola.
 */
public class LeitorConsola {
    private Scanner scanner;

    /**
     * Constutor da classe LeitorConsola.
     * Cria um scanner.
     */
    public LeitorConsola() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Lê um valor numérico inteiro da consola, repetindo o pedido em caso de input inválido.
     *
     * @param mensagem o texto a ser apresentado ao utilizador
     * @return o valor inteiro validado
     */
    public int lerInteiro(String mensagem) {
        boolean valido = false;
        int numFinal = 0;
        while (valido == false) {
            try {
                System.out.println(mensagem);
                numFinal = Integer.parseInt(this.scanner.nextLine().trim());
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Deve introduzir um número inteiro.");
            }
        }
        return numFinal;
    }

    /**
     * Lê um valor numérico double da consola, repetindo o pedido em caso de input inválido.
     *
     * @param mensagem o texto a ser apresentado ao utilizador
     * @return o valor double validado
     */
    public double lerDouble(String mensagem) {
        boolean valido = false;
        double numFinal = 0.0;
        while (valido == false) {
            try {
                System.out.println(mensagem);
                numFinal = Double.parseDouble(this.scanner.nextLine().trim());
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Deve introduzir um número decimal válido");
            }
        }
        return numFinal;
    }

    /**
     * Lê uma string da consola, repetindo o pedido em caso de esta estar vazia.
     *
     * @param mensagem o texto a ser apresentado ao utilizador
     * @return string não vazia
     */
    public String lerString(String mensagem) {
        boolean valido = false;
        String texto = "";

        while (valido == false) {
            System.out.println(mensagem);
            texto = this.scanner.nextLine().trim();

            if (!texto.isEmpty()) {
                valido = true;
            } else {
                System.out.println("Erro: O campo não pode ficar vazio");
            }
        }
        return texto;
    }
}



