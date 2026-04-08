package projekti.tyo.v1.controller.api;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projekti.tyo.v1.model.Expense;
import projekti.tyo.v1.repository.ExpenseRepository;
import projekti.tyo.v1.service.AppUserService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {

    private final ExpenseRepository expenseRepository;
    private final AppUserService appUserService;

    public ExpenseRestController(ExpenseRepository expenseRepository, AppUserService appUserService) {
        this.expenseRepository = expenseRepository;
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<Expense> getExpenses(Principal principal) {
        return expenseRepository.findByOwnerUsernameOrderByExpenseDateDesc(principal.getName());
    }

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense, Principal principal) {
        expense.setOwner(appUserService.getRequiredUser(principal.getName()));
        return expenseRepository.save(expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
    }
}
