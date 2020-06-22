package Controllers;

import org.jgroups.*;
import org.jgroups.util.Util;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;

@ServerEndpoint("/group")
public class JGroupsController implements Receiver {

    JChannel channel;
    final List<String> state = new LinkedList<>();
    private View lastView;
    private boolean inGroup = false;
    private Session userSession;

    private Optional<Address> getAddress(String name) {
        View view = channel.view();
        return view.getMembers().stream()
                .filter(address -> name.equals(address.toString()))
                .findAny();
    }

    private void sendPm() throws Exception {
        View view = channel.view();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        Address destination = null;
        System.out.println("Pra quem é a mensagem?");
        String destinationName = in.readLine();

        destination = getAddress(destinationName).orElseThrow(() -> new Exception("Membro não econtrado!"));

        System.out.println("Digite sua mensagem");
        String input = in.readLine();
        Message message = new Message(destination, "[PM]: " + input);
        channel.send(message);
    }

    public void viewAccepted(View newView) {

        if (lastView == null) {
            System.out.println("\n-------------------------");
            System.out.println("Membros do Grupo");
            newView.forEach(System.out::println);
            System.out.println("\n-------------------------");
        } else {
            System.out.println("\n--------------------------");
            System.out.println("\nLista de Membros atualizada");

            List<Address> newMembers = View.newMembers(lastView, newView);
            List<Address> exMembers = View.leftMembers(lastView, newView);

            System.out.println("Novos Membros: ");
            newMembers.forEach(System.out::println);

            System.out.println("Membros Antigos: ");
            exMembers.forEach(System.out::println);

            System.out.println("\n-----------------------");
        }
        lastView = newView;
    }

    public void receive(Message msg){
        //msg.getSrc() + ": " +

        String line = msg.getObject();
        System.out.println(line);
        try {
            this.userSession.getBasicRemote().sendText(line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized(state) {
            state.add(line);
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        List<String> list = Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state.clear();
            state.addAll(list);
        }

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println(list.size() + " mensagens no histórico do grupo");
        for (int i = 0; i < list.size(); i++) {
            String s = (String) list.get(i);
            this.userSession.getBasicRemote().sendText(s);
        }
        System.out.println("----------------------------------------------------------------------------------------");
    }

    private void chatting() {
        BufferedReader listener = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.flush();
                String message = listener.readLine();

                if(message.startsWith("/SENDPM")){
                    sendPm();
                }
                else if(message.equals("/EXIT")) {
                    System.out.println("Tchau!");
                    System.out.println("---------------");
                    return;
                }else{
                    Message msg = new Message(null, message);
                    channel.send(msg);
                }
            }
            catch(Exception e) {
            }
        }
    }

    private void listening() {
        while(true) {
            try {

            }
            catch(Exception e) {

            }
        }
    }

    public void sendMessage(String message) throws Exception {
        Message msg = new Message(null, message);
        channel.send(msg);
    }

    public void connectToGroup(String groupName) throws Exception {
        System.out.println("Conectando...");
        channel = new JChannel().setReceiver(this);

        try{
            channel.connect(groupName);
            channel.getState(null, 10000);
            inGroup = true;

            System.out.println("Conectando...");
            System.out.println("Bem-vindo ao grupo!");

        }catch(Exception e){
            System.out.println("Não foi possível conectar ao grupo devido ao erro:\n");
            System.out.println(e);
        }

        //channel.disconnect();
    }

    @OnMessage
    public String onMessage(String message) throws Exception {
        if(message.startsWith("[GROUP_NAME]")){
            connectToGroup(message);
            return "[CONEXION]";
        }else{
            this.sendMessage(message);
            return message;
        }
    }

    @OnOpen
    public void onOpen(Session session) throws Exception {
        this.userSession = session;
    }
}