import java.io.*;
import java.net.Socket;
import java.util.*;
import java.net.*;

public class Server {
 
 
    ArrayList clientOutPutStreams = new ArrayList();
 
    public class ClientHandler implements Runnable
    {
        BufferedReader reader;
        Socket socket;
 
        public ClientHandler(Socket clientSocket)
        {
            try {
                //与socket建立输入流
                this.socket = clientSocket;
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        @Override
        public void run()
        {
            String message = null;
            try
            {
                //不断地从socket上读入数据
                while ((message = reader.readLine()) != null)
                {
                    System.out.println("read " + message);
                    //将数据发送给每一个客户端
                    tellEveryone(message);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
 
        }
    }
 
    public static void main(String[] args) {
       new SimpleChatServer().go();
    }
 
    public void go()
    {
        try {
            //服务器应用程序对特定的端口号创建ServerSocket
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true)
            {
                //服务器创建出与客户端通信的socket
                Socket clientSocket = serverSocket.accept();
                //与socket建立输出流
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
                clientOutPutStreams.add(printWriter);
                //开启一个线程
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
                System.out.println("服务器得到一个连接");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public void tellEveryone(String message)
    {
        Iterator it = clientOutPutStreams.iterator();
        while (it.hasNext())
        {
            try
            {
                PrintWriter writer = (PrintWriter)it.next();
                writer.println(message);
                writer.flush();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
 
}