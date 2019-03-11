/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import static javax.swing.JOptionPane.showMessageDialog;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import photodetector2.SQLLite.Utils.UserException;
import photodetector2.SQLModels.PD_USER;

/**
 *
 * @author kyo
 */
public class PhotoDetectorTools {

  // Standart all faces
  opencv_objdetect.CascadeClassifier face_cascade = null;
  // Profiles 
  opencv_face.FaceRecognizer faceRecognizer = null;
  public String PROFILE_RECOGNIZER_FILE = null;

  public static final int TRAINING_MAX_HEIGHT = 100; // 
  public static final int COUNT_SKIP_FRAMES_BEFORE_ERRORS = 3;

  opencv_core.Scalar GREEN_COLOR = new opencv_core.Scalar(0, 255, 0, 1);
  opencv_core.Scalar RED_COLOR = new opencv_core.Scalar(0, 0, 255, 1);

  public boolean IS_LOADED = false;
  // Текущая картинка
  public opencv_core.IplImage image = null;

  // Конвертер из Frame в Image (Картинка)
  OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
  // Конвертер из Frame в Mat (Матрица)
  OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

  //Конвертер в BufferedImage для вывода в Окно, превью
  Java2DFrameConverter paintConverter = new Java2DFrameConverter();
  BufferedImage image_buf = null;

  // колво лиц
  public long previus_count_faces = -1;
  // колво фреймов с нарушением
  public int current_frame_inlog = 0;
  // колво фреймов с новым лицом
  public int current_frame_add_new_prfile = 0;

  public long CURRENT_FACES_COUNT = 0;

  WebPhotoDetector mainFrame = null;

  public PhotoDetectorTools(WebPhotoDetector mainFrame, String path) {
    this.mainFrame = mainFrame;
    IS_LOADED = false;
    PROFILE_RECOGNIZER_FILE = path + "\\data\\training.xml";
    //Попытка загрузки классификатора
    try {
      face_cascade = new opencv_objdetect.CascadeClassifier(
              path + "\\data\\haarcascade_frontalface_alt2.xml" // база лиц, классификатор
      // "data\\haarcascade_frontalface_alt.xml" // поиск совпадений
      // "data\\haarcascade_eye.xml"  // распознование по глазам
      );

      if (face_cascade.isNull()) {
        throw new IOException("Ошибка загрузки файла классификатора.");
      }
      IS_LOADED = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    //Выполнение загрузки выбранного классификатора
    loadFaceRecognizerStructure();
  }

  public void loadFaceRecognizerStructure() {
    try {
      faceRecognizer = getRecognizer();
      faceRecognizer.load(PROFILE_RECOGNIZER_FILE);
    } catch (Exception e) {
    }
  }

  public opencv_face.FaceRecognizer getRecognizer() {
    return opencv_face.createEigenFaceRecognizer();
  }

  public opencv_core.Mat getMapForTrainingOrPrediction(opencv_core.Mat src) {
    opencv_core.Mat face_ressized = new opencv_core.Mat();
    // Преобразовать в разрешение 100x100
    opencv_imgproc.resize(src, face_ressized, new opencv_core.Size(TRAINING_MAX_HEIGHT, TRAINING_MAX_HEIGHT));
    return face_ressized;
  }

  public opencv_core.Mat processImage(opencv_core.Mat src, WebPhotoDetectorEvent event, Map<String, PD_USER> users) {
    opencv_core.Mat result = null;
    boolean showMessage = false;
    CURRENT_FACES_COUNT = 0;
    
    PD_USER user = null;

    //if (effectMode == PhotoDetecter.EFFECT_MODE.RECOGNIZE) {
    opencv_core.RectVector faces = new opencv_core.RectVector();
    // Найти лица на изображении:
    //Mat videoMat = converterToMat.convert(src.asCvMat());

    opencv_core.Mat videoMat = new opencv_core.Mat();
    src.copyTo(videoMat);
    opencv_core.Mat videoMatGray = new opencv_core.Mat();
    // Конвертировать изображение в черно-белый формат:
    opencv_imgproc.cvtColor(videoMat, videoMatGray, opencv_imgproc.COLOR_BGRA2GRAY);
    //opencv_imgproc.equalizeHist(videoMatGray, videoMatGray);

    face_cascade.detectMultiScale(videoMatGray, faces);

    opencv_core.Scalar color = RED_COLOR;
    if (faces.size() == 1) {
      color = GREEN_COLOR;
    }

    CURRENT_FACES_COUNT = faces.size();

    int prediction = -1;
    for (int i = 0; i < faces.size(); i++) {
      opencv_core.Rect face_i = faces.get(i);

      opencv_core.Mat face = new opencv_core.Mat(videoMatGray, face_i);
      opencv_core.Mat face_ressized = getMapForTrainingOrPrediction(face);
      // Нахождение возможных лиц                
      try {
        prediction = faceRecognizer.predict_label(face_ressized);        
      } catch (Exception e) {
      }

      //int prediction = lbphFaceRecognizer..predict(face);
      // Создание прямоугольника
      opencv_imgproc.rectangle(videoMat, face_i, color);

      // Вычисление положения текста к прямоугольнику 
      int pos_x = Math.max(face_i.tl().x() - 10, 0);
      int pos_y = Math.max(face_i.tl().y() - 10, 0);
      //Отображение "face" при ошибке распознавания или отсутствии подходящего 
      //эталонного изображения в профилях
      String txt = "face " + (i + 1);
      //Отображение имени и фамилии пользователя при успешном распознавании
      if (users!=null){
        PD_USER uInfo = users.get(""+prediction);
        if (uInfo!=null){
          txt = uInfo.UNAME;
          user = uInfo;
        }
      }  
      /*if (prediction >= 0 && prediction < ProfileInfo.profiles.size()) {
        ProfileInfo info = ProfileInfo.profiles.get(prediction);
        txt = info.firstName + " " + info.lastName;
      }*/
      opencv_imgproc.putText(videoMat, txt, new opencv_core.Point(pos_x, pos_y), opencv_core.FONT_HERSHEY_PLAIN, 1.0, color);
    }

    long count_faces = faces.size();
    if (prediction == -1 && count_faces == 1) {
      //Добавить профиль можно лишь при распознавании на экране ровно одного лица
      current_frame_add_new_prfile++;
      if (current_frame_add_new_prfile >= COUNT_SKIP_FRAMES_BEFORE_ERRORS) {
        current_frame_add_new_prfile = 0;
        //addNewProfile();
      }
    }
    if (prediction != -1) {
      current_frame_add_new_prfile = 0;
    }

    //System.out.println("faces:"+count_faces+" prediction:"+prediction);
    if (previus_count_faces != count_faces) {
      current_frame_inlog++;
    } else {
      current_frame_inlog = 0;
    }

    if (current_frame_inlog >= COUNT_SKIP_FRAMES_BEFORE_ERRORS) {
      if (faces.size() == 0 || faces.size() > 1) {
        showMessage = true;
      } else {
        showMessage = false;
      }
      /*if (enableLogger && previus_count_faces != count_faces) {
          LogGridForm.addLog(image, LogGridForm.getByFacesCount(count_faces));
        }*/
      previus_count_faces = count_faces;
    }
    //Отображение текста нарушений на экране
    if (showMessage) {
      if (event != null) {
        event.photoDetecterAction(faces.size(),user);
      }
      if (faces.size() == 0) {
        opencv_imgproc.putText(videoMat, WebPhotoDetectorEvent.MESSAGE_FACESS_NOT_FOUND, new opencv_core.Point(30, 30), opencv_core.FONT_HERSHEY_PLAIN, 2.0, color);          
      }
      if (faces.size() > 1) {
        opencv_imgproc.putText(videoMat, WebPhotoDetectorEvent.MESSAGE_MORE_THAN_ONE, new opencv_core.Point(30, 30), opencv_core.FONT_HERSHEY_PLAIN, 2.0, color);
      }
    }
    //addProfileAction.setEnabled(faces.size() == 1)
    result = videoMat;
    //}
    return result;
  }

  public BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
    BufferedImage image;
    if (sourceImage.getType() == targetType) {
      image = sourceImage;
    } else {
      image = new BufferedImage(sourceImage.getWidth(),
              sourceImage.getHeight(), targetType);
      image.getGraphics().drawImage(sourceImage, 0, 0, null);
    }
    return image;
  }

  public static ImageIcon resize(Icon icon, int maxWidth, int maxHeight) {
    if (icon == null) {
      return null;
    }
    BufferedImage b = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
    icon.paintIcon(null, b.getGraphics(), 0, 0);
    double aspect = (double) icon.getIconWidth() / icon.getIconHeight();
    double newAspect = (double) maxWidth / maxHeight;
    int newWidth, newHeight;
    if (newAspect > aspect) {
      newHeight = maxHeight;
      newWidth = icon.getIconWidth() * maxHeight / icon.getIconHeight();
    } else {
      newWidth = maxWidth;
      newHeight = icon.getIconHeight() * maxWidth / icon.getIconWidth();
    }
    Image scaled = b.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
    return new ImageIcon(scaled);
  }

  public void lerning() throws UserException {
    List<PD_USER> list = PD_USER.dbControl.getList(mainFrame.con, "1=1 ORDER by UID");

    int countProfiles = list.size();
    if (countProfiles > 0) {
      opencv_core.MatVector images = new opencv_core.MatVector(countProfiles);
      opencv_core.Mat labels = new opencv_core.Mat(countProfiles, 1, opencv_core.CV_32SC1);
      IntBuffer labelsBuf = labels.createBuffer();
      int counter = 0;
      // Последний профиль должен распозноваться первым
      for (counter = countProfiles - 1; counter >= 0; counter--) {
        PD_USER info = list.get(counter);
        opencv_core.RectVector faces = new opencv_core.RectVector();    
        String fName = mainFrame.SERVLET_PATH + "/" + info.UFILE;
        fName  = fName.replace('/', '\\');
        opencv_core.Mat img = new opencv_core.Mat();
        img = opencv_imgcodecs.imread(fName);
        opencv_core.Mat grayImg = new opencv_core.Mat(img);
        opencv_imgproc.cvtColor(img, grayImg, opencv_imgproc.COLOR_BGRA2GRAY);
        face_cascade.detectMultiScale(grayImg, faces);                
        
        if (faces.size() == 1) {
          opencv_core.Rect face_i = faces.get(0);
          opencv_core.Mat face = new opencv_core.Mat(grayImg, face_i);
          opencv_core.Mat face_ressized = getMapForTrainingOrPrediction(face);
          // проверка превью
          info.UFILE_FOR_LEARNING = "users/" + info.UID + " [F].jpg";
          opencv_imgcodecs.cvSaveImage(mainFrame.SERVLET_PATH + "/" + info.UFILE_FOR_LEARNING, new opencv_core.IplImage(face_ressized));
          int label = info.UID;
          images.put(counter, face_ressized);
          labelsBuf.put(counter, label);
        }else{
          info.UFILE_FOR_LEARNING = "users/" + info.UID + " [F].jpg";
          opencv_imgcodecs.cvSaveImage(mainFrame.SERVLET_PATH + "/" + info.UFILE_FOR_LEARNING, new opencv_core.IplImage(grayImg));          
        }
      }
      try {
        faceRecognizer = getRecognizer();
        faceRecognizer.train(images, labels);
        faceRecognizer.save(PROFILE_RECOGNIZER_FILE);
        loadFaceRecognizerStructure();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      faceRecognizer.clear();
      new File(PROFILE_RECOGNIZER_FILE).delete();
      loadFaceRecognizerStructure();
    }
  }

}
