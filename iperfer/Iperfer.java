import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Iperfer {
    private static void runClient(String host, int port, int duration) {
        final int PACKET_SIZE = 1000;
        byte[] data = new byte[PACKET_SIZE];

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            long t_start = System.currentTimeMillis();
            long t_end = t_start + duration * 1000;
            long totalBytesSent = 0;
            while (System.currentTimeMillis() < t_end) {
                out.println(data);
                totalBytesSent += PACKET_SIZE;
            }

            double kbSent = totalBytesSent / 1000.0;
            double mbps = (kbSent * 8) / duration;

            System.out.printf("sent=%.0f KB rate=%.3f mbps%n", kbSent, mbps);

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

            long t_start = System.currentTimeMillis();
            int totalBytesReceived = 0;
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                totalBytesReceived += bytesRead;
            }

            long t_end = System.currentTimeMillis();
            double duration = (t_end - t_start) / 1000.0; // Convert ms to seconds

            double bkReceived = totalBytesReceived / 1000.0;
            double mbps = (bkReceived * 8) / duration;

            System.out.printf("received=%.0f KB rate=%.3f mbps%n", bkReceived, mbps);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 7) {
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
}
