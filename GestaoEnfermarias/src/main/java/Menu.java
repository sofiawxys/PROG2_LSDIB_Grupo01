import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.FileNotFoundException;
import java.io.File;


public class Menu {
    //CONSTANTES
    private static final int LIMITE_INF_OPCAO = 1;
    private static final int LIMITE_SUP_OPCAO = 6;
    private static final int LIMITE_SUP_SUBOPCAO = 2;

    private Hospital hospital;
    private Scanner scanner;

    public Menu() {

        this.hospital = new Hospital("Hospital S.João");
        this.scanner = new Scanner(System.in);
    }

    public void iniciar(){
            int opcao = 0;
            while (opcao != 6) {
                mostrarMenu();
                opcao = lerOpcao(scanner, LIMITE_INF_OPCAO, LIMITE_SUP_OPCAO);
                executarOpcao(opcao);
            }
            scanner.close();
    }

    private void executarOpcao(int opcao){
        switch (opcao) {
            case 1:
                try {
                    GeradorDados.criarDadosAutomaticos(hospital);
                } catch (DataInvalidaException e) {
                    System.out.println("Erro de datas: " + e.getMessage());
                } catch (CapacidadeExcedidaException e) {
                    System.out.println("Capacidade excedida: " + e.getMessage());
                }
                break;

            case 2:
                try {
                    carregarDadosFicheiro(hospital);
                }catch (FileNotFoundException e) {
                    System.out.println("Erro ao ler ficheiro");
                }
                break;

            case 3:
                inserirDadosConsola(hospital, scanner);
                break;
            case 4:
                mostrarIndicadoresOcupacao(hospital, scanner);
                break;

            case 5:
                mostrarEstadoPressao(hospital, scanner);
                break;
            case 6:
                mostrarListagens(hospital, scanner);
                break;
            case 7:
                mostrarTabelaOcupacao(hospital, scanner);
                break;
            case 8:
                mostrarGraficoBarras(hospital, scanner);
                break;
            case 9:
                System.out.println("A guardar a sessão atual...");
                GestorFicheiros.guardarDados(hospital,"hospital_dados.dat");
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
    System.out.println("2. Carregar dados de ficheiro .csv (sistema antigo)");
    System.out.println("3. Inserir dados na consola");
    System.out.println("4. Mostrar cálculo de indicadores de ocupação");
    System.out.println("5. Mostrar estado e indicadores de pressão");
    System.out.println("6. Apresentar listagens ordenadas");
    System.out.println("7. Mostrar tabela de ocupação");
    System.out.println("8. Mostrar grafico barras");
    System.out.println("9. Sair");
    System.out.print("Escolha uma opção: ");
}

private void inserirDadosConsola(Hospital hospital, Scanner scanner, Enfermaria enfermaria) { // REVER ENFERMARIA POR PARÂMETRO???
    System.out.println("Inserir enfermaria");
    System.out.println("Inserir episódio");
    int opcao = lerOpcao(scanner, 1, 2);
    if (opcao == 1) {
        inserirEnfermaria(hospital, scanner);
    }else{
        inserirEpisodio(enfermaria, scanner);
    }
}
private void inserirEnfermaria(Hospital hospital, Scanner scanner) {
    System.out.println("1. Enfermaria Geral");
    System.out.println("2. Enfermaria Psiquiátrica");
    System.out.println("3. Enfermaria Cuidados Intensivos");
    int tipo = lerOpcao(scanner, 1, 3);

    try {
        System.out.print("ID da enfermaria: ");
        String id = scanner.nextLine();

        System.out.print("Número de camas: ");
        int numCamas = Integer.parseInt(scanner.nextLine());

        if (tipo == 1) {
            System.out.print("Número de acompanhantes: ");
            int numAcomp = Integer.parseInt(scanner.nextLine());

            System.out.print("Recursos: ");
            String[] recursos = scanner.nextLine().split(",");

            EnfermariaGeral eg = new EnfermariaGeral(id, numCamas, numAcomp);
            for (String recurso : recursos) {
                eg.adicionarRecurso(recurso.trim());
            }
            hospital.adicionarEnfermaria(eg)
        } else {
            if (tipo == 2) {
                System.out.println("Horário de visitas: ");
                String horarioVisitas = scanner.nextLine();

                System.out.println("Nível de segurança: ");
                String nivelSeguranca = scanner.nextLine();

                hospital.adicionarEnfermaria(new EnfermariaPsiquiatrica(id, numCamas, horarioVisitas, nivelSeguranca));

            } else {
                if (tipo == 3) {
                    System.out.println("Horário visitas: ");
                    String horarioVisitas = scanner.nextLine();

                    System.out.println("Pressão atmosférica: ");
                    double pressaoAtmosferica = Double.parseDouble(scanner.nextLine());

                    System.out.println("Pressão de referência: ");
                    double pressaoReferencia = Double.parseDouble(scanner.nextLine());

                    hospital.adicionarEnfermaria(new EnfermariaCuidadosIntensivos(id, numCamas, horarioVisitas, pressaoAtmosferica, pressaoReferencia));
                }
            }
        }
        System.out.println("Enfermaria criada com sucesso!");
    } catch (NumberFormatException e) {
        System.out.println("Erro: introduza um número válido.");
    } catch (IllegalArgumentException e) {
        System.out.println("Erro: " + e.getMessage());
    }
}
private void inserirEpisodio(Enfermaria enfermaria, Scanner scanner) {
    try{
        System.out.print("ID da cama: ");
        String idCama = scanner.nextLine();
        System.out.println("Data de admissão (AAAA-MM-DD): ");
        DataAvancada dataAdmissao = DataAvancada.parseData(scanner.nextLine());
        System.out.println("Data de alta (AAAA-MM-DD) ou Enter para deixar vazio ");
        String dataAltaStr = scanner.nextLine();
        DataAvancada dataAlta = null;
        if (!dataAltaStr.isEmpty()) {
            dataAlta = DataAvancada.parseData(dataAltaStr);
        }
        enfermaria.adicionarEpisodio(new Episodio(idCama, dataAdmissao, dataAlta));
        System.out.println("Episódio criado com sucesso!");
    }catch (NumberFormatException e) {
        System.out.println("Erro: o ID da cama tem de ser um número inteiro.");
    }catch (CapacidadeExcedidaException e) {
        System.out.println("Erro: " + e.getMessage());
    }
}

/**
 * Metodo que solicita uma enfermaria e uma data ao utlizador e apresenta as metricas de ocupacao e LoS
 *
 * @param hospital -> hospital desejado
 * @param scanner
 */
private void mostrarIndicadoresOcupacao(Hospital hospital, Scanner scanner) {
    System.out.print("Introduza o ID da enfermaria: ");
    String idEnfermaria = scanner.nextLine();

    Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

    if (enfermaria == null) {
        System.out.println("Enfermaria não encontrada.");
        return;
    }
    System.out.println("Enfermaria encontrada: " + enfermaria.getIdEnfermaria());
    System.out.println(enfermaria.toString());

    //Pedir ao utilizador para introduzir a data de referência
    System.out.println("\nINDICADORES DE OCUPAÇÃO");
    System.out.println("Introduza a data de referência (AAAA-MM-DD): ");
    String dataReferenciaStr = scanner.nextLine();
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

}

/**
 * Metodo que faz o carregamento de ficheiros CSV e imprime no ecra eventuais erros de validacao
 *
 * @param hospital -> hospital desejado
 * @throws FileNotFoundException
 */
private void carregarDadosFicheiro(Hospital hospital) throws FileNotFoundException {
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
 * @param hospital -> hospital desejado
 * @param scanner
 */
private void mostrarEstadoPressao(Hospital hospital, Scanner scanner) {
    System.out.print("Introduza o ID da enfermaria: ");
    String idEnfermaria = scanner.nextLine();

    Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

    if (enfermaria == null) {
        System.out.println("Enfermaria não encontrada.");
        return;
    }
    System.out.println("Enfermaria encontrada!");

    //Pedir ao utilizador para introduzir as datas
    System.out.println("Introduza a data de início (AAAA-MM-DD): ");
    String dataInicioStr = scanner.nextLine();
    System.out.println("Introduza data de fim (AAAA-MM-DD): ");
    String dataFimStr = scanner.nextLine();
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
        System.out.println(dataAtual.toString() + " -> ");
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
}

/**
 * Metodo que permite listar enfermarias ordenadas por ocupacao ou listar os episodios de uma enfermaira ordenados por admissao
 *
 * @param hospital -> hospital desejado
 * @param scanner
 */
private void mostrarListagens(Hospital hospital, Scanner scanner) {
    System.out.println("\n1. Listar Enfermarias por taxa de ocupação");
    System.out.println("\n2. Listar Episódios de uma Enfermaria por data de admissão");
    System.out.println("Escolha a opção: ");
    int subopcao = lerOpcao(scanner, LIMITE_INF_OPCAO, LIMITE_SUP_SUBOPCAO);
    switch (subopcao) {
        case 1:
            System.out.println("Introduza a data de referência (AAAA-MM-DD): ");
            String dataReferenciaStr = scanner.nextLine();
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
            System.out.println("Introduza o ID da Enfermaria: ");
            Enfermaria enfermaria = hospital.procurarEnfermaria(scanner.nextLine());
            if (enfermaria != null) {
                List<Episodio> episodios = new ArrayList<>(enfermaria.getEpisodios());
                episodios.sort(new Comparator<Episodio>() {
                    @Override
                    public int compare(Episodio e1, Episodio e2) {
                        return e1.getDataAdmissao().compareTo(e2.getDataAdmissao());
                    }
                });
                System.out.println("\n--- Episódios Ordenados (Admissão) ---");
                for (Episodio ep : episodios) {
                    System.out.println(ep.toString());
                }
            } else {
                System.out.println("Erro: Enfermaria não encontrada!");
            }
            break;
    }
}
private void mostrarTabelaOcupacao(Hospital hospital, Scanner scanner) {
    System.out.print("ID da enfermaria: ");
    String idEnfermaria = scanner.nextLine();
    Enfermaria enfermaria = hospital.procurarEnfermaria(idEnfermaria);

    if (enfermaria == null) {
        System.out.println("Enfermaria não encontrada.");
        return;
    }

    System.out.print("Data de início (AAAA-MM-DD): ");
    DataAvancada dataInicio = DataAvancada.parseData(scanner.nextLine());
    System.out.print("Data de fim (AAAA-MM-DD): ");
    DataAvancada dataFim = DataAvancada.parseData(scanner.nextLine());

    // Cabeçalho
    System.out.println();
    System.out.printf("%-12s | %-12s | %8s | %11s | %6s | %9s | %s%n",
            "Enfermaria", "Data", "Ocupadas", "CamasTotais", "%Ocup", "Turnover%", "Barra");
    System.out.println("-".repeat(95)); // posso usar .repeat???

    int totalDias = dataInicio.calcularDiferenca(dataFim) + 1;
    DataAvancada dataAtual = new DataAvancada(dataInicio);

    for (int i = 0; i < totalDias; i++) {
        int ocupadas   = enfermaria.calcularOcupacao(dataAtual);
        int totalCamas = enfermaria.getNumCamas();
        double percOcup = enfermaria.calcularTaxaOcupacao(dataAtual);
        int admissoes  = enfermaria.calcularAdmissoes(dataAtual);
        int altas      = enfermaria.calcularAltas(dataAtual);
        double turnover = (double)(admissoes + altas) / totalCamas * 100;
        String barra   = gerarBarraHorizontal(percOcup, '#');

        System.out.printf("%-12s | %-12s | %8d | %11d | %5.1f%% | %8.1f%% | %s%n",
                enfermaria.getIdEnfermaria(),
                dataAtual.toAnoMesDiaString(),
                ocupadas, totalCamas, percOcup, turnover, barra);

        dataAtual.avancarUmDia();
    }
}
    private void mostrarGraficoBarras(Hospital hospital, Scanner scanner) {
        System.out.print("Data de referência (AAAA-MM-DD): ");
        Data dataRef = DataAvancada.parseData(scanner.nextLine()); //aqui guardamos a data numa variável do tipo Data e não DataAvancada, pois o método usado para a lista pede Data

        char simbolo = "#";

        System.out.println("Orientação:");
        System.out.println("1. Horizontal");
        System.out.println("2. Vertical");
        int orientacao = lerOpcao(scanner, 1, 2);

        List<Enfermaria> enfermarias = hospital.listarEnfermariasOrdenadasPorOcupacao(dataRef);
        if (enfermarias.isEmpty()) {
            System.out.println("Não foram encontradas enfermarias registadas");
            return;
        }

        if (orientacao == 1) {
            graficoHorizontal(enfermarias, dataRef, simbolo);
        } else {
            graficoVertical(enfermarias, dataRef, simbolo);
        }
    }
    private void graficoHorizontal(List<Enfermaria> enfermarias, Data dataRef, char simbolo) {
        System.out.println("GRÁFICO HORIZONTAL DE OCUPAÇÃO ");

        for (Enfermaria enfermaria : enfermarias) {
            double taxaOcupacao = enfermaria.calcularTaxaOcupacao(dataRef);
            int tamanhoBarra = (int) Math.round(taxaOcupacao / 2.0);
            if (tamanhoBarra < 0) {
                tamanhoBarra = 0;
            }
            StringBuilder barra = new StringBuilder();
            for (int i = 0; i < tamanhoBarra; i++) {
                barra.append(simbolo);
            }
            System.out.printf("%-6s [%3.0f%%] [%s]\n",
                    enfermaria.getIdEnfermaria(),
                    taxaOcupacao,
                    barra.toString());
        }
    }
    private void graficoVertical(List<Enfermaria> enfermarias, Data dataRef, char simbolo){
        int numEnfermarias = enfermarias.size();
        int[] alturas = new int[numEnfermarias];

        for (int i = 0; i < numEnfermarias; i++) {
            double taxaOcupacao = enfermarias.get(i).calcularTaxaOcupacao(dataRef);
            alturas[i] = (int) Math.round(taxaOcupacao / 2.0); // 100% -> 50 de altura
            if (alturas[i] < 0) alturas[i] = 0;
        }

        System.out.println("GRÁFICO VERTICAL DE OCUPAÇÃO ");


    }


//MÉTODOS AUXILIARES

/**
 * Metodo que le o input do teclado e garante que o utlizador introduziu um numero dentro dos limites do menu
 *
 * @param scanner
 * @param limiteInf ->numero minimo que o utilizador pode inserir (menor opcao do menu)
 * @param limiteSup -> numero maximo que o utilizador pode inseir (maior opcao do menu)
 * @return opcao introduzida pelo utilizador dentro dos limites definidos
 */
public int lerOpcao(Scanner scanner, int limiteInf, int limiteSup) {
    int opcao = Integer.parseInt(scanner.nextLine());
    while (opcao < limiteInf || opcao > limiteSup) {
        System.out.println("Opção inválida. Tente novamente.");
        opcao = Integer.parseInt(scanner.nextLine());
    }
    return opcao;
}

    private static String gerarBarraHorizontal(double taxa, char simbolo) {
        // 50 caracteres = 100%, por isso: taxa * 50 / 100
        int preenchidos = (int) (taxa * 50 / 100);
        // Garantir que não ultrapassa 50
        if (preenchidos > 50) preenchidos = 50;
        int vazios = 50 - preenchidos;

        String barra = String.valueOf(simbolo).repeat(preenchidos)
                + " ".repeat(vazios);
        return "[" + barra + "]";
    }

}




        }
