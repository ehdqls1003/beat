package com.kplo.beat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MServer implements Runnable {

    static ArrayList<Socket> list = new ArrayList<Socket>();
    static Socket socket = null;
    Meassage getString;



    public static final int ServerPort = 3333;



    //public static final String ServerIP = "192.168.0.6";

    @Override

    public void run() {

        try {

            System.out.println("Connecting...");

            ServerSocket serverSocket = new ServerSocket(ServerPort);



            while (true) {

                //client 접속 대기

                Socket client = serverSocket.accept();
                list.add(client);


                System.out.println("Receiving...");

                try {

                    //client data 수신
                    System.out.println("Client connected IP address =" + client.getRemoteSocketAddress().toString());

                    ClientManagerThread c_thread = new ClientManagerThread();
                    c_thread.setSocket(client);

                    c_thread.start();

                } catch (Exception e) {

                    System.out.println("Error");

                    e.printStackTrace();

                }

            }

        } catch (Exception e) {

            System.out.println("S: Error");

            e.printStackTrace();

        }

    }



    public static void main(String[] args) {

        Thread ServerThread = new Thread(new MServer());

        ServerThread.start();

    }

    class ClientManagerThread extends Thread{
        private Socket m_socket;
        private String m_ID;
        private int number;
        private ObjectOutputStream oos;
        private DataInputStream in;
        private DataOutputStream out;
        private String room_id;
        private ArrayList<room> rooms = new ArrayList<room>();
        boolean no_room = false;
        int what_room;
        ArrayList<User> e_User = new ArrayList<>();

        @Override
        public void run(){
            super.run();
            try{
                oos = new ObjectOutputStream(m_socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(m_socket.getInputStream());
                Meassage text;

                for(int i =0; i < rooms.size(); i++){
                    if(rooms.get(i).getRoom_id().equals(room_id)){
                        no_room = true;
                        what_room = i;
                    }
                }

                if (no_room){
                    e_User = rooms.get(what_room).getUser();
                    User item = new User();
                    item.setSocket(m_socket);
                    item.setOos(oos);
                    e_User.add(item);
                    rooms.get(what_room).setUser(e_User);
                }else {
                    room item = new room();
                    User item2 = new User();
                    item2.setSocket(m_socket);
                    item2.setOos(oos);
                    e_User.add(item2);
                    item.setRoom_id(room_id);
                    item.setUser(e_User);
                    rooms.add(item);
                }

                while(true){
                    text = (Meassage)ois.readObject();
                    ArrayList<User> a;
                    a = rooms.get(what_room).getUser();

                    for (int i =0; i<a.size();i++){
                        oos = a.get(i).getOos();
                        oos.writeObject(text);
                        oos.flush();
                    }
                }


            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        public void setSocket(Socket _socket){
            m_socket = _socket;
        }


    }
    class User {
        Socket socket;
        ObjectOutputStream oos;

        public ObjectOutputStream getOos() {
            return oos;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setOos(ObjectOutputStream oos) {
            this.oos = oos;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }
    }

    class room{
        String room_id;
        ArrayList<User> user;

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public void setUser(ArrayList<User> user) {
            this.user = user;
        }

        public ArrayList<User> getUser() {
            return user;
        }

        public String getRoom_id() {
            return room_id;
        }

    }

}