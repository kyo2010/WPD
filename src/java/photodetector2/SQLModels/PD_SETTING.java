/*** Class Generator V.0.1, This class is based on 'PD_SETTING' table.
 *** The class was generated automatically.  ***/

package photodetector2.SQLModels;

import photodetector2.SQLLite.DBControlSqlLite.DBModelControl;
import photodetector2.SQLLite.DBControlSqlLite.DBModelField;
import java.sql.Connection;
import java.sql.Time;
import photodetector2.SQLLite.Utils.UserException;

public class PD_SETTING {
  
  public String PARAM_NAME;   //  NOT_DETECTED
  public String PARAM_VALUE;   //  NOT_DETECTED
  
  /** Constructor */ 
  public PD_SETTING() {
  };
  
  public static DBModelControl<PD_SETTING> dbControl = new DBModelControl<PD_SETTING>(PD_SETTING.class, "PD_SETTING", new DBModelField[]{
    new DBModelField("PARAM_NAME").setDbFieldName("\"PARAM_NAME\""),
    new DBModelField("PARAM_VALUE").setDbFieldName("\"PARAM_VALUE\""),
  });
  
  
    public static String getParam(Connection conn, String paramName, String paramDefault) {
    try{
      PD_SETTING v1 = PD_SETTING.dbControl.getItem(conn, "PARAM_NAME=?", paramName);
      if (v1!=null){
        return v1.PARAM_VALUE;
      }  
    }catch(Exception e){
      e.printStackTrace();
    } 
    return paramDefault;
  }
  
  public static void setParam(Connection conn, String paramName, String paramValue) {
    try{
      PD_SETTING.dbControl.delete(conn, "PARAM_NAME=?", paramName);
      PD_SETTING v1 = new PD_SETTING();
      v1.PARAM_NAME = paramName;
      v1.PARAM_VALUE = paramValue;
      PD_SETTING.dbControl.save(conn, v1);
    }catch(UserException e){
      System.out.println("Error:"+e.error+", datails:"+e.details);              
    }catch(Exception e){
      e.printStackTrace();
    }     
  }
  
  public static int getParam(Connection conn, String paramName, int paramDefault) {
    try{
      PD_SETTING v1 = PD_SETTING.dbControl.getItem(conn, "PARAM_NAME=?", paramName);
      if (v1!=null){
        if (v1.PARAM_VALUE!=null && v1.PARAM_VALUE.equalsIgnoreCase("")) return 0;
        return Integer.parseInt(v1.PARAM_VALUE);
      }  
    }catch(Exception e){
      e.printStackTrace();
    } 
    return paramDefault;
  }
  
  public static double getParam(Connection conn, String paramName, double paramDefault) {
    try{
      PD_SETTING v1 = PD_SETTING.dbControl.getItem(conn, "PARAM_NAME=?", paramName);
      if (v1!=null){
        return Double.parseDouble(v1.PARAM_VALUE);
      }  
    }catch(Exception e){
      e.printStackTrace();
    } 
    return paramDefault;
  }

  
}
