package projekti.tyo.v1.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import projekti.tyo.v1.model.Expense;
import projekti.tyo.v1.repository.BudgetRepository;
import projekti.tyo.v1.repository.ExpenseRepository;
import projekti.tyo.v1.repository.RecipeRepository;
import projekti.tyo.v1.repository.ShoppingListRepository;
import projekti.tyo.v1.repository.WishItemRepository;

@Service
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final WishItemRepository wishItemRepository;

    public DashboardService(ExpenseRepository expenseRepository, BudgetRepository budgetRepository,
            RecipeRepository recipeRepository, ShoppingListRepository shoppingListRepository,
            WishItemRepository wishItemRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.recipeRepository = recipeRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.wishItemRepository = wishItemRepository;
    }

    public DashboardStats getStats(String username) {
        List<Expense> expenses = expenseRepository.findByOwnerUsernameOrderByExpenseDateDesc(username);
        BigDecimal totalExpenses = expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardStats(
            expenses.size(),
            totalExpenses,
            budgetRepository.findByOwnerUsernameOrderByBudgetMonthDesc(username).size(),
            recipeRepository.findByOwnerUsernameOrderByNameAsc(username).size(),
            shoppingListRepository.findByOwnerUsernameOrderByCreatedDateDesc(username).size(),
            wishItemRepository.findByOwnerUsernameOrderByStatusAscItemNameAsc(username).size()
        );
    }

    public record DashboardStats(
        int expenseCount,
        BigDecimal totalExpenses,
        int budgetCount,
        int recipeCount,
        int shoppingListCount,
        int wishItemCount
    ) {
    }
}
