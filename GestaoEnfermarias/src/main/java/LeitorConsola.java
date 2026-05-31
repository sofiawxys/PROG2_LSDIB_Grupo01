import java.util.Scanner;

public class LeitorConsola {
    private Scanner scanner;

    public LeitorConsola() {
        this.scanner = new Scanner(System.in);
    }

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

    public String lerString(String mensagem) {
        boolean valido = false;
        String texto = "";

        while (valido == false) {
            System.out.println(mensagem);
            texto = this.scanner.nextLine().trim();

            if(!texto.isEmpty()){
                valido = true;
            }else{
                System.out.println("Erro: O campo não pode ficar vazio");
            }
        }
        return texto;
    }
}



