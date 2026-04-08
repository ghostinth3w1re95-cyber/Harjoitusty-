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

import projekti.tyo.v1.model.Budget;
import projekti.tyo.v1.repository.BudgetRepository;
import projekti.tyo.v1.repository.CategoryRepository;
import projekti.tyo.v1.service.AppUserService;

@Controller
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final AppUserService appUserService;

    public BudgetController(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
            AppUserService appUserService) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.appUserService = appUserService;
    }

    @GetMapping("/budgets")
    public String list(Model model, Principal principal) {
        model.addAttribute("budgets", budgetRepository.findByOwnerUsernameOrderByBudgetMonthDesc(principal.getName()));
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("editing", false);
        return "budgets";
    }

    @GetMapping("/budgets/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("budgets", budgetRepository.findByOwnerUsernameOrderByBudgetMonthDesc(principal.getName()));
        model.addAttribute("budget", budgetRepository.findByIdAndOwnerUsername(id, principal.getName()).orElseThrow());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("editing", true);
        return "budgets";
    }

    @PostMapping("/budgets")
    public String save(@Valid @ModelAttribute("budget") Budget budget, BindingResult bindingResult, Model model,
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
            budget.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("budgets", budgetRepository.findByOwnerUsernameOrderByBudgetMonthDesc(principal.getName()));
            model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
            model.addAttribute("editing", budget.getId() != null);
            return "budgets";
        }
        budget.setOwner(appUserService.getRequiredUser(principal.getName()));
        budgetRepository.save(budget);
        return "redirect:/budgets";
    }

    @PostMapping("/budgets/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        budgetRepository.findByIdAndOwnerUsername(id, principal.getName()).ifPresent(budgetRepository::delete);
        return "redirect:/budgets";
    }
}
