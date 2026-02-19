package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.Protocol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    Optional<Protocol> findByCipherAndUniqueNumberAndSequentialNumberAndClientId( String cipher,
                                                                         String uniqueNumber,
                                                                         String sequentialNumber,
                                                                         Long clientId);

    @Query("SELECT DISTINCT p.cipher FROM Protocol p WHERE p.client.id = :clientId")
    List<String> findDistinctCiphersByClientId(@Param("clientId") Long clientId);

    @Query("SELECT DISTINCT p.uniqueNumber FROM Protocol p WHERE p.client.id = :clientId")
    List<String> findDistinctUniqueNumberByClientId(@Param("clientId") Long clientId);

    Long countByCipherAndClientId(String cipher, Long clientId);

    Long countByCipherNotInAndClientId(List<String> excludedCiphers, Long clientId);

    Long countByClientId(Long clientId);

    List<Protocol> findByClientId(Long clientId, PageRequest pageRequest);

    @Query(value = """
    SELECT * FROM protocols p
    WHERE p.client_id = :clientId
          AND p.cipher = :cipher
    AND (
        :search IS NULL
        OR :search = ''
        OR LOWER(p.cipher) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.unique_number) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.sequential_number) LIKE LOWER(CONCAT('%', :search, '%'))
        OR TO_CHAR(p.issue_date, 'DD.MM.YYYY')
            LIKE CONCAT('%', :search, '%')
    )
    ORDER BY p.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM protocols p
    WHERE p.client_id = :clientId
          AND p.cipher = :cipher
    AND (
        :search IS NULL
        OR :search = ''
        OR LOWER(p.cipher) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.unique_number) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.sequential_number) LIKE LOWER(CONCAT('%', :search, '%'))
        OR TO_CHAR(p.issue_date, 'DD.MM.YYYY')
            LIKE CONCAT('%', :search, '%')
    )
    """,
            nativeQuery = true)
    Page<Protocol> findProtocolsByClientIdWithSearchAndCipher(
            @Param("clientId") Long clientId,
            @Param("search") String search,
            @Param("cipher") String cipher,
            Pageable pageable);


}
