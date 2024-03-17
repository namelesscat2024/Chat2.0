import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class ClientA
{
    JTextArea incoming;              //文本域
    JTextField  outgoing;           //输入框
    BufferedReader reader;   //缓冲输入字符流
    PrintWriter writer;              //输出字符流
    Socket socket;
 
    public static void main(String[] args)
    {
        new ClientA().go();
    }
 
    public void go()
    {
        JFrame jFrame = new JFrame("simple chat clientA");
        JPanel jPanel = new JPanel();
 
        //创建文本域
        incoming = new JTextArea(15,30);
        incoming.setLineWrap(true);      //设置在行过长的时候是否要自动换行
        incoming.setWrapStyleWord(true);  //设置在单词过长的时候是否要把长单词移到下一行
        incoming.setEditable(false);
 
        //创建滚动面板，并将文本域放入其中
        JScrollPane jScrollPane = new JScrollPane(incoming);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
 
        //创建输入框
        outgoing = new JTextField(20);
 
        //创建发送按钮,并创建监听器
        JButton sendButton = new JButton("send");
        sendButton.addActionListener(new sendButtonListener());
 
        //将上面组件放入JPanel容器中
        jPanel.add(jScrollPane);
        jPanel.add(outgoing);
        jPanel.add(sendButton);
 
        //与服务器建立连接
        setUpNetworking();
 
        //开启一个线程，并执行run方法
        Thread thread = new Thread(new IncomingReader());
        thread.start();
 
        //将JPanel容器放入JFrame面板中
        jFrame.getContentPane().add(BorderLayout.CENTER,jPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(400,400);
        jFrame.setVisible(true);
 
    }
 
    /**
     * 与服务器建立socket连接,并在socket的基础上建立输入、输出流
     */
    private void setUpNetworking()
    {
 
        try {
            //与服务器建立socket连接
            socket = new Socket("127.0.0.1",5000);
 
            //与socket建立字符输入缓冲流
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStreamReader);
 
            //与socket建立字符输出流
            writer = new PrintWriter(socket.getOutputStream());
            incoming.append("客户端A 成功连接服务器......" + "\n");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
    }
 
    /**
     * 监听器
     */
    class sendButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //当发送按钮点击，则触发事件，将输入框中的文字获取，并输出到服务器
            writer.println("客户端A: " + outgoing.getText());
            writer.flush();
            //输入框文字清空
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }
 
    /**
     * 该线程任务是不断地读取服务器的信息，并把它加载到文本域中
     */
    class IncomingReader implements Runnable
    {
 
        @Override
        public void run()
        {
                String message;
                try {
                    while ((message = reader.readLine()) != null)
                    {
                        System.out.println("read " + message);
                        incoming.append(message + "\n");
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
        }
    }
}