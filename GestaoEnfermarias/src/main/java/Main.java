/**
 * Classe principal do program.
 * Atua exclusivamente como o ponto de entrada da aplicação, delegando toda a lógica
 * de apresentação, controlo e execução para a classe Menu.
 */
public class Main {
    /**
     * Metodo principal que inicia a execução do sistema hospitalar.
     * Instancia o Menu principal e arranca o ciclo de vida da aplicação.
     *
     * @param args
     */
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.iniciar();
    }
}