import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Iperfer {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: missing or additional arguments");
            return;
        }

        if (args[0].equals("-c")) {
            if (args.length != 7 || !args[1].equals("-h") || !args[3].equals("-p") || !args[5].equals("-t")) {
                System.err.println("Error: missing or additional arguments");
                return;
            }

            String serverHost = args[2];
            int port;
            int time;

            try {
                port = Integer.parseInt(args[4]);
                time = Integer.parseInt(args[6]);
            } catch (NumberFormatException e) {
                System.err.println("Error: invalid number format");
                return;
            }

            if (port < 1024 || port > 65535) {
                System.err.println("Error: port number must be in the range 1024 to 65535");
                return;
            }

            runClient(serverHost, port, time);

        } else if (args[0].equals("-s")) {
            if (args.length != 3 || !args[1].equals("-p")) {
                System.err.println("Error: missing or additional arguments");
                return;
            }

            int port;
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Error: invalid number format");
                return;
            }

            if (port < 1024 || port > 65535) {
                System.err.println("Error: port number must be in the range 1024 to 65535");
                return;
            }

            runServer(port);
        } else {
            System.err.println("Error: missing or additional arguments");
        }
    }

    private static void runClient(String host, int port, int duration) {
        final int PACKET_SIZE = 1000;
        byte[] data = new byte[PACKET_SIZE];

        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream()) {

            long startTime = System.currentTimeMillis();
            long endTime = startTime + duration * 1000;
            int totalBytesSent = 0;

            while (System.currentTimeMillis() < endTime) {
                out.write(data);
                totalBytesSent += PACKET_SIZE;
            }

            double kilobytesSent = totalBytesSent / 1000.0;
            double megabitsPerSecond = (kilobytesSent * 8) / duration;

            System.out.printf("sent=%.0f KB rate=%.3f Mbps%n", kilobytesSent, megabitsPerSecond);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void runServer(int port) {
        final int PACKET_SIZE = 1000;
        byte[] buffer = new byte[PACKET_SIZE];

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             InputStream in = clientSocket.getInputStream()) {

            long startTime = System.currentTimeMillis();
            int totalBytesReceived = 0;
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                totalBytesReceived += bytesRead;
            }

            long endTime = System.currentTimeMillis();
            double elapsedTime = (endTime - startTime) / 1000.0; // Convert ms to seconds

            double kilobytesReceived = totalBytesReceived / 1000.0;
            double megabitsPerSecond = (kilobytesReceived * 8) / elapsedTime;

            System.out.printf("received=%.0f KB rate=%.3f Mbps%n", kilobytesReceived, megabitsPerSecond);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
