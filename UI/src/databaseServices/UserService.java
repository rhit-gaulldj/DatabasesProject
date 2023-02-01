package databaseServices;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Base64;
import java.util.Random;

public class UserService {

    private static final Random RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private DBConnectionService dbService;

    private String sessionId;

    public UserService(DBConnectionService dbService) {
        this.dbService = dbService;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Returns session ID or null if none
    public String login(String email, String password) {
        try {
            CallableStatement getSaltStmt = dbService.getConnection()
                    .prepareCall("{call get_salt(?, ?)}");
            getSaltStmt.setString(1, email);
            getSaltStmt.registerOutParameter(2, Types.VARCHAR);
            getSaltStmt.execute();
            String saltStr = getSaltStmt.getString(2);
            byte[] salt = DECODER.decode(saltStr);
            // Hash our password and send it to the server to verify
            String myHashed = hashPassword(salt, password);

            CallableStatement loginStmt = dbService.getConnection()
                    .prepareCall("{? = call log_in(?, ?, ?)}");
            loginStmt.registerOutParameter(1, Types.INTEGER);
            loginStmt.setString(2, email);
            loginStmt.setString(3, myHashed);
            loginStmt.registerOutParameter(4, Types.VARCHAR);
            loginStmt.execute();
            int status = loginStmt.getInt(1);
            String sessionId = loginStmt.getString(4);

            if (status != 0) {
                return null;
            }

            // Save the session ID to a file
            FileWriter writer = new FileWriter(getSessionIdPath(), false);
            writer.write(sessionId);
            writer.close();

            this.sessionId = sessionId;

            return sessionId;

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String email, String password) {
        byte[] salt = getNewSalt();
        String hashed = hashPassword(salt, password);
        try {
            CallableStatement stmt = this.dbService.getConnection()
                    .prepareCall("{? = call register(?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, email);
            stmt.setString(3, hashed);
            stmt.setString(4, ENCODER.encodeToString(salt));
            stmt.execute();
            int status = stmt.getInt(1);
            if (status != 0) {
                return false;
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logOut() {
        try {
            CallableStatement stmt = dbService.getConnection()
                    .prepareCall("{? = call log_out(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, sessionId);
            stmt.execute();
            // TODO: Handle log out errors
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public byte[] getNewSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public String getStringFromBytes(byte[] data) {
        return ENCODER.encodeToString(data);
    }

    public String hashPassword(byte[] salt, String password) {

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f;
        byte[] hash = null;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
            e.printStackTrace();
        }
        return getStringFromBytes(hash);
    }

    public static String getSessionIdPath() {
        String binDir = System.getProperty("user.dir");
        return binDir + "\\token.txt";
    }

}
