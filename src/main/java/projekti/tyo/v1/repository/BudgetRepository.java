package projekti.tyo.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByOwnerUsernameOrderByBudgetMonthDesc(String username);
    java.util.Optional<Budget> findByIdAndOwnerUsername(Long id, String username);
    long countByCategoryId(Long categoryId);
}
