import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    static int port = 8989;

    public static void main(String[] args) {

        try {
            BooleanSearchEngine engine = new BooleanSearchEngine();
            List<PageEntry> resultList = engine.search("Бизнес");

            System.out.println("Результат по слову \"Бизнес\"");
            for (PageEntry pageEntry : resultList) {
                System.out.println(pageEntry.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            BooleanSearchEngine engine = new BooleanSearchEngine();
            Gson gson = new GsonBuilder().create();

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String word = in.readLine();
                    List<PageEntry> page = engine.search(word);

                    if (page == null) {
                        out.println("Совпадения отсутствуют");
                    }

                    var json = gson.toJson(page);
                    out.println(json);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
