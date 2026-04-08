package projekti.tyo.v1.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projekti.tyo.v1.model.Recipe;
import projekti.tyo.v1.repository.RecipeRepository;

@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

    private final RecipeRepository recipeRepository;

    public RecipeRestController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping
    public List<Recipe> getPublicRecipes() {
        return recipeRepository.findByPublicVisibleTrueOrderByNameAsc();
    }
}
