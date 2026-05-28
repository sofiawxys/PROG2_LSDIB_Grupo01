import java.util.List;
import java.util.ArrayList;

/**
 * Classe abstrata que serve de base para as enfermarias de diversos tipos
 * Centraliza a gestao da capacidade camas, o historico de episodios e o calculo de metricas de ocupacao e LoS
 */
public abstract class Enfermaria implements GestaoOcupacao, java.io.Serializable {

    //CONSTANTE
    private static final int LIMITE_PRESSAO = 85; //mudar para double

    //VARIÁVEIS
    private String idEnfermaria;
    private int numCamas;
    private List<Episodio> episodios;

    //CONSTRUTOR

    /**
     * Construtor que inicializa uma nova enfermaria com o respetivo identificador, capacidade maxima de camas e uma lista vazia de episodios
     *
     * @param idEnfermaria -> identificador da enfermaria
     * @param numCamas     -> capacidade maxima de camas da enfermaria
     */
    public Enfermaria(String idEnfermaria, int numCamas) {
        this.idEnfermaria = idEnfermaria;
        this.numCamas = numCamas;
        this.episodios = new ArrayList<>();
    }

    //METODOS
    //GETTERS E SETTERS

    /**
     * Devolve o identificador da enfermaria
     *
     * @return identificador da enfermaria
     */
    public String getIdEnfermaria() {
        return idEnfermaria;
    }

    /**
     * Define o identificador da enfermaria
     *
     * @param idEnfermaria -> identificador da enfermaria
     */
    public void setIdEnfermaria(String idEnfermaria) {
        this.idEnfermaria = idEnfermaria;
    }

    /**
     * Devolve o numero de camas
     *
     * @return numero de camas
     */
    public int getNumCamas() {
        return numCamas;
    }

    /**
     * Define o numero de camas
     *
     * @param numCamas -> capacidade maxima de camas
     */
    public void setNumCamas(int numCamas) {
        this.numCamas = numCamas;
    }

    /**
     * Devolve a lista de episodios
     *
     * @return lista de episodios
     */
    public List<Episodio> getEpisodios() {
        return new ArrayList<>(episodios); // devolve uma cópia defensiva
    }


    /*Não colocamos um setEpisodios propositadamente por segurança. Isto protege a integridade dos dados, esta classe tem controlo total sobre o que entra e sai da lista
    Impedindo que a lista inteira seja acidentalmente substituida ou apagada do exterior. */

    //OUTROS METODOS

    /**
     * Regista um novo episodio de internamento no historico desta enfermaria
     *
     * @param episodio -> episodio a adicionar ao historico da enfermaria
     */
    public void adicionarEpisodio(Episodio episodio) throws CapacidadeExcedidaException{
       int camasOcupadasNaData = this.calcularOcupacao(episodio.getDataAdmissao());
       if (camasOcupadasNaData >= this.numCamas) {
           throw new CapacidadeExcedidaException("Erro: A enfermaria " + this.idEnfermaria + " já tem " + this.numCamas + " camas ocupadas na data,o que corresponde à sua capacidade máxima.");
       }
       this.episodios.add(episodio);
    }

    /**
     * Calcula o numero total de camas ocupadas na data de referencia
     *
     * @param dataReferencia -> data de referencia
     * @return o numero de camas ocupadas
     */
    @Override
    public int calcularOcupacao(Data dataReferencia) {
        int ocupacao = 0;
        for (Episodio episodio : episodios) {
            if (episodio.isAtivo(dataReferencia)) {
                ocupacao++;
            }
        }
        return ocupacao;
    }

    /**
     * Calcula a percentagem de ocupacao face a capacidade total de camas
     *
     * @param dataReferencia -> data de referencia
     * @return percentagem de ocupacao das camas
     */
    @Override
    public double calcularTaxaOcupacao(Data dataReferencia) {
        if (numCamas == 0) {
            return 0;
        } else {
            double taxaOcupacao = (calcularOcupacao(dataReferencia) * 100.0) / numCamas;
            return taxaOcupacao;
        }
    }

    /**
     * Verifica se a enfermaria se encontra em situacao de pressao
     *
     * @param datareferencia -> data de referencia para a qual se vai verificar o estado da enfermaria
     * @return
     */
    @Override
    public boolean isEmPressao(Data datareferencia) {
        if (calcularTaxaOcupacao(datareferencia) > LIMITE_PRESSAO) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Identificar episódios com data de alta e criar uma lista só com esses episódios
     *
     * @return -> lista de episodios com alta
     */

    private List<Episodio> getEpisodiosComAlta() {
        List<Episodio> comAlta = new ArrayList<>();
        for (Episodio episodio : episodios) {
            if (episodio.isFlagAlta()) {
                comAlta.add(episodio);
            }
        }
        return comAlta;
    }

    //Medidas de sumário do LoS

    /**
     * Calcula a media do tempo de internamento em dias de todos os episodios concluidos
     *
     * @return media do LoS
     */
    public double calcularMediaLoS() {
        double soma = 0;
        List<Episodio> comAlta = getEpisodiosComAlta();
        if (comAlta.isEmpty()) {
            return 0;
        }

        for (Episodio episodio : comAlta) {
            soma += episodio.calcularLoS();
        }
        return soma / comAlta.size();
    }

    /**
     * Calcula o desvio padrao do tempo de internamento, demosntrando a dispersao dos dados em relacao a media
     *
     * @return -> desvio padrao do LoS
     */
    public double calcularDesvioPadraoLos() {
        double desvioPadraoLos = 0;
        List<Episodio> comAlta = getEpisodiosComAlta();
        if (comAlta.isEmpty()) {
            return 0;
        }

        double mediaLoS = calcularMediaLoS();
        double somaDiferencasQuadrado = 0;

        for (Episodio episodio : comAlta) {
            double diferenca = episodio.calcularLoS() - mediaLoS;
            somaDiferencasQuadrado += diferenca * diferenca;

        }
        return Math.sqrt(somaDiferencasQuadrado / comAlta.size());

    }

    /**
     * Encontra o tempo minimio de internamento em dias registado nos historico de episodios concluidos
     *
     * @return -> minimo dos LoS
     */
    public int calcularMinLoS() {
        List<Episodio> comAlta = getEpisodiosComAlta();
        if (comAlta.isEmpty()) {
            return 0;
        }
        int minLoS = comAlta.get(0).calcularLoS();
        for (Episodio episodio : comAlta) {
            if (episodio.calcularLoS() < minLoS) {
                minLoS = episodio.calcularLoS();
            }
        }
        return minLoS;
    }

    /**
     * Encontra o tempo maximo de internamento em dias registado no historico de episodios concluidos
     *
     * @return maximo dos LoS
     */
    public int calcularMaxLoS() {
        List<Episodio> comAlta = getEpisodiosComAlta();
        if (comAlta.isEmpty()) {
            return 0;
        }
        int maxLoS = comAlta.get(0).calcularLoS();
        for (Episodio episodio : comAlta) {
            if (episodio.calcularLoS() > maxLoS) {
                maxLoS = episodio.calcularLoS();
            }
        }
        return maxLoS;
    }

}


