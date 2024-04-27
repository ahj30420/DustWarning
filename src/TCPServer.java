import dao.DBConnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dto.AlertInfo;
import dto.AlertResponse;
import org.json.simple.parser.JSONParser;
import service.AlertService;
import util.JsonReader;

public class TCPServer {
    public static void main(String[] args) {

        DBConnect dbConnect = new DBConnect();
        JsonReader jsonReader = new JsonReader();
        AlertService alertService = new AlertService();

        JSONParser parser = new JSONParser();


        //DB 테이블 생성
        dbConnect.createTable();

        //JSON 파일 내용을 객체로 반환
        List<AlertInfo> alertInfoArrayList = jsonReader.parseJsonFileToAlertInfoList(dbConnect, parser);
        alertService.analyzePM(alertInfoArrayList);

        /**
         * TCP 서버 소켓 열고 알람 보내기
         */
        try {
            //서버소켓은 포트번호로 열어서 클라이언트 대기
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("서버시작");

            while (true) {
                //클라이언트 연결대기
                Socket clientSocket = serverSocket.accept();
                System.out.println("연결된 클라이언트 : " + clientSocket);

                //클라이언트에서 보낸 데이터 받음
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //클라이언트로 보낼 데이터 PrintWriter로 보냄
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                //경보 발령 정보 조회
                List<AlertResponse> alertList = dbConnect.getAlert();

                //경보 알람 보내기
                for (AlertResponse alertResponse : alertList) {
                    String station = alertResponse.getStation();
                    String level = alertResponse.getLevel();
                    LocalDateTime date = alertResponse.getDate();

                    String message = station + " 지역에 " + level + " 발령! (" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")";

                    System.out.println(message);
                    out.println(message);
                }

                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}