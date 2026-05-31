import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe utilitária com métodos estáticos para análise das enfermarias do hospital.
 * Responsavel por implementar calculos complexos, incluindo variaçao percentual de camas,
 * cálculo de percentagem de pressao e geração do ranking do indice de pressao
 */
public class AnalisadorEstatistico {

    private static final double PESO_OCUPACAO =0.7;
    private static final double PESO_TURNOVER= 0.3;

    private static final int LIMITE_OCUP_1=85;
    private static final int LIMITE_OCUP_2=90;
    private static final int LIMITE_OCUP_3=95;
    private static final int LIMITE_OCUP_4=100;

    private static final int LIMITE_TURN_1=10;
    private static final int LIMITE_TURN_2=20;
    private static final int LIMITE_TURN_3=30;
    private static final int LIMITE_TURN_4=40;

    private static final double LIMITE_INDICE_BAIXO = 2.0;
    private static final double LIMITE_INDICE_MODERADO = 3.5;

    /**
     * Metodo static que aplica uma variação percentual ao numCamas de todas as enfermarias. Garante que o resultado é sempre no mínimo 1.
     * @param enfermarias a lista de enfermarias a serem atualizadas
     * @param percentagem a percentagem de variação a aplicar (ex: 10.0 para aumentar 10%, -5.0 para reduzir 5%)
     */
    public static void alterarCamas(List<Enfermaria> enfermarias, double percentagem) {
        if (enfermarias == null) {
            return;
        }

        for (Enfermaria enfermaria : enfermarias) {
            int novasCamas = (int) Math.round(enfermaria.getNumCamas() * (1 + (percentagem / 100.0)));

            if (novasCamas < 1) {
                novasCamas = 1;
            }
            enfermaria.setNumCamas(novasCamas);
        }
    }

    /**
     *  Metodo static que calcula a percentagem de enfermarias com taxa de ocupação superior a 85%.
     * @param enfermarias lista de enfermarias a avaliar
     * @param dataReferencia data de referencia
     * @return percentagem de enfermarias em pressao
     */
    public static double calcularPercentagemEnfermariasEmPressao(List<Enfermaria> enfermarias, Data dataReferencia) {
        if (enfermarias == null || enfermarias.isEmpty()) {
            return 0.0;
        }

        int countPressao = 0;
        for (Enfermaria enfermaria : enfermarias) {
            if (enfermaria.isEmPressao(dataReferencia)) {
                countPressao++;
            }
        }
        return ((double) countPressao / enfermarias.size()) * 100.0;
    }

    /**
     * Classe auxiliar (Nested Class) para guardar os resultados do Índice de Pressão e permitir a ordenação.
     */
    public static class ResultadoPressao implements Comparable<ResultadoPressao> {
        private Enfermaria enfermaria;
        private int scoreOcup;
        private int scoreTurnover;
        private double indicePressao;
        private String classificacao;

        /**
         * Construtor do objeto que encapsula o resultado do cálculo de pressão.
         * @param enfermaria enfermaria avaliada
         * @param scoreOcup pontuaçao de 1 a 5 atribuida à taxa de ocupaçao
         * @param scoreTurnover pontuaçao de 1 a 5 atribuida ao turnover
         * @param indicePressao valor final calculado do indice de pressao
         * @param classificacao classificaçao textual interpretada (Baixa, Moderada ou Alta)
         */
        public ResultadoPressao(Enfermaria enfermaria, int scoreOcup, int scoreTurnover, double indicePressao, String classificacao) {
            this.enfermaria = enfermaria;
            this.scoreOcup = scoreOcup;
            this.scoreTurnover = scoreTurnover;
            this.indicePressao = indicePressao;
            this.classificacao = classificacao;
        }

        // --- GETTERS NECESSÁRIOS PARA OS TESTES ---

        /**
         * metodo getter
         * @return enfermaria avaliada
         */
        public Enfermaria getEnfermaria() {
            return enfermaria;
        }

        /**
         * metodo getter
         * @return pontuaçao atribuida à taxa de ocupaçao
         */
        public int getScoreOcup() {
            return scoreOcup;
        }

        /**
         * metodo getter
         * @return pontuaçao atribuida ao turnover
         */
        public int getScoreTurnover() {
            return scoreTurnover;
        }

        /**
         * metodo getter
         * @return indice de pressao calculado
         */
        public double getIndicePressao() {
            return indicePressao;
        }

        /**
         * metodo getter
         * @return classifcaçao textual do estado de pressao
         */
        public String getClassificacao() {
            return classificacao;
        }
        // ------------------------------------------

        // Ordenação Decrescente pelo Índice

        /**
         *Compara este resultado com outro para efeitos de ordenação nas listagens.
         * A comparação é feita de forma decrescente com base no valor do Índice de Pressão.
         * @param outro o outro objeto ResultadoPressao com o qual se vai comparar
         * @return um valor negativo, zero ou positivo consoante este índice seja maior, igual ou menor que o do outro objeto
         */
        @Override
        public int compareTo(ResultadoPressao outro) {
            return Double.compare(outro.indicePressao, this.indicePressao);
        }

        /**
         *Devolve uma representação textual perfeitamente formatada e alinhada dos resultados
         * do cálculo de pressão para exibição na consola.
         * @return uma String contendo o ID da enfermaria, os scores parcelares, o índice final e a classificação textual
         */
        @Override
        public String toString() {
            return String.format("Enfermaria: %-8s | scoreOcup=%d | scoreTurnover=%d | índice=%.1f | %s",
                    enfermaria.getIdEnfermaria(), scoreOcup, scoreTurnover, indicePressao, classificacao);
        }
    }

    /**
     * Calcula o indice de pressao para cada enfermaria fornecida e devolve o respetivo ranking
     * @param enfermarias enfermarias a avaliar
     * @param dataReferencia data de referencia
     * @return lista de objs ResultadoPressao ordenada de forma decrescente
     */
    public static List<ResultadoPressao> calcularRankingPressao(List<Enfermaria> enfermarias, Data dataReferencia) {
        List<ResultadoPressao> ranking = new ArrayList<>();

        if (enfermarias == null || enfermarias.isEmpty()) {
            return ranking;
        }

        for (Enfermaria enfermaria : enfermarias) {
            if (enfermaria.getNumCamas() > 0) {

                // 1. Componente Ocupação (70%)
                double percOcup = enfermaria.calcularTaxaOcupacao(dataReferencia);
                int scoreOcup;
                if (percOcup <= LIMITE_OCUP_1) scoreOcup = 1;
                else if (percOcup <= LIMITE_OCUP_2) scoreOcup = 2;
                else if (percOcup <= LIMITE_OCUP_3) scoreOcup = 3;
                else if (percOcup <= LIMITE_OCUP_4) scoreOcup = 4;
                else scoreOcup = 5;

                // 2. Componente Turnover (30%)
                int admissoes = enfermaria.calcularAdmissoes(dataReferencia);
                int altas = enfermaria.calcularAltas(dataReferencia);

                double percTurnover = ((double) (admissoes + altas) / enfermaria.getNumCamas()) * 100.0;
                int scoreTurnover;
                if (percTurnover <= LIMITE_TURN_1) scoreTurnover = 1;
                else if (percTurnover <= LIMITE_TURN_2) scoreTurnover = 2;
                else if (percTurnover <= LIMITE_TURN_3) scoreTurnover = 3;
                else if (percTurnover <= LIMITE_TURN_4) scoreTurnover = 4;
                else scoreTurnover = 5;

                // 3. Cálculo do Índice Final
                double indiceFinal = (PESO_OCUPACAO * scoreOcup) + (PESO_TURNOVER * scoreTurnover);
                indiceFinal = Math.round(indiceFinal * 10.0) / 10.0;

                // 4. Interpretação/Classificação
                String classificacao;
                if (indiceFinal <= LIMITE_INDICE_BAIXO) classificacao = "Pressão Baixa";
                else if (indiceFinal <= LIMITE_INDICE_MODERADO) classificacao = "Pressão Moderada";
                else classificacao = "Pressão Alta";

                // Adiciona à lista
                ranking.add(new ResultadoPressao(enfermaria, scoreOcup, scoreTurnover, indiceFinal, classificacao));
            }
        }

        // Ordena a lista de forma decrescente através do Collections Framework
        Collections.sort(ranking);

        return ranking;
    }
}