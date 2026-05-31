/**
 * Exceção lançada quando se tenta associar um episódio a uma cama que já se encontra ocupada num determinado período.
 */
public class CamaOcupadaException extends Exception {
    public CamaOcupadaException(String mensagem) {
        super(mensagem);
    }
}