package com.springboot.blog.BlogDemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.BlogDemo.entity.Post;
import com.springboot.blog.BlogDemo.payload.CommentDto;
import com.springboot.blog.BlogDemo.service.CommentService;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    public void givenCommentObject_whenCreateComment_thenReturnCreatedComment() throws Exception{

        long postId = 1L;

        long commentDtoId = 1L;

        Post post = new Post();
        post.setId(postId);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentDtoId);
        commentDto.setName("name");
        commentDto.setEmail("email@gmail.com");
        commentDto.setBody("Comment body should have at least 10 characters");


        when(commentService.createComment(postId,commentDto)).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{postId}/comments",postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(commentDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(commentDto.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(commentDto.getBody()));

    }
    @Test
    public void givenPostId_whenGetCommentsByPostId_thenListOfCommentsByPostId() throws  Exception{

        long postId = 1L;

        Post post = new Post();
        post.setId(postId);

        List<CommentDto> commentDtoList = new ArrayList<>();
        CommentDto commentDto1 = new CommentDto();
        commentDto1.setName("name");

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setName("name");

        commentDtoList.add(commentDto1);
        commentDtoList.add(commentDto2);

        when(commentService.getCommentsByPostId(postId)).thenReturn(commentDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments",postId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(commentDto1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(commentDto2.getName()));

    }
    @Test
    public void givenPostIdAndCommentId_whenGetCommentById_thenReturnCommentObject() throws  Exception{

        long postId = 1L;

        long commentId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setName("name");
        commentDto.setEmail("email@gmail.com");
        commentDto.setBody("Comment body should have at least 10 characters");


        when(commentService.getCommentById(postId,commentId)).thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments/{id}",postId,commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(commentDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(commentDto.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(commentDto.getBody()));

    }

    @Test
    public void givenPostIdAndCommentIdAndCommentObject_whenUpdateComment_thenReturnUpdatedCommentObject() throws  Exception{

        long postId = 1L;

        long commentId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setName("name");
        commentDto.setEmail("email@gmail.com");
        commentDto.setBody("Comment body should have at least 10 characters");

        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setName("updatedName");
        updatedCommentDto.setEmail("updatedEmail@gmail.com");
        updatedCommentDto.setBody("Updated Comment body should have at least 10 characters");

        when(commentService.updateComment(postId,commentId,commentDto)).thenReturn(updatedCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/{postId}/comments/{id}",postId,commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatedCommentDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updatedCommentDto.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").value(updatedCommentDto.getBody()));


    }
    @Test
    public void givenPostIdAndCommentId_whenDeleteComment_thenRemoveComment() throws  Exception {

        long postId = 1L;

        long commentId = 1L;

        doNothing().when(commentService).deleteComment(postId,commentId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}/comments/{id}",postId,commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Comment deleted successfully"));

    }

}
