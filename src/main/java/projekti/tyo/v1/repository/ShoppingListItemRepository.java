package projekti.tyo.v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.ShoppingListItem;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
    java.util.Optional<ShoppingListItem> findByIdAndShoppingListOwnerUsername(Long id, String username);
}
