package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.OcrJobResponseDto;
import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.enums.OcrJobStatus;
import controlm.qrcodegenerator.model.OcrJob;
import controlm.qrcodegenerator.model.OcrProtocolPreview;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.service.FileStorageService;
import controlm.qrcodegenerator.service.OcrJobService;
import controlm.qrcodegenerator.service.OcrProtocolPreviewService;
import controlm.qrcodegenerator.service.PdfProcessingService;
import controlm.qrcodegenerator.service.TempFileStorageService;
import controlm.qrcodegenerator.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/ocr")
@Controller
@RequiredArgsConstructor
public class OcrController {

    private final UserService userService;
    private final OcrJobService ocrJobService;
    private final OcrProtocolPreviewService ocrProtocolPreviewService;
    private final PdfProcessingService pdfProcessingService;
    private final FileStorageService fileStorageService;
    private final TempFileStorageService  tempFileStorageService;


    @GetMapping("/jobs")
    public String getJobMonitoringPage(Principal principal,
                                       @RequestParam(required = false, defaultValue = "0") int page,
                                       @RequestParam(required = false, defaultValue = "10") int size,
                                       Model model) {
        User user = userService.findByUsername(principal.getName());

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<OcrJobResponseDto> jobsPage = ocrJobService.getPaginatedDtoByUserIdAndStatusNotSaved(user.getId(), pageable);
        Map<String, Long> statusCounts = ocrJobService.getStatusCountsByUserId(user.getId());

        model.addAttribute("userId", user.getId());
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("jobsPage", jobsPage);
        model.addAttribute("ocrJobs", jobsPage.getContent());
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "ocr/job-monitor";
    }

    @GetMapping("/report/{jobId}")
    public String getOcrJobReport(@PathVariable Long jobId, Model model, HttpServletResponse response) {
        List<OcrProtocolPreview> protocols = ocrProtocolPreviewService.findAllByOcrJobId(jobId);
        OcrJobResponseDto byId = ocrJobService.getDtoById(jobId);

        model.addAttribute("protocols", protocols);
        model.addAttribute("ocrJob", byId);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return "ocr/confirm-pdf-form";
    }

    @PostMapping("/confirm-pdf/{jobId}")
    public String confirmPdf(@PathVariable Long jobId,
                             @RequestParam("protocolNumbers") String[] numbers,
                             @RequestParam("protocolDates") String[] dates,
                             @RequestParam(value = "fileName", required = false) String[] fileName) {
        OcrJob ocrJob = ocrJobService.findById(jobId);
        List<ProtocolPreviewDto> protocols = new ArrayList<>();

        for (int i = 0; i < numbers.length; i++) {
            ProtocolPreviewDto dto = new ProtocolPreviewDto();
            dto.setNumber(numbers[i]);
            dto.setIssueDate(dates[i]);
            if (fileName != null && i < fileName.length) {
                dto.setFileName(fileName[i]);
            }
            protocols.add(dto);
        }

        try {
            pdfProcessingService.confirm(protocols, ocrJob.getClientId());

            ocrJob.setStatus(OcrJobStatus.SAVED);
            ocrJobService.save(ocrJob);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return "redirect:/clients/" + ocrJob.getClientId();
        }
        return "redirect:/ocr/jobs";
    }

    @PostMapping("/cancel-pdf/{jobId}")
    public String cancelPdf(@PathVariable Long jobId) throws IOException {
        OcrJob ocrJob = ocrJobService.findById(jobId);

        fileStorageService.deleteFile(ocrJob.getOriginalFilePath());
        ocrProtocolPreviewService.deleteAllByOcrJobId(jobId);
        ocrJobService.deleteById(jobId);

        return "redirect:/ocr/jobs";
    }
}
