package com.springboot.blog.BlogDemo.service;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.exception.BlogAPIException;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import com.springboot.blog.BlogDemo.payload.CategoryDto;
import com.springboot.blog.BlogDemo.repository.CategoryRepository;
import com.springboot.blog.BlogDemo.service.impl.CategoryServiceImpl;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {


    private CategoryRepository categoryRepository;


    private ModelMapper modelMapper;


    private CategoryServiceImpl categoryService;

    @BeforeEach
    public void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        modelMapper = Mockito.mock(ModelMapper.class);

        categoryService = new CategoryServiceImpl(categoryRepository,modelMapper);



    }



    @Test
    public void givenCategoryObject_whenAddCategory_thenReturnCategoryObject(){

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("name");
        categoryDto.setDescription("description");

        Category category = Category.builder()
                .name("name")
                .description("description")
                .build();

        Category savedCategory = Category.builder()
                .name("name")
                .description("description")
                .build();

        Mockito.when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        Mockito.when(categoryRepository.save(savedCategory)).thenReturn(savedCategory);
        Mockito.when(modelMapper.map(savedCategory, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.addCategory(categoryDto);


        assertEquals(result,categoryDto);


        Mockito.verify(modelMapper).map(categoryDto, Category.class);
        Mockito.verify(categoryRepository).save(savedCategory);
        Mockito.verify(modelMapper).map(savedCategory, CategoryDto.class);


    }

    @Test
    public void givenCategoryId_whenGetCategory_thenReturnCategoryObject() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("name");
        categoryDto.setDescription("description");

        Category category = Category.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategory(categoryDto.getId());

        assertEquals(result,categoryDto);

        Mockito.verify(categoryRepository).findById(category.getId());
        Mockito.verify(modelMapper).map(category, CategoryDto.class);



    }

    @Test
    public void givenInvalidCategoryId_whenCategoryNotExist_thenReturnThrowError() {

        long categoryId = 1L;


        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());


        Throwable exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategory(categoryId);
        });
        String quotes ="'";

        assertEquals("Category not found with id : " + quotes+categoryId+quotes, exception.getMessage());
    }



    @Test
    public void givenCategoryObjectList_whenGetAllCategories_thenReturnCategoryList() {


        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");
        category1.setDescription("Description 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");
        category2.setDescription("Description 2");

        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        ModelMapper modelMapper = new ModelMapper();


        List<CategoryDto> result = categoryService.mapCategoriesToDto(categories, modelMapper);


        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Description 1", result.get(0).getDescription());
        assertEquals("Category 2", result.get(1).getName());
        assertEquals("Description 2", result.get(1).getDescription());

    }
    @Test
    public void givenCategoryObjectAndCategoryId_whenUpdateCategoryObject_thenReturnUpdatedCategoryObject() {


        Category category = Category.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

        Mockito.when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("updatedName");
        categoryDto.setDescription("updatedDescription");

        Mockito.when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto updatedCategoryDto = categoryService.updateCategory(categoryDto, category.getId());


        assertEquals(categoryDto.getName(), updatedCategoryDto.getName());
        assertEquals(categoryDto.getDescription(), updatedCategoryDto.getDescription());



        Mockito.verify(categoryRepository).findById(anyLong());
        Mockito.verify(categoryRepository).save(any(Category.class));
        Mockito.verify(modelMapper).map(category, CategoryDto.class);


    }

    @Test
    public void givenCategoryId_whenDeleteCategoryObject_thenNothing() {

        Category category = Category.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        categoryService.deleteCategory(category.getId());

        verify(categoryRepository).findById(anyLong());


    }

}
