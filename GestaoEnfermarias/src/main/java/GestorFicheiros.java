import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe responsável pela serialização e deserialização do objeto Hospital para ficheiro binário (.dat),
 * garantindo a persistência dos dados entre sessões.
 */

public class GestorFicheiros {
    /**
     * Serializa o estado atual do hospital e guarda-o num ficheiro binário local.
     *
     * @param hospital     a instância do hospital a ser guardada
     * @param nomeFicheiro o nome do ficheiro de destino
     */
    public static void guardarDados(Hospital hospital, String nomeFicheiro) {
        try {
            FileOutputStream fileOut = new FileOutputStream(nomeFicheiro);
            ObjectOutputStream outStream = new ObjectOutputStream(fileOut);

            outStream.writeObject(hospital);

            outStream.close();
            fileOut.close();
            System.out.println("\nFicheiro " + nomeFicheiro + " guardado com sucesso.");
        } catch (IOException e) {
            System.out.println("\nErro ao guardar o ficheiro");
            e.printStackTrace();
        }
    }

    /**
     * Deserializa os dados de um ficheiro binário local, reconstruindo o objeto Hospital
     *
     * @param nomeFicheiro nome do ficheiro a ser lido
     * @return a instancia recuperada do Hospital, ou null em caso de falha ou ficheiro inexistente
     */
    public static Hospital lerDados(String nomeFicheiro) {
        Hospital hospital = null;
        try {
            FileInputStream fileIn = new FileInputStream(nomeFicheiro);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            hospital = (Hospital) in.readObject();

            in.close();
            fileIn.close();
            System.out.println("Dados carregados do ficheiro " + nomeFicheiro + " com sucesso");
        } catch (IOException e) {
            System.out.println("Aviso: Ficheiro " + nomeFicheiro + " não encontrado. A iniciar um sitema vazio...");
        } catch (ClassNotFoundException c) {
            System.out.println("Erro ao ler dados");
            c.printStackTrace();
        }
        return hospital;
    }
}
