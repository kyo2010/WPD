/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2.SQLModels;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import photodetector2.SQLLite.Utils.Tools;
import photodetector2.SQLLite.Utils.UserException;

/**
 *
 * @author kyo
 */
public class DataBaseStructure {

  public static DBAddonStructure[] sql_addons = new DBAddonStructure[]{
     new DBAddonStructure(1.1, "ALTER TABLE PD_TEST ADD VIDEO_FILE_FULL VARCHAR(255) NOT NULL DEFAULT '';"),
     new DBAddonStructure(1.2, "ALTER TABLE PD_TEST_LOG ADD TEST_ID INTEGER DEFAULT NULL;"),
     new DBAddonStructure(1.3, "ALTER TABLE PD_TEST ADD LOGS INTEGER NOT NULL DEFAULT 0;"),
     new DBAddonStructure(1.4, "ALTER TABLE PD_USER ADD UFILE_FOR_LEARNING VARCHAR(255) NOT NULL DEFAULT '';"),
     new DBAddonStructure(1.5, "ALTER TABLE PD_TEST ADD FRAME_INTERVAL INTEGER NOT NULL DEFAULT 300;"),               
  };

  public static class DBAddonStructure {

    public double version;
    public String sql;

    public DBAddonStructure(double version, String sql) {
      this.version = version;
      this.sql = sql;
    }
  }

  public static double executeAddons(double currentVersion, Connection conn) throws UserException {
    double max_version = 1.0;
    try {      
      for (DBAddonStructure addon : sql_addons) {
        PreparedStatement stat = null;
        ResultSet rs = null;
        try{
          if (addon.version>currentVersion){
            stat = conn.prepareStatement(addon.sql);
            stat.execute();
          }  
        }catch(Exception e){
            int res = JOptionPane.showConfirmDialog(null, "Do you want to continue? Addon sql version " + addon.sql + ", sql:"+addon.sql, "Please check database and restart programm", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
            
            }else{
              throw e;
            }          
        }finally{
          if (rs!=null) rs.close();
          if (stat!=null) stat.close();
        }  
        if (max_version<addon.version) max_version = addon.version;
      }
    } catch (Exception e) {
      throw new UserException("System update is error", Tools.traceError(e));
    }
    return max_version;
  }
}