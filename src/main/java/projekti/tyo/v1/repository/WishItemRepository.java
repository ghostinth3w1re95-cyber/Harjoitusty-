package projekti.tyo.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.WishItem;

public interface WishItemRepository extends JpaRepository<WishItem, Long> {
    List<WishItem> findByOwnerUsernameOrderByStatusAscItemNameAsc(String username);
}
