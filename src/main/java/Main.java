
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    public static String Naam;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException exception) {
            throw new RuntimeException("kan db.properties niet laden", exception);
        }
    }

    public static void main(String[] args) {

        getReizigers();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void getReizigers() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("verbonden");

                String sqlstm = "SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum FROM reiziger";

                PreparedStatement ps = conn.prepareStatement(sqlstm);

                ResultSet results = ps.executeQuery();
                System.out.println("Alle reizigers:");

                while (results.next()) {
                    int id = results.getInt("reiziger_id");
                    String voorletters = results.getString("voorletters");
                    String tussenvoegsel = results.getString("tussenvoegsel");
                    String achternaam = results.getString("achternaam");
                    String geboortedatum = results.getString("geboortedatum");

                    if (tussenvoegsel != null && !tussenvoegsel.isEmpty()) {
                        Naam = voorletters + "." + " " + tussenvoegsel + " " + achternaam;
                    } else {
                        Naam = voorletters + "." + " " + achternaam;
                    }

                    System.out.println("     #" + id + " "  +Naam +  " (" + geboortedatum + ")" );
                }

                results.close();
                ps.close();
                conn.close();
                System.out.println("Verbinding gesloten");

            } else {
                System.out.println("Geen verbinding .");
            }
        } catch(SQLException exception) {
            System.out.println("SQL Error: " + exception.getMessage());
        }
    }
}
