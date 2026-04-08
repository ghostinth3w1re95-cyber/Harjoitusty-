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

import projekti.tyo.v1.model.ShoppingList;
import projekti.tyo.v1.model.ShoppingListItem;
import projekti.tyo.v1.repository.ShoppingListItemRepository;
import projekti.tyo.v1.repository.ShoppingListRepository;
import projekti.tyo.v1.service.AppUserService;

@Controller
public class ShoppingListController {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository itemRepository;
    private final AppUserService appUserService;

    public ShoppingListController(ShoppingListRepository shoppingListRepository,
            ShoppingListItemRepository itemRepository, AppUserService appUserService) {
        this.shoppingListRepository = shoppingListRepository;
        this.itemRepository = itemRepository;
        this.appUserService = appUserService;
    }

    @GetMapping("/shopping-lists")
    public String list(Model model, Principal principal) {
        model.addAttribute("shoppingLists", shoppingListRepository.findByOwnerUsernameOrderByCreatedDateDesc(principal.getName()));
        model.addAttribute("shoppingList", new ShoppingList());
        model.addAttribute("shoppingItem", new ShoppingListItem());
        return "shopping-lists";
    }

    @PostMapping("/shopping-lists")
    public String save(@Valid @ModelAttribute("shoppingList") ShoppingList shoppingList,
            BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shoppingLists", shoppingListRepository.findByOwnerUsernameOrderByCreatedDateDesc(principal.getName()));
            model.addAttribute("shoppingItem", new ShoppingListItem());
            return "shopping-lists";
        }
        shoppingList.setOwner(appUserService.getRequiredUser(principal.getName()));
        shoppingListRepository.save(shoppingList);
        return "redirect:/shopping-lists";
    }

    @PostMapping("/shopping-lists/{id}/items")
    public String addItem(@PathVariable Long id, @RequestParam("itemName") String itemName,
            @RequestParam(name = "quantity", required = false) String quantity, Model model, Principal principal) {
        if (itemName == null || itemName.isBlank()) {
            model.addAttribute("shoppingLists", shoppingListRepository.findByOwnerUsernameOrderByCreatedDateDesc(principal.getName()));
            model.addAttribute("shoppingList", new ShoppingList());
            model.addAttribute("shoppingItem", new ShoppingListItem());
            return "shopping-lists";
        }
        ShoppingList list = shoppingListRepository.findByIdAndOwnerUsername(id, principal.getName()).orElseThrow();
        ShoppingListItem item = new ShoppingListItem();
        item.setItemName(itemName.trim());
        item.setQuantity(quantity == null ? "" : quantity.trim());
        item.setShoppingList(list);
        list.getItems().add(item);
        shoppingListRepository.save(list);
        return "redirect:/shopping-lists";
    }

    @PostMapping("/shopping-lists/items/{id}/toggle")
    public String toggle(@PathVariable Long id, Principal principal) {
        ShoppingListItem item = itemRepository.findByIdAndShoppingListOwnerUsername(id, principal.getName()).orElseThrow();
        item.setPurchased(!item.isPurchased());
        itemRepository.save(item);
        return "redirect:/shopping-lists";
    }

    @PostMapping("/shopping-lists/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        shoppingListRepository.findByIdAndOwnerUsername(id, principal.getName()).ifPresent(shoppingListRepository::delete);
        return "redirect:/shopping-lists";
    }
}
