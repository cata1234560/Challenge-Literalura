import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Conversor {

    // Método para convertir entre dos monedas
    public static double convertir(double cantidad, String monedaOrigen, String monedaDestino, JsonObject rates) {
        if (!rates.has(monedaOrigen) || !rates.has(monedaDestino)) {
            throw new IllegalArgumentException("Moneda no encontrada en las tasas.");
        }

        double tasaOrigen = rates.get(monedaOrigen).getAsDouble();
        double tasaDestino = rates.get(monedaDestino).getAsDouble();

        // Conversión pasando primero a USD como base
        return (cantidad / tasaOrigen) * tasaDestino;
    }

    // Método para mostrar el menú
    public static void exibirMenu() {
        System.out.println("\n=== Sea bienvenido/a al Conversor de Moneda ===");
        System.out.println("1. ARS - Peso argentino");
        System.out.println("2. BOB - Boliviano boliviano");
        System.out.println("3. BRL - Real brasileño");
        System.out.println("4. CLP - Peso chileno");
        System.out.println("5. COP - Peso colombiano");
        System.out.println("6. USD - Dólar estadounidense");
        System.out.println("7. Salir");
        System.out.print("Elija una opción válida: ");
    }

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);

        // Solicitud a la API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.exchangerate-api.com/v4/latest/USD"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject rates = json.getAsJsonObject("rates");

            boolean continuar = true;

            while (continuar) {
                exibirMenu();
                int opcion = scanner.nextInt();

                if (opcion == 7) {
                    continuar = false;
                    System.out.println("Gracias por usar el Conversor de Moneda. ¡Hasta pronto!");
                    break;
                }

                String[] codigos = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};

                if (opcion < 1 || opcion > 6) {
                    System.out.println("❌ Opción inválida. Intente de nuevo.");
                    continue;
                }

                String monedaOrigen = codigos[opcion - 1];

                System.out.print("Ingrese la cantidad en " + monedaOrigen + ": ");
                double cantidad = scanner.nextDouble();

                // Mostrar opciones de destino
                System.out.println("\nConvertir a:");
                for (int i = 0; i < codigos.length; i++) {
                    System.out.println((i + 1) + ". " + codigos[i]);
                }
                System.out.print("Elija la moneda de destino: ");
                int opcionDestino = scanner.nextInt();

                if (opcionDestino < 1 || opcionDestino > 6) {
                    System.out.println("❌ Moneda de destino inválida.");
                    continue;
                }

                String monedaDestino = codigos[opcionDestino - 1];

                // Evitar conversión a la misma moneda
                if (monedaOrigen.equals(monedaDestino)) {
                    System.out.println("⚠ No tiene sentido convertir " + monedaOrigen + " a la misma moneda.");
                    continue;
                }

                double resultado = convertir(cantidad, monedaOrigen, monedaDestino, rates);

                System.out.println("\n💱 Resultado: " + cantidad + " " + monedaOrigen + " = "
                        + resultado + " " + monedaDestino);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
