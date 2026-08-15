package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.AuditLogDto;
import controlm.qrcodegenerator.dto.response.CipherDto;
import controlm.qrcodegenerator.dto.response.FailedFileAdminDto;
import controlm.qrcodegenerator.dto.response.RegistrationUserResponseDto;
import controlm.qrcodegenerator.dto.response.UserDashboardResponseDto;
import controlm.qrcodegenerator.dto.response.UserStatisticsResponseDto;
import controlm.qrcodegenerator.service.AuditLogService;
import controlm.qrcodegenerator.service.CipherService;
import controlm.qrcodegenerator.service.FailedFileService;
import controlm.qrcodegenerator.service.RoleService;
import controlm.qrcodegenerator.service.StorageArchiveService;
import controlm.qrcodegenerator.service.UserService;
import controlm.qrcodegenerator.service.UserStatisticsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admins")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserStatisticsService userStatisticsService;
    private final AuditLogService auditLogService;
    private final StorageArchiveService storageArchiveService;
    private final CipherService cipherService;
    private final FailedFileService failedFileService;

    @GetMapping
    public String getDashboard(Model model) {
        List<UserDashboardResponseDto> users = userService.getAllUsers();
        List<String> all = roleService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("availableRoles", all);
        return "users/dashboard";
    }

    @GetMapping("/logs")
    @ResponseBody
    public List<AuditLogDto> getLogs() {
        return auditLogService.findAllByOrderByPerformedAtDesc();
    }

    @GetMapping("/ciphers")
    @ResponseBody
    public List<CipherDto> getCiphers() {
        return cipherService.findAll();
    }

    @PostMapping("/ciphers")
    public ResponseEntity<Void> saveCipher(@RequestBody CipherDto cipherDto) {
        cipherService.save(cipherDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ciphers")
    public ResponseEntity<Void> updateCipher(@RequestBody CipherDto cipherDto) {
        cipherService.update(cipherDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ciphers")
    public  ResponseEntity<Void> deleteCipher(@RequestParam("id") Long id) {
        cipherService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    public String getStatisticsPage(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
            Model model) {

        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        return "users/statistic";
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<?> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo) {

        try {
            UserStatisticsResponseDto statistics = userStatisticsService.getStatistics(dateFrom, dateTo);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{userId}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String newRole = payload.get("role");
        userService.changeRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/reset-password")
    @ResponseBody
    public ResponseEntity<RegistrationUserResponseDto> resetPassword(@PathVariable Long userId) {
        try {
            RegistrationUserResponseDto response = userService.resetPassword(userId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Попытка сброса пароля: пользователь id={} не найден", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Ошибка при сбросе пароля для пользователя id={}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/storage/archive")
    public void downloadArchive(HttpServletResponse response) throws IOException {
        // Генерируем имя файла с датой
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        String fileName = "storage_archive_" + timestamp + ".zip";

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");


        storageArchiveService.archiveStorage(response.getOutputStream());
    }

    @GetMapping("/failed-files")
    @ResponseBody
    public List<FailedFileAdminDto> getAllFailedFiles() {
        return failedFileService.getAllFailedFiles();
    }
}
