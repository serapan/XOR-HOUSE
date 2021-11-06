package gr.ntua.ece.softeng18b.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.ntua.ece.softeng18b.model.Role;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Integer>
{
    @Query(value = "select * from role where role = ?1", nativeQuery = true)
    Optional<Role> getRole(String role);
}