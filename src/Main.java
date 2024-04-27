import dao.DBConnect;

import java.util.List;

import dto.AlertInfo;
import org.json.simple.parser.JSONParser;
import service.AlertService;
import util.JsonReader;

public class Main {
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

    }

}