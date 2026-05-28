/**
 * Representa um episodio de internamento de um doente numa cama especifica
 * Responsavel por registar as datas de admissao e alta, verificar o estado ativo de internamento e calcular o LoS (Length of Stay) em dias
 */
public class Episodio implements java.io.Serializable {

    //VARIAVEIS
    private int idCama;
    private Data dataAdmissao;
    private Data dataAlta;
    private int los; // Length of Stay em dias
    private boolean flagAlta; // true se o episódio já tem alta

    //CONSTRUTOR

    /**
     * Construtor que cria um novo episodio de internamento, validando se a data de alta e de admissao sao coerentes
     *
     * @param idCama       -> identificador da cama do paciente na enfermaria
     * @param dataAdmissao -> data de admissao do paciente na enfermaria
     * @param dataAlta     -> data de alta do paciente da enfermaria
     */
    public Episodio(int idCama, Data dataAdmissao, Data dataAlta) throws DataInvalidaException {
        this.idCama = idCama;
        //Encapsulamento: guardar uma cópia da data
        this.dataAdmissao = new Data(dataAdmissao);
        if (dataAlta != null) {
            if (dataAdmissao.isMaior(dataAlta)) {
                throw new DataInvalidaException("Erro: A data de alta não pode ser anterior à de admissão.");
            } else {
                this.dataAlta = new Data(dataAlta); //cópia da data de alta
                this.flagAlta = true;
            }
        } else {
            this.dataAlta = null;
            this.flagAlta = false;
        }

        calcularLoS();
    }

    /**
     * Calcula o tempo de internamento em dias do paciente. Se ele ainda estiver internado, retorna -1.
     *
     * @return LoS
     */
    public int calcularLoS() {
        if (this.flagAlta == false || this.dataAlta == null) {
            this.los = -1; // não aplicável
            return -1;
        }
        this.los = dataAdmissao.calcularDiferenca(dataAlta);
        return this.los;
    }

    //Getters

    /**
     * Devolve o identificador da  do paciente na enfermaria
     *
     * @return identificador da cama
     */
    public int getIdCama() {
        return idCama;
    }

    /**
     * Devolve a data de admissao do paciente na enfermaria
     *
     * @return data de admissao
     */
    public Data getDataAdmissao() {
        return new Data(dataAdmissao); //devolve cópia defensiva
    }

    /**
     * Devolve a data de alta do paciente da enfermaria
     *
     * @return data de alta
     */
    public Data getDataAlta() {

        if (dataAlta == null) {
            return null;
        } else {
            return new Data(dataAlta); //devolve cópia defensiva
        }
    }

    /**
     * Devolve o tempo de internamento em dias do paciente na enfermaria
     *
     * @return LoS
     */
    public int getLoS() {
        return los;
    }

    /**
     * Se o paciente tiver tido alta devolve true e se ele permanecer internado devolve false
     *
     * @return true (paciente com alta) ou false (paciente ainda internado)
     */
    public boolean isFlagAlta() {
        return flagAlta;
    }

    //Setters

    /**
     * Define o identificador da cama
     *
     * @param idCama -> identificador da cama
     */
    public void setIdCama(int idCama) {
        this.idCama = idCama;
    }

    /**
     * Define a data de admissao do paciente na enfermaria
     *
     * @param d -> data de admissao
     */
    public void setDataAdmissao(Data d) throws DataInvalidaException {
        if (this.dataAlta != null && d.isMaior(this.dataAlta)) {
            throw new DataInvalidaException("Erro: A nova data de admissão não pode ser posterior à data de alta já registada.");
        }
        this.dataAdmissao = new Data(d); //guarda cópia defensiva
        calcularLoS();
    }

    /**
     * Define a data de alta do paciente da enfermaria
     *
     * @param dataAlta -> data de alta
     */
    public void setDataAlta(Data dataAlta) throws DataInvalidaException {
        if (dataAlta != null && dataAdmissao.isMaior(dataAlta)) {
            throw new DataInvalidaException("Erro: A nova data de alta não pode ser anterior à data de admissão já registada.");
        }
        this.dataAlta = new Data(dataAlta); // guarda cópia defensiva
        this.flagAlta = (dataAlta != null);
        calcularLoS(); //recalcular
    }

    /**
     * Verifica se o episodio esta ativo na data de referencia (paciente admitido e sem alta ou com alta posterior a data de referencia)
     *
     * @param dataReferencia -> data de referencia para verificar o estado do episodio
     * @return -> true (episodio ativo) ou false (episodio nao ativo)
     */
    public boolean isAtivo(Data dataReferencia) {
        // Episódio ativo se foi admitido até à data de referência
        // e ainda não teve alta (ou a alta é após a data de referência)
        boolean admitido = !dataAdmissao.isMaior(dataReferencia);
        boolean semAlta = !flagAlta || dataAlta.isMaior(dataReferencia);
        return admitido && semAlta;
    }

    /**
     * Devolve uma representacao textual formatada do episodio, incluindo o LoS para os doentes com alta e N/A para os doentes sem alta
     *
     * @return representacao textual do episodio
     */
    @Override
    public String toString() {

        String textoDataAlta;
        String textoLos;

        if (dataAlta == null) {
            textoDataAlta = "—"; // (Ainda internado)
            textoLos = "N/A";
        } else {
            textoDataAlta = dataAlta.toString();
            textoLos = los + " dias";
        }

        return String.format("ID: %d, Data Admissão: %s, Data Alta: %s, LoS: %s",
                idCama, dataAdmissao, textoDataAlta, textoLos);
    }
}
