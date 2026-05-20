/**
 * Adpatacao da classe Data fornecida pelos professores (adicao de alguns metodos e alteracoes noutros)
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
     * Constrói uma instância de Data com a data por omissão.
     */
    public Data(Data data) {
        ano = data.ano;
        mes = data.mes;
        dia = data.dia;
    }

    /**
     * Constrói uma instância de Data com a data por omissão.
     */
    public Data() {
        ano = ANO_POR_OMISSAO;
        mes = MES_POR_OMISSAO;
        dia = DIA_POR_OMISSAO;
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
     * @return caraterísticas da data
     */
    public String toString() {
        return this.determinarDiaDaSemana() + ", " + this.dia + " de " + nomeMes[mes] + " de " + ano;
    }

    /**
     * Devolve a data no formato:%04d/%02d/%02d.
     *
     * @return caraterísticas da data
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
     * Devolve true se a data for maior do que a data recebida por parametro.
     * Se a data for menor ou igual à data recebida por parametro, devolve
     * false.
     *
     * @param outraData a outra data com a qual se compara a data
     * @return true se a data for maior do que a data recebida por parametro,
     * caso contrario devolve false
     */
    public boolean isMaior(Data outraData) {
        int totalDias = contarDias();
        int totalDias1 = outraData.contarDias();

        return totalDias > totalDias1;
    }

    /**
     * Devolve a diferença em número de dias entre a data e a data recebida por
     * parâmetro.
     *
     * @param outraData a outra data com a qual se compara a data para calcular
     *                  a diferença do número de dias
     * @return diferença em número de dias entre a data e a data recebida por
     * parâmetro
     */
    public int calcularDiferenca(Data outraData) {
        int totalDias = this.contarDias();
        int totalDias1 = outraData.contarDias();

        return Math.abs(totalDias - totalDias1);
    }

    /**
     * Devolve a diferença em número de dias entre a data e a data recebida por
     * parâmetro com ano, mês e dia
     *
     * @param ano o ano da data com a qual se compara a data para calcular a
     *            diferença do número de dias
     * @param mes o mês da data com a qual se compara a data para calcular a
     *            diferença do número de dias
     * @param dia o dia da data com a qual se compara a data para calcular a
     *            diferença do número de dias
     * @return diferença em número de dias entre a data e a data recebida por
     * parâmetro com ano, mês e dia
     */
    public int calcularDiferenca(int ano, int mes, int dia) {
        int totalDias = contarDias();
        Data outraData = new Data(ano, mes, dia);
        int totalDias1 = outraData.contarDias();

        return Math.abs(totalDias - totalDias1);
    }

    /**
     * Devolve true se o ano passado por parâmetro for bissexto.
     * Se o ano passado por parâmetro não for bissexto, devolve false.
     *
     * @param ano o ano a validar
     * @return true se o ano passado por parâmetro for bissexto, caso contrário
     * devolve false
     */
    public static boolean isAnoBissexto(int ano) {
        return ano % 4 == 0 && ano % 100 != 0 || ano % 400 == 0;
    }

    /**
     * Devolve o número de dias desde o dia 1/1/1 até à data.
     *
     * @return número de dias desde o dia 1/1/1 até à data
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
     * Incrementa data em exatamente um dia, gerindo automaticamente a passagem de meses e anos (incluindo anos bissextos)
     */
    public void avancarUmDia() {
        // Verificar quantos dias tem o mês atual
        int diasDoMes = diasPorMes[mes];

        // Ajustar para anos bissextos em fevereiro
        if (mes == 2 && isAnoBissexto(ano)) {
            diasDoMes = 29;
        }

        // Se ainda há dias no mês, avança o dia
        if (dia < diasDoMes) {
            dia++;
            // Se chegou ao fim do mês, avança para o mês seguinte
        } else if (mes < 12) {
            dia = 1;
            mes++;
            // Se chegou ao fim do ano, avança para o ano seguinte
        } else {
            dia = 1;
            mes = 1;
            ano++;
        }
    }

    /**
     * Verifica se dois objetos Data sao identicos
     *
     * @param outroObjeto -> outra data
     * @return true (se os objetos data forem iguais) ou false (se os objetos data nao forem iguais)
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
        if (this.ano != other.ano) {
            return false;
        }
        if (this.mes != other.mes) {
            return false;
        }
        if (this.dia != other.dia) {
            return false;
        }
        return true;
    }

    /**
     * Compara duas datas para efeitos de ordenacao, devolvendo 1, -1 ou 0 conforme a precedencia temporal
     *
     * @param outraData -> outra data a comparar
     * @return 1(data original depois da outra data), 0 (mesma data), -1 (outra data depois da data original)
     */
    @Override
    public int compareTo(Data outraData) {
        if (this.isMaior(outraData))
            return 1;
        else if (this.equals(outraData))
            return 0;
        else
            return -1;
    }

    /**
     * Converte uma representacao textual de uma data (AAAA-MM-DD) num objeto Data
     *
     * @param dataStr -> data em String (AAAA-MM-DD)
     * @return objeto data
     */
    public static Data parseData(String dataStr) {
        String[] data = dataStr.split("-");
        int ano = Integer.parseInt(data[0]);
        int mes = Integer.parseInt(data[1]);
        int dia = Integer.parseInt(data[2]);
        return new Data(ano, mes, dia);
    }
}

