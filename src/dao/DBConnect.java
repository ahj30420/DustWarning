package dao;

import java.sql.*;

public class DBConnect {

    /**
     * 테이블 생성
     * - Inspection 테이블: 측정소 점검 내역 테이블
     * (station: 측정소 / content: 점검 내역)
     *
     * - AlertInfo 테이블: 경보 발령 정보 테이블
     * (statin: 측정소 / level: 경보 단계 / time: 발령 시간)
     */
    public void createTable() {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;

        String sql1 = "CREATE TABLE Inspection (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    station VARCHAR(255)," +
                "    content TEXT" +
                ")";

        String sql2 = "CREATE TABLE AlertInfo (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    station VARCHAR(255)," +
                "    level VARCHAR(255)," +
                "    time DATETIME" +
                ")";
        try{
            pstmt = conn.prepareStatement(sql1);
            pstmt.execute();
            pstmt = conn.prepareStatement(sql2);
            pstmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeConnection(null, pstmt, conn);
        }
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
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/exem?characterEncoding=UTF-8&serverTimezone=Asia/Seoul&useSSL=false", "root","z30420qwas");
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
