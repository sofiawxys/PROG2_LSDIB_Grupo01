import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GestorFicheiros {
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
