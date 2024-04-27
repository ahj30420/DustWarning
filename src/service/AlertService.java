package service;

import dao.DBConnect;
import dto.AlertInfo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlertService {

    private DBConnect dbConnect = new DBConnect();
    private Map<String, Integer> pmAlertDuration = new HashMap<>();

    /**
     * 경보 발령 기준이 충족되는지 분석하고 경보 발령 정보 DB에 저장
     */
    public void analyzePM(List<AlertInfo> AlertInfoList) {

        //측정소가 바뀔때 마다 PM10, PM2.5 지속 시간 초기화
        for (AlertInfo alertInfo : AlertInfoList) {
            if(pmAlertDuration.get("code") == null || pmAlertDuration.get("code") != alertInfo.getCode()){
                Initialization(pmAlertDuration, alertInfo.getCode());
            }

            String station = alertInfo.getStation();
            LocalDateTime date = alertInfo.getDate();
            int pm10 = alertInfo.getPm10();
            int pm2_5 = alertInfo.getPm2_5();

            //경보 발령 기준 충족 여부 확인 및 DB 저장
            checkAlert(station, date, pm10, pm2_5);

        }
    }


    //--------------------------------------Private Method-----------------------------------------------------

    /*
     * 측정소 별로 미세먼지 경보,주의보 / 초미세먼지 경보,주의보 각각의 지속시간 초기화
     */
    private void Initialization(Map<String, Integer> pmAlertDuration, int code){
        pmAlertDuration.put("code", code);
        pmAlertDuration.put("pm10_Alert", 0);
        pmAlertDuration.put("pm10_Caution", 0);
        pmAlertDuration.put("pm2_5_Alert", 0);
        pmAlertDuration.put("pm2_5_Caution",0);

    }


    /**
     * 경보 발령 기준 충족 여부 확인하고 DB에 저장합니다.
     * 우선 순위에 따라 경보 발령 기준을 검사하고, 충족된 경우 DB에 저장합니다.
     * 우선 순위: 초미세먼지 경보 > 미세먼지 경보 > 초미세먼지 주의보 > 미세먼지 주의보
     * levelAlerted 변수는 가장 높은 우선 순위의 경보가 DB에 저장되었는지 여부를 나타냅니다.
     * DB에 경보 발령 정보가 저장되면 해당 단계 이하의 경보 발령에 대해서는 더 이상 저장하지 않습니다.
     *
     * @param station: 측정소
     * @param date: 관측 날짜
     * @param pm10: 미세먼지 농도
     * @param pm2_5: 초미세먼지 농도
     */
    private void checkAlert(String station, LocalDateTime date,
                            int pm10, int pm2_5) {

        boolean levelAlerted = false;
        levelAlerted = handleLevel(station, date, "초미세먼지 경보", pm2_5 >= 150, "pm2_5_Alert", levelAlerted);
        levelAlerted = handleLevel(station, date, "미세먼지 경보", pm10 >= 300, "pm10_Alert", levelAlerted);
        levelAlerted = handleLevel(station, date, "초미세먼지 주의보", pm2_5 >= 75, "pm2_5_Caution", levelAlerted);
        handleLevel(station, date, "미세먼지 주의보", pm10 >= 150, "pm10_Caution", levelAlerted);

    }

    /**
     * 실제 경보 발령 기준을 검증하고 DB에 저장합니다.
     * 1. PM10과 PM2.5가 각각의 기준치 이상인지 확인합니다.
     * 2. 지속시간이 2시간 이상인지 확인합니다.
     * 3. 상위 경보가 발령되지 않았을 경우 해당 경보로 발령하고 DB에 저장합니다.
     * 4. 1번 조건을 만족하면 해당 경보의 지속시간을 1시간 추가합니다.
     * 5. 1번 조건을 만족하지 못하면 해당 경보의 지속시간을 0으로 초기화합니다.
     *
     * @param station 측정소
     * @param date 측정 날짜
     * @param level 경보 단계
     * @param condition 시간당 농도 조건
     * @param alertDuration 모든 지속시간 정보 중 측정하고 싶은 지속시간 정보
     * @param levelAlerted 상위 경보 발령 여부
     * @return 상위 경보 발령 여부
     */
    private boolean handleLevel(String station, LocalDateTime date, String level, boolean condition,
                                String alertDuration, boolean levelAlerted) {

        if (condition) {
            int duration = pmAlertDuration.get(alertDuration) + 1;
            if(duration >= 2) {
                if (!levelAlerted) {
                    dbConnect.saveAlertInfo(station, level, date);
                    levelAlerted = true;
                }
            }
            pmAlertDuration.put(alertDuration, duration);
        } else {
            pmAlertDuration.put(alertDuration, 0);
        }
        return levelAlerted;
    }

}
