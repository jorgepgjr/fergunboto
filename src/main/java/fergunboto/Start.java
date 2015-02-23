package fergunboto;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import nu.pattern.OpenCV;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Start {

	public static void main(String[] args) throws AWTException, IOException {
		OpenCV.loadShared();
		System.out.println("Welcome to OpenCV " + Core.VERSION);
		System.out.println("Welcome to OpenCV " + Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());
        Start.captureScreen();
        matchImage();
	}
	
	public static void matchImage(){
		System.out.println("\nRunning Template Matching");

        Mat img = Highgui.imread("src/main/resources/poker.jpg");
        Mat templ = Highgui.imread("src/main/resources/template_pot.jpg");

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF || Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        // / Show me what you got
        
        Point pontoFinal = new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows());
        
        Core.rectangle(img, matchLoc, pontoFinal, new Scalar(0, 255, 0));
        
        Point pontoDoValor = new Point(pontoFinal.x+80, pontoFinal.y-25);
        
        Core.rectangle(img, pontoFinal, pontoDoValor, new Scalar(255, 0, 0));
        
        // Save the visualized detection.
        System.out.println("Writing "+ "saida.jpg");
        Highgui.imwrite("src/main/resources/saida.jpg", img);

	}
	
	
	public static void captureScreen() throws AWTException, IOException{
		Robot robot = new Robot();
		BufferedImage a = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(a, "jpg", new File("src/main/resources/testPrintScreen.jpg"));
	}

}
