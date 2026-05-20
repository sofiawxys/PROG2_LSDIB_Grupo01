import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe com métodos estáticos para análise das enfermarias do hospital.
 * Requisitos Funcionais 4, 5 e 6 da Iteração II.
 */
public class AnalisadorEstatistico {

    /**
     * Req. Funcional 4 — Alteração percentual de camas.
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
     * Req. Funcional 5 — Percentagem de Enfermarias em pressão.
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

        public ResultadoPressao(Enfermaria enfermaria, int scoreOcup, int scoreTurnover, double indicePressao, String classificacao) {
            this.enfermaria = enfermaria;
            this.scoreOcup = scoreOcup;
            this.scoreTurnover = scoreTurnover;
            this.indicePressao = indicePressao;
            this.classificacao = classificacao;
        }

        // --- GETTERS NECESSÁRIOS PARA OS TESTES ---
        public Enfermaria getEnfermaria() { return enfermaria; }
        public int getScoreOcup() { return scoreOcup; }
        public int getScoreTurnover() { return scoreTurnover; }
        public double getIndicePressao() { return indicePressao; }
        public String getClassificacao() { return classificacao; }
        // ------------------------------------------

        // Ordenação Decrescente pelo Índice
        @Override
        public int compareTo(ResultadoPressao outro) {
            return Double.compare(outro.indicePressao, this.indicePressao);
        }

        @Override
        public String toString() {
            return String.format("Enfermaria: %-8s | scoreOcup=%d | scoreTurnover=%d | índice=%.1f | %s",
                    enfermaria.getIdEnfermaria(), scoreOcup, scoreTurnover, indicePressao, classificacao);
        }
    }

    /**
     * Req. Funcional 6 — Índice de Pressão e Ranking.
     */
    public static List<ResultadoPressao> calcularRankingPressao(List<Enfermaria> enfermarias, Data dataReferencia) {
        List<ResultadoPressao> ranking = new ArrayList<>();

        if (enfermarias == null || enfermarias.isEmpty()) {
            return ranking;
        }

        for (Enfermaria enfermaria : enfermarias) {
            if (enfermaria.getNumCamas() == 0) continue;

            // 1. Componente Ocupação (70%)
            double percOcup = enfermaria.calcularTaxaOcupacao(dataReferencia);
            int scoreOcup;
            if (percOcup <= 85) scoreOcup = 1;
            else if (percOcup <= 90) scoreOcup = 2;
            else if (percOcup <= 95) scoreOcup = 3;
            else if (percOcup <= 100) scoreOcup = 4;
            else scoreOcup = 5;

            // 2. Componente Turnover (30%)
            int admissoes = 0;
            int altas = 0;
            for (Episodio ep : enfermaria.getEpisodios()) {
                if (!ep.getDataAdmissao().isMaior(dataReferencia)) {
                    admissoes++;
                }
                if (ep.isFlagAlta() && !ep.getDataAlta().isMaior(dataReferencia)) {
                    altas++;
                }
            }

            double percTurnover = ((double) (admissoes + altas) / enfermaria.getNumCamas()) * 100.0;
            int scoreTurnover;
            if (percTurnover <= 10) scoreTurnover = 1;
            else if (percTurnover <= 20) scoreTurnover = 2;
            else if (percTurnover <= 30) scoreTurnover = 3;
            else if (percTurnover <= 40) scoreTurnover = 4;
            else scoreTurnover = 5;

            // 3. Cálculo do Índice Final
            double indiceFinal = (0.7 * scoreOcup) + (0.3 * scoreTurnover);
            indiceFinal = Math.round(indiceFinal * 10.0) / 10.0;

            // 4. Interpretação/Classificação
            String classificacao;
            if (indiceFinal <= 2.0) classificacao = "Pressão Baixa";
            else if (indiceFinal <= 3.5) classificacao = "Pressão Moderada";
            else classificacao = "Pressão Alta";

            // Adiciona à lista
            ranking.add(new ResultadoPressao(enfermaria, scoreOcup, scoreTurnover, indiceFinal, classificacao));
        }

        // Ordena a lista de forma decrescente através do Collections Framework
        Collections.sort(ranking);

        return ranking;
    }
}