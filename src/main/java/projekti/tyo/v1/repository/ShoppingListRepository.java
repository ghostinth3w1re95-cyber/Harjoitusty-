package projekti.tyo.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.ShoppingList;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findByOwnerUsernameOrderByCreatedDateDesc(String username);
    java.util.Optional<ShoppingList> findByIdAndOwnerUsername(Long id, String username);
}
