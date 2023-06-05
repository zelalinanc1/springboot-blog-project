package com.springboot.blog.BlogDemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.payload.CategoryDto;
import com.springboot.blog.BlogDemo.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }


    @Test
    public void givenCategoryObject_whenAddCategory_thenReturnSavedCategoryObject() throws Exception {


        CategoryDto savedCategoryDto = new CategoryDto();
        savedCategoryDto.setId(1L);
        savedCategoryDto.setName("Category Name");
        savedCategoryDto.setDescription("Category Description");

        when(categoryService.addCategory(any(CategoryDto.class))).thenReturn(savedCategoryDto);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(savedCategoryDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Category Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Category Description"));
    }
    @Test
    public void givenCategoryId_whenGetCategory_thenReturnCategoryObject() throws Exception{


        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Category Name");
        categoryDto.setDescription("Category Description");

        when(categoryService.getCategory(anyLong())).thenReturn(categoryDto);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Category Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Category Description"));


    }
    @Test
    public void givenNothing_whenGetAllCategories_thenReturnListOfCategories() throws Exception{

        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);
        categoryDto1.setName("Category Name1");
        categoryDto1.setDescription("Category Description1");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("Category Name2");
        categoryDto2.setDescription("Category Description2");

        List<CategoryDto> categories = new ArrayList<>();
        categories.add(categoryDto1);
        categories.add(categoryDto2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Category Name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Category Description1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Category Name2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Category Description2"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenCategoryObject_whenUpdateCategory_thenReturnUpdatedCategoryObject() throws Exception{

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Category Name");
        categoryDto.setDescription("Category Description");

        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setId(1L);
        updatedCategoryDto.setName("Updated Category Name");
        updatedCategoryDto.setDescription("Updated Category Description");

        when(categoryService.updateCategory(any(CategoryDto.class), anyLong())).thenReturn(updatedCategoryDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(categoryDto)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Category Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Category Description"))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void givenCategoryObjectId_whenDeleteCategory_thenReturnNothing() throws  Exception{

        long categoryId = 1L;

        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Category deleted successfully!"));




    }






}









