/**
 * Representa uma enferamaria de cuidados intensivos (ECI)
 * Requer a gestao do horario de visitas bem como das pressoes atmosfericas atuais e de referencia
 */
public class EnfermariaCuidadosIntensivos extends Enfermaria {

    //VARIAVEIS
    private String horarioVisitas;
    private double pressaoAtmosferica;
    private double pressaoReferencia;

    /**
     * Construtor de uma enfermaria de cuidados intensivos, que se caracteriza pelo seu identificador, horario de visitas e pressoes atmosferica atual e de referencia
     *
     * @param idEnfermaria       -> identificador da enfermaria
     * @param numCamas           -> capacidade total de camas da enfermaria
     * @param horarioVisitas     -> horario de visitas da enfermaria
     * @param pressaoAtmosferica -> pressao atmosferica atual na enfermaria
     * @param pressaoReferencia  -> pressao atmosferica referencia
     */
    public EnfermariaCuidadosIntensivos(String idEnfermaria, int numCamas, String horarioVisitas, double pressaoAtmosferica, double pressaoReferencia) {
        super(idEnfermaria, numCamas);
        this.horarioVisitas = horarioVisitas;
        this.pressaoAtmosferica = pressaoAtmosferica;
        this.pressaoReferencia = pressaoReferencia;
    }

    //Getters e Setters

    /**
     * Devolve o horario de visitas da enfermaria
     *
     * @return horario de visitas
     */
    public String getHorarioVisitas() {
        return horarioVisitas;
    }

    /**
     * Define o horario de visitas da enfermaria
     *
     * @param horarioVisitas -> horario de visitas da enfermaria
     */
    public void setHorarioVisitas(String horarioVisitas) {
        this.horarioVisitas = horarioVisitas;
    }

    /**
     * Devolve a pressao atmosferica atual na enfermaria
     *
     * @return pressao atmosferica na enfermaria
     */
    public double getPressaoAtmosferica() {
        return pressaoAtmosferica;
    }

    /**
     * Define a pressao atmosferica atual da enfermaria
     *
     * @param pressaoAtmosferica -> pressao atmosferica atual da enfermaria
     */
    public void setPressaoAtmosferica(double pressaoAtmosferica) {
        this.pressaoAtmosferica = pressaoAtmosferica;
    }

    /**
     * Devolve a pressao atmosferica de referencia
     *
     * @return pressao atmosferica de referencia
     */
    public double getPressaoReferencia() {
        return pressaoReferencia;
    }

    /**
     * Define a pressao atmosferica de referencia
     *
     * @param pressaoReferencia -> pressao atmosferica de referencia
     */
    public void setPressaoReferencia(double pressaoReferencia) {
        this.pressaoReferencia = pressaoReferencia;
    }

    /**
     * Devolve uma representacao textual formatada com os dados especificos desta tipologia de enfermaria
     *
     * @return representacao textual da enfermaria
     */
    @Override
    public String toString() {
        return String.format("Cuidados Intensivos [%s], Camas: %d, Visitas: %s, Pressão atual: %.2f, Pressão referência: %.2f",
                getIdEnfermaria(), getNumCamas(), this.horarioVisitas, this.pressaoAtmosferica, this.pressaoReferencia);
    }
}
