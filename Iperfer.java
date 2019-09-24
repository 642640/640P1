import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class Iperfer {
    private static DecimalFormat df = new DecimalFormat();

    public static int checkParamsLength(String[] args) {
        if(args.length == 7) {
            return 0;
        } else if(args.length == 3) {
            return 1;
        }

        System.out.println("Error: invalid arguments");
        System.exit(0);

        return -1;
    }

    public static void validateDashParams(String[] args, String[] flags) {
        for(int i = 1, j = 0; i < args.length; i += 2, j++) {
            if(!args[i].equals(flags[j])) {
                System.out.println("Error: invalid arguments");
                System.exit(0);
            }
        }
    }

    public static void checkPortRange(String sPort) {
        try {
            int iPort = Integer.parseInt(sPort);
            // check correct port range
            if(iPort < 1024 || iPort > 65535) {
                System.out.println("Error: port number must be in the range 1024 to 65535");
                System.exit(0);
            }
        } catch(NumberFormatException ex) {
            System.out.println("Error: invalid arguments");
            System.exit(0);
        }
    }

    public static void checkTimeParam(String sTime) {
        try {
            int iTime = Integer.parseInt(sTime);
        } catch(NumberFormatException ex) {
            System.out.println("Error: invalid arguments");
            System.exit(0);
        }
    }

    public static void validateClientModeParams(String[] args) {
        String[] flags = {"-h", "-p", "-t"};

        validateDashParams(args, flags);
        checkPortRange(args[4]);
        checkTimeParam(args[6]);
    }

    public static void validateServerModeParams(String[] args) {
        String[] flags = {"-p"};

        validateDashParams(args, flags);
        checkPortRange(args[2]);
    }

    public static void validateParams(int mode, String[] args) {
        if(mode == 0) {
            validateClientModeParams(args);
        } else if(mode == 1) {
            validateServerModeParams(args);
        }
    }

    public static Object[] parseClientModeParams(String[] args) {
        Object[] results = {
            0,  // mode
            args[2], // hostname
            Integer.parseInt(args[4]), // port
            Integer.parseInt(args[6]) // time
        };

        return results;
    }

    public static Object[] parseServerModeParams(String[] args) {
        Object[] results = {
            1, // mode
            Integer.parseInt(args[2]) // port
        };

        return results;
    }

    public static Object[] parseParams(String[] args) {
        int mode = -1;
        Object[] params = null;

        // check param length
        mode = checkParamsLength(args);
        // validate parameters
        validateParams(mode, args);
        // parase parameters
        if(mode == 0) {
            params = parseClientModeParams(args);
        } else if(mode == 1) {
            params = parseServerModeParams(args);
        }

        return params;
    }

    public static void startClient(Object[] params) {
        String hostname = (String)params[1];
        int port = (Integer)params[2];
        int time = (Integer)params[3];
        try {
            Socket socket = new Socket(hostname, port);
            OutputStream socketOutputStream = socket.getOutputStream();
            byte[] emptyBuffer = new byte[1000];
            long totalBytes = 0;

            // calculate the end time
            long endTime = System.currentTimeMillis() + (1000 * time);

            while(System.currentTimeMillis() < endTime) {
                // send out 1000 bytes
                socketOutputStream.write(emptyBuffer, 0, 1000);
                // keep track bytes
                totalBytes += 1000;
            }

            // close socket
            double sent = (double)totalBytes / 1000;
            double rate = (double)(totalBytes * 8) / (1000 * 1000) / time;
            socket.close();
            System.out.println("sent=" + (int)sent + " KB " + " rate=" + String.format("%.3f",rate)+ " Mbps");
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public static void startServer(Object[] params) {
        int port = (Integer)params[1];

        // creates a server socket
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1000];
            long totalBytesRead = 0;
            long currBytesRead = 0;

            long startTime = System.currentTimeMillis();
            // start timing
            while((currBytesRead = inputStream.read(buffer, 0, 1000)) != -1) {
                totalBytesRead += currBytesRead;
            }
            long endTime = System.currentTimeMillis();

            socket.close();
            serverSocket.close();
            double timeInMs = (double)(endTime - startTime) / 1000;
            double received = (double)totalBytesRead / 1000;
            double rate = (double)(totalBytesRead * 8) / 1000 / 1000 / timeInMs;

            System.out.println("received=" + (int)received + " KB"+ " rate=" + String.format("%.3f",rate) + " Mbps");
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public static void startJob(Object[] params) {
        int mode = (Integer)params[0];
        if(mode == 0) {
            startClient(params);
        } else if(mode == 1) {
            startServer(params);
        }
    }

    public static void main(String[] args) {
        Object[] params = parseParams(args);
        df.setMaximumFractionDigits(3);
        startJob(params);
    }
}
