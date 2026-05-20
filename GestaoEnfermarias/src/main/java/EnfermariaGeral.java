
import java.util.List;
import java.util.ArrayList;

/**
 * Representa uma enfermaria generica
 * Estende as funcionalidades base da superclasse Enfermaria, adicionado o controlo do numero de acompanhantes permitido e a gestao do inventario de recursos
 */
public class EnfermariaGeral extends Enfermaria {

    //VARIAVEIS
    private int numAcompanhantes;
    private List<String> recursos;

    /**
     * Construtor de uma enfermaria geral que se caracteriza pelo seu identificador, numero de camas e numero de acompanhantes permitidos
     *
     * @param idEnfermaria     -> identificador da enfermaria
     * @param numCamas         -> capacidade total de camas
     * @param numAcompanhantes -> numero maximo de acompanhantes permitido
     */
    public EnfermariaGeral(String idEnfermaria, int numCamas, int numAcompanhantes) {
        super(idEnfermaria, numCamas);
        this.numAcompanhantes = numAcompanhantes;
        this.recursos = new ArrayList<String>(); //iniciar a lista como lista vazia
    }

    //Getters e Setters

    /**
     * Devolve o numero maximo de acompanhantes permitidos numa enfermaria
     *
     * @return numero de acompanhantes permitido
     */
    public int getNumAcompanhantes() {
        return numAcompanhantes;
    }

    /**
     * Define o numero maximo de acompanhantes permitidos numa enfermaria
     *
     * @param numAcompanhantes -> numero de acompanhantes permitido
     */
    public void setNumAcompanhantes(int numAcompanhantes) {
        this.numAcompanhantes = numAcompanhantes;
    }

    /**
     * Devolve uma copia da lista de recursos da enfermaria
     *
     * @return copia da lista de recursos da enfermaria
     */
    public List<String> getRecursos() {
        return new ArrayList<>(recursos); // devolve uma cópia defensiva
    }

    /* Nao criamos setRecursos e assim a lista é gerida pelos seguintes métodos. Isto protege a integridade dos dados, esta classe tem controlo total sobre o que entra e sai da lista
    Impedindo que a lista inteira seja acidentalmente substituida ou apagada do exterior. */

    /**
     * Permite adicionar um recurso a lista de recursos de uma enfermaria
     *
     * @param recurso -> recurso a adicionar
     */
    public void adicionarRecurso(String recurso) {
        this.recursos.add(recurso);
    }

    /**
     * Permite remover um recurso a lista de recursos de uma enfermaria
     *
     * @param recurso -> recurso a remover
     */
    public void removerRecurso(String recurso) {
        this.recursos.remove(recurso);
    }

    /**
     * Devolve uma representacao textual formatada com os dados especificos desta tipologia de enfermaria
     *
     * @return representacao textual da enfermaria
     */
    @Override
    public String toString() {
        return String.format("Enfermaria Geral [%s] | Camas: %d | Acompanhantes: %d | Recursos: %s", getIdEnfermaria(), // vem da classe mãe
                getNumCamas(),   // vem da classe mãe
                this.numAcompanhantes, this.recursos.toString());
    }
}
