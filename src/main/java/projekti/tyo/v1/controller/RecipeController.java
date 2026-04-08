package projekti.tyo.v1.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import projekti.tyo.v1.model.Recipe;
import projekti.tyo.v1.repository.IngredientRepository;
import projekti.tyo.v1.repository.RecipeRepository;
import projekti.tyo.v1.service.AppUserService;

@Controller
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final AppUserService appUserService;

    public RecipeController(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
            AppUserService appUserService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.appUserService = appUserService;
    }

    @GetMapping("/recipes")
    public String publicRecipes(Model model) {
        model.addAttribute("recipes", recipeRepository.findByPublicVisibleTrueOrderByNameAsc());
        return "recipes-public";
    }

    @GetMapping("/manage-recipes")
    public String list(Model model, Principal principal) {
        model.addAttribute("recipes", recipeRepository.findByOwnerUsernameOrderByNameAsc(principal.getName()));
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("ingredients", ingredientRepository.findAllByOrderByNameAsc());
        model.addAttribute("selectedIngredientIds", List.of());
        model.addAttribute("editing", false);
        return "recipes-manage";
    }

    @GetMapping("/manage-recipes/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow();
        model.addAttribute("recipes", recipeRepository.findByOwnerUsernameOrderByNameAsc(principal.getName()));
        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredients", ingredientRepository.findAllByOrderByNameAsc());
        model.addAttribute("selectedIngredientIds", recipe.getIngredients().stream().map(i -> i.getId()).toList());
        model.addAttribute("editing", true);
        return "recipes-manage";
    }

    @PostMapping("/manage-recipes")
    public String save(@Valid @ModelAttribute("recipe") Recipe recipe, BindingResult bindingResult, Model model,
            Principal principal, @RequestParam(name = "ingredientIds", required = false) List<Long> ingredientIds) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipes", recipeRepository.findByOwnerUsernameOrderByNameAsc(principal.getName()));
            model.addAttribute("ingredients", ingredientRepository.findAllByOrderByNameAsc());
            model.addAttribute("selectedIngredientIds", ingredientIds == null ? List.of() : ingredientIds);
            model.addAttribute("editing", recipe.getId() != null);
            return "recipes-manage";
        }
        recipe.setOwner(appUserService.getRequiredUser(principal.getName()));
        recipe.setIngredients(new HashSet<>(ingredientIds == null ? List.of() : ingredientRepository.findAllById(ingredientIds)));
        recipeRepository.save(recipe);
        return "redirect:/manage-recipes";
    }

    @PostMapping("/manage-recipes/{id}/delete")
    public String delete(@PathVariable Long id) {
        recipeRepository.deleteById(id);
        return "redirect:/manage-recipes";
    }
}
