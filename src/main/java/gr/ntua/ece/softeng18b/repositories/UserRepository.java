package gr.ntua.ece.softeng18b.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.ntua.ece.softeng18b.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer>
{
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT COUNT(*) FROM user", nativeQuery = true)
    int howMany();

    Optional<User> findByEmail(String email);
}