package fergunboto;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import nu.pattern.OpenCV;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Start {

	public static void main(String[] args) throws Exception {
		OpenCV.loadShared();
//		System.out.println("Welcome to OpenCV " + Core.VERSION);
//		System.out.println("Welcome to OpenCV " + Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		Mat m = Mat.eye(3, 3, CvType.CV_8UC1);
//		System.out.println("m = " + m.dump());
		Start.captureScreen();
		matchImage();
	}

	public static void matchImage() throws Exception {
		System.out.println("\nRunning Template Matching");

		Mat img = Highgui.imread("src/main/resources/poker.jpg");
		Mat templ = Highgui.imread("src/main/resources/template_pot.jpg");

		// / Create the result matrix
		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		/// Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);

		Point matchLoc;
		if (Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF
				|| Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = mmr.minLoc;
		} else {
			matchLoc = mmr.maxLoc;
		}

		// / Show me what you got

		Point pontoFinal = new Point(matchLoc.x + templ.cols(), matchLoc.y
				+ templ.rows());

		//Rect da label pot
//		Core.rectangle(img, matchLoc, pontoFinal, new Scalar(0, 255, 0));

		Point pontoDoValor = new Point(pontoFinal.x + 80, pontoFinal.y - 25);

		//Rect do valor do pot
		Rect valor = new Rect(pontoFinal, pontoDoValor); 
//		Core.rectangle(img, pontoFinal, pontoDoValor, new Scalar(255, 0, 0));
		 
		Mat x = img.submat(valor);
		
		// Save the visualized detection.
		System.out.println("Writing " + "saida.jpg");
		Highgui.imwrite("src/main/resources/saida.jpg", img);

		Mat grayScale = x.clone();
		Mat grayScale2 = new Mat();
		
		Imgproc.cvtColor(x, grayScale , Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(grayScale, grayScale2, 127, 255, 0);
		
		Highgui.imwrite("src/main/resources/valorpot.jpg", grayScale2);
		
		File imagemValorPot = new File("src/main/resources/valorpot.jpg");
//		MatOfByte bytemat = new MatOfByte();
//		Highgui.imencode(".jpg", x, bytemat);
//		byte[] bytes = bytemat.toArray();
//		InputStream in = new ByteArrayInputStream(bytes);
//		BufferedImage buffImg = ImageIO.read(in);
		try{
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("tesseract src/main/resources/valorpot.jpg src/main/resources/valorpot");		
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void captureScreen() throws AWTException, IOException {
		Robot robot = new Robot();
		BufferedImage a = robot.createScreenCapture(new Rectangle(Toolkit
				.getDefaultToolkit().getScreenSize()));
		ImageIO.write(a, "jpg", new File(
				"src/main/resources/testPrintScreen.jpg"));
	}

}
