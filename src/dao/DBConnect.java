package dao;

import dto.AlertResponse;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBConnect {

    /**
     * 테이블 생성
     * 해당 테이블이 이미 생성되어 있다면 삭제하고 다시 생성
     *
     * - Inspection 테이블: 측정소 점검 내역 테이블
     * (station: 측정소 / content: 점검 내역)
     *
     * - AlertInfo 테이블: 경보 발령 정보 테이블
     * (statin: 측정소 / level: 경보 단계 / time: 발령 시간)
     */
    public void createTable() {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql1 = "CREATE TABLE Inspection (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    station VARCHAR(255)," +
                "    content TEXT" +
                ")";

        String sql2 = "CREATE TABLE AlertInfo (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    station VARCHAR(255)," +
                "    level VARCHAR(255)," +
                "    date DATETIME" +
                ")";
        try{
            // 테이블 존재 여부 확인을 위한 쿼리
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, null, "AlertInfo", null);

            // 테이블이 존재하는 경우
            if (rs.next()) {
                dropTable(conn, pstmt,"AlertInfo"); // 테이블 삭제
            }

            rs = metaData.getTables(null, null, "inspection", null);

            // 테이블이 존재하는 경우
            if(rs.next()){
                dropTable(conn, pstmt,"inspection"); // 테이블 삭제
            }

            pstmt = conn.prepareStatement(sql1);
            pstmt.execute();
            pstmt = conn.prepareStatement(sql2);
            pstmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection(rs, pstmt, conn);
        }
    }

    /**
     * 테이블 삭제
     */
    public void dropTable(Connection conn, PreparedStatement pstmt, String tableName) throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        pstmt = conn.prepareStatement(sql);
        pstmt.execute();
    }

    /**
     * 측정소 검증 내역 저장하기
     */
    public void saveInspection(String station, String content) {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;

        String sql = "insert into inspection(station, content) values(?,?)";

        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,station);
            pstmt.setString(2, content);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeConnection(null, pstmt, conn);
        }
    }

    /**
     * 경보 발령 정보 저장
     */
    public void saveAlertInfo(String station, String level, LocalDateTime date){
        Connection conn = getConnection();
        PreparedStatement pstmt = null;

        String sql = "insert into alertInfo(station, level, date) values(?,?,?)";

        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, station);
            pstmt.setString(2, level);
            pstmt.setTimestamp(3, Timestamp.valueOf(date));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(null, pstmt, conn);
        }
    }

    /**
     * 경보 발령 알람을 위해 경보 정보 불러오기
     * 발생한 시간 순서대로 정렬
     */
    public List<AlertResponse> getAlert(){
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "select * from alertInfo order by date";

        List<AlertResponse> alertList = new ArrayList<>();

        try{
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()){
                String station = rs.getString("station");
                String level = rs.getString("level");
                Timestamp timestamp = rs.getTimestamp("date");
                LocalDateTime date = timestamp.toLocalDateTime();

                alertList.add(new AlertResponse(station, level, date));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(rs, pstmt, conn);
        }

        return alertList;
    }

    /**
     * DB 연결
     */
    public Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC driver load success");
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC driver load fail");
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/exem?characterEncoding=UTF-8&serverTimezone=Asia/Seoul&useSSL=false", "root","${USER.PW}");
            System.out.println("DB connect success");
        }
        catch(SQLException e) {
            System.out.println("connect fail");
            e.printStackTrace();
        }

        return con;
    }

    /**
     * 커넥션 해제하기
     */
    public void closeConnection(ResultSet rs, PreparedStatement pstmt, Connection con) {
        if(rs!=null)
        {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(pstmt!=null)
        {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(con!=null)
        {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
