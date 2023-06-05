package com.springboot.blog.BlogDemo.repository;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.exception.BlogAPIException;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;


@DataJpaTest
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    public void setup() {
       category = Category.builder()
               .name("name")
               .description("description")
               .build();
    }

    @Test
    public void givenCategoryObject_whenSave_thenReturnSavedCategory() {

        Category savedCategory = categoryRepository.save(category);

        Assertions.assertThat(savedCategory).isNotNull();

        Assertions.assertThat(savedCategory.getId()).isGreaterThan(0);
    }
    @Test
    public void givenCategoryObject_whenFindById_thenReturnCategoryObject() {

        Category savedCategory = categoryRepository.save(category);

        Category returnedCategory=categoryRepository.findById(savedCategory.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",savedCategory.getId()));

        Assertions.assertThat(returnedCategory).isNotNull();

    }
    @Test
    public void givenCategoryList_whenFindAll_thenCategoryList(){

        Category category1  = Category.builder()
                .name("name1")
                .description("description1")
                .build();

        categoryRepository.save(category);

        categoryRepository.save(category1);

        List<Category> categoryList = categoryRepository.findAll();

        Assertions.assertThat(categoryList).isNotNull();
        Assertions.assertThat(categoryList.size()).isEqualTo(2);

    }

    @Test
    public void givenCategoryObject_whenFindByName_thenReturnCategoryObject() {

        categoryRepository.save(category);

        Category categoryWithName=categoryRepository.findByName(category.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",category.getId()));

        Assertions.assertThat(categoryWithName).isNotNull();
        Assertions.assertThat(categoryWithName.getName()).isEqualTo(category.getName());


    }

    @Test
    public void givenCategoryObject_whenUpdateCategory_thenReturnUpdatedCategory(){

        categoryRepository.save(category);

        Category savedCategory=categoryRepository.findById(category.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",category.getId()));

        savedCategory.setName("updateName");

        savedCategory.setDescription("updateDescription");

        Category updatedCategory=categoryRepository.save(savedCategory);

        Assertions.assertThat(updatedCategory.getName()).isEqualTo("updateName");

        Assertions.assertThat(updatedCategory.getDescription()).isEqualTo("updateDescription");

    }

    @Test
    public void givenCategoryObject_whenDelete_thenRemoveCategory(){

        categoryRepository.save(category);

        categoryRepository.deleteById(category.getId());

        Optional<Category> categoryOptional = categoryRepository.findById(category.getId());

        Assertions.assertThat(categoryOptional).isEmpty();


    }


}
