import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.io.File;

/**
 * Classe principal que lê os inputs do utilizador, formata outputs  e possui um menu com diferentes funcionalidades
 */
public class Main {

    //CONSTANTES
    private static final int LIMITE_INF_OPCAO = 1;
    private static final int LIMITE_SUP_OPCAO = 6;
    private static final int LIMITE_SUP_SUBOPCAO = 2;

    /**
     * Metodo principal que lida com o menu
     *
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Hospital hospital = new Hospital("Hospital S.João");
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;
        while (opcao != 6) {
            mostrarMenu();
            opcao = lerOpcao(scanner, LIMITE_INF_OPCAO, LIMITE_SUP_OPCAO);

            switch (opcao) {
                case 1:
                    try {
                        GeradorDados.criarDadosAutomaticos(hospital);
                    } catch (DataInvalidaException e) {
                        System.out.println("Erro ao criar dados automáticos: " + e.getMessage());
                    } catch (CapacidadeExcedidaException e){
                        System.out.println("Erro de lotação na geração automática: " + e.getMessage());
                    }
                    break;
                case 2:
                    carregarDadosFicheiro(hospital);
                    break;
                case 3:
                    mostrarIndicadoresOcupacao(hospital, scanner);
                    break;

                case 4:
                    mostrarEstadoPressao(hospital, scanner);
                    break;
                case 5:
                    mostrarListagens(hospital, scanner);
                    break;
                case 6:
                    System.out.println("A fechar o sistema...");
            }
        }

        scanner.close();
    }

    //MÉTODOS PRINCIPAIS

    /**
     * Metodo que imprime o menu com as varias funcionalidades para o ecra
     */
    private static void mostrarMenu() {
        System.out.println("\n===MENU===");
        System.out.println("1. Criar dados automaticamente");
        System.out.println("2. Carregar dados do ficheiro");
        System.out.println("3. Mostrar cálculo de indicadores de ocupação");
        System.out.println("4. Mostrar estado e indicadores de pressão");
        System.out.println("5. Apresentar listagens ordenadas");
        System.out.println("6. Sair");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Metodo que solicita uma enfermaria e uma data ao utlizador e apresenta as metricas de ocupacao e LoS
     *
     * @param hospital -> hospital desejado
     * @param scanner
     */
    private static void mostrarIndicadoresOcupacao(Hospital hospital, Scanner scanner) {
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
    private static void carregarDadosFicheiro(Hospital hospital) throws FileNotFoundException {
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
    private static void mostrarEstadoPressao(Hospital hospital, Scanner scanner) {
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
    private static void mostrarListagens(Hospital hospital, Scanner scanner) {
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


//MÉTODOS AUXILIARES

    /**
     * Metodo que le o input do teclado e garante que o utlizador introduziu um numero dentro dos limites do menu
     *
     * @param scanner
     * @param limiteInf ->numero minimo que o utilizador pode inserir (menor opcao do menu)
     * @param limiteSup -> numero maximo que o utilizador pode inseir (maior opcao do menu)
     * @return opcao introduzida pelo utilizador dentro dos limites definidos
     */
    public static int lerOpcao(Scanner scanner, int limiteInf, int limiteSup) {
        int opcao = Integer.parseInt(scanner.nextLine());
        while (opcao < limiteInf || opcao > limiteSup) {
            System.out.println("Opção inválida. Tente novamente.");
            opcao = Integer.parseInt(scanner.nextLine());
        }
        return opcao;
    }

}


