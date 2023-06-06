package com.springboot.blog.BlogDemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.payload.PostDto;
import com.springboot.blog.BlogDemo.payload.PostResponse;
import com.springboot.blog.BlogDemo.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }


    @Test
    public void givenPostObject_whenCreatePost_thenReturnCreatedPost() throws Exception{
        long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);

        PostDto postDto = new PostDto();
        postDto.setContent("content");
        postDto.setTitle("title");
        postDto.setDescription("description");
        postDto.setCategoryId(category.getId());

        when(postService.createPost(postDto)).thenReturn(postDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(postDto.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(postDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(postDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(postDto.getCategoryId()));

    }
    @Test
    public void givenPostId_whenGetPostById_thenReturnPostObject() throws Exception{
        long categoryId = 1L;
        long postId = 1L;

        Category category = new Category();
        category.setId(categoryId);

        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setContent("content");
        postDto.setTitle("title");
        postDto.setDescription("description");
        postDto.setCategoryId(category.getId());

        when(postService.getPostById(postId)).thenReturn(postDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(postDto.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(postDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(postDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(postDto.getCategoryId()));

    }
    @Test
    public void givenPostObject_whenUpdatePost_thenReturnUpdatedPostObject() throws Exception{

        long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setContent("content");
        postDto.setTitle("title");
        postDto.setDescription("description");

        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setId(postId);
        updatedPostDto.setContent("updatedContent");
        updatedPostDto.setTitle("updatedTitle");
        updatedPostDto.setDescription("updatedDescription");


        when(postService.updatePost(postDto,postId)).thenReturn(updatedPostDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{id}",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(updatedPostDto.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(updatedPostDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(updatedPostDto.getTitle()));
    }
    @Test
    public void givenPostObjectId_whenDeletePost_thenReturnNothing() throws  Exception {
        long postId = 1L;

        doNothing().when(postService).deletePostById(postId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}",postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Post entity deleted successfully"));
    }

    @Test
    public void givenPostObjectId_whenGetPostByCategory_thenReturnListOfPostsByCategory() throws Exception{

        long categoryId = 1L;

        List<PostDto> postDtos = new ArrayList<>();

        PostDto postDto1 = new PostDto();
        postDto1.setCategoryId(categoryId);

        PostDto postDto2 = new PostDto();
        postDto2.setCategoryId(categoryId);

        postDtos.add(postDto1);

        postDtos.add(postDto2);


        when(postService.getPostByCategory(categoryId)).thenReturn(postDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/category/{id}", categoryId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(postDtos.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(postDtos.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(postDtos.get(1).getId()));

    }
    @Test
    public void givenPageNoAndPageSize_whenGetAllPosts_thenReturnPostResponse() throws  Exception{
        long postId = 1L;
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "createdAt";
        String sortDir = "ASC";

        List<PostDto> expectedPostDtos = new ArrayList<>();

        PostDto postDto1 = new PostDto();
        postDto1.setId(postId);

        PostDto postDto2 = new PostDto();
        postDto2.setId(postId);

        expectedPostDtos.add(postDto1);

        expectedPostDtos.add(postDto2);

        int totalElements = expectedPostDtos.size();
        int totalPages = 1;
        boolean last = true;

        PostResponse expectedResponse = new PostResponse();
        expectedResponse.setContent(expectedPostDtos);
        expectedResponse.setPageNo(pageNo);
        expectedResponse.setPageSize(pageSize);
        expectedResponse.setTotalElements(totalElements);
        expectedResponse.setTotalPages(totalPages);
        expectedResponse.setLast(last);

        when(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir)).thenReturn(expectedResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .param("pageNo", String.valueOf(pageNo))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortBy", sortBy)
                        .param("sortDir", sortDir))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(expectedPostDtos.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(expectedResponse.getPageNo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(expectedResponse.getPageSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(expectedResponse.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(expectedResponse.getTotalPages()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(expectedResponse.isLast()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(expectedPostDtos.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(expectedPostDtos.get(1).getId()));

    }



}
