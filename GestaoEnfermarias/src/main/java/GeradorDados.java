/**
 * Classe utilitária responsável por gerar um conjunto de dados predefinidos
 * Facilita os testes e a demonstração das funcionalidades do sitema, povoando o hospital
 * com enfermarias e episodios clinicos sem necessidade de inserçao manual ou leitura de ficheiros
 */
public class GeradorDados {
    /**
     * Cria e insere automaticamente dados ficticios na instancia do hospital fornecida.
     * Gera instancias dos diferentes tipos de enfermaria (Geral, Psiquiátrica e Cuidados Intensivos)
     * e preenche-as com varios episodios de internamento e recursos
     *
     * @param hospital a instancia do hospital onde os dados gerados serão inseridos
     * @throws DataInvalidaException       se houver alguma incoerência cronológica nas datas dos episódios gerados
     * @throws CapacidadeExcedidaException se a geração de dados tentar ultrapassar o limite de camas da enfermaria
     * @throws CamaOcupadaException        se ocorrer uma sobreposição forçada de datas na mesma cama
     */
    public static void criarDadosAutomaticos(Hospital hospital) throws DataInvalidaException, CapacidadeExcedidaException, CamaOcupadaException {
        EnfermariaGeral eg1 = new EnfermariaGeral("eg1", 4, 2);
        eg1.adicionarRecurso("Cadeira de rodas");
        eg1.adicionarRecurso("Ventilador");

        EnfermariaGeral eg2 = new EnfermariaGeral("eg2", 6, 1);
        eg2.adicionarRecurso("Monitor cardíaco");

        EnfermariaPsiquiatrica ep1 = new EnfermariaPsiquiatrica("ep1", 5, "9:00-11:00", "Alto");

        EnfermariaCuidadosIntensivos eci1 = new EnfermariaCuidadosIntensivos("eci1", 9, "15:00-19:00", 730, 760);

        // Adicionar enfermarias ao hospital
        hospital.adicionarEnfermaria(eg1);
        hospital.adicionarEnfermaria(eg2);
        hospital.adicionarEnfermaria(ep1);
        hospital.adicionarEnfermaria(eci1);

        // Criar episódios — eg1
        eg1.adicionarEpisodio(new Episodio(1, new Data(2025, 3, 1), new Data(2025, 3, 7)));
        eg1.adicionarEpisodio(new Episodio(2, new Data(2025, 3, 3), new Data(2025, 3, 10)));
        eg1.adicionarEpisodio(new Episodio(3, new Data(2025, 3, 5), new Data(2025, 3, 20)));
        eg1.adicionarEpisodio(new Episodio(4, new Data(2025, 3, 18), null));

        // Criar episódios — ep1 (100% ocupação)
        ep1.adicionarEpisodio(new Episodio(1, new Data(2025, 3, 10), null));
        ep1.adicionarEpisodio(new Episodio(2, new Data(2025, 3, 10), null));
        ep1.adicionarEpisodio(new Episodio(3, new Data(2025, 3, 10), null));
        ep1.adicionarEpisodio(new Episodio(4, new Data(2025, 3, 10), null));
        ep1.adicionarEpisodio(new Episodio(5, new Data(2025, 3, 10), null));

        // Criar episódios — eci1
        eci1.adicionarEpisodio(new Episodio(1, new Data(2025, 3, 15), new Data(2025, 3, 18)));
        eci1.adicionarEpisodio(new Episodio(2, new Data(2025, 3, 16), null));
        eci1.adicionarEpisodio(new Episodio(3, new Data(2025, 3, 17), null));

        System.out.println("Dados criados com sucesso!");
    }
}
