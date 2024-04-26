import dao.DBConnect;

public class Main {
    public static void main(String[] args) {

        DBConnect dbConnect = new DBConnect();

        dbConnect.createTable();

    }
}