package projekti.tyo.v1.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import projekti.tyo.v1.repository.AppUserRepository;
import projekti.tyo.v1.repository.BudgetRepository;
import projekti.tyo.v1.repository.ExpenseRepository;
import projekti.tyo.v1.repository.RecipeRepository;
import projekti.tyo.v1.repository.ShoppingListRepository;
import projekti.tyo.v1.repository.WishItemRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AppUserRepository appUserRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final WishItemRepository wishItemRepository;

    public AdminController(AppUserRepository appUserRepository, ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository, RecipeRepository recipeRepository,
            ShoppingListRepository shoppingListRepository, WishItemRepository wishItemRepository) {
        this.appUserRepository = appUserRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.recipeRepository = recipeRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.wishItemRepository = wishItemRepository;
    }

    @GetMapping("/admin")
    public String adminPanel(@RequestParam(name = "username", required = false) String username, Model model) {
        model.addAttribute("users", appUserRepository.findAllByOrderByUsernameAsc());
        if (username != null && !username.isBlank()) {
            model.addAttribute("selectedUser", appUserRepository.findByUsername(username).orElseThrow());
            model.addAttribute("expenses", expenseRepository.findByOwnerUsernameOrderByExpenseDateDesc(username));
            model.addAttribute("budgets", budgetRepository.findByOwnerUsernameOrderByBudgetMonthDesc(username));
            model.addAttribute("recipes", recipeRepository.findByOwnerUsernameOrderByNameAsc(username));
            model.addAttribute("shoppingLists", shoppingListRepository.findByOwnerUsernameOrderByCreatedDateDesc(username));
            model.addAttribute("wishItems", wishItemRepository.findByOwnerUsernameOrderByStatusAscItemNameAsc(username));
        }
        return "admin";
    }

    @PostMapping("/admin/expenses/{id}/delete")
    public String deleteExpense(@PathVariable Long id, @RequestParam String username, RedirectAttributes redirectAttributes) {
        expenseRepository.findById(id).ifPresent(expenseRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Kulu poistettiin.");
        return "redirect:/admin?username=" + username;
    }

    @PostMapping("/admin/budgets/{id}/delete")
    public String deleteBudget(@PathVariable Long id, @RequestParam String username, RedirectAttributes redirectAttributes) {
        budgetRepository.findById(id).ifPresent(budgetRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Budjetti poistettiin.");
        return "redirect:/admin?username=" + username;
    }

    @PostMapping("/admin/recipes/{id}/delete")
    public String deleteRecipe(@PathVariable Long id, @RequestParam String username, RedirectAttributes redirectAttributes) {
        recipeRepository.findById(id).ifPresent(recipeRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Resepti poistettiin.");
        return "redirect:/admin?username=" + username;
    }

    @PostMapping("/admin/shopping-lists/{id}/delete")
    public String deleteShoppingList(@PathVariable Long id, @RequestParam String username,
            RedirectAttributes redirectAttributes) {
        shoppingListRepository.findById(id).ifPresent(shoppingListRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Ostoslista poistettiin.");
        return "redirect:/admin?username=" + username;
    }

    @PostMapping("/admin/wish-items/{id}/delete")
    public String deleteWishItem(@PathVariable Long id, @RequestParam String username, RedirectAttributes redirectAttributes) {
        wishItemRepository.findById(id).ifPresent(wishItemRepository::delete);
        redirectAttributes.addFlashAttribute("successMessage", "Toivelistan tuote poistettiin.");
        return "redirect:/admin?username=" + username;
    }
}
