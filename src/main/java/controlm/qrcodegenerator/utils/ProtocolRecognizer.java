package controlm.qrcodegenerator.utils;

import controlm.qrcodegenerator.model.ProtocolMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ProtocolRecognizer {

    private static final Pattern PROTOCOL_PATTERN = Pattern.compile(
            "Протокол\\s*№\\s*" +
                    "([А-ЯA-Z]{1,4}\\s*[-—]\\s*\\d+[а-яa-z]?\\s*[-—]\\s*\\d+\\s*[а-нп-яa-z]?)" +
                    "(?:\\s*(?:от|oт|0т|о\\s*т)\\s*[:.]?\\s*" +
                    "(\\d{2}\\s*\\.\\s*\\d{2}\\s*\\.\\s*(?:\\d{4}|\\d{2})))?",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern FALLBACK_PATTERN = Pattern.compile(
            "([А-ЯA-Z]{1,4}\\s*[-—]\\s*\\d+[а-яa-z]?\\s*[-—]\\s*\\d+\\s*[а-нп-яa-z]?)" +
                    "(?:\\s*(?:от|oт|0т|о\\s*т)\\s*[:.]?\\s*" +
                    "(\\d{2}\\s*\\.\\s*\\d{2}\\s*\\.\\s*(?:\\d{4}|\\d{2})))?",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern NO_DASH_PATTERN = Pattern.compile(
            "([А-ЯA-Z]{1,4})\\s+(\\d+[а-яa-z]?)\\s+(\\d+\\s*[а-нп-яa-z]?)" +
                    "(?:\\s*(?:от|0т|oт|@т)?\\s*(\\d{2}\\.\\d{2}\\.(?:\\d{4}|\\d{2})))?",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern NO_SPACE_PATTERN = Pattern.compile(
                    "([А-ЯA-Z]{1,4}\\s*(?:[-—]?\\s*\\d+[а-яa-z]?){2})" +
                    "(?:\\s*(?:от|oт|0т|о\\s*т)\\s*[:.]?\\s*" +
                    "(\\d{2}\\s*\\.\\s*\\d{2}\\s*\\.\\s*(?:\\d{4}|\\d{2})))?",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    public ProtocolMetadata extract(String text) {
        Matcher main = PROTOCOL_PATTERN.matcher(text);
        if (main.find()) {
            log.info("main {} {}", main.group(1), main.group(2));

            return new ProtocolMetadata(
                    normalizeNumber(main.group(1)),
                    main.group(2)
            );
        }

        Matcher fallBack = FALLBACK_PATTERN.matcher(text);
        while (fallBack.find()) {
            if (looksLikeProtocol(fallBack.group(1))) {
                log.info("fallback {} {}", fallBack.group(1), fallBack.group(2));

                return new ProtocolMetadata(
                        normalizeNumber(fallBack.group(1)),
                        fallBack.group(2));
            }
        }

        Matcher noDash = NO_DASH_PATTERN.matcher(text);
        while (noDash.find()) {
            if (noDash.group(1).matches("[А-ЯA-Z]+")) {
                log.info("noDash {} {}", noDash.group(1), noDash.group(4));

                return new ProtocolMetadata(
                        normalizeNumber(noDash.group(2)),
                        noDash.group(4));
            }
        }

        Matcher noSpace = NO_SPACE_PATTERN.matcher(text);
        while (noSpace.find()) {
            if (noSpace.group(1).matches("[А-ЯA-Z]+")) {
                log.info("noSpace {}, {}, {}, {}", noSpace.group(1), noSpace.group(2), noSpace.group(3), noSpace.group(4));

                return new ProtocolMetadata(
                        normalizeNumber(noSpace.group(2)),
                        noSpace.group(4));
            }
        }

        return null;
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
}
