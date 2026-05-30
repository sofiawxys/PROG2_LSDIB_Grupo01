import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.File;

public class Menu {
    //CONSTANTES
    private static final int LIMITE_INF_OPCAO = 1;
    private static final int LIMITE_SUP_OPCAO = 13;
    private static final int LIMITE_SUP_SUBOPCAO = 2;

    private Hospital hospital;
    private LeitorConsola leitor;

    public Menu() {

        this.hospital = new Hospital("Hospital S.João");
        this.leitor = new LeitorConsola();
    }

    public void iniciar() {
        int opcao = 0;
        while (opcao != LIMITE_SUP_OPCAO) {
            mostrarMenu();
            opcao = lerOpcao(LIMITE_INF_OPCAO, LIMITE_SUP_OPCAO);
            executarOpcao(opcao);
        }
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                try {
                    GeradorDados.criarDadosAutomaticos(hospital);
                } catch (DataInvalidaException e) {
                    System.out.println("Erro de datas: " + e.getMessage());
                } catch (CapacidadeExcedidaException e) {
                    System.out.println("Capacidade excedida: " + e.getMessage());
                } catch (CamaOcupadaException e) {
                    System.out.println("Erro de sobreposição de camas: " + e.getMessage());
                }
                break;

            case 2:
                try {
                    carregarDadosFicheiro();
                } catch (FileNotFoundException e) {
                    System.out.println("Erro ao ler ficheiro");
                }
                break;

            case 3:
                System.out.println("\nA restaurar dados da sessão anterior...");
                Hospital hospitalCarregado = GestorFicheiros.lerDados("hospital_dados.dat");

                if (hospitalCarregado != null) {
                    this.hospital = hospitalCarregado;
                    System.out.println("Sessão restaurada com sucesso!");
                } else {
                    System.out.println("Nenhum hospital foi encontrado.");
                }
                break;

            case 4:
                inserirDadosConsola();
                break;
            case 5:
                mostrarIndicadoresOcupacao();
                break;

            case 6:
                mostrarEstadoPressao();
                break;
            case 7:
                mostrarListagens();
                break;
            case 8:
                mostrarTabelaOcupacao();
                break;
            case 9:
                mostrarGraficoBarras();
                break;
            case 10:
                alterarCamasEnfermarias();
                break;
            case 11:
                mostrarPercentagemPressao();
                break;
            case 12:
                mostrarRankingPressao();
                break;
            case 13:
                System.out.println("A guardar a sessão atual...");
                GestorFicheiros.guardarDados(hospital, "hospital_dados.dat");
                System.out.println("A fechar o sistema...");
                break;
        }
    }

//MÉTODOS PRINCIPAIS

    /**
     * Metodo que imprime o menu com as varias funcionalidades para o ecra
     */
    private void mostrarMenu() {
        System.out.println("\n===MENU===");
        System.out.println("1. Criar dados automaticamente");
        System.out.println("2. Carregar dados de ficheiro .csv");
        System.out.println("3. Carregar dados da sessão anterior");
        System.out.println("4. Inserir dados na consola");
        System.out.println("5. Mostrar cálculo de indicadores de ocupação");
        System.out.println("6. Mostrar estado e indicadores de pressão");
        System.out.println("7. Apresentar listagens ordenadas");
        System.out.println("8. Mostrar tabela de ocupação");
        System.out.println("9. Mostrar grafico barras");
        System.out.println("10.Alterar camas totais");
        System.out.println("11.Ver percentagem de enfermarias em pressão");
        System.out.println("12.Mostrar Ranking do Indice de Pressão");
        System.out.println("13.Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void inserirDadosConsola() { // REVER ENFERMARIA POR PARÂMETRO???
        System.out.println("\n1. Inserir enfermaria");
        System.out.println("2. Inserir episódio");
        int opcao = lerOpcao(1, 2);
        if (opcao == 1) {
            inserirEnfermaria();
        } else {
            inserirEpisodio();
        }
    }

    private void inserirEnfermaria() {
        System.out.println("\nNova Enfermaria:");
        System.out.println("1. Enfermaria Geral");
        System.out.println("2. Enfermaria Psiquiátrica");
        System.out.println("3. Enfermaria Cuidados Intensivos");
        int tipo = lerOpcao(1, 3);

        try {
            //Mostrar enfermarias já existentes
            List<Enfermaria> existentes = hospital.getEnfermarias();
            if (!existentes.isEmpty()) {
                System.out.println("IDS já em uso no hospital: ");
                for (Enfermaria e : existentes) {
                    System.out.print("[" + e.getIdEnfermaria() + "] ");
                }
                System.out.println();
            }
            // Não deixar o ID repetir
            String id = "";
            boolean idValido = false;
            while (!idValido) {
                id = leitor.lerString("ID da nova enfermaria: ");
                if (hospital.procurarEnfermaria(id) != null) {
                    System.out.println("Erro: Já existe uma enfermaria com esse ID. Escolha um ID único.");

                } else {
                    idValido = true;
                }
            }
            int numCamas = 0;
            while (numCamas <= 0) {
                numCamas = leitor.lerInteiro("Número de camas: ");
                if (numCamas <= 0) {
                    System.out.println("Erro: O número de camas tem de ser maior que zero.");
                }
            }
            switch (tipo) {
                case 1: {
                    int numAcomp = leitor.lerInteiro("Número de acompanhantes: ");
                    String[] recursos = leitor.lerString("Recursos: ").split(",");

                    EnfermariaGeral eg = new EnfermariaGeral(id, numCamas, numAcomp);
                    for (String recurso : recursos) {
                        eg.adicionarRecurso(recurso.trim());
                    }
                    hospital.adicionarEnfermaria(eg);
                    break;
                }
                case 2: {
                    String horarioVisitas = leitor.lerString("Horário de visitas: ");
                    String nivelSeguranca = leitor.lerString("Nível de segurança: ");
                    hospital.adicionarEnfermaria(new EnfermariaPsiquiatrica(id, numCamas, horarioVisitas, nivelSeguranca));
                    break;
                }
                case 3: {
                    String horarioVisitas = leitor.lerString("Horário visitas: ");
                    double pressaoAtmosferica = leitor.lerDouble("Pressão atmosférica: ");
                    double pressaoReferencia = leitor.lerDouble("Pressão de referência: ");
                    hospital.adicionarEnfermaria(new EnfermariaCuidadosIntensivos(id, numCamas, horarioVisitas, pressaoAtmosferica, pressaoReferencia));
                    break;
                }
            }
            System.out.println("Enfermaria criada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void inserirEpisodio() {
        List<Enfermaria> existentes = hospital.getEnfermarias();
        if (existentes.isEmpty()) {
            System.out.println("Erro: Não existem enfermarias registadas. Crie uma enfermaria primeiro!");
            return;
        }
        System.out.println("Enfermarias Disponiveis:");
        for (Enfermaria e : existentes) {
            System.out.printf("ID: %-6s | Camas totais: %d\n", e.getIdEnfermaria(), e.getNumCamas());
        }
        String idEnfermaria = leitor.lerString("\nID da enfermaria: ");
        Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

        if (enfermaria == null) {
            System.out.println("Erro: Enfermaria não encontrada!");
            return;
        }

        try {
            int idCama = leitor.lerInteiro("ID da cama: ");
            DataAvancada dataAdmissao = DataAvancada.parseData(leitor.lerString("Data de admissão (AAAA-MM-DD): "));
            String dataAltaStr = leitor.lerString("Data de alta (AAAA-MM-DD) ou - se não tiver alta");
            DataAvancada dataAlta = null;
            if (!dataAltaStr.equals("-")) {
                dataAlta = DataAvancada.parseData(dataAltaStr);
            }
            enfermaria.adicionarEpisodio(new Episodio(idCama, dataAdmissao, dataAlta));
            System.out.println("Episódio criado com sucesso!");
        } catch (CapacidadeExcedidaException e) {
            System.out.println("Erro (capacidade excedida): " + e.getMessage());
        } catch (CamaOcupadaException e) {
            System.out.println("Erro (sobreposição de camas): " + e.getMessage());
        } catch (DataInvalidaException e) {
            System.out.println("Erro de validação de datas: " + e.getMessage());
        }
    }

    /**
     * Metodo que solicita uma enfermaria e uma data ao utlizador e apresenta as metricas de ocupacao e LoS
     */
    private void mostrarIndicadoresOcupacao() {
        String idEnfermaria = leitor.lerString("\nIntroduza o ID da enfermaria: ");

        Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

        if (enfermaria == null) {
            System.out.println("Enfermaria não encontrada.");
            return;
        }
        System.out.println("Enfermaria encontrada: " + enfermaria.getIdEnfermaria());
        System.out.println(enfermaria.toString());

        try {
            //Pedir ao utilizador para introduzir a data de referência
            System.out.println("\nINDICADORES DE OCUPAÇÃO");
            String dataReferenciaStr = leitor.lerString("Introduza a data de referência (AAAA-MM-DD): ");
            Data dataReferencia = DataAvancada.parseData(dataReferenciaStr);

            int ocupacao = enfermaria.calcularOcupacao(dataReferencia);
            double taxaOcupacao = enfermaria.calcularTaxaOcupacao(dataReferencia);
            boolean emPressao = enfermaria.isEmPressao(dataReferencia);
            System.out.println("Ocupação: " + ocupacao);
            System.out.println("Taxa de Ocupação: " + taxaOcupacao + "%");
            if (emPressao) {
                System.out.println("Em Pressão");
            } else {
                System.out.println("Estado Normal");
            }

            System.out.println("\nMEDIDAS DE SUMÁRIO LoS");
            System.out.println("Media LoS: " + enfermaria.calcularMediaLoS());
            System.out.println("Desvio Padrão LoS: " + enfermaria.calcularDesvioPadraoLos());
            System.out.println("Mínimo LoS: " + enfermaria.calcularMinLoS());
            System.out.println("Máximo LoS: " + enfermaria.calcularMaxLoS());
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    /**
     * Metodo que faz o carregamento de ficheiros CSV e imprime no ecra eventuais erros de validacao
     *
     * @throws FileNotFoundException
     */
    private void carregarDadosFicheiro() throws FileNotFoundException {
        // Criar objetos File apenas para verificar se existem
        File ficheiroEnfermarias = new File("enfermarias.csv");
        File ficheiroEpisodios = new File("episodios.csv");

        // Usar um IF para testar a existência
        if (!ficheiroEnfermarias.exists() || !ficheiroEpisodios.exists()) {
            System.out.println("ERRO: Os ficheiros .csv não foram encontrados na pasta do projeto.");
            System.out.println("Por favor, verifique se estão no local correto e tente novamente.");
            return; // Sai do método imediatamente e volta ao menu
        }

        // Se o código chegar aqui, é porque os ficheiros existem
        // Carregar episódios e enfermarias de ficheiros csv
        hospital.carregarEnfermarias("enfermarias.csv");
        hospital.carregarEpisodios("episodios.csv");
        System.out.println("Dados carregados com sucesso!");

        // Mostrar erros registados no log
        if (!hospital.getRegistoErros().isEmpty()) {
            System.out.println("\n--- Erros encontrados no carregamento ---");
            for (String erro : hospital.getRegistoErros()) {
                System.out.println("[LOG] " + erro);
            }
        }
    }

    /**
     * Metodo que analisa e imprime o estado diario de uma enfermaria num certo intervalo de tempo
     *
     */
    private void mostrarEstadoPressao() {
        String idEnfermaria = leitor.lerString("Introduza o ID da enfermaria: ");
        Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

        if (enfermaria == null) {
            System.out.println("Enfermaria não encontrada.");
            return;
        }
        try {
            System.out.println("Enfermaria encontrada!");
            //Pedir ao utilizador para introduzir as datas
            String dataInicioStr = leitor.lerString("Introduza a data de início (AAAA-MM-DD): ");
            String dataFimStr = leitor.lerString("Introduza data de fim (AAAA-MM-DD): ");
            Data dataInicio = DataAvancada.parseData(dataInicioStr);
            Data dataFim = DataAvancada.parseData(dataFimStr);

            if (!dataFim.isMaior(dataInicio) && !dataFim.equals(dataInicio)) {
                System.out.println("Erro: a data de fim tem de ser posterior à data de início.");
                return;
            }

            int totalDias = dataFim.calcularDiferenca(dataInicio) + 1;
            DataAvancada dataAtual = new DataAvancada(dataInicio); //cópia da data inicial
            int diasEmPressao = 0;

            System.out.println("\n --- Histórico de Ocupação---");
            for (int i = 0; i < totalDias; i++) {
                double taxaOcupacao = enfermaria.calcularTaxaOcupacao(dataAtual);
                boolean emPressao = enfermaria.isEmPressao(dataAtual);
                if (emPressao) {
                    diasEmPressao++;
                    System.out.println(dataAtual.toString() + " -> Em Pressão (Taxa: " + String.format("%.2f", taxaOcupacao) + " %)");
                } else {
                    System.out.println(dataAtual.toString() + " -> Estado Normal (Taxa: " + String.format("%.2f", taxaOcupacao) + " %)");
                }
                dataAtual.avancarUmDia();
            }
            double percentagemDiasPressao = ((double) diasEmPressao / totalDias) * 100;
            System.out.println("\nPercentagem de dias em pressão: " + String.format("%.2f", percentagemDiasPressao) + " %");
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage()); //único erro possível de ter passado
        }
    }

    /**
     * Metodo que permite listar enfermarias ordenadas por ocupacao ou listar os episodios de uma enfermaira ordenados por admissao
     *
     */
    private void mostrarListagens() {
        System.out.println("\n1. Listar Enfermarias por taxa de ocupação");
        System.out.println("\n2. Listar Episódios de uma Enfermaria por data de admissão");
        System.out.println("Escolha a opção: ");
        int subopcao = lerOpcao(LIMITE_INF_OPCAO, LIMITE_SUP_SUBOPCAO);
        try {
            switch (subopcao) {
                case 1:
                    String dataReferenciaStr = leitor.lerString("Introduza a data de referência (AAAA-MM-DD): ");
                    Data dataReferencia = DataAvancada.parseData(dataReferenciaStr);

                    List<Enfermaria> ordenadas = hospital.listarEnfermariasOrdenadasPorOcupacao(dataReferencia);
                    System.out.println("\n--- Enfermarias Ordenadas (Ocupação Decrescente) ---");
                    for (Enfermaria enfermaria : ordenadas) {
                        double taxa = enfermaria.calcularTaxaOcupacao(dataReferencia);
                        System.out.println(enfermaria.toString());
                        String estadoEnfermaria;
                        if (enfermaria.isEmPressao(dataReferencia)) {
                            estadoEnfermaria = "Em pressão";
                        } else {
                            estadoEnfermaria = "Normal";
                        }

                        System.out.printf("Ocupação: %d/%d camas | Taxa: %.2f%% | Estado: %s\n", enfermaria.calcularOcupacao(dataReferencia), enfermaria.getNumCamas(), taxa, estadoEnfermaria);
                        System.out.println("-");
                    }
                    break;
                case 2:
                    Enfermaria enfermaria = hospital.procurarEnfermaria(leitor.lerString("Introduza o ID da Enfermaria: "));
                    if (enfermaria != null) {
                        List<Episodio> episodios = new ArrayList<>(enfermaria.getEpisodiosOrdenadosPorAdmissao());
                        System.out.println("\n--- Episódios Ordenados (Admissão) ---");
                        for (Episodio ep : episodios) {
                            System.out.println(ep.toString());
                        }
                    } else {
                        System.out.println("Erro: Enfermaria não encontrada!");
                    }
                    break;
            }
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void mostrarTabelaOcupacao() {
        String idEnfermaria = leitor.lerString("ID da enfermaria: ");
        Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

        if (enfermaria == null) {
            System.out.println("Enfermaria não encontrada.");
            return;
        }
        try {
            DataAvancada dataInicio = DataAvancada.parseData(leitor.lerString("Data de início (AAAA-MM-DD): "));
            DataAvancada dataFim = DataAvancada.parseData(leitor.lerString("Data de fim (AAAA-MM-DD): "));

            // Cabeçalho
            System.out.println();
            System.out.printf("%-12s | %-12s | %8s | %11s | %6s | %9s | %s%n",
                    "Enfermaria", "Data", "Ocupadas", "CamasTotais", "%Ocup", "Turnover%", "Barra");
            for (int i = 0; i < 95; i++) {
                System.out.print("-");
            }
            System.out.println();
            int totalDias = dataInicio.calcularDiferenca(dataFim) + 1;
            DataAvancada dataAtual = new DataAvancada(dataInicio);

            for (int i = 0; i < totalDias; i++) {
                int ocupadas = enfermaria.calcularOcupacao(dataAtual);
                int totalCamas = enfermaria.getNumCamas();
                double percOcup = enfermaria.calcularTaxaOcupacao(dataAtual);
                int admissoes = enfermaria.calcularAdmissoes(dataAtual);
                int altas = enfermaria.calcularAltas(dataAtual);
                double turnover = enfermaria.calcularTurnover(dataAtual);
                String barra = gerarBarraHorizontal(percOcup, '#');

                System.out.printf("%-12s | %-12s | %8d | %11d | %5.1f%% | %8.1f%% | %s%n",
                        enfermaria.getIdEnfermaria(),
                        dataAtual.toAnoMesDiaString(),
                        ocupadas, totalCamas, percOcup, turnover, barra);

                dataAtual.avancarUmDia();
            }
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void mostrarGraficoBarras() {
        try {
            Data dataRef = DataAvancada.parseData(leitor.lerString("\nData de referência (AAAA-MM-DD): ")); //aqui guardamos a data numa variável do tipo Data e não DataAvancada, pois o método usado para a lista pede Data

            System.out.println("Escolha a métrica para o gráfico: ");
            System.out.println("1. Taxa de Ocupação(%)");
            System.out.println("2. Turnover (%)");
            System.out.println("3. LoS médio (dias)");
            int opcaoMetrica = lerOpcao(1, 3);
            System.out.println("Orientação:");
            System.out.println("1. Horizontal");
            System.out.println("2. Vertical");
            int orientacao = lerOpcao(1, 2);

            List<Enfermaria> enfermarias = hospital.listarEnfermariasOrdenadasPorOcupacao(dataRef);
            if (enfermarias.isEmpty()) {
                System.out.println("Não foram encontradas enfermarias registadas");
                return;
            }
            double[] valoresMetrica = new double[enfermarias.size()];
            int[] tamanhosBarra = new int[enfermarias.size()];
            String nomeMetrica = "";
            char simbolo = '-';
            switch (opcaoMetrica) {
                case 1: {
                    simbolo = '#';
                    nomeMetrica = "OCUPAÇÃO (%)";
                    break;
                }
                case 2: {
                    simbolo = '+';
                    nomeMetrica = "TURNOVER (%)";
                    break;
                }
                case 3: {
                    simbolo = '*';
                    nomeMetrica = "LoS MÉDIO (DIAS)";
                    break;
                }
            }
            for (int i = 0; i < enfermarias.size(); i++) {
                Enfermaria enfermaria = enfermarias.get(i);
                switch (opcaoMetrica) {
                    case 1: {
                        valoresMetrica[i] = enfermaria.calcularTaxaOcupacao(dataRef);
                        tamanhosBarra[i] = (int) Math.round(valoresMetrica[i] / 2.0);
                        break;
                    }
                    case 2: {
                        valoresMetrica[i] = enfermaria.calcularTurnover(dataRef);
                        tamanhosBarra[i] = (int) Math.round(valoresMetrica[i] / 2.0);
                        break;
                    }
                    case 3: {
                        valoresMetrica[i] = enfermaria.calcularMediaLoS();
                        tamanhosBarra[i] = (int) Math.round(valoresMetrica[i]);
                        break;
                    }
                }
                if (tamanhosBarra[i] < 0) {
                    tamanhosBarra[i] = 0;
                }
                if (tamanhosBarra[i] > 50) {
                    tamanhosBarra[i] = 50;
                }
            }
            if (orientacao == 1) {
                graficoHorizontal(enfermarias, valoresMetrica, tamanhosBarra, simbolo, opcaoMetrica, nomeMetrica, dataRef);
            } else {
                graficoVertical(enfermarias, tamanhosBarra, simbolo, opcaoMetrica, nomeMetrica, dataRef);
            }
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }


    private void graficoHorizontal(List<Enfermaria> enfermarias, double valoresMetrica[], int tamanhosBarra[], char simbolo, int opcaoMetrica, String nomeMetrica, Data dataRef) {
        System.out.println("\nGRÁFICO HORIZONTAL DE " + nomeMetrica + " (" + dataRef.toString() + ")\n");
        for (int i = 0; i < enfermarias.size(); i++) {
            Enfermaria enfermaria = enfermarias.get(i);
            StringBuilder barra = new StringBuilder();
            for (int j = 0; j < tamanhosBarra[i]; j++) {
                barra.append(simbolo);
            }
            if (opcaoMetrica == 3) {
                System.out.printf("%-6s [%4.1f dias] [%s]\n", enfermaria.getIdEnfermaria(), valoresMetrica[i], barra.toString());
            } else {
                System.out.printf("%-6s [%3.0f%%] [%s]\n",
                        enfermaria.getIdEnfermaria(),
                        valoresMetrica[i],
                        barra.toString());
            }
        }
    }

    private void graficoVertical(List<Enfermaria> enfermarias, int[] alturas, char simbolo, int opcaoMetrica, String nomeMetrica, Data dataRef) {
        int numEnfermarias = enfermarias.size();
        int alturaMax = 0;

        for (int altura : alturas) {
            if (altura > alturaMax) {
                alturaMax = altura;
            }
        }
        System.out.println("\nGRÁFICO VERTICAL DE " + nomeMetrica + " (" + dataRef.toString() + ")\n");

        if (alturaMax == 0) {
            System.out.println("As enfermarias selecionadas têm um valor de 0 para esta métrica.");
            return;
        }
        for (int nivel = alturaMax; nivel > 0; nivel--) {
            //eixo do y
            if (opcaoMetrica == 3) {
                System.out.printf("%4d |", nivel);
            } else {
                System.out.printf("%4d%% |", nivel * 2);
            }
            for (int i = 0; i < numEnfermarias; i++) {
                if (alturas[i] >= nivel) {
                    System.out.print("  " + simbolo + "  ");
                } else {
                    System.out.print("     ");
                }
            }
            System.out.println();
        }
        //eixo do x
        System.out.print("-------");
        for (int i = 0; i < numEnfermarias; i++) {
            System.out.print("-----");
        }
        System.out.println();

        //legenda
        System.out.print("       ");
        for (Enfermaria enfermaria : enfermarias) {
            String id = enfermaria.getIdEnfermaria();
            if (id.length() > 4) {
                id = id.substring(0, 4); // corta o id se for muito grande
            }
            System.out.printf("%-4s ", id);
        }
        System.out.println("\n");
    }

    private void alterarCamasEnfermarias() {
        System.out.println("ALTERAR CAMAS TOTAIS EM TODAS AS ENFERMARIAS");
        double percentagem = leitor.lerDouble("Introduza a percentagem de variação (ex: 10 para aumentar 10%, -5 para reduzir 5%):");

        AnalisadorEstatistico.alterarCamas(hospital.getEnfermarias(), percentagem);
        System.out.println("Camas alteradas com sucesso em todas as enfermarias.");
    }

    private void mostrarRankingPressao() {
        try {
            System.out.println("\nRANKING DE ENFERMARIAS POR PRESSAO");
            DataAvancada dataRef = DataAvancada.parseData(leitor.lerString("Introduza a data de referência (AAAA-MM-DD):"));
            List<AnalisadorEstatistico.ResultadoPressao> ranking = AnalisadorEstatistico.calcularRankingPressao(hospital.getEnfermarias(), dataRef);

            if (ranking.isEmpty()) {
                System.out.println("Não existem enfermarias registadas com camas para calcular o ranking.");
                return;
            }
            System.out.println("RANKING DE ENFERMARIAS POR PRESSAO (ordem decrescente)");
            for (AnalisadorEstatistico.ResultadoPressao rp : ranking) {
                System.out.printf("Enfermaria: %-6s | Score Ocup: %d | Score Turnover: %d | Índice de Pressão: %.1f | [%s]\n", rp.getEnfermaria().getIdEnfermaria(), rp.getScoreOcup(), rp.getScoreTurnover(), rp.getIndicePressao(), rp.getClassificacao());
            }
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void mostrarPercentagemPressao() {
        try {
            System.out.println("PERCENTAGEM DE ENFERMARIAS EM PRESSAO");
            DataAvancada dataRef = DataAvancada.parseData(leitor.lerString("Introduza a data de referência (AAAA-MM-DD):"));
            double percentagem = AnalisadorEstatistico.calcularPercentagemEnfermariasEmPressao(hospital.getEnfermarias(), dataRef);
            System.out.printf("Percentagem de enfermarias em pressão (>85%%): %.2f%%\n", percentagem);
        } catch (DataInvalidaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

//MÉTODOS AUXILIARES

    /**
     * Metodo que le o input do teclado e garante que o utlizador introduziu um numero dentro dos limites do menu
     *
     * @param limiteInf ->numero minimo que o utilizador pode inserir (menor opcao do menu)
     * @param limiteSup -> numero maximo que o utilizador pode inseir (maior opcao do menu)
     * @return opcao introduzida pelo utilizador dentro dos limites definidos
     */
    public int lerOpcao(int limiteInf, int limiteSup) {
        int opcao = 0;
        boolean opcaoValida = false;
        while (!opcaoValida) {
            opcao = leitor.lerInteiro("");
            if (opcao >= limiteInf && opcao <= limiteSup) {
                opcaoValida = true;
            } else {
                System.out.println("Opção inválida. Tem de ser entre " + limiteInf + " e " + limiteSup + ".");
            }
        }
        return opcao;
    }

    private static String gerarBarraHorizontal(double taxa, char simbolo) {
        // 50 caracteres = 100%, por isso: taxa * 50 / 100
        int preenchidos = (int) (taxa * 50 / 100);
        // Garantir que não ultrapassa 50
        if (preenchidos > 50) {
            preenchidos = 50;
        }

        int vazios = 50 - preenchidos;
        StringBuilder barra = new StringBuilder();
        for (int i = 0; i < preenchidos; i++) {
            barra.append(simbolo);
        }
        for (int i = 0; i < vazios; i++) {
            barra.append(" ");
        }
        return "[" + barra.toString() + "]";
    }
}





