/**
 * Classe para representação e manipulação de datas.
 * Implementa a interface Comparable para permitir a ordenação cronológica.
 */
public class Data implements Comparable<Data> {

    /**
     * O ano da data.
     */
    private int ano;

    /**
     * O mês da data.
     */
    private int mes;

    /**
     * O dia da data.
     */
    private int dia;

    /**
     * O ano da data por omissão.
     */
    private static final int ANO_POR_OMISSAO = 1;

    /**
     * O mês da data por omissão.
     */
    private static final int MES_POR_OMISSAO = 1;

    /**
     * O dia da data por omissão.
     */
    private static final int DIA_POR_OMISSAO = 1;

    /**
     * Nomes dos dias da semana.
     */
    private static String[] nomeDiaDaSemana = {"Domingo", "Segunda-feira",
            "Terça-feira", "Quarta-feira",
            "Quinta-feira", "Sexta-feira",
            "Sábado"};

    /**
     * Número de dias de cada mês do ano.
     */
    private static int[] diasPorMes = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};

    /**
     * Nomes dos meses do ano.
     */
    private static String[] nomeMes = {"Inválido", "Janeiro", "Fevereiro",
            "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro",
            "Outubro", "Novembro", "Dezembro"};

    /**
     * Constrói uma instância de Data recebendo o ano, o mês e o dia.
     *
     * @param ano o ano da data
     * @param mes o mês da data
     * @param dia o dia da data
     */
    public Data(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

    /**
     * Constrói uma instância de Data duplicando outra data (Construtor de cópia).
     *
     * @param outraData a data a copiar
     */
    public Data(Data outraData) {
        if (outraData != null) {
            this.ano = outraData.ano;
            this.mes = outraData.mes;
            this.dia = outraData.dia;
        } else {
            this.ano = ANO_POR_OMISSAO;
            this.mes = MES_POR_OMISSAO;
            this.dia = DIA_POR_OMISSAO;
        }
    }

    /**
     * Constrói uma instância de Data com a data por omissão (1/1/1).
     */
    public Data() {
        this.ano = ANO_POR_OMISSAO;
        this.mes = MES_POR_OMISSAO;
        this.dia = DIA_POR_OMISSAO;
    }

    /**
     * Devolve o ano da data.
     *
     * @return ano da data
     */
    public int getAno() {
        return ano;
    }

    /**
     * Devolve o mês da data.
     *
     * @return mês da data
     */
    public int getMes() {
        return mes;
    }

    /**
     * Devolve o dia da data.
     *
     * @return dia da data
     */
    public int getDia() {
        return dia;
    }

    /**
     * Modifica o ano, o mês e o dia da data.
     *
     * @param ano o novo ano da data
     * @param mes o novo mês da data
     * @param dia o novo dia da data
     */
    public void setData(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

    /**
     * Devolve a descrição textual da data no formato: diaDaSemana, dia, mês, ano.
     *
     * @return características da data em formato extenso
     */
    @Override
    public String toString() {
        return this.determinarDiaDaSemana() + ", " + this.dia + " de " + nomeMes[mes] + " de " + ano;
    }

    /**
     * Devolve a data no formato: AAAA/MM/DD.
     *
     * @return string formatada da data
     */
    public String toAnoMesDiaString() {
        return String.format("%04d/%02d/%02d", ano, mes, dia);
    }

    /**
     * Devolve o dia da semana da data.
     *
     * @return dia da semana da data
     */
    public String determinarDiaDaSemana() {
        int totalDias = contarDias();
        totalDias = totalDias % 7;
        return nomeDiaDaSemana[totalDias];
    }

    /**
     * Devolve true se a data for estritamente maior do que a outra data recebida por parâmetro.
     *
     * @param outraData a outra data com a qual se compara
     * @return true se for maior, caso contrário false
     */
    public boolean isMaior(Data outraData) {
        if (outraData == null) {
            return true;
        }
        return this.contarDias() > outraData.contarDias();
    }

    /**
     * Devolve a diferença absoluta em número de dias entre a data e a outra data recebida por parâmetro.
     *
     * @param outraData a outra data com a qual se compara
     * @return diferença absoluta em dias
     */
    public int calcularDiferenca(Data outraData) {
        if (outraData == null) {
            return 0;
        }
        return Math.abs(this.contarDias() - outraData.contarDias());
    }

    /**
     * Devolve a diferença absoluta em número de dias entre a data e uma data definida por inteiros.
     */
    public int calcularDiferenca(int ano, int mes, int dia) {
        Data outraData = new Data(ano, mes, dia);
        return Math.abs(this.contarDias() - outraData.contarDias());
    }

    /**
     * Devolve true se o ano passado por parâmetro for bissexto.
     *
     * @param ano o ano a validar
     * @return true se for bissexto, caso contrário false
     */
    public static boolean isAnoBissexto(int ano) {
        return (ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0);
    }

    /**
     * Devolve o número total de dias desde o dia 1/1/1 até à data atual.
     */
    private int contarDias() {
        int totalDias = 0;

        for (int i = 1; i < ano; i++) {
            totalDias += isAnoBissexto(i) ? 366 : 365;
        }
        for (int i = 1; i < mes; i++) {
            totalDias += diasPorMes[i];
        }
        totalDias += (isAnoBissexto(ano) && mes > 2) ? 1 : 0;
        totalDias += dia;

        return totalDias;
    }

    /**
     * Incrementa a data em exatamente um dia, gerindo de forma automática a passagem
     * de meses e anos (incluindo as especificidades dos anos bissextos).
     */
    public void avancarUmDia() {
        int diasDoMes = diasPorMes[mes];

        if (mes == 2 && isAnoBissexto(ano)) {
            diasDoMes = 29;
        }

        if (dia < diasDoMes) {
            dia++;
        } else if (mes < 12) {
            dia = 1;
            mes++;
        } else {
            dia = 1;
            mes = 1;
            ano++;
        }
    }

    /**
     * Compara se este objeto de data é idêntico a outro objeto passado.
     */
    @Override
    public boolean equals(Object outroObjeto) {
        if (this == outroObjeto) {
            return true;
        }
        if (outroObjeto == null || getClass() != outroObjeto.getClass()) {
            return false;
        }
        final Data other = (Data) outroObjeto;
        return this.ano == other.ano && this.mes == other.mes && this.dia == other.dia;
    }

    /**
     * Implementação obrigatória da interface Comparable para ordenação cronológica.
     * Devolve 1 se for posterior, -1 se for anterior e 0 se for idêntica.
     */
    @Override
    public int compareTo(Data outraData) {
        if (this.isMaior(outraData)) {
            return 1;
        } else if (this.equals(outraData)) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Converte uma representação textual de uma data no formato ISO (AAAA-MM-DD)
     * num objeto estruturado da classe Data.
     *
     * @param dataStr string da data (ex: "2026-05-20")
     * @return uma nova instância da classe Data
     */
    public static Data parseData(String dataStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return new Data();
        }
        String[] partes = dataStr.split("-");
        int ano = Integer.parseInt(partes[0].trim());
        int mes = Integer.parseInt(partes[1].trim());
        int dia = Integer.parseInt(partes[2].trim());
        return new Data(ano, mes, dia);
    }
}