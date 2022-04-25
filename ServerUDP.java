import java.net.*;
import java.io.*;
import java.util.*;

public class ServerUDP{


    public static void main(String[] args) throws IOException{

        DatagramSocket ds = new DatagramSocket(7070);  
        byte[] receive = new byte[256];
        int clients=0;
        DatagramPacket DPreceive = null;
        System.out.println("Server is running.");
        DatagramPacket DPsend = null;
        byte buf[] = null;
        Map<Integer, String> clientsMap = new HashMap<Integer, String>();        
        

        while(true){
            Arrays.fill(receive, (byte)0);
            DPreceive = new DatagramPacket(receive, receive.length);
            ds.receive(DPreceive);
            String receiveString = data(receive).toString();
            //Adds client to active clients
            int clientPort;
            if(receiveString.contains("/PORT:")){
                clientPort = Integer.parseInt(receiveString.substring(6, receiveString.length()));
                if(!clientsMap.containsKey(clientPort)){
                    clients++;
                    clientsMap.put(clientPort, "client" + clients);
                    System.out.println("List of connections: ");
                    for(Map.Entry<Integer, String> entre : clientsMap.entrySet()){
                        System.out.println(entre.getValue() + " Port: " + entre.getKey());
                    }
                }
            }

            // Gets list of active connections
            if(receiveString.contains("list")){
                System.out.println("List of connections: ");
                for(Map.Entry<Integer, String> entre : clientsMap.entrySet()){
                    System.out.println(entre.getValue() + " Port: " + entre.getKey());
                }
            }

            // Returns an active connection (that is not the requester) back to the client.
            String activePort="";
            if(receiveString.contains("request")){
                try{
                clientPort = Integer.parseInt(receiveString.substring(7, receiveString.length()));
                System.out.println(clientsMap.get(clientPort) + " requested to connect to a client...");
                for(Map.Entry<Integer, String> entre : clientsMap.entrySet()){
                    if(entre.getKey() != clientPort){
                        activePort = entre.getKey().toString();
                        buf = activePort.getBytes();
                        System.out.println("Sending port number for " + entre.getValue());
                        break;
                    }
                }
                if(!activePort.equals("")){
                System.out.println("Sending...");
                DPsend = new DatagramPacket(buf, buf.length, DPreceive.getAddress(), DPreceive.getPort());
                ds.send(DPsend);
                }
                else{
                    System.out.println("No active clients found.");
                    DPsend = new DatagramPacket("No active clients.".getBytes(), "No active clients".getBytes().length, DPreceive.getAddress(), DPreceive.getPort());
                    ds.send(DPsend);
                }
                }catch(Exception e){
                    continue;
                }
            }

            // Removes client from list of active connections
            if(receiveString.contains("/EXIT")){
                try{
                    clientPort = Integer.parseInt(receiveString.substring(5, receiveString.length()));
                    System.out.println("Removing " + clientsMap.get(clientPort) + " from active connections.");
                    clientsMap.remove(clientPort);
                    clients--;
                    continue;
                }catch(Exception e){
                    continue;
                }
                
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