public class GeradorDados {
    public static void criarDadosAutomaticos(Hospital hospital) throws DataInvalidaException, CapacidadeExcedidaException, CamaOcupadaException{
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
