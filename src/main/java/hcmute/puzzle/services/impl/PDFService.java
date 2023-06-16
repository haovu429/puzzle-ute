package hcmute.puzzle.services.impl;

import com.ironsoftware.ironpdf.PdfDocument;
import com.ironsoftware.ironpdf.edit.PageSelection;
import com.ironsoftware.ironpdf.image.ToImageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PDFService {
	public static void convertPdfToImage(MultipartFile multipartFile) throws IOException {
		PdfDocument instance = PdfDocument.fromFile(Paths.get("business plan.pdf"));
		List<BufferedImage> extractedImages = instance.toBufferedImages();
		ToImageOptions rasterOptions = new ToImageOptions();
		rasterOptions.setImageMaxHeight(800);
		rasterOptions.setImageMaxWidth(500);

		List<BufferedImage> sizedExtractedImages = instance.toBufferedImages(rasterOptions, PageSelection.allPages());
		int pageIndex = 1;
		for (BufferedImage extractedImage : sizedExtractedImages) {
			String fileName = "assets/images/" + pageIndex++ + ".png";
			ImageIO.write(extractedImage, "PNG", new File(fileName));
		}
	}
}
