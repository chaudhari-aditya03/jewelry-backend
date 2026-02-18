package com.jewelryshop.repository;

import com.jewelryshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByParentIsNull();

    List<Category> findByParentId(Long parentId);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parent IS NULL")
    List<Category> findAllCategoriesWithSubCategories();

    boolean existsByName(String name);
}
