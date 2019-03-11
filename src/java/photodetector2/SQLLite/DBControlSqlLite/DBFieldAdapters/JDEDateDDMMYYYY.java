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
public class JDEDateDDMMYYYY extends JDEDate {

  @Override
  public boolean equals(Object obj) {
    JDEDateDDMMYYYY data = (JDEDateDDMMYYYY) obj;
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
