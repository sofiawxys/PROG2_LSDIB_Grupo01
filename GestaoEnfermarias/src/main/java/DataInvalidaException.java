/**
 * Exceção lançada quando as datas inseridas são cronologicamente incoerentes ou possuem formato inválido.
 */
public class DataInvalidaException extends Exception {

    public DataInvalidaException(String mensagem) {
        super(mensagem);
    }

}
