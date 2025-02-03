import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame{
    Socket socket;
    BufferedReader br;
    PrintWriter out;


    private JLabel heading=new JLabel("Client Area");
    private JTextArea msgArea=new JTextArea();
    private JTextField msgInput=new JTextField();
    private Font font=new Font("Roboto" , Font.PLAIN,20);

    public Client(){
        try {
            System.out.println("Sending request to Server");
            socket=new Socket("127.0.0.1",7777);
            System.out.println("connection done..");

            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
            // startWriting();//console k liye
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    private void handleEvents(){
        msgInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("key released"+e.getKeyCode());
                if(e.getKeyCode()==10){
                    // System.out.println("you have pressed enter button");
                    String contentToSend=msgInput.getText();
                    msgArea.append("Me : "+contentToSend+"\n");
                    msgArea.setCaretPosition(msgArea.getDocument().getLength()); 
                    out.println(contentToSend);
                    out.flush();
                    msgInput.setText("");
                    msgInput.requestFocus();
                }
            }
            
        });
    }

    private void createGUI(){
        //gui code..
        //this=window
        this.setTitle("Client Messager[END]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        msgArea.setFont(font);
        msgInput.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        msgArea.setEditable(false);
        msgInput.setHorizontalAlignment(SwingConstants.CENTER);

        //frame ka layout set krenge
        this.setLayout(new BorderLayout());//north center south

        //adding the components to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(msgArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(msgInput,BorderLayout.SOUTH);

        this.setVisible(true);

    }

    public void startReading() {
        //thread - read krke deta rhega
        Runnable r1=()->{
            System.out.println("reader started...");
            try{
                while (true) {
                    String msg=br.readLine();
                    if(msg.equals("exit")){
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        msgInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // System.out.println("Server : "+msg);
                    msgArea.append("Server : "+msg+"\n");
                    msgArea.setCaretPosition(msgArea.getDocument().getLength());
                }
            }catch(Exception e){
                System.out.println("connection closed..");
            }
        };

        new Thread(r1).start();

    }

    public void startWriting() {
        //thread - data user se lega and then usko send krega client tak
        Runnable r2=()->{
            System.out.println("writer started...");
            try {
                while (!socket.isClosed()) {

                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
                    String content=br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }

                }
                
            } catch (Exception e) {
                System.out.println("connection is closed..");
            }
        };

        new Thread(r2).start();

    }
    public static void main(String[] args) {
        System.out.println("this is client..");
        new Client();
    }
}
