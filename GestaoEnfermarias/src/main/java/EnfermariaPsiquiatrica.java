/**
 * Subclasse da Classe Enfermaria para gestao de uma unidade de internamento direcionada para cuidados de saude mental
 * Como tal definem-se atributos de controlo como o horario de visitas e o nivel de seguranca da instalacao
 */
public class EnfermariaPsiquiatrica extends Enfermaria {

    //VARIAVEIS
    private String horarioVisitas;
    private String nivelSeguranca;

    //CONSTRUTOR

    /**
     * Construtor que define uma nova Enfermaria Psiquiatrica que se catacteriza por um identificador, uma capacidade total de camas, um horario de viistas e um nivel de seguranca
     *
     * @param idEnfermaria   -> identificador da enfermaria
     * @param numCamas       -> capacidade total de camas
     * @param horarioVisitas -> horario especifico para as visitas na enfermaria
     * @param nivelSeguranca -> nivel de seguranca da instalacao
     */
    public EnfermariaPsiquiatrica(String idEnfermaria, int numCamas, String horarioVisitas, String nivelSeguranca) {
        super(idEnfermaria, numCamas);
        this.horarioVisitas = horarioVisitas;
        this.nivelSeguranca = nivelSeguranca;
    }

    //Getters e Setters

    /**
     * Devolve o horario destinado a visistas da enfermaria
     *
     * @return horario de visistas
     */
    public String getHorarioVisitas() {
        return horarioVisitas;
    }

    /**
     * Define o horario destinado a visitas na enfermaria
     *
     * @param horarioVisitas -> horario destinado a visitas na enfermaria
     */
    public void setHorarioVisitas(String horarioVisitas) {
        this.horarioVisitas = horarioVisitas;
    }

    /**
     * Devolve o nivel de seguranca da enfermaria
     *
     * @return nivel de seguranca da enfermaria
     */
    public String getNivelSeguranca() {
        return nivelSeguranca;
    }

    /**
     * Define o nivel de seguranca da enfermaria
     *
     * @param nivelSeguranca -> nivel de seguranca da enfermaria
     */
    public void setNivelSeguranca(String nivelSeguranca) {
        this.nivelSeguranca = nivelSeguranca;
    }

    /**
     * Devolve uma representacao textual formatada com os dados especificos desta tipologia de enfermaria
     *
     * @return representacao textual dos dados da enfermaria
     */
    @Override
    public String toString() {
        return String.format("Enfermaria Psiquiátrica [%s], Camas: %d, Visitas: %s, Segurança: %s",
                getIdEnfermaria(), getNumCamas(), this.horarioVisitas, this.nivelSeguranca);
    }
}
