package controlm.qrcodegenerator.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class FastOcrService {
    private final Tesseract tesseract;

    public FastOcrService() {
        tesseract = new Tesseract();
        tesseract.setDatapath(System.getenv("TESSDATA_PREFIX"));
        tesseract.setLanguage("rus");
        tesseract.setPageSegMode(6);
        tesseract.setOcrEngineMode(1);
    }

    public String recognizeHeader(BufferedImage pageImage) throws TesseractException {
        BufferedImage top = cropTop(pageImage);
        BufferedImage gray = toGray(top);
        return tesseract.doOCR(gray);
    }

    private BufferedImage cropTop(BufferedImage img) {
        int y = (int)  (img.getHeight() * 0.15);
        int h = (int) (img.getHeight() * 0.20);
        return img.getSubimage(0, y, img.getWidth(), h);
        // TODO сделать возможность регулировать диапазон сканирования
    }

    private BufferedImage toGray(BufferedImage img) {
        BufferedImage gray = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        gray.getGraphics().drawImage(img, 0, 0, null);
        return gray;
    }
}
