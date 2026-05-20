import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe AnalisadorEstatistico (RF4, RF5, RF6)
 * e para métodos da Iteração I (Enfermaria, Episodio, Data).
 */
class AnalisadorEstatisticoTest {

    private Data dataRef;
    private EnfermariaGeral eg1;
    private EnfermariaGeral eg2;
    private EnfermariaPsiquiatrica ep1;

    @BeforeEach
    void setUp() {
        dataRef = new Data(2025, 3, 20);

        eg1 = new EnfermariaGeral("eg1", 4, 2);
        eg1.adicionarEpisodio(new Episodio(1, new Data(2025, 3, 1),  new Data(2025, 3, 7)));
        eg1.adicionarEpisodio(new Episodio(2, new Data(2025, 3, 3),  new Data(2025, 3, 10)));
        eg1.adicionarEpisodio(new Episodio(3, new Data(2025, 3, 5),  new Data(2025, 3, 20)));
        eg1.adicionarEpisodio(new Episodio(4, new Data(2025, 3, 18), null));

        eg2 = new EnfermariaGeral("eg2", 6, 1);

        ep1 = new EnfermariaPsiquiatrica("ep1", 5, "9:00-11:00", "Alto");
        for (int i = 1; i <= 5; i++) {
            ep1.adicionarEpisodio(new Episodio(i, new Data(2025, 3, 10), null));
        }
    }

    // =========================================================================
    // RF4 — alterarCamas
    // =========================================================================

    @Test
    @DisplayName("RF4 — Aumento de 50%: 4 camas → 6")
    void testAlterarCamas_aumento() {
        AnalisadorEstatistico.alterarCamas(List.of(eg1), 50.0);
        assertEquals(6, eg1.getNumCamas());
    }

    @Test
    @DisplayName("RF4 — Redução de 25%: 6 camas → 5 (arredondamento)")
    void testAlterarCamas_reducao() {
        AnalisadorEstatistico.alterarCamas(List.of(eg2), -25.0);
        assertEquals(5, eg2.getNumCamas());
    }

    @Test
    @DisplayName("RF4 — 0% não altera o número de camas")
    void testAlterarCamas_zero() {
        AnalisadorEstatistico.alterarCamas(List.of(eg1), 0.0);
        assertEquals(4, eg1.getNumCamas());
    }

    @Test
    @DisplayName("RF4 — Redução extrema respeita o mínimo de 1 cama")
    void testAlterarCamas_minimo() {
        EnfermariaGeral e = new EnfermariaGeral("x", 1, 0);
        AnalisadorEstatistico.alterarCamas(List.of(e), -99.0);
        assertEquals(1, e.getNumCamas());
    }

    @Test
    @DisplayName("RF4 — Lista null não lança exceção")
    void testAlterarCamas_null() {
        assertDoesNotThrow(() -> AnalisadorEstatistico.alterarCamas(null, 10.0));
    }

    @Test
    @DisplayName("RF4 — Todas as enfermarias da lista são atualizadas")
    void testAlterarCamas_todasAtualizadas() {
        List<Enfermaria> lista = new ArrayList<>();
        lista.add(eg1);
        lista.add(eg2);
        AnalisadorEstatistico.alterarCamas(lista, 100.0);
        assertEquals(8,  eg1.getNumCamas());
        assertEquals(12, eg2.getNumCamas());
    }

    // =========================================================================
    // RF5 — calcularPercentagemEnfermariasEmPressao
    // =========================================================================

    @Test
    @DisplayName("RF5 — 0% quando nenhuma enfermaria está em pressão")
    void testPercentagemPressao_nenhuma() {
        double resultado = AnalisadorEstatistico.calcularPercentagemEnfermariasEmPressao(List.of(eg1, eg2), dataRef);
        assertEquals(0.0, resultado, 0.001);
    }

    @Test
    @DisplayName("RF5 — 100% quando todas estão em pressão")
    void testPercentagemPressao_todas() {
        double resultado = AnalisadorEstatistico.calcularPercentagemEnfermariasEmPressao(List.of(ep1), dataRef);
        assertEquals(100.0, resultado, 0.001);
    }

    @Test
    @DisplayName("RF5 — 50% quando metade está em pressão")
    void testPercentagemPressao_metade() {
        double resultado = AnalisadorEstatistico.calcularPercentagemEnfermariasEmPressao(List.of(ep1, eg1), dataRef);
        assertEquals(50.0, resultado, 0.001);
    }

    @Test
    @DisplayName("RF5 — Lista vazia devolve 0.0")
    void testPercentagemPressao_listaVazia() {
        double resultado = AnalisadorEstatistico.calcularPercentagemEnfermariasEmPressao(new ArrayList<>(), dataRef);
        assertEquals(0.0, resultado, 0.001);
    }

    @Test
    @DisplayName("RF5 — Lista null devolve 0.0")
    void testPercentagemPressao_null() {
        double resultado = AnalisadorEstatistico.calcularPercentagemEnfermariasEmPressao(null, dataRef);
        assertEquals(0.0, resultado, 0.001);
    }

    // =========================================================================
    // RF6 — calcularRankingPressao
    // =========================================================================

    @Test
    @DisplayName("RF6 — Ranking tem o mesmo número de entradas que a lista")
    void testRanking_tamanho() {
        List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(List.of(eg1, eg2, ep1), dataRef);
        assertEquals(3, ranking.size());
    }

    @Test
    @DisplayName("RF6 — Ranking está ordenado de forma decrescente")
    void testRanking_ordemDecrescente() {
        List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(List.of(eg1, eg2, ep1), dataRef);
        for (int i = 0; i < ranking.size() - 1; i++) {
            // Alterado para usar o Getter getIndicePressao()
            assertTrue(ranking.get(i).getIndicePressao() >= ranking.get(i + 1).getIndicePressao());
        }
    }

    @Test
    @DisplayName("RF6 — eg2 sem episódios tem scoreOcup = 1 (percOcup = 0%)")
    void testRanking_scoreOcup1() {
        List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(List.of(eg2), dataRef);
        assertEquals(1, ranking.get(0).getScoreOcup());
    }

    @Test
    @DisplayName("RF6 — ep1 com 100% de ocupação tem scoreOcup = 4")
    void testRanking_scoreOcup4() {
        List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(List.of(ep1), dataRef);
        assertEquals(4, ranking.get(0).getScoreOcup());
    }

    @Test
    @DisplayName("RF6 — eg2 sem episódios tem classificação 'Pressão Baixa' e índice 1.0")
    void testRanking_classificacaoBaixa() {
        List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(List.of(eg2), dataRef);
        // Alterado para usar os Getters
        assertEquals("Pressão Baixa", ranking.get(0).getClassificacao());
        assertEquals(1.0, ranking.get(0).getIndicePressao(), 0.001);
    }

    @Test
    @DisplayName("RF6 — ep1 tem classificação 'Pressão Alta' e índice 4.3")
    void testRanking_classificacaoAlta() {
        // ep1: scoreOcup=4, scoreTurnover=5 → 0.7×4 + 0.3×5 = 4.3 → Pressão Alta
        List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(List.of(ep1), dataRef);
        // Alterado para usar os Getters
        assertEquals("Pressão Alta", ranking.get(0).getClassificacao());
        assertEquals(4.3, ranking.get(0).getIndicePressao(), 0.001);
    }

    @Test
    @DisplayName("RF6 — Lista vazia devolve lista vazia")
    void testRanking_listaVazia() {
        assertTrue(AnalisadorEstatistico.calcularRankingPressao(new ArrayList<>(), dataRef).isEmpty());
    }

    @Test
    @DisplayName("RF6 — Lista null devolve lista vazia")
    void testRanking_null() {
        assertTrue(AnalisadorEstatistico.calcularRankingPressao(null, dataRef).isEmpty());
    }

    // =========================================================================
    // Iteração I — Episodio
    // =========================================================================

    @Test
    @DisplayName("IT1 — calcularLoS() com alta devolve diferença em dias")
    void testEpisodio_loS_comAlta() {
        Episodio ep = new Episodio(1, new Data(2025, 3, 1), new Data(2025, 3, 7));
        assertEquals(6, ep.calcularLoS());
    }

    @Test
    @DisplayName("IT1 — calcularLoS() sem alta devolve -1")
    void testEpisodio_loS_semAlta() {
        Episodio ep = new Episodio(1, new Data(2025, 3, 1), null);
        assertEquals(-1, ep.calcularLoS());
    }

    @Test
    @DisplayName("IT1 — isAtivo() true quando admitido e sem alta")
    void testEpisodio_ativo_semAlta() {
        Episodio ep = new Episodio(1, new Data(2025, 3, 1), null);
        assertTrue(ep.isAtivo(new Data(2025, 3, 20)));
    }

    @Test
    @DisplayName("IT1 — isAtivo() false antes da admissão")
    void testEpisodio_ativo_antesAdmissao() {
        Episodio ep = new Episodio(1, new Data(2025, 3, 10), null);
        assertFalse(ep.isAtivo(new Data(2025, 3, 5)));
    }

    @Test
    @DisplayName("IT1 — Alta inválida (anterior à admissão) é ignorada")
    void testEpisodio_altaInvalida() {
        Episodio ep = new Episodio(1, new Data(2025, 3, 10), new Data(2025, 3, 1));
        assertFalse(ep.isFlagAlta());
    }

    // =========================================================================
    // Iteração I — Enfermaria
    // =========================================================================

    @Test
    @DisplayName("IT1 — calcularOcupacao() conta só os episódios ativos")
    void testEnfermaria_ocupacao() {
        assertEquals(2, eg1.calcularOcupacao(dataRef));
    }

    @Test
    @DisplayName("IT1 — calcularTaxaOcupacao() = 100% quando todas as camas estão ocupadas")
    void testEnfermaria_taxaTotal() {
        assertEquals(100.0, ep1.calcularTaxaOcupacao(dataRef), 0.001);
    }

    @Test
    @DisplayName("IT1 — isEmPressao() true com 100% de ocupação")
    void testEnfermaria_emPressao() {
        assertTrue(ep1.isEmPressao(dataRef));
    }

    @Test
    @DisplayName("IT1 — isEmPressao() false com 50% de ocupação")
    void testEnfermaria_semPressao() {
        assertFalse(eg1.isEmPressao(dataRef));
    }

    @Test
    @DisplayName("IT1 — calcularMediaLoS() com 3 altas")
    void testEnfermaria_mediaLoS() {
        double esperado = (6.0 + 7.0 + 15.0) / 3.0;
        assertEquals(esperado, eg1.calcularMediaLoS(), 0.001);
    }

    @Test
    @DisplayName("IT1 — calcularMinLoS() devolve o menor LoS")
    void testEnfermaria_minLoS() {
        assertEquals(6, eg1.calcularMinLoS());
    }

    @Test
    @DisplayName("IT1 — calcularMaxLoS() devolve o maior LoS")
    void testEnfermaria_maxLoS() {
        assertEquals(15, eg1.calcularMaxLoS());
    }

    // =========================================================================
    // Iteração I — Data
    // =========================================================================

    @Test
    @DisplayName("IT1 — parseData() converte string AAAA-MM-DD")
    void testData_parseData() {
        Data d = Data.parseData("2025-03-20");
        assertEquals(2025, d.getAno());
        assertEquals(3,    d.getMes());
        assertEquals(20,   d.getDia());
    }

    @Test
    @DisplayName("IT1 — avancarUmDia() passa para o mês seguinte no fim do mês")
    void testData_avancarMes() {
        Data d = new Data(2025, 3, 31);
        d.avancarUmDia();
        assertEquals(4, d.getMes());
        assertEquals(1, d.getDia());
    }

    @Test
    @DisplayName("IT1 — avancarUmDia() gere corretamente o ano bissexto")
    void testData_bissexto() {
        Data d = new Data(2024, 2, 28);
        d.avancarUmDia();
        assertEquals(29, d.getDia());
    }

    @Test
    @DisplayName("IT1 — calcularDiferenca() devolve valor absoluto")
    void testData_diferenca() {
        Data d1 = new Data(2025, 3, 1);
        Data d2 = new Data(2025, 3, 7);
        assertEquals(6, d1.calcularDiferenca(d2));
        assertEquals(6, d2.calcularDiferenca(d1));
    }
}