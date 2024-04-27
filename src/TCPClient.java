import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args){

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            //Server 소켓 연결
            socket = new Socket("localhost", 12345);

            //PrintWriter로 서버에 보낼 값들 스트림에 담아서 보낼 수 있음
            out = new PrintWriter(socket.getOutputStream(), true);

            //BufferedReader로 서버에서 받은 응답 읽기
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("<경보 발령 알림>");

            String response;
            while((response = in.readLine()) != null){
                System.out.println(response);
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}