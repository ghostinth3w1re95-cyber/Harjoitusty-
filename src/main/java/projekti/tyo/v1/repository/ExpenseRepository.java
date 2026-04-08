package projekti.tyo.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findTop5ByOrderByExpenseDateDescIdDesc();
    List<Expense> findByOwnerUsernameOrderByExpenseDateDesc(String username);
    java.util.Optional<Expense> findByIdAndOwnerUsername(Long id, String username);
    long countByCategoryId(Long categoryId);
}
