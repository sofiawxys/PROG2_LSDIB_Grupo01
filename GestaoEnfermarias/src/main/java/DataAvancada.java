/**
 * Subclasse de Data que estende as funcionalidades originais, permitindo o avanço cronológico
 * e a conversão segura de Strings para instâncias de Data.
 */
public class DataAvancada extends Data {
    /**
     * Constroi uma instancia de DataAvançada recebendo o ano, mes e dia
     *
     * @param ano ano da data
     * @param mes mes da data
     * @param dia dia da data
     */
    public DataAvancada(int ano, int mes, int dia) {
        super(ano, mes, dia);
    }

    /**
     * Construtor de cópia. Cria uma nova instância de DataAvançada a partir de um objeto Data existente
     *
     * @param data objeto data original a ser copiado
     */
    public DataAvancada(Data data) {
        super(data.getAno(), data.getMes(), data.getDia());
    }

    /**
     * Constroi uma instancia de DataAvançada com a data por omissão
     */
    public DataAvancada() {
        super();
    }

    /**
     * Incrementa a data atual em exatamente um dia, gerindo automaticamente as transições de mês,
     * ano e as regras de anos bissextos.
     */
    public void avancarUmDia() {

        int ano = getAno();
        int mes = getMes();
        int dia = getDia();

        // Verificar quantos dias tem o mês atual
        int[] diasPorMes = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
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
        setData(ano, mes, dia);
    }

    /**
     * Converte uma String com uma data para um objeto DataAvancada
     *
     * @param dataStr -> a string contendo a data (formato esperado: AAAA-MM-DD)
     * @return uma nova instancia de DataAvançada
     * @throws DataInvalidaException se a string não respeitar a formatação ou contiver caracteres invalidos
     */

    public static DataAvancada parseData(String dataStr) throws DataInvalidaException {
        try {
            String[] data = dataStr.split("-");
            if (data.length != 3) {
                throw new Exception(); //força a ida para o catch
            }
            int ano = Integer.parseInt(data[0]);
            int mes = Integer.parseInt(data[1]);
            int dia = Integer.parseInt(data[2]);
            return new DataAvancada(ano, mes, dia);

        } catch (Exception e) {
            throw new DataInvalidaException("Formato inválido, as datas devem ser AAAA-MM-DD");
        }
    }

    /**
     * Compara a igualdade entre esta data e outro objeto.
     *
     * @param obj outro objeto com o qual se compara a data
     * @return true se representarem exatamente a mesma data, caso contrário devolve false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Data)) {
            return false;
        }
        final Data d = (Data) obj;

        return this.getAno() == d.getAno() && this.getMes() == d.getMes() && this.getDia() == d.getDia();
    }

    /**
     * Compara esta data com outra data fornecida para efeitos de ordenação cronológica.
     *
     * @param d a outra data a comparar
     * @return 1 se esta data for posterior, 0 se forem iguais, ou -1 se for anterior
     */
    public int compareTo(Data d) {
        if (this.isMaior(d)) {
            return 1;
        } else {
            if (this.equals(d)) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}