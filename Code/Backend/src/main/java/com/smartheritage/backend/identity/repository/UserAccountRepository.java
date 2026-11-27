package com.smartheritage.backend.identity.repository;

import com.smartheritage.backend.identity.entity.UserAccount;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<UserAccount> findAllByOrderByIdAsc();

    long countByRole_NameIgnoreCaseAndActiveTrue(String roleName);

    @EntityGraph(attributePaths = "role")
    Optional<UserAccount> findWithRoleById(Long id);
}
