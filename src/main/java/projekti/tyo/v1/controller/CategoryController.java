package projekti.tyo.v1.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import projekti.tyo.v1.model.Category;
import projekti.tyo.v1.repository.BudgetRepository;
import projekti.tyo.v1.repository.CategoryRepository;
import projekti.tyo.v1.repository.ExpenseRepository;

@Controller
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public CategoryController(CategoryRepository categoryRepository, ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository) {
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/categories")
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        model.addAttribute("editing", false);
        return "categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow());
        model.addAttribute("editing", true);
        return "categories";
    }

    @PostMapping("/categories")
    public String save(@Valid @ModelAttribute("category") Category category, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        if (category.getName() != null) {
            category.setName(category.getName().trim());
        }
        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }
        boolean creatingNew = category.getId() == null;
        if (creatingNew && category.getName() != null && !category.getName().isBlank()
                && categoryRepository.existsByNameIgnoreCase(category.getName())) {
            bindingResult.rejectValue("name", "category.duplicate", "Samanniminen kategoria on jo olemassa.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
            model.addAttribute("editing", category.getId() != null);
            return "categories";
        }
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategoria tallennettiin.");
        return "redirect:/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (expenseRepository.countByCategoryId(id) > 0 || budgetRepository.countByCategoryId(id) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Kategoriaa ei voi poistaa, koska siihen liittyy kuluja tai budjetteja.");
            return "redirect:/categories";
        }
        categoryRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Kategoria poistettiin.");
        return "redirect:/categories";
    }
}
