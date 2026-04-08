package projekti.tyo.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByPublicVisibleTrueOrderByNameAsc();
    List<Recipe> findByOwnerUsernameOrderByNameAsc(String username);
}
