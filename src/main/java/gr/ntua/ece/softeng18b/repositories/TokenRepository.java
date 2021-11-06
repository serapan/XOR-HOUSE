package gr.ntua.ece.softeng18b.repositories;

import gr.ntua.ece.softeng18b.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Component("tokenrepository")
public interface TokenRepository extends JpaRepository<Token, String> {

    @Modifying
    @Transactional
    @Query(value="DELETE FROM token WHERE token = ?1", nativeQuery = true)
    void deleteToken(String token);

    @Query(value="SELECT * FROM token WHERE token = ?1", nativeQuery = true)
    List<Token> findToken(String token);
}
