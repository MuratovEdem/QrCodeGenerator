package controlm.qrcodegenerator.testPdf;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.service.PdfProcessingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PdfBatchTestRunner {

    private static final Logger log =
            LoggerFactory.getLogger(PdfBatchTestRunner.class);

    private final PdfProcessingService pdfProcessingService;
    private final BatchSecurityConfig security;

    @Value("${test.input.dir}")
    private String inputDir;

    @Value("${test.client-id}")
    private Long clientId;


//    public void runBatch() {
//
//        File dir = new File(inputDir);
//        File[] files = dir.listFiles(f ->
//                f.isFile() && f.getName().toLowerCase().endsWith(".pdf"));
//
//        if (files == null || files.length == 0) {
//            log.warn("⚠️ No PDF files found in {}", inputDir);
//            return;
//        }
//
//        log.info("📂 Found {} PDF files", files.length);
//
//        List<TestResult> results = new ArrayList<>();
//
//        for (File pdf : files) {
//            results.add(processOne(pdf));
//        }
//
//        printSummary(results);
//    }

//    private TestResult processOne(File pdf) {
//        long start = System.currentTimeMillis();
//
//        log.info("📄 Processing: {}", pdf.getName());
//
//        try {
//            List<ProtocolPreviewDto> previews =
//                    pdfProcessingService.analyze(pdf, 0);
//
//            previews.forEach(p ->
//                    log.info("   ↳ {} | {}", p.getNumber(), p.getIssueDate())
//            );
//
//            // Эмуляция подтверждения пользователем
//            pdfProcessingService.confirm(previews, clientId);
//
//            long time = System.currentTimeMillis() - start;
//
//            log.info("✅ Done in {} ms", time);
//
//            return new TestResult(
//                    pdf.getName(),
//                    true,
//                    previews.size(),
//                    time,
//                    null
//            );
//
//        } catch (Exception e) {
//            long time = System.currentTimeMillis() - start;
//
//            log.error("❌ Error processing {}", pdf.getName(), e);
//
//            return new TestResult(
//                    pdf.getName(),
//                    false,
//                    0,
//                    time,
//                    e.getMessage()
//            );
//        }
//    }

    private void printSummary(List<TestResult> results) {

        long ok = results.stream().filter(TestResult::getSuccess).count();
        long fail = results.size() - ok;

        log.info("=================================");
        log.info("📊 BATCH TEST SUMMARY");
        log.info("Total  : {}", results.size());
        log.info("Success: {}", ok);
        log.info("Failed : {}", fail);

        results.stream()
                .filter(r -> !r.success)
                .forEach(r ->
                        log.warn("❗ {} → {}", r.fileName, r.error)
                );

        log.info("=================================");
    }
}