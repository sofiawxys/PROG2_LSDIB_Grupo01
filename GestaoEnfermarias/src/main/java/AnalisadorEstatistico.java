import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalisadorEstatistico {

    /**
     * Req. Funcional 4 — Alteração percentual de camas.
     * Altera o número de camas de todas as enfermarias com base numa percentagem (ex: 10 para +10%, -20 para -20%).
     */
    public static void alterarCamas(List<Enfermaria> enfermarias, double percentagem) {
        for (Enfermaria enfermaria : enfermarias) {
            // Calcula as novas camas e arredonda para o inteiro mais próximo
            int novasCamas = (int) Math.round(enfermaria.getNumCamas() * (1 + (percentagem / 100.0)));

            // Garante que o número de camas nunca é negativo
            if (novasCamas < 0) {
                novasCamas = 0;
            }
            enfermaria.setNumCamas(novasCamas);
        }
    }

    /**
     * Req. Funcional 5 — Percentagem de Enfermarias em pressão.
     * Devolve a percentagem de enfermarias que estão com taxa de ocupação > 85% numa certa data.
     */
    public static double calcularPercentagemEnfermariasEmPressao(List<Enfermaria> enfermarias, Data dataReferencia) {
        if (enfermarias.isEmpty()) return 0.0;

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
        public Enfermaria enfermaria;
        public double indice;
        public String classificacao;

        public ResultadoPressao(Enfermaria enfermaria, double indice, String classificacao) {
            this.enfermaria = enfermaria;
            this.indice = indice;
            this.classificacao = classificacao;
        }

        // Ordenação Decrescente pelo Índice
        @Override
        public int compareTo(ResultadoPressao outro) {
            return Double.compare(outro.indice, this.indice);
        }

        @Override
        public String toString() {
            return String.format("Enfermaria: %s | Índice: %.1f | Classificação: %s",
                    enfermaria.getIdEnfermaria(), indice, classificacao);
        }
    }

    /**
     * Req. Funcional 6 — Índice de Pressão e Ranking.
     * Calcula o Índice de Pressão e devolve uma lista ordenada descrescentemente.
     */
    public static List<ResultadoPressao> calcularRankingPressao(List<Enfermaria> enfermarias, Data dataReferencia) {
        List<ResultadoPressao> ranking = new ArrayList<>();

        for (Enfermaria enfermaria : enfermarias) {
            if (enfermaria.getNumCamas() == 0) continue; // Evita divisão por zero

            // 1. Componente Ocupação (70%)
            double percOcup = enfermaria.calcularTaxaOcupacao(dataReferencia);
            int scoreOcup;
            if (percOcup <= 85) scoreOcup = 1;
            else if (percOcup <= 90) scoreOcup = 2;
            else if (percOcup <= 95) scoreOcup = 3;
            else if (percOcup <= 100) scoreOcup = 4;
            else scoreOcup = 5;

            // 2. Componente Turnover (30%)
            // Contabiliza admissões e altas que ocorreram até à data de referência (inclusive)
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

            // 3. Cálculo do Índice Final (com arredondamento a 1 casa decimal)
            double indiceFinal = (0.7 * scoreOcup) + (0.3 * scoreTurnover);
            indiceFinal = Math.round(indiceFinal * 10.0) / 10.0;

            // 4. Interpretação/Classificação
            String classificacao;
            if (indiceFinal <= 2.0) classificacao = "Pressão Baixa";
            else if (indiceFinal <= 3.5) classificacao = "Pressão Moderada";
            else classificacao = "Pressão Alta";

            // Adiciona à lista
            ranking.add(new ResultadoPressao(enfermaria, indiceFinal, classificacao));
        }

        // Ordena a lista (baseado no compareTo que definimos como decrescente)
        Collections.sort(ranking);

        return ranking;
    }
}