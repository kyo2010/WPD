/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wpd;

import photodetector2.WebPhotoDetector;

/**
 *
 * @author kyo
 */
public class UserInfo {
  public String ID = "";
  
  // for Test
  public String USER_NAME = "";
  public String TEST_NAME = "Ручной тест";
  
  // For admin & web_contolr
  public String LOGIN = "";
  public boolean IS_ADMIN = false;
  public boolean IS_LOGIN = false;
  public String WEB_PAGE_CAPTION;
  public String LAST_ERROR = "";

  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }
    
  public String getUSER_NAME() {
    return USER_NAME;
  }

  public void setUSER_NAME(String USER_NAME) {
    this.USER_NAME = USER_NAME;
  }

  public boolean isIS_ADMIN() {
    return IS_ADMIN;
  }

  public void setIS_ADMIN(boolean IS_ADMIN) {
    this.IS_ADMIN = IS_ADMIN;
  }

  public String getWEB_PAGE_CAPTION() {
    return WEB_PAGE_CAPTION;
  }

  public void setWEB_PAGE_CAPTION(String WEB_PAGE_CAPTION) {
    this.WEB_PAGE_CAPTION = WEB_PAGE_CAPTION;
  }
  
  public boolean login(WebPhotoDetector mainForm,String login, String password){
    IS_LOGIN = false;
    IS_ADMIN = false;
    if (login==null) login = "";
    LOGIN = login;
    if ("admin".equalsIgnoreCase(login) && mainForm.configTools.getADMIN_PASSWORD().equals(password)){    
      IS_LOGIN = true;
      IS_ADMIN = true;      
      LOGIN = login;
    }else{
      
    }
    return IS_LOGIN;
  }
  
  public void logoff(){
    IS_LOGIN = false;
    IS_ADMIN = false;
    LOGIN = "";
  }

  public String getLOGIN() {
    return LOGIN;
  }

  public void setLOGIN(String LOGIN) {
    this.LOGIN = LOGIN;
  }

  public boolean isIS_LOGIN() {
    return IS_LOGIN;
  }

  public void setIS_LOGIN(boolean IS_LOGIN) {
    this.IS_LOGIN = IS_LOGIN;
  }
  
  
      
}
