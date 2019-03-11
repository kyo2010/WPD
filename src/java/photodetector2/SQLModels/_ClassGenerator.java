/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2.SQLModels;

import java.sql.Connection;
import photodetector2.SQLLite.DBControlSqlLite.DBModelTest;
import photodetector2.SQLLite.Utils.UserException;

/**
 *
 * @author kyo
 */
public class _ClassGenerator {
  public static void main(String[] args){
    Connection con = null;
    try {
      con = DBModelTest.getConnection("web/WPD-FILES");
            
      //DBModelTest.generateClazz(con,"PD_SETTING","PD_SETTING","photodetector2.SQLModels");
      //DBModelTest.generateClazz(con,"PD_TEST","PD_TEST","photodetector2.SQLModels");
      //DBModelTest.generateClazz(con,"PD_TEST_LOG","PD_TEST_LOG","photodetector2.SQLModels");      
      
      DBModelTest.generateClazz(con,"PD_USER","PD_USER","photodetector2.SQLModels");      
      
    } catch (UserException ue) {
      System.out.println("Error : " + ue.error + " details : " + ue.details);
    } catch (Exception e) {
      e.printStackTrace();
    }finally{
      try{
        if (con!=null) con.close();
      }catch(Exception e){}  
    }
  }
}
