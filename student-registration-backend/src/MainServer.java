import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class MainServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/register", new RegisterHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }

    static class RegisterHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String response;

            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    response = "Method Not Allowed";
                    exchange.sendResponseHeaders(405, response.length());
                } else {

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
                    );

                    String formData = br.readLine();

                    Map<String, String> params = parseFormData(formData);

                    insertStudent(params);

                    response = "<h2 style='color:green;'>Student Registered Successfully!</h2>";
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                }

            } catch (Exception e) {
                e.printStackTrace();
                response = "<h2 style='color:red;'>Server Error Occurred</h2>";
                exchange.sendResponseHeaders(500, response.getBytes().length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {

            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = keyValue.length > 1
                        ? URLDecoder.decode(keyValue[1], "UTF-8")
                        : "";
                map.put(key, value);
            }

            return map;
        }

        private void insertStudent(Map<String, String> data) throws Exception {

            try (Connection conn = DBConnection.getConnection()) {

                String sql = "INSERT INTO students " +
                        "(first_name, last_name, email, phone, date_of_birth, gender, course) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, data.get("first_name"));
                stmt.setString(2, data.get("last_name"));
                stmt.setString(3, data.get("email"));
                stmt.setString(4, data.get("phone"));
                stmt.setString(5, data.get("date_of_birth"));
                stmt.setString(6, data.get("gender"));
                stmt.setString(7, data.get("course"));

                stmt.executeUpdate();

                System.out.println("Student inserted successfully");
            }
        }
    }
}