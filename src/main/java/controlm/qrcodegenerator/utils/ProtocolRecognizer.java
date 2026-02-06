package controlm.qrcodegenerator.utils;

import controlm.qrcodegenerator.model.ProtocolMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ProtocolRecognizer {

    private static final Pattern FALLBACK_PATTERN = Pattern.compile(
            "([А-ЯA-Z]{2,4}" +
                    "\\s*-\\s*" +
                    "\\d+" +
                    "[а-яa-z]?" +
                    "\\s*-\\s*" +
                    "\\d+" +
                    "[а-нп-яa-z]?)" +
                    "\\s*(?:от|oт|0т)\\s*" +
                    "(\\d{2}\\s*\\.\\s*\\d{2}\\s*\\.\\s*(?:\\d{4}|\\d{2}))",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    public ProtocolMetadata extract(String text) {

        String normalizeText = normalizeOcrText(text);
        Matcher fallBack = FALLBACK_PATTERN.matcher(normalizeText);
        while (fallBack.find()) {
            if (looksLikeProtocol(fallBack.group(1))) {
                log.info("fallback {} {}", fallBack.group(1), fallBack.group(2));

                return new ProtocolMetadata(
                        normalizeNumber(fallBack.group(1)),
                        fallBack.group(2));
            }
        }

        return null;
    }

    private String normalizeOcrText(String text) {
        return text
                // похожие на тире → тире
                .replaceAll("[—–−]", "-")

                // шум между цифрами и буквами → тире
                .replaceAll("(?<=\\d)\\p{Punct}&&[^.-]+(?=\\d)", "-")

                // шум между буквами и цифрами
                .replaceAll("(?<=[А-ЯA-Z])[\\p{Punct}\\s]+(?=\\d)", "-")

                // шум между цифрами и буквами
                .replaceAll("(?<=\\d)[\\p{Punct}\\s]+(?=[А-ЯA-Zа-яa-z])", "")

                // "о т" → "от"
                .replaceAll("о\\s*т", "от")

                // лишние символы
                .replaceAll("[`'\"|\\\\]", "")

                // схлопываем пробелы
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private String normalizeNumber(String n) {
        return n.replaceAll("\\s+", "")
                .replace("—", "-");
    }

    private boolean looksLikeProtocol(String s) {
        String normalize = normalizeNumber(s);

        if (normalize.chars().filter(ch -> ch == '-').count() != 2) return false;

        return normalize.split("-")[0].matches("[А-ЯA-Z]+");
    }

    // TODO сделать шаблон для СШ (дата отдельно)
}
