package imageCrop;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ReadImage {
    private static final int IMG_WIDTH = 1200;
    private static final int IMG_HEIGHT = 900;
    private static final int Resized_IMG_WIDTH = 300;
    private static final int Resized_IMG_HEIGHT = 300;
    private static BufferedImage[][] bil = new BufferedImage[4][3];
    public ReadImage(String fileNameFire, String resizedImageFire, String fileNameNoFire, String resizedImageNoFire, String outputFileName) throws IOException {
    	
        File fileFire = new File(fileNameFire);
        
        ArrayList<String> fireList = new ArrayList<String>(); // list of file names w/ fire
        resizeImage(fileFire, fireList, fileNameFire, resizedImageFire);
        
        File fileNoFire = new File(fileNameNoFire);
       
        ArrayList<String> noFireList = new ArrayList<String>(); // list of file names w/out fire
        resizeImage(fileNoFire, noFireList, fileNameNoFire, resizedImageNoFire);
        
        //writeToFile(fireList, noFireList, resizedImageFire, resizedImageNoFire, outputFileName);
    	
    }

    private static BufferedImage resizeImageHelper(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    // Resizes all images in a folder to a uniform size
    public static void resizeImage(File directory, ArrayList<String> list, String fileName, String newFileName) throws IOException {
        File[] items = directory.listFiles();
        
        for (int i = 0; i < items.length; i++) {
            list.add(items[i].getName());
            try {
                String file = fileName;			//reading the file path
                file += "\\" + items[i].getName();
                String newFile = newFileName;
                newFile += "\\" + items[i].getName();
                BufferedImage originalImage = ImageIO.read(new File(file));    //using the path
                int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
                if (items[i].getName().endsWith("jpg")) {
                	System.out.println("Resizing image name =  " + items[i].getName());
                    BufferedImage resizeImageJpg = resizeImageHelper(originalImage, type);
                    cropTheImage(resizeImageJpg, newFile, "jpg");
                    //ImageIO.write(resizeImageJpg, "jpg", new File(newFile));
                } else if (items[i].getName().endsWith("jpeg")) {
                	System.out.println("Resizing image name =  " + items[i].getName());
                    BufferedImage resizeImageJpeg = resizeImageHelper(originalImage, type);
                    cropTheImage(resizeImageJpeg, newFile, "jpeg");
                    //ImageIO.write(resizeImageJpeg, "jpeg", new File(newFile));
                } else {
                	System.out.println("Resizing image name =  " + items[i].getName());
                    BufferedImage resizeImagePng = resizeImageHelper(originalImage, type);
                    cropTheImage(resizeImagePng, newFile, "png");
                    //ImageIO.write(resizeImagePng, "png", new File(newFile));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void cropTheImage(BufferedImage resizeImagePng, String newFile, String extension) throws IOException {
		//resizeImagePng.getRGB()
    	
    	
    	System.out.println("Created new buffer Image of size 300*300");
    	for (int i=0; i<4; i++) {
    		for (int j=0; j<3; j++) {
    			BufferedImage bi =  new BufferedImage(300, 300, resizeImagePng.getType());
    			for (int m=0 ; m<300; m++) {   //outer loop to go through the height
					for (int n=0 ; n<300; n++) {
						bi.setRGB(m, n, resizeImagePng.getRGB(m+ i*300, n + j*300) );
						//System.out.printf("wring at  bi [ %d][%d ] in ....   bil[ %d ] , [ %d ] \n",i,j,m , n);
					}
					
    			}
    			bil[i][j] = bi;
    		}
    	}
    	for( int i=0; i<4; i++) {
    		for (int j=0; j< 3; j++) {
    			String newFileR = newFile.substring(0, newFile.length()-4) + i + j;
    			//System.out.println("writing bil[] [] " + i + j);
    			ImageIO.write(bil[i][j], extension, new File(newFileR+ "."+ extension));
    		}
    	}
    	
    }
	// Takes both fire/no fire folders and creates csv file out of their pixel values
    public static void writeToFile(ArrayList<String> fileNameOne, ArrayList<String> fileNameTwo, String resizedFire, String resizedNoFire, String outputFileName) throws IOException {
        BufferedImage raw;
        PrintWriter pw = new PrintWriter(new File(outputFileName));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileNameOne.size(); i++) {
            sb.append("1");
            sb.append(',');
            String image = resizedFire;
            image += "\\" + fileNameOne.get(i);
            raw = ImageIO.read(new File(image));
            int width = raw.getWidth();
            int height = raw.getHeight();
            int count = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //this is how we grab the RGBvalue of a pixel
                    // at x,y coordinates in the image
                    int rgb = raw.getRGB(x, y);
                    //extract the red value
                    int r = (rgb >> 16) & 0xFF;
                    sb.append((double) r / 255);
                    sb.append(',');
                    count++;
                    //extract the green value
                    int g = (rgb >> 8) & 0xFF;
                    sb.append((double) g / 255);
                    sb.append(',');
                    count++;
                    //extract the blue value
                    int b = rgb & 0xFF;
                    sb.append((double) b / 255);
                    sb.append(',');
                    count++;
                }
            }
            sb.append('\n');
        }

        for (int i = 0; i < fileNameTwo.size(); i++) {
            sb.append("0");
            sb.append(',');
            String image = resizedNoFire;
            image += "\\" + fileNameTwo.get(i);
            
            raw = ImageIO.read(new File(image));
            sb.append(imageStringHelper(image, raw));
            
            
        }
        pw.write(sb.toString());
        pw.close();
    }
    
    private static String imageStringHelper(String image, BufferedImage raw) {
    	StringBuilder sb = new StringBuilder();
    	
    	
        int width = raw.getWidth();
        int height = raw.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //this is how we grab the RGBvalue
                // of a pixel at x,y coordinates in the image
                int rgb = raw.getRGB(x, y);
                //extract the red value
                int r = (rgb >> 16) & 0xFF;
                sb.append((double) r / 255);
                sb.append(',');
                //extract the green value
                int g = (rgb >> 8) & 0xFF;
                sb.append((double) g / 255);
                sb.append(',');
                //extract the blue value
                int b = rgb & 0xFF;
                sb.append((double) b / 255);
                sb.append(',');
            }
        }
        sb.append('\n');
    	
    	
    	return sb.toString();
    }
}












//package imageCrop;
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.awt.image.RenderedImage;
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Random;
//
//public class ReadImage {
//    private static final int IMG_WIDTH = 1200;
//    private static final int IMG_HEIGHT = 900;
//	private static BufferedImage[] cropedImages;
//	
//	static Random random = new Random();
//	private static RenderedImage resizeImageJpeg;
//    
//    public ReadImage(String fileNameFire, String resizedImageFire, String fileNameNoFire, String resizedImageNoFire, String outputFileName) throws IOException {
//    	
//        File fileFire = new File(fileNameFire);
//        
//        ArrayList<String> fireList = new ArrayList<String>(); // list of file names w/ fire
//        resizeImage2(fileFire, fireList, fileNameFire, resizedImageFire);
//        
//        File fileNoFire = new File(fileNameNoFire);
//       
//        ArrayList<String> noFireList = new ArrayList<String>(); // list of file names w/out fire
//        resizeImage2(fileNoFire, noFireList, fileNameNoFire, resizedImageNoFire);
//        
//        writeToFile(fireList, noFireList, resizedImageFire, resizedImageNoFire, outputFileName);
//    	
//    }
//
//    private static BufferedImage resizeImageHelper(BufferedImage originalImage, int type) {
//    	int height = originalImage.getHeight();
//    	int width = originalImage.getWidth();
////    
//        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
//        Graphics2D g = resizedImage.createGraphics();
//        
//        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
//        g.dispose();
//        return resizedImage;
//    }
//
//		 private static BufferedImage[] resizeImageHelper2(BufferedImage originalImage, int type) {
//			 
//			  	cropedImages = new BufferedImage[12];
//		    	int height = originalImage.getHeight();
//		    	int width = originalImage.getWidth();
//		    	
//		    	int cropH = height/3;
//		    	int cropW = width/4;
//		    	System.out.println("h : " + height + "W = " +width + " c H "+ cropH +" cW = " +cropW);
//		    	BufferedImage p; 
//				for (int i= 0; i<12; i++) {
//					p = new BufferedImage(width,height,originalImage.getType());
//		    		for (int k =1; k<4; k++) {
//		    			System.out.print(k);
//		    			for(int y=k*cropH; y<cropH +(k*cropH) ;y++){
//		    				System.out.println("y= "+ y);
//		    				for(int x=k*cropW;x<  cropW + (k* cropW) ;x++){
//		    					System.out.print("x= "+ x);
//		    					p.setRGB(x, y, originalImage.getRGB(x+ (k*cropW),y+ (k*cropH)));
//		    					System.out.println("cordinate "+ x+ " " +(k*cropW) + " " +y+" "  + (k*cropH));
//		    					System.out.println("h : " + height + "W = " +width + " c H "+ cropH +" cW = " +cropW);
//		    				}
//		    			}
//		    		}
//		    		cropedImages[i]= p;
//
//				}
//		        return cropedImages;
//		    }
//		 
//		 public static void resizeImage2(File directory, ArrayList<String> list, String fileName, String newFileName) throws IOException {
//		        File[] items = directory.listFiles();
//		        BufferedImage[] cr = new BufferedImage[12];
//		        for (int i = 0; i < items.length; i++) {
//		            list.add(items[i].getName());
//		            try {
//		                String file = fileName;
//		                file += "\\" + items[i].getName();
//		                String newFile = newFileName;
//		                newFile += "\\" + items[i].getName()+ Integer.toString(random.nextInt(1001));
//		                BufferedImage originalImage = ImageIO.read(new File(file));
//		                int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
//		                if (items[i].getName().endsWith("jpg")) {
//		                	cr = resizeImageHelper2(originalImage, type);
//		                	for(int j=0; j<cr.length; j++) {
//		                	BufferedImage resizeImageJpg = cr[i]; 
//		                    ImageIO.write(resizeImageJpg, "jpg", new File(newFile));
//		                	}
//		                } else if (items[i].getName().endsWith("jpeg")) {
//		                	cr = resizeImageHelper2(originalImage, type);
//		                	for(int j=0; j<cr.length; j++) {
//		                	BufferedImage resizeImageJpeg = cr[i];
//		                    ImageIO.write(resizeImageJpeg, "jpeg", new File(newFile));
//		                	}
//		                } else {
//		                	cr = resizeImageHelper2(originalImage, type);
//		                	for(int j=0; j<cr.length; j++) {
//		                	BufferedImage resizeImagePng = cr[i];
//		                    ImageIO.write(resizeImagePng, "png", new File(newFile));
//		                }
//		            } }catch (IOException e) {
//		                System.out.println(e.getMessage());
//		            }
//		        }
//		    }
//		 
//		 
//		
//		
//    // Resizes all images in a folder to a uniform size
//    

//	public static void resizeImage(File directory, ArrayList<String> list, String fileName, String newFileName) throws IOException {
//        File[] items = directory.listFiles();
//        
//        for (int i = 0; i < items.length; i++) {
//            list.add(items[i].getName());
//            try {
//                String file = fileName;
//                file += "\\" + items[i].getName();
//                String newFile = newFileName;
//                newFile += "\\" + items[i].getName();
//                BufferedImage originalImage = ImageIO.read(new File(file));
//                int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
//                if (items[i].getName().endsWith("jpg")) {
//                    BufferedImage resizeImageJpg = resizeImageHelper(originalImage, type);
//                    ImageIO.write(resizeImageJpg, "jpg", new File(newFile));
//                } else if (items[i].getName().endsWith("jpeg")) {
//                    BufferedImage resizeImageJpeg = resizeImageHelper(originalImage, type);
//                    ImageIO.write(resizeImageJpeg, "jpeg", new File(newFile));
//                } else {
//                    BufferedImage resizeImagePng = resizeImageHelper(originalImage, type);
//                    ImageIO.write(resizeImagePng, "png", new File(newFile));
//                }
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//    }
//
//    // Takes both fire/no fire folders and creates csv file out of their pixel values
//    public static void writeToFile(ArrayList<String> fileNameOne, ArrayList<String> fileNameTwo, String resizedFire, String resizedNoFire, String outputFileName) throws IOException {
//        BufferedImage raw;
//        PrintWriter pw = new PrintWriter(new File(outputFileName));
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < fileNameOne.size(); i++) {
//            sb.append("1");
//            sb.append(',');
//            String image = resizedFire;
//            image += "\\" + fileNameOne.get(i);
//            raw = ImageIO.read(new File(image));
//            int width = raw.getWidth();
//            int height = raw.getHeight();
//            int count = 0;
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    //this is how we grab the RGBvalue of a pixel
//                    // at x,y coordinates in the image
//                    int rgb = raw.getRGB(x, y);
//                    //extract the red value
//                    int r = (rgb >> 16) & 0xFF;
//                    sb.append((double) r / 255);
//                    sb.append(',');
//                    count++;
//                    //extract the green value
//                    int g = (rgb >> 8) & 0xFF;
//                    sb.append((double) g / 255);
//                    sb.append(',');
//                    count++;
//                    //extract the blue value
//                    int b = rgb & 0xFF;
//                    sb.append((double) b / 255);
//                    sb.append(',');
//                    count++;
//                }
//            }
//            sb.append('\n');
//        }
//
//        for (int i = 0; i < fileNameTwo.size(); i++) {
//            sb.append("0");
//            sb.append(',');
//            String image = resizedNoFire;
//            image += "\\" + fileNameTwo.get(i);
//            
//            raw = ImageIO.read(new File(image));
//            sb.append(imageStringHelper(image, raw));
//            
//            
//        }
//        pw.write(sb.toString());
//        pw.close();
//    }
//    
//    private static String imageStringHelper(String image, BufferedImage raw) {
//    	StringBuilder sb = new StringBuilder();
//    	
//    	
//        int width = raw.getWidth();
//        int height = raw.getHeight();
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                //this is how we grab the RGBvalue
//                // of a pixel at x,y coordinates in the image
//                int rgb = raw.getRGB(x, y);
//                //extract the red value
//                int r = (rgb >> 16) & 0xFF;
//                sb.append((double) r / 255);
//                sb.append(',');
//                //extract the green value
//                int g = (rgb >> 8) & 0xFF;
//                sb.append((double) g / 255);
//                sb.append(',');
//                //extract the blue value
//                int b = rgb & 0xFF;
//                sb.append((double) b / 255);
//                sb.append(',');
//            }
//        }
//        sb.append('\n');
//    	
//    	
//    	return sb.toString();
//    }
//}
//
//
//
//
//
//
