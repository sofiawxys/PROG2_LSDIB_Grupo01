import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


/**
 * Entidade central do programa que representa o complexo hospitalar
 * Responsavel por gerir a lista de todas as enfermarias, processar o carregamento de dados a partir de ficheiros CSV, manter um log de erros
 */
public class Hospital implements java.io.Serializable{

    //VARIAVEIS
    private String nome;
    private List<Enfermaria> enfermarias;
    private List<String> registoErros;

    //CONSTRUTOR

    /**
     * Construtor que define um novo hospital, que se caracteriza pelo seu nome
     *
     * @param nome -> nome do hospital
     */
    public Hospital(String nome) {
        this.nome = nome;
        this.enfermarias = new ArrayList<Enfermaria>();
        this.registoErros = new ArrayList<String>();
    }

    //Getters

    /**
     * Devolve o nome do hospital
     *
     * @return nome do hospital
     */
    public String getNome() {
        return nome;
    }

    /**
     * Devolve o registo de erros encontrados no carregamento dos ficheiros
     *
     * @return registo de erros
     */
    public List<String> getRegistoErros() {
        //devolve cópia defensiva
        return new ArrayList<String>(registoErros);
    }

    /**
     * Adiciona uma nova enfermaria a rede do hospital
     *
     * @param enfermaria -> nova enfermaria
     */
    public void adicionarEnfermaria(Enfermaria enfermaria) {
        this.enfermarias.add(enfermaria);
    }

    /**
     * Pesquisa e devolve uma enfermaria do hopsital com base no identificador
     *
     * @param id -> identificador da enfermaria
     * @return representacao textual da enfermaria
     */
    public Enfermaria procurarEnfermaria(String id) {
        for (Enfermaria enfermaria : this.enfermarias) {
            if (enfermaria.getIdEnfermaria().equals(id)) {
                return enfermaria;
            }
        }
        return null;
    }

    /**
     * Lê um ficheiro CSV e carrega os episódios clínicos, associando-os às enfermarias correspondentes.
     * Utiliza o mecanismo de try-with-resources para garantir que o ficheiro é fechado automaticamente
     * após a leitura, prevenindo fugas de memória e ficheiros trancados mesmo em caso de erro.
     *
     * @param nomeFicheiroCSV o nome ou caminho do ficheiro CSV contendo os dados dos episódios
     */
    public void carregarEpisodios(String nomeFicheiroCSV) {
        File ficheiro = new File(nomeFicheiroCSV);

        try (Scanner lerFicheiro = new Scanner(ficheiro)) {
            if (lerFicheiro.hasNextLine()) {
                lerFicheiro.nextLine();
            }
            while (lerFicheiro.hasNextLine()) {
                processarLinhaEpisodio(lerFicheiro.nextLine());
            }
        } catch (FileNotFoundException e) {
            // Apenas regista o erro
            registoErros.add("Ficheiro não encontrado: " + nomeFicheiroCSV);
        }
    }

    /**
     * Analisa e valida uma única linha de texto do ficheiro de episódios clínicos.
     * Extrai as informações da cama e das datas, validando a integridade dos dados e
     * associando o episódio à enfermaria correspondente.
     * Centraliza o tratamento de erros usando múltiplos blocos catch para lidar com
     * falhas de formatação numérica e exceções próprias das regras de negócio do hospital.
     *
     * @param linha a linha de texto do ficheiro CSV a ser processada
     */
    private void processarLinhaEpisodio(String linha) {
        String[] partes = linha.split(";");
        if (partes.length < 3) {
            registoErros.add("Episódio inválido por falta de dados: " + linha);
            return;
        }

        String idEnfermaria = partes[0].trim();
        Enfermaria enfermaria = procurarEnfermaria(idEnfermaria);

        if (enfermaria == null) {
            registoErros.add("Enfermaria não encontrada: " + linha);
            return;
        }

        try {
            // 1. Tenta converter o ID (lança NumberFormatException se falhar)
            int idCama = Integer.parseInt(partes[1].trim());

            // 2. Extrai as datas
            Data dataAdmissao = extrairData(partes[2].trim());
            if (dataAdmissao == null) {
                registoErros.add("Data de admissão com formato incorreto: " + linha);
                return;
            }

            Data dataAlta = null;
            if (partes.length >= 4 && !partes[3].trim().isEmpty() && !partes[3].trim().equals("-")) {
                dataAlta = extrairData(partes[3].trim());
                if (dataAlta == null) {
                    registoErros.add("Data de alta com formato incorreto: " + linha);
                    return;
                }
            }

            // 3. Tenta criar e adicionar o episódio
            Episodio ep = new Episodio(idCama, dataAdmissao, dataAlta);
            enfermaria.adicionarEpisodio(ep);

            //os diferentes erros um a um:
        } catch (NumberFormatException e) {
            registoErros.add("ID da cama não é um número válido: " + linha);

        } catch (DataInvalidaException e) {
            // Apanha especificamente os erros de datas incoerentes (ex: alta anterior à admissão)
            registoErros.add("Inconsistência nos dados de internamento: " + e.getMessage() + " [Linha: " + linha + "]");

        } catch (CapacidadeExcedidaException e) {
            // Apanha especificamente os erros de lotação (ex: enfermaria já atingiu o limite)
            registoErros.add("Inconsistência nos dados de internamento: " + e.getMessage() + " [Linha: " + linha + "]");

        } catch (Exception e) {
            // Apanha qualquer outro erro inesperado (rede de segurança final)
            registoErros.add("Erro crítico desconhecido: " + e.getMessage());
        }
    }

    /**
     * Le um ficheiro CSV, ignorando o cabecalho e cria instancias de enfermarias baseadas nos dados fornecidos
     *
     * @param nomeFicheiroCSV -> nome do ficheiro CSV
     * @throws FileNotFoundException
     */
    public void carregarEnfermarias(String nomeFicheiroCSV) throws FileNotFoundException {
        File ficheiro = new File(nomeFicheiroCSV);
        Scanner lerFicheiro = new Scanner(ficheiro);

        // Ignorar o cabeçalho
        if (lerFicheiro.hasNextLine()) {
            lerFicheiro.nextLine();
        }

        while (lerFicheiro.hasNextLine()) {
            String linha = lerFicheiro.nextLine().trim();
            if (linha.isEmpty()) {
                continue;
            }
            processarLinhaEnfermaria(linha);
        }
        lerFicheiro.close();
    }

    /**
     * Analisa e valida uma unica linha de texto do ficheiro de enfermarias, registando os diferentes erros no loh
     *
     * @param linha
     */
    private void processarLinhaEnfermaria(String linha) {
        String[] partes = linha.split(";", -1);

        if (partes.length < 3) {
            registoErros.add("Enfermaria inválida por falta de dados: " + linha);
            return;
        }

        String id = partes[0].trim();
        String numCamasStr = partes[1].trim();
        String tipo = partes[2].trim();

        if (id.isEmpty()) {
            registoErros.add("ID de enfermaria vazio: " + linha);
            return;
        }

        if (!isNumero(numCamasStr)) {
            registoErros.add("Número de camas inválido: " + linha);
            return;
        }
        int numCamas = Integer.parseInt(numCamasStr);

        if (tipo.equals("GERAL")) {
            if (partes.length < 4 || !isNumero(partes[3].trim())) {
                registoErros.add("Número de acompanhantes inválido: " + linha);
                return;
            }
            int numAcompanhantes = Integer.parseInt(partes[3].trim());
            EnfermariaGeral eg = new EnfermariaGeral(id, numCamas, numAcompanhantes);

            // Recursos são opcionais
            if (partes.length >= 5 && !partes[4].trim().isEmpty()) {
                String[] recursos = partes[4].split(",");
                for (String recurso : recursos) {
                    eg.adicionarRecurso(recurso.trim());
                }
            }
            this.enfermarias.add(eg);

        } else if (tipo.equals("PSIQUIATRICA")) {
            if (partes.length < 5 || partes[3].trim().isEmpty() || partes[4].trim().isEmpty()) {
                registoErros.add("Dados insuficientes para Enfermaria Psiquiátrica: " + linha);
                return;
            }
            String horario = partes[3].trim();
            String nivelSeguranca = partes[4].trim();
            this.enfermarias.add(new EnfermariaPsiquiatrica(id, numCamas, horario, nivelSeguranca));

        } else if (tipo.equals("ECI")) {
            if (partes.length < 6 || partes[3].trim().isEmpty()) {
                registoErros.add("Dados insuficientes para Enfermaria ECI: " + linha);
                return;
            }
            String horario = partes[3].trim();
            if (!isDecimal(partes[4].trim()) || !isDecimal(partes[5].trim())) {
                registoErros.add("Valores de pressão inválidos: " + linha);
                return;
            }
            double pressaoAtual = Double.parseDouble(partes[4].trim());
            double pressaoRef = Double.parseDouble(partes[5].trim());

            this.enfermarias.add(new EnfermariaCuidadosIntensivos(id, numCamas, horario, pressaoAtual, pressaoRef));
        } else {
            registoErros.add("Tipo de enfermaria desconhecido (" + tipo + "): " + linha);
        }
    }

    /**
     * Converte uma string de data no formato AAAA-MM-DD para um objeto da classe Data.
     * Utiliza um bloco try-catch nativo para validar se os componentes da data
     * (ano, mês e dia) são números inteiros válidos.
     *
     * @param dataStr a data em formato de String a ser avaliada (ex: "2026-04-15")
     * @return um objeto da classe Data devidamente instanciado, ou null se a string
     * contiver letras ou tiver um formato inválido
     */
    private Data extrairData(String dataStr) {
        String[] partesData = dataStr.split("-");

        if (partesData.length != 3) {
            return null;
        }

        try {
            // Tenta converter. Se houver letras, salta para o catch
            int ano = Integer.parseInt(partesData[0].trim());
            int mes = Integer.parseInt(partesData[1].trim());
            int dia = Integer.parseInt(partesData[2].trim());

            return new Data(ano, mes, dia);

        } catch (NumberFormatException e) {
            return null; // A data tinha texto em vez de números, logo é inválida
        }
    }

    /**
     * Cria uma copia da lista de enfermarias e ordena-a por ordem decrescente de taxa de ocupacao na data fornecida
     *
     * @param dataReferencia -> data de referencia
     * @return lista ordenada de enfermarias por taxa de ocupacao numa certa data (ordem decrescente)
     */
    public List<Enfermaria> listarEnfermariasOrdenadasPorOcupacao(Data dataReferencia) {
        List<Enfermaria> enfermariasOrdenadasOcupacao = new ArrayList<Enfermaria>(enfermarias);

        enfermariasOrdenadasOcupacao.sort(new Comparator<Enfermaria>() {
            @Override
            public int compare(Enfermaria e1, Enfermaria e2) {
                double taxa1 = e1.calcularTaxaOcupacao(dataReferencia);
                double taxa2 = e2.calcularTaxaOcupacao(dataReferencia);
                return Double.compare(taxa2, taxa1); //ordem decrescente
            }
        });
        return enfermariasOrdenadasOcupacao;
    }


    //MÉTODOS DE VALIDAÇÃO

    /**
     * Valida se uma dada String contem exclusivamente numeros
     *
     * @param texto -> string a analisar
     * @return true(se for um numero) ou false (se nao tiver apenas numeros)
     */
    private boolean isNumero(String texto) {
        if (texto == null) {
            return false;
        }
        String textoLimpo = texto.trim();
        if (textoLimpo.isEmpty()) {
            return false;
        }

        for (int i = 0; i < textoLimpo.length(); i++) {
            char c = textoLimpo.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }

    /**
     * Valida se uma string representa um numero decimal valido
     *
     * @param texto -> string a analisar
     * @return true(se for um numero decimal) ou false (se nao for um numero decimal)
     */
    private boolean isDecimal(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        String textoLimpo = texto.trim();
        int contaPontos = 0;

        for (int i = 0; i < textoLimpo.length(); i++) {
            char c = textoLimpo.charAt(i);
            if (c == '.') {
                contaPontos++;
                if (contaPontos > 1) {
                    return false;
                }
            } else if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

    /**
     * Devolve uma representacao textual do hospital com o seu nome e numero de enfermarias
     *
     * @return representacao textual do hospital
     */
    @Override
    public String toString() {
        return String.format("Hospital: %s | Enfermarias: %d", nome, enfermarias.size());
    }
}



