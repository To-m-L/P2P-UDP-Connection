import java.net.*;
import java.io.*;
import java.util.*;

public class ClientUDP {

    public static int opposing_port = 7070;
    public static int incoming_port;
    public static boolean connection_request = false;
    public static void main(String[] args) throws IOException{
        ClientListener listener = new ClientListener();
        new Thread(listener).start();
        byte[] receive = new byte[256];
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte buf[] = null;
        String portString = "/PORT:" + ClientListener.listener_port;
        buf = portString.getBytes();
        DatagramPacket send_port = new DatagramPacket(buf, buf.length, ip, 7070);
        ds.send(send_port);
        DatagramPacket DPsend = null;
        DatagramPacket DPreceive = null;
        int port_clientB;
        System.out.println("Listener Port: " + ClientListener.listener_port);
        
        

        while(true){
            Arrays.fill(buf, (byte)0);
            Arrays.fill(receive, (byte)0);
            //Taking an input, putting it in a payload, and then sending it to opposing port (which is the main server or another client)
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            String modifiedInput = input + ClientListener.listener_port;
            buf = modifiedInput.getBytes();
            DPsend = new DatagramPacket(buf, buf.length, ip, opposing_port);
            ds.send(DPsend);

            //If-conditions for different commands
            //------------------------------------------------------------------------
            //Requests for port of another client

            if(input.equals("request") && opposing_port == 7070){
                DPreceive = new DatagramPacket(receive, receive.length);
                ds.receive(DPreceive);
                if(data(receive).toString().equals("No active clients")){
                    System.out.println("No active clients.");
                    continue;
                }
                //Requests for connection to another client
                else {
                    System.out.println("Port number received.");
                    port_clientB = Integer.parseInt(data(receive).toString());
                    System.out.println("Port Number: " + port_clientB);
                    System.out.println("Sending connection request to " + ip + ":" + port_clientB);
                    String requestConnection = "Connection Request" + ClientListener.listener_port;
                    DPsend = new DatagramPacket(requestConnection.getBytes(), requestConnection.getBytes().length, ip, port_clientB);
                    ds.send(DPsend);
                }

            }

            if(connection_request==true && input.equalsIgnoreCase("Connection Accept")){
                System.out.println("Connection Accepted, establishing connection...");
                System.out.println("Connection established with " + ip + ":" + opposing_port);
                connection_request=false;

            }            

            if(connection_request==true&& input.equals("Connection Reject")){
                System.out.println("Connection declined, rejecting connection...");
                opposing_port = 7070;
                System.out.println("Connection declined, connected user back to main server.");
                connection_request = false;
            }

            if(input.equals("Connection Termination")){
                System.out.println("Terminating client connection...");
                opposing_port = 7070;
                System.out.println("Connection with client ended, connected user back to main server.");
            }

            if(input.equals("/EXIT") && opposing_port == 7070){ 
                
                break;

            }

        }


    }

    private static class ClientListener implements Runnable{
        
        public static int listener_port;

        public void run(){
            try{
                DatagramSocket listener = new DatagramSocket();
                listener_port = listener.getLocalPort();
                DatagramPacket DPreceive = null;
                byte[] receive = new byte[256];

                while(true){
                    Arrays.fill(receive, (byte)0);
                    DPreceive = new DatagramPacket(receive, receive.length);
                    listener.receive(DPreceive);
                    if(data(receive).toString().contains("Connection Request") && opposing_port==7070){
                        connection_request=true;
                        System.out.println("Connection Request, Type 'Connection Accept' to accept or 'Connection Reject' to decline.");
                        int destination_port = Integer.parseInt(data(receive).toString().substring(18, data(receive).toString().length()));
                        System.out.println("Incoming Port: " + destination_port);
                        opposing_port = destination_port;
                        continue;
                    }
                    if(data(receive).toString().contains("Connection Accept")){
                        int destination_port = Integer.parseInt(data(receive).toString().substring(17, data(receive).toString().length()));
                        System.out.println("Connection accepted by client, establishing connection...");
                        opposing_port = destination_port;
                        System.out.println("Connection established.");
                        continue;
                    }
                    if(data(receive).toString().contains("Connection Reject")){
                        //int destination_port = Integer.parseInt(data(receive).toString().substring(17, data(receive).toString().length())); 
                        System.out.println("Connection declined by client, closing connection...");
                        opposing_port = 7070;
                        System.out.println("Connected user back to main server.");
                        continue;
                    }


                    if(data(receive).toString().contains("Connection Termination")){
                        opposing_port = 7070;
                        System.out.println("Connection terminated by opposing client.");
                        continue;
                    }
                    
                    //removes port number from the end of string inputs
                    String receiveString = data(receive).toString();
                    String newString = "";
                    for(int i=0;i<receiveString.length();i++){
                        if(!Character.isDigit(receiveString.charAt(i))){
                            newString+=receiveString.charAt(i);
                        }
                        else{
                            break;
                        }
                    }
                    System.out.println(newString);

                }



            }
            catch(Exception e){

            }
        }

    }

    //Function for translating byte array to String
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
