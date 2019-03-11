/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2;

import photodetector2.SQLModels.PD_USER;

/**
 *
 * @author kyo
 */
public interface WebPhotoDetectorEvent {
 
   public final static String MESSAGE_FACESS_NOT_FOUND = "Error! Faces are not found!";
   public final static String MESSAGE_MORE_THAN_ONE = "Error! More than One Face!";
  
  public void photoDetecterAction(long countFaces, PD_USER user);
}
