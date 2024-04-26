import dao.DBConnect;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import dto.AlertInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) {

        DBConnect dbConnect = new DBConnect();
        JSONParser parser = new JSONParser();

        //DB 테이블 생성
        dbConnect.createTable();

        //JSON 파일 내용을 객체로 반환
        ArrayList<AlertInfo> alertInfoArrayList = parseJsonFileToAlertInfoList(dbConnect, parser);

    }

    /**
     *  JSON 파일을 읽어와서 그 안에 있는 데이터를 AlertInfo 객체로 변환한 후, ArrayList에 추가
     */
    private static ArrayList<AlertInfo> parseJsonFileToAlertInfoList(DBConnect dbConnect, JSONParser parser) {
        try (FileReader reader = new FileReader("src/resources/dustInfo.json")) {
            // JSON 파일을 파싱하여 JSONArray로 변환
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            ArrayList<AlertInfo> alertInfoArrayList = new ArrayList<>();

            for(Object obj : jsonArray){
                JSONObject jsonObject = (JSONObject) obj;

                //"날짜" 데이터를 LocalDateTime으로 반환
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
                LocalDateTime date = LocalDateTime.parse((String)jsonObject.get("날짜"), formatter);

                String station = (String) jsonObject.get("측정소명");
                int code = Integer.parseInt((String)jsonObject.get("측정소코드"));

                //PM10, PM2.5 값이 없을 경우 0으로 초기화 하고 측정소 점검 내역 DB에 저장
                int pm10 = 0;
                int pm2_5 = 0;
                if(jsonObject.get("PM10") == null || jsonObject.get("PM2.5") == null){
                    String content = station + " 측정소 점검이 있던 날 입니다.(" + date + ")";
                    dbConnect.saveInspection(station, content);
                } else {
                    pm10 = Integer.parseInt((String) jsonObject.get("PM10"));
                    pm2_5 = Integer.parseInt((String) jsonObject.get("PM2.5"));
                }

                alertInfoArrayList.add(new AlertInfo(date, station, code, pm10, pm2_5));
            }
            return alertInfoArrayList;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}