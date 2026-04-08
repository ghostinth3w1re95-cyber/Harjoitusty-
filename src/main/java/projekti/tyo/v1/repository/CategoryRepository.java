package projekti.tyo.v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projekti.tyo.v1.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
