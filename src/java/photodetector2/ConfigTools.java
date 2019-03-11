/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2;

import photodetector2.SQLModels.PD_SETTING;

public class ConfigTools {
  WebPhotoDetector mainForm = null;
  public ConfigTools(WebPhotoDetector mainForm){
    this.mainForm = mainForm;
  }
  
  public int getTIME_INTERVAL() {
    return PD_SETTING.getParam(mainForm.con, "TIME_INTERVAL", 200);
  }
  
  public String getADMIN_PASSWORD() {
    return PD_SETTING.getParam(mainForm.con, "ADMIN_PASSWORD", "admin");
  }
  
  public int getHIGHLIGHT_VIDEO() {
    return PD_SETTING.getParam(mainForm.con, "HIGHLIGHT_VIDEO", 1);
  }
}
