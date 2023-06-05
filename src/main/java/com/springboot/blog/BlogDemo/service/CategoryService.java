package com.springboot.blog.BlogDemo.service;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.payload.CategoryDto;
import org.modelmapper.ModelMapper;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto getCategory(long categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto updateCategory(CategoryDto categoryDto,long categoryId);

    void deleteCategory(long categoryId);
     List<CategoryDto> mapCategoriesToDto(List<Category> categories, ModelMapper modelMapper);
}
