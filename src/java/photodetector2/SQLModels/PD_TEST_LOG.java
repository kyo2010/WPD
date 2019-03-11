/*** Class Generator V.0.1, This class is based on 'PD_TEST_LOG' table.
 *** The class was generated automatically.  ***/

package photodetector2.SQLModels;

import photodetector2.SQLLite.DBControlSqlLite.DBModelControl;
import photodetector2.SQLLite.DBControlSqlLite.DBModelField;
import java.sql.Time;
import photodetector2.SQLLite.Utils.JDEDate;

public class PD_TEST_LOG {
  
  public long LOG_ID;   //  NOT_DETECTED
  public JDEDate LOG_AT;   //  NOT_DETECTED
  public String LOG_MSG;   //  NOT_DETECTED
  public String LOG_IMAGE="";   //  NOT_DETECTED
  public int LOG_TYPE;   //  NOT_DETECTED
  public int TEST_ID = 0;
  
  /** Constructor */ 
  public PD_TEST_LOG() {
  };
  
  public static DBModelControl<PD_TEST_LOG> dbControl = new DBModelControl<PD_TEST_LOG>(PD_TEST_LOG.class, "PD_TEST_LOG", new DBModelField[]{
    new DBModelField("LOG_ID").setDbFieldName("\"LOG_ID\"").setAutoIncrement(),
    new DBModelField("LOG_AT").setDbFieldName("\"LOG_AT\""),
    new DBModelField("LOG_MSG").setDbFieldName("\"LOG_MSG\""),
    new DBModelField("LOG_IMAGE").setDbFieldName("\"LOG_IMAGE\""),
    new DBModelField("LOG_TYPE").setDbFieldName("\"LOG_TYPE\""),
    new DBModelField("TEST_ID").setDbFieldName("\"TEST_ID\""),
  });  
}
