package projekti.tyo.v1.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import projekti.tyo.v1.controller.dto.RegistrationForm;
import projekti.tyo.v1.repository.AppUserRepository;
import projekti.tyo.v1.repository.ExpenseRepository;
import projekti.tyo.v1.repository.RecipeRepository;
import projekti.tyo.v1.service.AppUserService;

@Controller
public class HomeController {

    private final RecipeRepository recipeRepository;
    private final ExpenseRepository expenseRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;

    public HomeController(RecipeRepository recipeRepository, ExpenseRepository expenseRepository,
            AppUserRepository appUserRepository, AppUserService appUserService) {
        this.recipeRepository = recipeRepository;
        this.expenseRepository = expenseRepository;
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recipes", recipeRepository.findByPublicVisibleTrueOrderByNameAsc());
        model.addAttribute("latestExpenses", expenseRepository.findTop5ByOrderByExpenseDateDescIdDesc());
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (registrationForm.getUsername() != null) {
            registrationForm.setUsername(registrationForm.getUsername().trim());
        }
        if (registrationForm.getFullName() != null) {
            registrationForm.setFullName(registrationForm.getFullName().trim());
        }
        if (registrationForm.getEmail() != null) {
            registrationForm.setEmail(registrationForm.getEmail().trim());
        }

        if (registrationForm.getUsername() != null
                && appUserRepository.existsByUsernameIgnoreCase(registrationForm.getUsername())) {
            bindingResult.rejectValue("username", "user.username.exists", "Käyttäjätunnus on jo käytössä.");
        }
        if (registrationForm.getEmail() != null
                && appUserRepository.existsByEmailIgnoreCase(registrationForm.getEmail())) {
            bindingResult.rejectValue("email", "user.email.exists", "Sähköpostiosoite on jo käytössä.");
        }
        if (registrationForm.getPassword() != null
                && !registrationForm.getPassword().equals(registrationForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "user.password.mismatch", "Salasanat eivät täsmää.");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        appUserService.registerUser(
            registrationForm.getUsername(),
            registrationForm.getFullName(),
            registrationForm.getEmail(),
            registrationForm.getPassword()
        );
        redirectAttributes.addFlashAttribute("successMessage",
            "Käyttäjä luotiin onnistuneesti. Voit nyt kirjautua sisään.");
        return "redirect:/login";
    }
}
