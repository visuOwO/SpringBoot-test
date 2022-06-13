package DbConnectTesting;

import java.sql.Connection;
import java.util.Map;

public class DbConnector {
    private String dbUser = null;
    private String dbPwd = null;
    private String jdbcConnectString = null;
    private String jdbcDriver = null;
    private String dbPlatform = "h2";
    public void init(Map<String, String> params) {
        dbUser = (String) params.get("Username");
        dbPwd = (String) params.get("Password");
        jdbcConnectString = "jdbc:h2:~/OpenAS2-DB/openas2";
        jdbcDriver = "org.h2.Driver";
        try{
            Class.forName(jdbcDriver);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() throws Exception {
    }
    public void selectTest() {
        Connection conn = null;
    }
}
