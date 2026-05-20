
/**
 * Interface que define as operações obrigatórias para qualquer entidade que faça gestão de camas e ocupação hospitalar
 * Neste momento apenas temos enfermarias, mas será uma interface útil para a segunda iteração
 * Caso começemos também a lidar com centros de saúde ou clínicas, por exemplo
 */
public interface GestaoOcupacao {

    /**
     * Metodo para calcular a ocupacao numa data de referencia
     *
     * @param dataReferencia -> data de referencia
     * @return
     */
    int calcularOcupacao(Data dataReferencia);

    /**
     * Metodo para calcular a taxa de ocupacao numa data de referencia
     *
     * @param dataReferencia -> data de referencia
     * @return
     */
    double calcularTaxaOcupacao(Data dataReferencia);

    /**
     * Metodo para verificar se uma unidade se encontra em pressao numa data de referencia
     *
     * @param dataReferencia -> data de referencia
     * @return
     */
    boolean isEmPressao(Data dataReferencia);
}

