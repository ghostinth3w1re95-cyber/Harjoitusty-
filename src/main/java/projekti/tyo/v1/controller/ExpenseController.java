package projekti.tyo.v1.controller;

import java.security.Principal;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import projekti.tyo.v1.model.Expense;
import projekti.tyo.v1.repository.CategoryRepository;
import projekti.tyo.v1.repository.ExpenseRepository;
import projekti.tyo.v1.service.AppUserService;

@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final AppUserService appUserService;

    public ExpenseController(ExpenseRepository expenseRepository, CategoryRepository categoryRepository,
            AppUserService appUserService) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.appUserService = appUserService;
    }

    @GetMapping("/expenses")
    public String list(Model model, Principal principal) {
        model.addAttribute("expenses", expenseRepository.findByOwnerUsernameOrderByExpenseDateDesc(principal.getName()));
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("editing", false);
        return "expenses";
    }

    @GetMapping("/expenses/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("expenses", expenseRepository.findByOwnerUsernameOrderByExpenseDateDesc(principal.getName()));
        model.addAttribute("expense", expenseRepository.findByIdAndOwnerUsername(id, principal.getName()).orElseThrow());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("editing", true);
        return "expenses";
    }

    @PostMapping("/expenses")
    public String save(@Valid @ModelAttribute("expense") Expense expense, BindingResult bindingResult, Model model,
            Principal principal, @RequestParam("categoryId") String categoryIdValue) {
        Long categoryId = null;
        try {
            if (categoryIdValue != null && !categoryIdValue.isBlank()) {
                categoryId = Long.valueOf(categoryIdValue);
            }
        } catch (NumberFormatException exception) {
            categoryId = null;
        }

        if (categoryId == null || categoryRepository.findById(categoryId).isEmpty()) {
            bindingResult.rejectValue("category", "category.invalid", "Valitse kategoria.");
        } else {
            expense.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("expenses", expenseRepository.findByOwnerUsernameOrderByExpenseDateDesc(principal.getName()));
            model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
            model.addAttribute("editing", expense.getId() != null);
            return "expenses";
        }
        expense.setOwner(appUserService.getRequiredUser(principal.getName()));
        expenseRepository.save(expense);
        return "redirect:/expenses";
    }

    @PostMapping("/expenses/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        expenseRepository.findByIdAndOwnerUsername(id, principal.getName()).ifPresent(expenseRepository::delete);
        return "redirect:/expenses";
    }
}
