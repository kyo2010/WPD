/*** Class Generator V.0.1, This class is based on 'PD_USER' table.
 *** The class was generated automatically.  ***/

package photodetector2.SQLModels;

import photodetector2.SQLLite.DBControlSqlLite.*;
import java.sql.Time;
import photodetector2.SQLLite.Utils.JDEDate;

public class PD_USER {
  
  public int UID = 0;  
  public String UNAME = ""; 
  public String UFILE = ""; 
  public String UFILE_FOR_LEARNING = ""; 
  public JDEDate UCREATEDAT = new JDEDate(); 
  
  /** Constructor */ 
  public PD_USER() {
  };
  
  public static DBModelControl<PD_USER> dbControl = new DBModelControl<PD_USER>(PD_USER.class, "PD_USER", new DBModelField[]{
    new DBModelField("UID").setDbFieldName("\"UID\"").setAutoIncrement(),
    new DBModelField("UNAME").setDbFieldName("\"UNAME\""),
    new DBModelField("UFILE").setDbFieldName("\"UFILE\""),
    new DBModelField("UFILE_FOR_LEARNING").setDbFieldName("\"UFILE_FOR_LEARNING\""),      
    new DBModelField("UCREATEDAT").setDbFieldName("\"UCREATEDAT\""),
  });
  
}
