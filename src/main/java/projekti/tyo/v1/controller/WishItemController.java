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

import projekti.tyo.v1.model.WishItem;
import projekti.tyo.v1.model.WishStatus;
import projekti.tyo.v1.repository.WishItemRepository;
import projekti.tyo.v1.service.AppUserService;

@Controller
public class WishItemController {

    private final WishItemRepository wishItemRepository;
    private final AppUserService appUserService;

    public WishItemController(WishItemRepository wishItemRepository, AppUserService appUserService) {
        this.wishItemRepository = wishItemRepository;
        this.appUserService = appUserService;
    }

    @GetMapping("/wish-items")
    public String list(Model model, Principal principal) {
        model.addAttribute("wishItems", wishItemRepository.findByOwnerUsernameOrderByStatusAscItemNameAsc(principal.getName()));
        model.addAttribute("wishItem", new WishItem());
        model.addAttribute("statuses", WishStatus.values());
        model.addAttribute("editing", false);
        return "wish-items";
    }

    @GetMapping("/wish-items/{id}/edit")
    public String edit(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("wishItems", wishItemRepository.findByOwnerUsernameOrderByStatusAscItemNameAsc(principal.getName()));
        model.addAttribute("wishItem", wishItemRepository.findById(id).orElseThrow());
        model.addAttribute("statuses", WishStatus.values());
        model.addAttribute("editing", true);
        return "wish-items";
    }

    @PostMapping("/wish-items")
    public String save(@Valid @ModelAttribute("wishItem") WishItem wishItem, BindingResult bindingResult, Model model,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("wishItems", wishItemRepository.findByOwnerUsernameOrderByStatusAscItemNameAsc(principal.getName()));
            model.addAttribute("statuses", WishStatus.values());
            model.addAttribute("editing", wishItem.getId() != null);
            return "wish-items";
        }
        wishItem.setOwner(appUserService.getRequiredUser(principal.getName()));
        wishItemRepository.save(wishItem);
        return "redirect:/wish-items";
    }

    @PostMapping("/wish-items/{id}/delete")
    public String delete(@PathVariable Long id) {
        wishItemRepository.deleteById(id);
        return "redirect:/wish-items";
    }
}
