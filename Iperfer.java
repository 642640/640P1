import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Iperfer {

    private static void client(String[] args ){

        //basic command line requirements
        //java Iperfer -c -h <server hostname> -p <server port> -t <time>

        if (args[1].compareTo("-h") != 0
        || args[3].compareTo("-p") != 0
        || args[5].compareTo("-t") != 0
        || args.length > 7 ){
            error("Error: invalid arguments\n");
            return;
        }
        //port number requirements
        if (Integer.parseInt(args[4]) < 1024
        || Integer.parseInt(args[4]) > 65535 ){
            error("Error: port number must be in the range 1024 to 65535");
            return;
        }

        int port = Integer.parseInt(args[4]);
        int time = Integer.parseInt(args[6]);
        try {
            //create socket
            Socket socket = new Socket(args[2], port);
            OutputStream output = socket.getOutputStream();

            double byteSent = 0;
			
            //send bytes for certain amount of time
            long t= System.currentTimeMillis();
            long end = t + time * 1000;
            while(System.currentTimeMillis() < end) {
                output.write(new byte[1000]);
                byteSent+=1000;
            }
            String msg = "sent=";
            double rate = byteSent * 8;
            if (byteSent >= 1000 )
                if (byteSent >= 1000000)
                    msg += byteSent / 1000000 + " MB";
                else
                    msg += byteSent / 1000 + " KB";
            else
                msg += byteSent + " B";
            msg += " rate=";
            rate = rate / time;
            if (rate >= 1000 )
                if (rate >= 1000000)
                    msg += rate / 1000000 + " MBbps";
                else
                    msg += rate / 1000 + " KBps";
            else
                msg += rate + " Bps";
            System.out.println(msg);
            output.close();
            socket.close();
        } catch (Exception e){
            error("Exception Captured " + "\n" + e.getMessage());
            return;
        }
    }
    private static void server(String[] args ){
        if (args[1].compareTo("-p") != 0 || args.length > 7) {
            error("Error: invalid arguments\n");
            return;
        }
        //port number requirements
        if (Integer.parseInt(args[2]) < 1024
                || Integer.parseInt(args[2]) > 65535 ){
            error("Error: port number must be in the range 1024 to 65535");
            return;
        }
        try {
            int port = Integer.parseInt(args[2]);
            ServerSocket server = new ServerSocket(port);

            Socket socket = server.accept();
            InputStream input = socket.getInputStream();
            //start time
            long start = System.currentTimeMillis();
            byte[] zero = new byte[1000];
            double byteReceived = 0;
            int tmp = -1;
            while ((tmp = input.read(zero,0,1000)) != -1) {
                byteReceived+=tmp;
            }
            //end time
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;

            String msg = "received=";
            double rate = byteReceived * 8;
            if (byteReceived >= 1000 )
                if (byteReceived >= 1000000)
                    msg += byteReceived / 1000000 + " MB";
                else
                    msg += byteReceived / 1000 + " KB";
            else
                msg += byteReceived + " B";
            msg += " rate=";
            // rate = rate / (double)(timeElapsed / 1000);
            rate = rate / ((double)timeElapsed / 1000);
            if (rate >= 1000 )
                if (rate >= 1000000)
                    msg += rate / 1000000 + " MBbps";
                else
                    msg += rate / 1000 + " KBps";
            else
                msg += rate + " Bps";
            System.out.println(msg);
            input.close();
            socket.close();
            server.close();
        } catch (IOException e){
            error("IOException");
            return;
        }
    }
    private static void error(String msg){
        System.out.println(msg);
        return;
    }
    public static void main(String[] args){

        if (args.length < 3
                || args[0].charAt(0) != '-') {
            error("Error: invalid arguments");
            return;
        }
        switch(args[0].charAt(1)){
            case 's' : server(args);
                        break;
            case 'c' : client(args);
                        break;
            default : error("Error: invalid arguments");
        }


    }
}
