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

    boolean existsByProtocolNumberAndClientId(String protocolNumber, Long clientId);

    Optional<Protocol> findByProtocolNumberAndClientId(String protocolNumber, Long clientId);

    Long countByProtocolNumberContainingIgnoreCaseAndClientId(String protocolNumber, Long clientId);

    @Query("SELECT COUNT(e) FROM Protocol e WHERE e.client.id = :clientId AND " +
            "NOT EXISTS (SELECT 1 FROM Protocol e2 WHERE e2.id = e.id AND " +
            "(" +
            "    e2.protocolNumber LIKE CONCAT('%', :excluded1, '%') OR " +
            "    e2.protocolNumber LIKE CONCAT('%', :excluded2, '%')" +
            "))")
    Long countByProtocolNumberNotLikeAndClientId(@Param("clientId") Long clientId,
                                         @Param("excluded1") String excluded1,
                                         @Param("excluded2") String excluded2);

    Long countByClientId(Long clientId);

    List<Protocol> findByClientId(Long clientId, PageRequest pageRequest);

//    @Query(value = """
//    SELECT * FROM protocols p
//    WHERE p.client_id = :clientId
//    AND (
//        :search IS NULL
//        OR :search = ''
//        OR LOWER(p.protocol_number) LIKE LOWER(CONCAT('%', :search, '%'))
//        OR TO_CHAR(p.issue_date, 'DD.MM.YYYY') LIKE CONCAT('%', :search, '%')
//    )
//    ORDER BY p.created_at DESC
//    """,
//            countQuery = """
//    SELECT COUNT(*) FROM protocols p
//    WHERE p.client_id = :clientId
//    AND (
//        :search IS NULL
//        OR :search = ''
//        OR LOWER(p.protocol_number) LIKE LOWER(CONCAT('%', :search, '%'))
//        OR TO_CHAR(p.issue_date, 'DD.MM.YYYY')
//            LIKE CONCAT('%', :search, '%')
//    )
//    """,
//            nativeQuery = true)
//    Page<Protocol> findProtocolsByClientIdWithSearchAndCipher(
//            @Param("clientId") Long clientId,
//            @Param("search") String search,
//            Pageable pageable);

    @Query(value = """
    SELECT * FROM protocols p
    WHERE p.client_id = :clientId
    AND (
        :search IS NULL
        OR :search = ''
        OR LOWER(p.protocol_number) LIKE LOWER(CONCAT('%', :search, '%'))
        OR TO_CHAR(p.issue_date, 'DD.MM.YYYY') LIKE CONCAT('%', :search, '%')
    )
    AND p.protocol_number LIKE CONCAT(:cipher, '-%')
    ORDER BY p.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM protocols p
    WHERE p.client_id = :clientId
    AND (
        :search IS NULL
        OR :search = ''
        OR LOWER(p.protocol_number) LIKE LOWER(CONCAT('%', :search, '%'))
        OR TO_CHAR(p.issue_date, 'DD.MM.YYYY') LIKE CONCAT('%', :search, '%')
    )
    AND p.protocol_number LIKE CONCAT(:cipher, '-%')
    """,
            nativeQuery = true)
    Page<Protocol> findProtocolsByClientIdWithSearchAndCipher(
            @Param("clientId") Long clientId,
            @Param("search") String search,
            @Param("cipher") String cipher,
            Pageable pageable);

    @Query(value = """
    SELECT DISTINCT 
        CASE 
            WHEN POSITION('-' IN p.protocol_number) > 0 
            THEN SUBSTRING(p.protocol_number, 1, POSITION('-' IN p.protocol_number) - 1)
            ELSE p.protocol_number
        END as cipher
    FROM protocols p
    WHERE p.client_id = :clientId
    ORDER BY cipher
    """, nativeQuery = true)
    List<String> findAllCiphersByClientId(@Param("clientId") Long clientId);

}
