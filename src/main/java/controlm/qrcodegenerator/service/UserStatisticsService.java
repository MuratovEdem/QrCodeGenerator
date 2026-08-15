package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.UserStatisticsDto;
import controlm.qrcodegenerator.dto.response.UserStatisticsResponseDto;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.repository.ProtocolRepository;
import controlm.qrcodegenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatisticsService {
    private final UserRepository userRepository;
    private final ProtocolRepository protocolRepository;

    public UserStatisticsResponseDto getStatistics(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
        }

        LocalDateTime startDateTime = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime endDateTime = dateTo != null ? dateTo.atTime(23, 59, 59) : null;

        List<User> users = userRepository.findAll();

        // ✅ Каждый запрос без null параметров
        Map<String, Long> createdMap = toMap(fetchCreated(startDateTime, endDateTime));
        Map<String, Long> editedMap = toMap(fetchEdited(startDateTime, endDateTime));

        List<UserStatisticsDto> userStats = users.stream().map(user -> {
            long created = createdMap.getOrDefault(user.getUsername(), 0L);
            long edited = editedMap.getOrDefault(user.getUsername(), 0L);

            return UserStatisticsDto.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .createdCount(created)
                    .editedCount(edited)
                    .totalOperations(created + edited)
                    .build();
        }).sorted((a, b) -> Long.compare(b.getTotalOperations(), a.getTotalOperations())).collect(Collectors.toList());

        long totalCreated = userStats.stream().mapToLong(UserStatisticsDto::getCreatedCount).sum();
        long totalEdited = userStats.stream().mapToLong(UserStatisticsDto::getEditedCount).sum();
        long activeUsers = userStats.stream().filter(u -> u.getTotalOperations() > 0).count();

        return UserStatisticsResponseDto.builder()
                .totalUsers(users.size())
                .activeUsers(activeUsers)
                .totalProtocols(totalCreated)
                .totalEdited(totalEdited)
                .users(userStats)
                .build();
    }

    // ✅ Выбираем метод в зависимости от наличия дат — без null в параметрах запроса
    private List<Object[]> fetchCreated(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return protocolRepository.countCreatedGroupedByUser();
        if (start == null) return protocolRepository.countCreatedGroupedByUserBefore(end);
        if (end == null) return protocolRepository.countCreatedGroupedByUserAfter(start);
        return protocolRepository.countCreatedGroupedByUserBetween(start, end);
    }

    private List<Object[]> fetchEdited(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return protocolRepository.countEditedGroupedByUser();
        if (start == null) return protocolRepository.countEditedGroupedByUserBefore(end);
        if (end == null) return protocolRepository.countEditedGroupedByUserAfter(start);
        return protocolRepository.countEditedGroupedByUserBetween(start, end);
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
        ));
    }
}
