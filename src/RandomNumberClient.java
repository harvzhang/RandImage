import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class RandomNumberClient {

	public static void main(String[] args) {
		try {
			saveRandImage(128);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Requests for random integers of specific size and saves the image of random pixels
	 */
	public static void saveRandImage(int size) throws MalformedURLException, IOException {
		System.out.println("Status: saving random image of size 128x128");
		
		ArrayList<Integer> output = HTTPManager.getRandInts(0,255, size*size, size);
		
		BufferedImage buffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				buffer.setRGB(i, j, output.get(i*size + j));
			}
		}
		
		File outputImage = new File("randImg.bmp");
	    ImageIO.write(buffer, "bmp", outputImage);
	    System.out.println("Status: Image Saved");
	}
}
