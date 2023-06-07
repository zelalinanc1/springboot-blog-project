package com.springboot.blog.BlogDemo.service;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.entity.Post;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import com.springboot.blog.BlogDemo.payload.PostDto;
import com.springboot.blog.BlogDemo.payload.PostResponse;
import com.springboot.blog.BlogDemo.repository.CategoryRepository;
import com.springboot.blog.BlogDemo.repository.PostRepository;
import com.springboot.blog.BlogDemo.service.impl.CategoryServiceImpl;
import com.springboot.blog.BlogDemo.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    private PostServiceImpl postService;

    private PostRepository postRepository;
    private ModelMapper modelMapper;

    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        postRepository = Mockito.mock(PostRepository.class);
        modelMapper = Mockito.mock(ModelMapper.class);
        postService = new PostServiceImpl(postRepository,categoryRepository,modelMapper);

    }



    @Test
    public void givenPostObject_whenCreatePost_thenReturnSavedPostObject() {

        long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        PostDto postDto = new PostDto();
        postDto.setCategoryId(categoryId);

        Post post = new Post();
        when(modelMapper.map(postDto, Post.class)).thenReturn(post);

        Post savedPost = new Post();
        when(postRepository.save(post)).thenReturn(savedPost);

        PostDto mappedDto = new PostDto();
        when(modelMapper.map(savedPost, PostDto.class)).thenReturn(mappedDto);

        PostDto result = postService.createPost(postDto);


        assertEquals(mappedDto, result);

        verify(categoryRepository).findById(categoryId);
        verify(postRepository).save(post);
        verify(modelMapper).map(savedPost, PostDto.class);

    }
    @Test
    public void givenPageNoAndPageSize_whenGetAllPosts_thenReturnPostResponse() {

        // Arrange
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "createdAt";
        String sortDir = "ASC";

        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());

        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());
        when(postRepository.findAll(pageable)).thenReturn(page);

        List<Post> expectedListOfPosts = page.getContent();

        List<PostDto> mappedDtos = new ArrayList<>();
        mappedDtos.add(new PostDto());
        mappedDtos.add(new PostDto());
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto()).thenReturn(new PostDto());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(mappedDtos);
        postResponse.setPageNo(page.getNumber());
        postResponse.setPageSize(page.getSize());
        postResponse.setTotalElements(page.getTotalElements());
        postResponse.setTotalPages(postResponse.getTotalPages());
        postResponse.setLast(page.isLast());

        PostResponse result = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        verify(postRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(2)).map(any(Post.class), eq(PostDto.class));

        assertEquals(postResponse.getContent(), result.getContent());
        assertEquals(postResponse.getPageNo(), result.getPageNo());
        assertEquals(postResponse.getPageSize(), result.getPageSize());
        assertEquals(postResponse.getTotalElements(), result.getTotalElements());
        assertEquals(postResponse.getTotalPages(), result.getTotalPages());
        assertEquals(postResponse.isLast(), result.isLast());

    }
    @Test
    public void givenId_whenGetPostById_thenReturnPostObjectById() {

        long postId = 1L;

        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDto mappedDto = new PostDto();
        when(modelMapper.map(post, PostDto.class)).thenReturn(mappedDto);

        PostDto result = postService.getPostById(postId);

        verify(postRepository).findById(postId);
        verify(modelMapper).map(post, PostDto.class);
        assertEquals(mappedDto, result);

    }

    @Test
    public void givenInvalidId_whenGetPostById_thenReturnNothing() {
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(postId));
        verify(postRepository).findById(postId);
        verify(modelMapper, never()).map(any(), eq(PostDto.class));

    }

    @Test
    public void givenPostObjectAndId_whenUpdatePost_thenReturnUpdatedPost() {

        long postId = 1L;
        long categoryId = 1L;

        PostDto postDto = new PostDto();
        postDto.setCategoryId(categoryId);
        postDto.setTitle("title");
        postDto.setDescription("description");
        postDto.setContent("content");

        Post post = new Post();

        Category category = new Category();
        category.setId(categoryId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Post updatedPost = new Post();
        when(postRepository.save(post)).thenReturn(updatedPost);

        PostDto mappedDto = new PostDto();
        when(modelMapper.map(updatedPost, PostDto.class)).thenReturn(mappedDto);

        PostDto result = postService.updatePost(postDto, postId);

        verify(postRepository).findById(postId);
        verify(categoryRepository).findById(categoryId);
        verify(postRepository).save(post);
        verify(modelMapper).map(updatedPost, PostDto.class);
        assertEquals(mappedDto, result);


    }

    @Test
    public void givenInvalidPostObject_whenUpdatePost_thenReturnNothing() {

        long postId = 1L;
        long categoryId = 1L;

        PostDto postDto = new PostDto();
        postDto.setCategoryId(categoryId);
        postDto.setTitle("title");
        postDto.setDescription("description");
        postDto.setContent("content");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(postDto, postId));
        verify(postRepository).findById(postId);
        verify(categoryRepository, never()).findById(anyLong());
        verify(postRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), eq(PostDto.class));

    }
    @Test
    public void givenId_whenDeletePostObject_thenRemovePostObject() {
        long postId = 1L;

        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePostById(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(post);


    }

    @Test
    public void givenInvalidId_whenDeletePostObject_thenNothing() {
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePostById(postId));


        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any());


    }

    @Test
    public void givenCategoryId_whenGetPostByCategory_thenReturnListOfPostByCategory() {

        long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());

        when(postRepository.findByCategoryId(categoryId)).thenReturn(posts);


        List<PostDto> mappedDtos = new ArrayList<>();

        PostDto postDto1 = new PostDto();

        PostDto postDto2 = new PostDto();

        mappedDtos.add(postDto1);

        mappedDtos.add(postDto2);

        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(postDto1).thenReturn(postDto2);

        List<PostDto> result = postService.getPostByCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(postRepository, times(1)).findByCategoryId(categoryId);
        verify(modelMapper, times(2)).map(any(Post.class), eq(PostDto.class));
        assertEquals(mappedDtos, result);

    }





}
