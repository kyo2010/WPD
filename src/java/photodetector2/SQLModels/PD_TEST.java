/*** Class Generator V.0.1, This class is based on 'PD_TEST' table.
 *** The class was generated automatically.  ***/

package photodetector2.SQLModels;

import photodetector2.SQLLite.DBControlSqlLite.DBModelControl;
import photodetector2.SQLLite.DBControlSqlLite.DBModelField;
import java.sql.Time;
import java.util.Vector;
import photodetector2.SQLLite.Utils.JDEDate;

public class PD_TEST {
  
  public int     TEST_ID;   //  NOT_DETECTED
  public String  TEST_NAME = "Manual";   //  NOT_DETECTED
  public String  FIO = "";   //  NOT_DETECTED
  public String  WEB_ID = "";   //  NOT_DETECTED
  public JDEDate START_AT = new JDEDate();   //  NOT_DETECTED
  public JDEDate FINISH_AT = new JDEDate();   //  NOT_DETECTED
  public String  VIDEO_FILE = "";   //  NOT_DETECTED
  public String  VIDEO_FILE_FULL = "";   //  NOT_DETECTED
  public int LOGS = 0;
  
  // Доступен, только для текущего теста
  public Vector<PD_TEST_LOG> logs = new Vector<PD_TEST_LOG>();
  
  /** Constructor */ 
  public PD_TEST() {
  };
  
  public static DBModelControl<PD_TEST> dbControl = new DBModelControl<PD_TEST>(PD_TEST.class, "PD_TEST", new DBModelField[]{
    new DBModelField("TEST_ID").setDbFieldName("\"TEST_ID\"").setAutoIncrement(),
    new DBModelField("TEST_NAME").setDbFieldName("\"TEST_NAME\""),
    new DBModelField("FIO").setDbFieldName("\"FIO\""),
    new DBModelField("WEB_ID").setDbFieldName("\"WEB_ID\""),
    new DBModelField("START_AT").setDbFieldName("\"START_AT\""),
    new DBModelField("FINISH_AT").setDbFieldName("\"FINISH_AT\""),
    new DBModelField("VIDEO_FILE").setDbFieldName("\"VIDEO_FILE\""),
    new DBModelField("VIDEO_FILE_FULL").setDbFieldName("\"VIDEO_FILE_FULL\""),
    new DBModelField("LOGS").setDbFieldName("\"LOGS\""),
  }); 
  
}
