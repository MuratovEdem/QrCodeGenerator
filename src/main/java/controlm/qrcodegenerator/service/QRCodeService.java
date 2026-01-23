package controlm.qrcodegenerator.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import controlm.qrcodegenerator.model.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {
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
            String transliterated = transliterateToLatin(client.getName());

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

    private String transliterateToLatin(String text) {
        if (text == null) return "";

        Map<Character, String> mapping = new HashMap<>();
        mapping.put('а', "a"); mapping.put('б', "b"); mapping.put('в', "v"); mapping.put('г', "g");
        mapping.put('д', "d"); mapping.put('е', "e"); mapping.put('ё', "e"); mapping.put('ж', "zh");
        mapping.put('з', "z"); mapping.put('и', "i"); mapping.put('й', "i"); mapping.put('к', "k");
        mapping.put('л', "l"); mapping.put('м', "m"); mapping.put('н', "n"); mapping.put('о', "o");
        mapping.put('п', "p"); mapping.put('р', "r"); mapping.put('с', "s"); mapping.put('т', "t");
        mapping.put('у', "u"); mapping.put('ф', "f"); mapping.put('х', "h"); mapping.put('ц', "c");
        mapping.put('ч', "ch"); mapping.put('ш', "sh"); mapping.put('щ', "sch"); mapping.put('ъ', "");
        mapping.put('ы', "y"); mapping.put('ь', ""); mapping.put('э', "e"); mapping.put('ю', "yu");
        mapping.put('я', "ya");
        mapping.put('А', "A"); mapping.put('Б', "B"); mapping.put('В', "V"); mapping.put('Г', "G");
        mapping.put('Д', "D"); mapping.put('Е', "E"); mapping.put('Ё', "E"); mapping.put('Ж', "Zh");
        mapping.put('З', "Z"); mapping.put('И', "I"); mapping.put('Й', "I"); mapping.put('К', "K");
        mapping.put('Л', "L"); mapping.put('М', "M"); mapping.put('Н', "N"); mapping.put('О', "O");
        mapping.put('П', "P"); mapping.put('Р', "R"); mapping.put('С', "S"); mapping.put('Т', "T");
        mapping.put('У', "U"); mapping.put('Ф', "F"); mapping.put('Х', "H"); mapping.put('Ц', "C");
        mapping.put('Ч', "Ch"); mapping.put('Ш', "Sh"); mapping.put('Щ', "Sch"); mapping.put('Ъ', "");
        mapping.put('Ы', "Y"); mapping.put('Ь', ""); mapping.put('Э', "E"); mapping.put('Ю', "Yu");
        mapping.put('Я', "Ya");

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (mapping.containsKey(c)) {
                result.append(mapping.get(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
