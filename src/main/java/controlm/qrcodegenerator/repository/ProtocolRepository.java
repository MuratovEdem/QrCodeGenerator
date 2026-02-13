package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.Protocol;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProtocolRepository extends JpaRepository<Protocol, Long> {
    List<Protocol> findByClientId(Long clientId);

    Optional<Protocol> findFirstByClientIdOrderByCreatedAtDesc(Long clientId);

    @Query("SELECT COUNT(p) > 0 FROM Protocol p WHERE p.uniqueNumber = :uniqueNumber AND p.client.id <> :excludedClientId")
    boolean existsByUniqueNumberAndClientIdNot(@Param("uniqueNumber") String uniqueNumber,
                                               @Param("excludedClientId") Long excludedClientId);

    boolean existsByCipherAndUniqueNumberAndSequentialNumberAndClientId( String cipher,
                                                                         String uniqueNumber,
                                                                         String sequentialNumber,
                                                                         Long clientId);

    @Query("SELECT DISTINCT p.cipher FROM Protocol p WHERE p.client.id = :clientId")
    List<String> findDistinctCiphersByClientId(@Param("clientId") Long clientId);

    @Query("SELECT DISTINCT p.uniqueNumber FROM Protocol p WHERE p.client.id = :clientId")
    List<String> findDistinctUniqueNumberByClientId(@Param("clientId") Long clientId);

    Long countByCipherAndClientId(String cipher, Long clientId);

    Long countByCipherNotInAndClientId(List<String> excludedCiphers, Long clientId);

    @Query("SELECT p FROM Protocol p " +
            "WHERE p.client.id = :clientId " +
            "AND ( " +
            "   LOWER(p.cipher) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "   LOWER(p.uniqueNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "   LOWER(p.sequentialNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "   LOWER(CONCAT(p.cipher, '-', p.uniqueNumber, '-', p.sequentialNumber)) " +
            "       LIKE LOWER(CONCAT('%', :searchText, '%'))" +
            ")")
    List<Protocol> findProtocolsByClientIdAndSearchText(
            @Param("clientId") Long clientId,
            @Param("searchText") String searchText,
            PageRequest pageable);

    List<Protocol> findByClientId(Long clientId, PageRequest pageRequest);
}
