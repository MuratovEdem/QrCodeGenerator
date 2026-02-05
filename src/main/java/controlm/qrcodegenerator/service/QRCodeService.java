package controlm.qrcodegenerator.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.utils.TransliterateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class QRCodeService {

    private final TransliterateUtils transliterateUtils;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.qr-code.directory:./qr-codes}")
    private String qrCodeDirectory;

    public String generateQRCodeImage(Long clientId) throws Exception {
        String url = baseUrl + "/client/" + clientId;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                url,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        // Сохраняем файл
        String fileName = "client_" + clientId + "_qr.png";
        Path path = Paths.get(qrCodeDirectory, fileName);
        Files.createDirectories(path.getParent());

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(image, "PNG", path.toFile());

        return fileName;
    }

    public String getQRCodeAsBase64(Long clientId) throws Exception {
        String url = baseUrl + "/client/" + clientId;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                url,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", os);

        return "data:image/png;base64," +
                Base64.getEncoder().encodeToString(os.toByteArray());
    }

    public byte[] getQRCodeImageBytes(Long clientId) throws Exception {
        String url = baseUrl + "/client/" + clientId;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                url,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", os);

        return os.toByteArray();
    }

    public String generateSafeFileName(Client client) {
        String baseName = "client_" + client.getId();

        if (client.getName() != null && !client.getName().trim().isEmpty()) {
            String transliterated = transliterateUtils.transliterateToLatin(client.getName());

            String asciiOnly = transliterated.replaceAll("[^\\x00-\\x7F]", "");

            String safe = asciiOnly.replaceAll("[\\\\/:*?\"<>|]", "_")
                    .replaceAll("\\s+", "_")
                    .trim();

            if (!safe.isEmpty()) {
                return safe + "_qr.png";
            }
        }

        return baseName + "_qr.png";
    }
}
