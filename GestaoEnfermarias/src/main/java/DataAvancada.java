public class DataAvancada extends Data{

    public DataAvancada(int ano, int mes, int dia) {
        super(ano, mes, dia);
    }

    public DataAvancada(Data data) {
        super(data.getAno(), data.getMes(), data.getDia());
    }

    public DataAvancada() {
        super();
    }

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

    public static DataAvancada parseData(String dataStr) {
        String[] data = dataStr.split("-");
        int ano = Integer.parseInt(data[0]);
        int mes = Integer.parseInt(data[1]);
        int dia = Integer.parseInt(data[2]);
        return new DataAvancada(ano, mes, dia);
    }

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