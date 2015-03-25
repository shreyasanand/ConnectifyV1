package com.team5.uta.connectifyv1;

import android.os.AsyncTask;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Shrikant on 3/1/2015.
 */
class DBConnection extends AsyncTask {

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */

    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    boolean isInsert = false;
    Object output = null;
    int updated = -1;

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            String url = "jdbc:mysql://connectifydbinstance.cl7ia31oqlit.us-east-1.rds.amazonaws.com:5555/";
            String dbName = "connectifydb";
            String driver = "com.mysql.jdbc.Driver";
            String userName = "connectify";
            String password = "password123";
            try {
                Class.forName(driver).newInstance();
                conn = DriverManager.getConnection(url + dbName, userName, password);
                System.out.println("Connected to the database");
            } catch (Exception e) {
                e.printStackTrace();
            }
            st = conn.createStatement();
            if (params[1] == true) {
                rs = st.executeQuery((String) params[0]);
            } else {
                isInsert = true;
                updated = st.executeUpdate((String) params[0]);
            }
            if (isInsert == false) {
                if (rs != null) {
                    output = (ResultSet) rs;
                    rs.close();
                }
            } else {
                output = (int) updated;
            }

            st.close();
            conn.close();
            System.out.println("Disconnected from database");
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getResult() {
        return output;
    }
}
