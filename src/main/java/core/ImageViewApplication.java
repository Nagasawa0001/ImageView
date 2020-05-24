package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageViewApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(ImageViewApplication.class, args);

		File file = new File("src/main/resources/input.png");

		BufferedImage bi = ImageIO.read(file);
		System.out.println(bi.getHeight());
		System.out.println(bi.getWidth());
	}

}
