import auth.AuthService;
import auth.AuthServiceImpl;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChatServer {

    //Для организации ввода логин-пароль
    private static final Pattern AUTH_PATTERN = Pattern.compile("^/auth (.+) (.+)$");

    //Авторизация пользователя
    private AuthService authService = new AuthServiceImpl();

    //Здесь храним подключишвшихся клиентов (Мар должен быть синхронизирован)
    private Map<String, ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());


    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start(7777);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inp = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.println("New client connected!");

                try {
                    String authMessage = inp.readUTF(); //Получаем на вход сервера логин и пароль от клиента
                    Matcher matcher = AUTH_PATTERN.matcher(authMessage); //Разобраться, как это работает
                    if (matcher.matches()) { //возвращает true, если строка соответсвует заданному Pattern
                        String username = matcher.group(1);
                        String password = matcher.group(2);
                        if (authService.authUser(username, password)) {
                            clientHandlerMap.put(username, new ClientHandler(username, socket, this));
                            out.writeUTF("/auth successful");
                            out.flush();
                            System.out.printf("Authorization for user %s successful%n", username);
                        } else {
                            System.out.printf("Authorization for user %s failed%n", username);
                            out.writeUTF("/auth fails");
                            out.flush();
                            socket.close();
                        }
                    } else {
                        System.out.printf("Incorrect authorization message %s%n", authMessage);
                        out.writeUTF("/auth fails");
                        out.flush();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при работе сервера");
        }
        //Я хочу закрывать сокет при какой-либо ошибке на сервере
        //Так как написано - в блоке try не видно socket, почему?
        //Вообще нужно ли это делать?

        /*
        finally {
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        */
    }

    public void sendMessage(String userTo, String userFrom, String msg) {
        // TODO реализовать отправку сообщения пользователю с именем username
        //Не понял, как это сделать
        if( clientHandlerMap.get(authService.authUser(username));
    }
}