package controlm.qrcodegenerator.testPdf;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final PdfBatchTestRunner testRunner;

//    @GetMapping("/test")
//    public String test() {
//        testRunner.runBatch();
//
//        return "";
//    }
}
