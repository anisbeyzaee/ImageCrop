package imageCrop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Crop {
	BufferedImage orig, croped;
	
	
	public void cropImage() throws IOException {
		
		 String fileNameFire = "Testing\\FireImages"; // original file
	        String resizedImagesFire = "Testing\\FireResized"; // resized file
	        String fileNameNoFire = "Testing\\NoFireImages"; // original file
	        String resizedImagesNoFire = "Testing\\NoFireResized"; // resized file
	        String outputFileName = "testing_data.csv";
			
	        System.out.println("creating Crop");
			ReadImage imageReader = new ReadImage(fileNameFire, resizedImagesFire, fileNameNoFire, resizedImagesNoFire, outputFileName);
		
	}
	public static void main(String[] args) throws IOException {
		Crop crop = new Crop();
		crop.cropImage();

	}

}
