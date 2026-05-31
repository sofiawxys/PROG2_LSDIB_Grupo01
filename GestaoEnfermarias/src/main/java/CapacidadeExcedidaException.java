/**
 * Exceção lançada quando a lotação máxima da enfermaria é ultrapassada numa data específica.
 */
public class CapacidadeExcedidaException extends Exception {
    public CapacidadeExcedidaException(String mensagem) {
        super(mensagem);
    }
}
