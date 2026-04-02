package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findByNameIsContainingIgnoreCase(String name, Pageable pageable);

    @Query("select c.name from Client c where c.id = :id")
    String findNameById(@Param("id") Long id);

    @Query("select max(u.number) from Client c " +
            "join UniqueNumber u on u.client.id = c.id")
    Long getMaxUniqueNumber();

    @Query("select f.filePath from Client c " +
            "join ClientFile f on f.client.id = c.id " +
            "where c.id = :clientId and f.id = :clientFileId")
    String getFilePathByClientFileId(@Param("clientId") Long clientId, @Param("clientFileId") Long clientFileId);

    @Query("select f.contentType from Client c " +
            "join ClientFile f on f.client.id = c.id " +
            "where c.id = :clientId and f.id = :clientFileId")
    String getContentTypeByFileId(@Param("clientId") Long clientId, @Param("clientFileId") Long clientFileId);
}
