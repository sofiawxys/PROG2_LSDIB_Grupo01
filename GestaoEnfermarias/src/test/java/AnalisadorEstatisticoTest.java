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
    void setUp() throws DataInvalidaException, CapacidadeExcedidaException, CamaOcupadaException {
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
    void testEpisodio_loS_comAlta() throws DataInvalidaException{
        Episodio ep = new Episodio(1, new Data(2025, 3, 1), new Data(2025, 3, 7));
        assertEquals(6, ep.calcularLoS());
    }

    @Test
    @DisplayName("IT1 — calcularLoS() sem alta devolve -1")
    void testEpisodio_loS_semAlta() throws DataInvalidaException {
        Episodio ep = new Episodio(1, new Data(2025, 3, 1), null);
        assertEquals(-1, ep.calcularLoS());
    }

    @Test
    @DisplayName("IT1 — isAtivo() true quando admitido e sem alta")
    void testEpisodio_ativo_semAlta() throws DataInvalidaException {
        Episodio ep = new Episodio(1, new Data(2025, 3, 1), null);
        assertTrue(ep.isAtivo(new Data(2025, 3, 20)));
    }

    @Test
    @DisplayName("IT1 — isAtivo() false antes da admissão")
    void testEpisodio_ativo_antesAdmissao() throws DataInvalidaException {
        Episodio ep = new Episodio(1, new Data(2025, 3, 10), null);
        assertFalse(ep.isAtivo(new Data(2025, 3, 5)));
    }
/*
    @Test
    @DisplayName("IT1 — Alta inválida (anterior à admissão) é ignorada")
    void testEpisodio_altaInvalida() throws DataInvalidaException {
        Episodio ep = new Episodio(1, new Data(2025, 3, 10), new Data(2025, 3, 1));
        assertFalse(ep.isFlagAlta());
    }
*/
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
    void testData_parseData() throws DataInvalidaException {
        Data d = DataAvancada.parseData("2025-03-20");
        assertEquals(2025, d.getAno());
        assertEquals(3,    d.getMes());
        assertEquals(20,   d.getDia());
    }

    @Test
    @DisplayName("IT1 — avancarUmDia() passa para o mês seguinte no fim do mês")
    void testData_avancarMes() {
        DataAvancada d = new DataAvancada(2025, 3, 31);
        d.avancarUmDia();
        assertEquals(4, d.getMes());
        assertEquals(1, d.getDia());
    }

    @Test
    @DisplayName("IT1 — avancarUmDia() gere corretamente o ano bissexto")
    void testData_bissexto() {
        DataAvancada d = new DataAvancada(2024, 2, 28);
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

    // =========================================================================
// EXCEÇÕES — CapacidadeExcedidaException e DataInvalidaException
// =========================================================================

    @Test
    @DisplayName("EXCEÇÃO — Deve lançar CapacidadeExcedidaException ao ultrapassar o limite de camas")
    void testExcecao_capacidadeExcedida() {
        // Na data 2025-03-20, as camas 3 e 4 estão ativas (2 de 4)
        // Precisamos de uma data onde as 4 camas estejam todas ocupadas
        // eg1 tem 4 camas — vamos forçar 4 ocupadas na mesma data numa nova enfermaria
        EnfermariaGeral egTeste = new EnfermariaGeral("egTeste", 2, 0);
        assertThrows(CapacidadeExcedidaException.class, () -> {
            egTeste.adicionarEpisodio(new Episodio(1, new Data(2025, 3, 10), null));
            egTeste.adicionarEpisodio(new Episodio(2, new Data(2025, 3, 10), null));
            // 3º episódio numa enfermaria de 2 camas — deve lançar a exceção
            egTeste.adicionarEpisodio(new Episodio(3, new Data(2025, 3, 10), null));
        });
    }

    @Test
    @DisplayName("EXCEÇÃO — Deve lançar DataInvalidaException quando a alta é anterior à admissão")
    void testExcecao_dataInvalida() {
        Data dataAdmissao = new Data(2025, 3, 10);
        Data dataAlta     = new Data(2025, 3, 1);
        assertThrows(DataInvalidaException.class, () -> {
            new Episodio(99, dataAdmissao, dataAlta);
        });
    }

    @Test
    @DisplayName("EXCEÇÃO — Deve lançar CamaOcupadaException quando a mesma cama é usada em datas sobrepostas")
    void testExcecao_camaOcupada() {
        assertThrows(CamaOcupadaException.class, () -> {
            eg1.adicionarEpisodio(new Episodio(4, new Data(2025, 3, 19), null));
        });
    }

// =========================================================================
// RF6 — calcularAdmissoes e calcularAltas (base do Turnover)
// =========================================================================

    @Test
    @DisplayName("Turnover — calcularAdmissoes conta corretamente num dia com admissões")
    void testCalcularAdmissoes_comAdmissoes() {
        assertEquals(5, ep1.calcularAdmissoes(new Data(2025, 3, 10)));
    }

    @Test
    @DisplayName("Turnover — calcularAdmissoes devolve 0 num dia sem admissões")
    void testCalcularAdmissoes_semAdmissoes() {
        assertEquals(0, ep1.calcularAdmissoes(new Data(2025, 3, 11)));
    }

    @Test
    @DisplayName("Turnover — calcularAltas conta corretamente num dia com altas")
    void testCalcularAltas_comAltas() {
        assertEquals(1, eg1.calcularAltas(new Data(2025, 3, 7)));
    }

    @Test
    @DisplayName("Turnover — calcularAltas devolve 0 num dia sem altas")
    void testCalcularAltas_semAltas() {
        assertEquals(0, eg1.calcularAltas(new Data(2025, 3, 15)));
    }

// =========================================================================
// SERIALIZAÇÃO — GestorFicheiros
// =========================================================================

    @Test
    @DisplayName("Serialização — Deve guardar e recuperar o Hospital mantendo nome e enfermarias")
    void testSerializacaoHospital() {
        String ficheiroTeste = "teste_hospital_temp.dat";
        Hospital hospitalOriginal = new Hospital("Hospital Curry Cabral");
        hospitalOriginal.adicionarEnfermaria(new EnfermariaGeral("EG_Teste", 10, 2));

        GestorFicheiros.guardarDados(hospitalOriginal, ficheiroTeste);
        Hospital hospitalRecuperado = GestorFicheiros.lerDados(ficheiroTeste);

        assertNotNull(hospitalRecuperado);
        assertEquals("Hospital Curry Cabral", hospitalRecuperado.getNome());
        assertNotNull(hospitalRecuperado.procurarEnfermaria("EG_Teste"));

        new java.io.File(ficheiroTeste).delete();
    }

    @Test
    @DisplayName("Serialização — Hospital recuperado preserva os episódios das enfermarias")
    void testSerializacaoComEpisodios() throws DataInvalidaException, CapacidadeExcedidaException, CamaOcupadaException {
        String ficheiroTeste = "teste_episodios_temp.dat";
        Hospital h = new Hospital("Hospital Teste");
        EnfermariaGeral eg = new EnfermariaGeral("EG_Serial", 5, 1);
        eg.adicionarEpisodio(new Episodio(1, new Data(2025, 4, 1), new Data(2025, 4, 5)));
        h.adicionarEnfermaria(eg);

        GestorFicheiros.guardarDados(h, ficheiroTeste);
        Hospital hRecuperado = GestorFicheiros.lerDados(ficheiroTeste);

        assertNotNull(hRecuperado);
        Enfermaria egRecuperada = hRecuperado.procurarEnfermaria("EG_Serial");
        assertNotNull(egRecuperada);
        assertEquals(1, egRecuperada.getEpisodios().size());

        new java.io.File(ficheiroTeste).delete();
    }

    // Testes extra para DataAvancada
    @Test
    @DisplayName("IT1 — parseData() lança DataInvalidaException com formato inválido")
    void testData_parseData_invalida() {
        assertThrows(DataInvalidaException.class, () -> {
            DataAvancada.parseData("20/03/2025");
        });
    }

    @Test
    @DisplayName("IT1 — avancarUmDia() avança o ano no último dia")
    void testData_avancarAno() {
        DataAvancada d = new DataAvancada(2025, 12, 31);
        d.avancarUmDia();
        assertEquals(2026, d.getAno());
        assertEquals(1, d.getMes());
        assertEquals(1, d.getDia());
    }
}