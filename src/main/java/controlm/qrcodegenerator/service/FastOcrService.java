package controlm.qrcodegenerator.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

@Slf4j
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
        BufferedImage top = safeCrop(pageImage);
        BufferedImage gray = toGray(top);
        return tesseract.doOCR(gray);
    }

    private BufferedImage safeCrop(
            BufferedImage img
    ) {
        int w = img.getWidth();
        int h = img.getHeight();

        int x = (int) Math.round(w * 0.1);
        int y = (int) Math.round(h * 0.15);

        int x2 = (int) Math.round(w * 0.9);
        int y2 = (int) Math.round(h * 0.30);

        // 🔐 жёсткая нормализация
        x = Math.max(0, x);
        y = Math.max(0, y);
        x2 = Math.min(w, x2);
        y2 = Math.min(h, y2);

        int cw = x2 - x;
        int ch = y2 - y;

        if (cw < 20 || ch < 20) {
            // слишком маленькая область — OCR бессмысленен
            return img;
        }

        return img.getSubimage(x, y, cw, ch);
    }
    // TODO сделать возможность регулировать диапазон сканирования

    private BufferedImage toGray(BufferedImage img) {
        BufferedImage gray = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        gray.getGraphics().drawImage(img, 0, 0, null);
        return gray;
    }
}
