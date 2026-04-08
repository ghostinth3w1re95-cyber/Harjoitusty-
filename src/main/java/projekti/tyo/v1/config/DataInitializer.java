package projekti.tyo.v1.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import projekti.tyo.v1.model.AppUser;
import projekti.tyo.v1.model.Budget;
import projekti.tyo.v1.model.Category;
import projekti.tyo.v1.model.Expense;
import projekti.tyo.v1.model.Ingredient;
import projekti.tyo.v1.model.Recipe;
import projekti.tyo.v1.model.Role;
import projekti.tyo.v1.model.ShoppingList;
import projekti.tyo.v1.model.ShoppingListItem;
import projekti.tyo.v1.model.WishItem;
import projekti.tyo.v1.model.WishStatus;
import projekti.tyo.v1.repository.AppUserRepository;
import projekti.tyo.v1.repository.BudgetRepository;
import projekti.tyo.v1.repository.CategoryRepository;
import projekti.tyo.v1.repository.ExpenseRepository;
import projekti.tyo.v1.repository.IngredientRepository;
import projekti.tyo.v1.repository.RecipeRepository;
import projekti.tyo.v1.repository.ShoppingListRepository;
import projekti.tyo.v1.repository.WishItemRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final WishItemRepository wishItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository userRepository, CategoryRepository categoryRepository,
            ExpenseRepository expenseRepository, BudgetRepository budgetRepository,
            IngredientRepository ingredientRepository, RecipeRepository recipeRepository,
            ShoppingListRepository shoppingListRepository, WishItemRepository wishItemRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.wishItemRepository = wishItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        AppUser admin = userRepository.save(new AppUser(
            "admin", "Admin User", "admin@budgetbite.local", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN));
        AppUser user = userRepository.save(new AppUser(
            "mira", "Mira Mealplanner", "mira@budgetbite.local", passwordEncoder.encode("user123"), Role.ROLE_USER));

        Category food = categoryRepository.save(new Category("Ruoka", "Päivittäiset ruokaostokset ja ravintolat"));
        Category travel = categoryRepository.save(new Category("Liikkuminen", "Bussi, juna ja muut matkat"));
        Category leisure = categoryRepository.save(new Category("Vapaa-aika", "Harrastukset ja viihtyminen"));
        Category housing = categoryRepository.save(new Category("Asuminen", "Vuokra, sähkö ja muut asumiskulut"));

        expenseRepository.save(new Expense("Viikko-ostokset", new BigDecimal("54.20"),
            LocalDate.now().minusDays(2), "Kasvikset ja aamupalat", food, user));
        expenseRepository.save(new Expense("Kuukausikortti", new BigDecimal("62.50"),
            LocalDate.now().minusDays(4), "Joukkoliikenne", travel, user));
        expenseRepository.save(new Expense("Elokuvalippu", new BigDecimal("14.90"),
            LocalDate.now().minusDays(1), "Perjantai-ilta", leisure, admin));
        expenseRepository.save(new Expense("Vuokra", new BigDecimal("720.00"),
            LocalDate.now().minusDays(6), "Kuukauden vuokra", housing, user));

        budgetRepository.save(new Budget("2026-04", new BigDecimal("350.00"), food, user));
        budgetRepository.save(new Budget("2026-04", new BigDecimal("90.00"), travel, user));
        budgetRepository.save(new Budget("2026-04", new BigDecimal("720.00"), housing, user));

        Ingredient oats = ingredientRepository.save(new Ingredient("Kaurahiutaleet", "dl"));
        Ingredient banana = ingredientRepository.save(new Ingredient("Banaani", "kpl"));
        Ingredient yogurt = ingredientRepository.save(new Ingredient("Jogurtti", "dl"));

        Recipe breakfast = new Recipe("Nopea aamiainen", "Kaurakulho banaanilla ja jogurtilla.", true, user);
        breakfast.setIngredients(Set.of(oats, banana, yogurt));
        recipeRepository.save(breakfast);

        Recipe pasta = new Recipe("Arkipasta", "Edullinen ja nopea kasvispasta kiireiseen arkeen.", true, admin);
        pasta.setIngredients(Set.of(banana));
        recipeRepository.save(pasta);

        ShoppingList shoppingList = new ShoppingList("Viikon ruokaostokset", user);
        shoppingList.getItems().add(new ShoppingListItem("Maito", "2 l", false, shoppingList));
        shoppingList.getItems().add(new ShoppingListItem("Tomaatit", "6 kpl", true, shoppingList));
        shoppingListRepository.save(shoppingList);

        ShoppingList adminList = new ShoppingList("Adminin ruokaostokset", admin);
        adminList.getItems().add(new ShoppingListItem("Kahvi", "1 pkt", false, adminList));
        shoppingListRepository.save(adminList);

        wishItemRepository.save(new WishItem("Air fryer", new BigDecimal("129.90"), WishStatus.PLANNED, user));
        wishItemRepository.save(new WishItem("Uudet juoksukengät", new BigDecimal("99.00"), WishStatus.BOUGHT, admin));
    }
}
