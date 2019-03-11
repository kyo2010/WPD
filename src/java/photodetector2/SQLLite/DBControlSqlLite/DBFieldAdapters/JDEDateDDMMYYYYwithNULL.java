/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2.SQLLite.DBControlSqlLite.DBFieldAdapters;

import photodetector2.SQLLite.Utils.JDEDate;

/**
 *
 * @author kyo
 */
public class JDEDateDDMMYYYYwithNULL extends JDEDate {

  @Override
  public boolean equals(Object obj) {
    JDEDateDDMMYYYYwithNULL data = (JDEDateDDMMYYYYwithNULL) obj;    
    if (data!=null && 
          data.getRealYear()==getRealYear() && 
          data.getRealMonth()==getRealMonth() && 
          data.getDay()==getDay())
    {
      return true;
    }      
    return false;  
  }
  
  
  
}
