import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.io.IOException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static final String DIRECTORY_WITH_PDF = "pdfs";
    public static final int PORT = 8989;
    public static final String HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File(DIRECTORY_WITH_PDF));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            Gson gson = new GsonBuilder().create();

            System.out.println("Сервер запустился");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream())
                ) {
                    List<PageEntry> searchResult = engine.search(reader.readLine());

                    StringBuilder jsonAnswer = new StringBuilder();

                    searchResult.forEach(page -> jsonAnswer.append(gson.toJson(page)));

                    writer.println(jsonAnswer);
                }
            }
        } catch (IOException e) {
            System.out.println("Не получается запустить сервер");
            e.printStackTrace();
        }
    }
}