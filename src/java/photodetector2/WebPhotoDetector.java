/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2;

import java.awt.Dimension;
import photodetector2.PhotoDetectorTools;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import photodetector2.SQLLite.DBControlSqlLite.DBModelTest;
import photodetector2.SQLLite.Utils.JDEDate;
import photodetector2.SQLLite.Utils.Tools;
import photodetector2.SQLLite.Utils.UserException;
import photodetector2.SQLModels.DataBaseStructure;
import photodetector2.SQLModels.PD_SETTING;
import photodetector2.SQLModels.PD_TEST;
import photodetector2.SQLModels.PD_TEST_LOG;
import photodetector2.SQLModels.PD_USER;
import wpd.UserInfo;

/**
 *
 * @author kyo
 */
public class WebPhotoDetector implements WebPhotoDetectorEvent {

  // Номер Вебкамеры в системе, выбор камеры по умолчанию
  private static int WEBCAM_DEVICE_INDEX = 0;
  // Разрешение камеры
  private int captureWidth = 640;
  private int captureHeight = 480;
  public int SCREEN_PREVIEW_WIDTH = 640;
  public int SCREEN_PREVIEW_HEIGHT = 480;
  //Полное разрешение записи
  public int FULL_VIDEO_WIDTH = 1280;
  public int FULL_VIDEO_HEIGHT = 480;
  public Connection con = null;
  public static String VIDEO_PATH = "video";
  public static String USERS_PHOTO_PATH = "photo";
  public long STRAT_TIME = 0;
  public long STOP_TIME = 0;
  public String LAST_ERROR = "";
  public String CURRENT_IMAGE_FILE_NAME = "";
  public String CURRENT_IMAGE_FILE_NAME_WITH_DESCKTOP = "";
  public PD_USER CURRENT_USER = null;
  public PD_TEST CURRENT_TEST = null;
  public ConfigTools configTools = new ConfigTools(this);

  public final static String WEB_PATH_WPD_FILES = "WPD-FILES/";

  public boolean IS_INIT = false;
  public String SERVLET_PATH = "";

  public void destroy() {
    try {
      stopAction(null);
      con.close();
    } catch (Exception e) {
    }
  }

  public void init(String SERVLET_PATH) {
    this.SERVLET_PATH = SERVLET_PATH;
    VIDEO_PATH = SERVLET_PATH + "/video";
    USERS_PHOTO_PATH = SERVLET_PATH + "/users";
    PD_TOOLS = new PhotoDetectorTools(this, SERVLET_PATH);

    try {
      con = DBModelTest.getConnection(SERVLET_PATH);
      // check for update
      double db_version = PD_SETTING.getParam(con, "DataBaseVersion", 1.0);
      double db_version_new = DataBaseStructure.executeAddons(db_version, con);
      PD_SETTING.setParam(con, "DataBaseVersion", "" + db_version_new);
    } catch (UserException ue) {
      LAST_ERROR = ue.details + " " + ue.error;
    } catch (Exception e) {
      LAST_ERROR = "Database file is not found. " + DBModelTest.DATABASE;
    }
  }

  public String CURRENT_VIDEO_FILE_NAME = "";
  public String CURRENT_VIDEO_FILE_FOR_WEB = "";


  PD_TEST_LOG CURRENT_LOG_ITEM = null;

  @Override
  public void photoDetecterAction(long countFaces, PD_USER user) {
    if (CURRENT_TEST != null) {
      if (countFaces == 0) {
        // создаю классы для логов, но в базу еще не пишу, так как сами картинки еще не созданы
        // в основном методе CameraTimer - приосходит создание картинки и запись информации о логе в файл
        CURRENT_LOG_ITEM = new PD_TEST_LOG();
        CURRENT_LOG_ITEM.TEST_ID = CURRENT_TEST.TEST_ID;
        CURRENT_LOG_ITEM.LOG_AT = new JDEDate();
        CURRENT_LOG_ITEM.LOG_TYPE = 0;
        CURRENT_LOG_ITEM.LOG_MSG = WebPhotoDetectorEvent.MESSAGE_FACESS_NOT_FOUND;
      }
      if (countFaces > 1) {
        CURRENT_LOG_ITEM = new PD_TEST_LOG();
        CURRENT_LOG_ITEM.TEST_ID = CURRENT_TEST.TEST_ID;
        CURRENT_LOG_ITEM.LOG_AT = new JDEDate();
        CURRENT_LOG_ITEM.LOG_TYPE = 0;
        CURRENT_LOG_ITEM.LOG_MSG = WebPhotoDetectorEvent.MESSAGE_MORE_THAN_ONE;
      }
    }
  }

  //режим приложения
  enum WORK_MODE {
    NONE,
    CAMERA
    //CAMERA, IMAGE, VIDEO
  }

  //режим эффекта записи
  enum EFFECT_MODE {
    NORNAL, GRAY, CANNY, RECOGNIZE
  }

  //режим работы приложения
  public WORK_MODE workMode = WORK_MODE.NONE;
  public EFFECT_MODE effectMode = EFFECT_MODE.RECOGNIZE;

  PhotoDetectorTools PD_TOOLS = null;

  public WebPhotoDetector() {
    // Get Connection and install new Updates    
  }

  public void openCameraGraber() {
    try {
      cmeraGrabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
      cmeraGrabber.setImageWidth(captureWidth);
      cmeraGrabber.setImageHeight(captureHeight);
      cmeraGrabber.start();
    } catch (Exception e) {
      cmeraGrabber = null;
      LAST_ERROR = "Please check camera connection " + e.getMessage();
    }
  }  

  public int FRAME_INTERVAL = 0;
  public int CURRENT_LOG_IMAGES = 0;
  public Map<String, PD_USER> users = null;
  int HIGTLIGHT_VIDEO = 0;

  public void startAction(UserInfo ui) throws UserException {
    CURRENT_IMAGE_FILE_NAME = "";
    HIGTLIGHT_VIDEO = configTools.getHIGHLIGHT_VIDEO();
    PD_USER user = PD_USER.dbControl.getItem(con, "UNAME=?", ui.USER_NAME);
    if (user != null) {
      if (user.UFILE != null && !user.UFILE.equals("")) {
        new File(user.UFILE).delete();
      }
      PD_USER.dbControl.delete(con, "UNAME=?", ui.USER_NAME);
    }
    user = new PD_USER();
    user.UNAME = ui.USER_NAME;
    user.UFILE = "";
    PD_USER.dbControl.insert(con, user);
    CURRENT_USER = user;

    PD_TEST test = new PD_TEST();
    test.FIO = user.UNAME;
    test.START_AT = new JDEDate();
    test.TEST_NAME = ui.TEST_NAME;
    PD_TEST.dbControl.insert(con, test);
    CURRENT_TEST = test;
    CURRENT_LOG_IMAGES = 0;
    
    users = PD_USER.dbControl.getMap(con,"UID", "1=1 ORDER by UID");

    workMode = workMode.CAMERA;
    STRAT_TIME = Calendar.getInstance().getTime().getTime();
    try {
      openCameraGraber();
      if (cmeraGrabber != null) {
        startRecorder(ui);
        FRAME_INTERVAL = configTools.getTIME_INTERVAL();
        cameraTimer.setDelay(FRAME_INTERVAL);
        cameraTimer.start();
      } else {
        stopAction(ui);
      }
    } catch (Exception e) {
      cmeraGrabber = null;
      LAST_ERROR = "Please check camera connection " + e.getMessage();
      stopAction(ui);
    }
  }

  public void closeCameraGraber() {
    try {
      if (cmeraGrabber != null) {
        cmeraGrabber.close();
        cmeraGrabber.release();
        cmeraGrabber = null;
      }
    } catch (Exception ex) {
    }
  }

  public boolean testIsStarted() {
    return (workMode != workMode.NONE);
  }

  public void stopAction(UserInfo ui) {
    STOP_TIME = Calendar.getInstance().getTime().getTime();
    cameraTimer.stop();
    stopRecorder();
    workMode = workMode.NONE;
    closeCameraGraber();
    CURRENT_USER = null;
    CURRENT_TEST = null;
  }
 
  Timer cameraTimer = new Timer(1000, (e) -> {
    try {
      // Выполнение во время захвата изображения
      org.bytedeco.javacv.Frame capturedFrame = null;
      boolean skipFrame = true;
      try {
        capturedFrame = WebPhotoDetector.this.cmeraGrabber.grab();
        skipFrame = false;
      } catch (Exception ein1) {
        //ein1.printStackTrace();
        try {
          // trye to grab camera
          closeCameraGraber();
          openCameraGraber();
        } catch (Exception ein2) {
        }
      }

      if (!skipFrame) {
        opencv_core.Mat img_cam = null;
        // Получение изображения с камеры
        img_cam = PD_TOOLS.converterToMat.convert(capturedFrame);
        opencv_core.IplImage img_new2 = opencv_core.IplImage.create(FULL_VIDEO_WIDTH, FULL_VIDEO_HEIGHT, opencv_core.IPL_DEPTH_8U, 3);
        opencv_core.CvRect rect2 = new opencv_core.CvRect(0, 0, captureWidth, captureHeight);
        opencv_core.cvSetImageROI(img_new2, rect2);
        opencv_core.cvZero(img_new2);
        opencv_core.cvCopy(new opencv_core.IplImage(img_cam), img_new2);
        PD_TOOLS.image = new opencv_core.IplImage(img_cam);
        // Выполнение распознавания по изображения с камеры
        opencv_core.Mat img = PD_TOOLS.processImage(img_cam, this,users);
        opencv_core.IplImage img_add = null;
        // Создание нового полноразмерного изображения, удвоенное в размере
        opencv_core.IplImage img_new = opencv_core.IplImage.create(FULL_VIDEO_WIDTH, FULL_VIDEO_HEIGHT, opencv_core.IPL_DEPTH_8U, 3);
        opencv_core.CvRect rect = new opencv_core.CvRect(0, 0, captureWidth, captureHeight);
        opencv_core.cvSetImageROI(img_new, rect);
        opencv_core.cvZero(img_new);
        opencv_core.cvCopy(new opencv_core.IplImage(img), img_new);
        // add screen 
        if (1 == 1) {
          // Получить снимок рабочего стола
          BufferedImage screen = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
          // Конвертация в подходящий формат
          screen = PD_TOOLS.convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
          BufferedImage screen_small = new BufferedImage(SCREEN_PREVIEW_WIDTH, SCREEN_PREVIEW_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
          Graphics g = screen_small.createGraphics();
          g.drawImage(screen, 0, 0, SCREEN_PREVIEW_WIDTH, SCREEN_PREVIEW_HEIGHT, null);
          g.dispose();
          opencv_core.IplImage screen_mat = PD_TOOLS.grabberConverter.convert(PD_TOOLS.paintConverter.getFrame(screen_small));
          opencv_core.CvRect rectCam = new opencv_core.CvRect(captureWidth, 0, SCREEN_PREVIEW_WIDTH, SCREEN_PREVIEW_HEIGHT);

          // To Save Recognize face
          if (HIGTLIGHT_VIDEO==1){
            opencv_core.cvSetImageROI(img_new, rectCam);
            opencv_core.cvZero(img_new);
            opencv_core.cvCopy(screen_mat, img_new);
            img_add = img_new;  
          }else{  
            // To Save Original face
            opencv_core.cvSetImageROI(img_new2, rectCam);
            opencv_core.cvZero(img_new2);
            opencv_core.cvCopy(screen_mat, img_new2);
            img_add = img_new2;
          };  

          //screen_mat.release();
        } else {
        }
        Frame frameOut = null;
        frameOut = PD_TOOLS.converterToMat.convert(img_add);
        
        //ImageIcon imageForIndex = new ImageIcon(PD_TOOLS.image_buf);
        //imageForIndex.s                
        // Если одно лицо и у пользователя нет фото файл,  создаем файл 
        if (PD_TOOLS.CURRENT_FACES_COUNT == 1 && CURRENT_USER != null && CURRENT_USER.UFILE.equalsIgnoreCase("")) {
          PD_TOOLS.image_buf = PD_TOOLS.paintConverter.getBufferedImage(frameOut);
          CURRENT_IMAGE_FILE_NAME = SERVLET_PATH + "/last_view.jpg";
          CURRENT_IMAGE_FILE_NAME_WITH_DESCKTOP = SERVLET_PATH + "/last_view2.jpg";
          ImageIO.write(PD_TOOLS.image_buf, "jpg", new File(CURRENT_IMAGE_FILE_NAME_WITH_DESCKTOP));
          BufferedImage bi = PD_TOOLS.paintConverter.getBufferedImage(capturedFrame);
          ImageIO.write(bi, "jpg", new File(CURRENT_IMAGE_FILE_NAME));
          new File(CURRENT_IMAGE_FILE_NAME);
          String fileName = "users/" + CURRENT_USER.UID + ".jpg";
          new File(SERVLET_PATH + "/users").mkdirs();
          FileUtils.copyFile(new File(CURRENT_IMAGE_FILE_NAME), new File(SERVLET_PATH + "/" + fileName));
          CURRENT_USER.UFILE = fileName;
          CURRENT_USER.dbControl.save(con, CURRENT_USER);
          
          PD_TOOLS.lerning();          
          users = PD_USER.dbControl.getMap(con,"UID", "1=1 ORDER by UID");
        }

        if (CURRENT_LOG_ITEM != null) {
          PD_TOOLS.image_buf = PD_TOOLS.paintConverter.getBufferedImage(frameOut);
          CURRENT_LOG_IMAGES++;
          new File(SERVLET_PATH+"/video/" + CURRENT_VIDEO_FILE_FOR_WEB).mkdirs();
          CURRENT_LOG_ITEM.LOG_IMAGE = "video/" + CURRENT_VIDEO_FILE_FOR_WEB+"/log"+Tools.padl(""+CURRENT_LOG_IMAGES, 10, '0')+".jpg";
          ImageIO.write(PD_TOOLS.image_buf, "jpg", new File(SERVLET_PATH+"/"+CURRENT_LOG_ITEM.LOG_IMAGE));          
          CURRENT_LOG_ITEM.dbControl.insert(con, CURRENT_LOG_ITEM);
          CURRENT_TEST.logs.add(CURRENT_LOG_ITEM);
          CURRENT_LOG_ITEM = null;
          if (CURRENT_TEST!=null) CURRENT_TEST.LOGS = CURRENT_LOG_IMAGES;
        }

        // add frame to recoder
        if (WebPhotoDetector.this.RECODER_IS_RUN) {
          WebPhotoDetector.this.recorder.record(frameOut); // Запись картинки с камеры с распознованием
        }
        img_new.release();
        img_new2.release();
        img_cam.release();
      }
    } catch (Exception ein) {
      ein.printStackTrace();
    }
  });

  public OpenCVFrameGrabber cmeraGrabber = null;
  //final private static int FRAME_RATE = 30;
  public boolean RECODER_IS_RUN = false;
  public FFmpegFrameRecorder recorder = null;
  //public final String VIDEO_EXT = "avi";
  public final String VIDEO_EXT = "mp4";
  
  public void startRecorder(UserInfo ui) {
    new File(VIDEO_PATH).mkdirs();

    //Создание названия файла записи
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    Date date = new Date(Calendar.getInstance().getTimeInMillis());
    String uid = simpleDateFormat.format(date);
    JDEDate jd = new JDEDate();
    String folder = jd.getDateAsYYYYMMDD("-");
    new File(VIDEO_PATH + "/" + folder).mkdirs();
    CURRENT_VIDEO_FILE_NAME = VIDEO_PATH + "/" + folder + "/" + uid + "." + VIDEO_EXT;
    CURRENT_VIDEO_FILE_FOR_WEB = folder + "/" + uid;
    recorder = new FFmpegFrameRecorder(CURRENT_VIDEO_FILE_NAME, FULL_VIDEO_WIDTH, FULL_VIDEO_HEIGHT);
    // AV_CODEC_ID_MPEG4 = 13,
    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
    recorder.setAudioCodec(avcodec.AV_CODEC_ID_NONE);
    recorder.setFormat(VIDEO_EXT);
    recorder.setPixelFormat(0);
    recorder.setFrameRate(9); //TODO: Don't hardcode.
    recorder.setVideoBitrate(10 * 1024 * 1024);
    try {
      recorder.start();
      RECODER_IS_RUN = true;
    } catch (Exception e) {
      ui.LAST_ERROR = "Ошибка записи! " + e.toString();
      e.printStackTrace();
    }
  }

  public void stopRecorder() {
    if (RECODER_IS_RUN) {
      RECODER_IS_RUN = false;
      try {
        if (recorder != null) {
          // задержка для созранения последних кадров записи
          try {
            Thread.currentThread().sleep(FRAME_INTERVAL);
          } catch (Exception e) {
          }
          recorder.stop();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    try {
      if (CURRENT_TEST != null) {
        CURRENT_TEST.VIDEO_FILE = "video/" + CURRENT_VIDEO_FILE_FOR_WEB;
        CURRENT_TEST.VIDEO_FILE_FULL = "video/" + CURRENT_VIDEO_FILE_FOR_WEB + "." + VIDEO_EXT;
        CURRENT_TEST.FINISH_AT = new JDEDate();
        CURRENT_TEST.LOGS = CURRENT_LOG_IMAGES;
        CURRENT_TEST.dbControl.update(con, CURRENT_TEST);
      }
      // save images
      FileUtils.copyFile(new File(CURRENT_IMAGE_FILE_NAME), new File(SERVLET_PATH + "/video/" + CURRENT_VIDEO_FILE_FOR_WEB + "/start.jpg"));
      FileUtils.copyFile(new File(CURRENT_IMAGE_FILE_NAME_WITH_DESCKTOP), new File(SERVLET_PATH + "/video/" + CURRENT_VIDEO_FILE_FOR_WEB + "/start_full.jpg"));
    } catch (Exception e) {
    }
  }
  
  public void deleteTest(int test_id) throws UserException, IOException{
    PD_TEST test = PD_TEST.dbControl.getItem(con, "TEST_ID=?",test_id);
    if (test!=null){
      new File(SERVLET_PATH + "/"+test.VIDEO_FILE_FULL).delete();
      FileUtils.deleteDirectory( new File(SERVLET_PATH + "/"+test.VIDEO_FILE) );
      PD_TEST_LOG.dbControl.delete(con, "TEST_ID=?",test_id);
      PD_TEST.dbControl.delete(con, "TEST_ID=?",test_id);
    }
  }
  
  public void deleteUser(int user_id) throws UserException, IOException{
    PD_USER user = PD_USER.dbControl.getItem(con, "UID=?",user_id);
    if (user!=null){
      new File(SERVLET_PATH + "/"+user.UFILE).delete();
      PD_USER.dbControl.delete(con, "UID=?",user_id);
    }
  }
  
  public void learning() throws UserException{
    PD_TOOLS.lerning();
  }
}
