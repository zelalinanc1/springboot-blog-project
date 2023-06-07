package com.springboot.blog.BlogDemo.service;

import com.springboot.blog.BlogDemo.entity.Comment;
import com.springboot.blog.BlogDemo.entity.Post;
import com.springboot.blog.BlogDemo.exception.BlogAPIException;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import com.springboot.blog.BlogDemo.payload.CommentDto;
import com.springboot.blog.BlogDemo.payload.PostDto;
import com.springboot.blog.BlogDemo.repository.CategoryRepository;
import com.springboot.blog.BlogDemo.repository.CommentRepository;
import com.springboot.blog.BlogDemo.repository.PostRepository;
import com.springboot.blog.BlogDemo.service.impl.CommentServiceImpl;
import com.springboot.blog.BlogDemo.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    private CommentServiceImpl commentService;
    private  CommentRepository commentRepository;
    private   PostRepository postRepository;
    private  ModelMapper modelMapper;
    @BeforeEach
    public void setUp() {

        postRepository = Mockito.mock(PostRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        modelMapper = Mockito.mock(ModelMapper.class);
        commentService = new CommentServiceImpl(commentRepository,postRepository,modelMapper);
    }

    @Test
    public void givenCommentObjectAndPostId_whenCreateComment_thenReturnSavedCommentObject() {

        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        CommentDto commentDto = new CommentDto();

        Comment comment = new Comment();
        comment.setName("name");
        comment.setEmail("email@gmail.com");
        comment.setBody("body");
        comment.setPost(post);

        when(modelMapper.map(commentDto,Comment.class)).thenReturn(comment);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        comment.setPost(post);

        Comment newComment = new Comment();

        when(commentRepository.save(comment)).thenReturn(newComment);

        CommentDto mappedCommentDto = new CommentDto();

        when(modelMapper.map(newComment,CommentDto.class)).thenReturn(mappedCommentDto);

        CommentDto result = commentService.createComment(postId,commentDto);

        assertEquals(mappedCommentDto, result);

        verify(modelMapper).map(commentDto,Comment.class);
        verify(postRepository).findById(postId);
        verify(commentRepository).save(comment);
        verify(modelMapper).map(newComment,CommentDto.class);

    }
    @Test
    public void givenId_whenGetCommentsById_thenReturnListOfCommentsByPostId() {

        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        List<Comment> comments = new ArrayList<>();

        Comment comment1 = new Comment();
        comment1.setPost(post);

        Comment comment2 = new Comment();
        comment2.setPost(post);

        comments.add(comment1);
        comments.add(comment2);


        when(commentRepository.findByPostId(postId)).thenReturn(comments);

        List<CommentDto> commentDtos = new ArrayList<>();

        CommentDto commentDto1 = new CommentDto();

        CommentDto commentDto2 = new CommentDto();

        commentDtos.add(commentDto1);
        commentDtos.add(commentDto2);

        when(modelMapper.map(any(Comment.class),eq(CommentDto.class))).thenReturn(commentDto1).thenReturn(commentDto1);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertEquals(result,commentDtos);

        verify(commentRepository, times(1)).findByPostId(postId);
        verify(modelMapper,times(2)).map(any(Comment.class),eq(CommentDto.class));
        verify(commentRepository, times(1)).findByPostId(postId);

    }

    @Test
    public void givenPostIdAndCommentId_whenGetCommentById_thenReturnCommentObjectById() {

        long postId = 1L;
        long commentId = 2L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        CommentDto result = commentService.getCommentById(postId, commentId);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());



    }

    @Test
    public void givenPostIdAndCommentId_whenGetCommentById_thenReturnError() {


        long postId = 1L;
        long commentId = 2L;
        long differentPostId = 3L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);


        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        when(postRepository.findById(differentPostId)).thenReturn(Optional.of(new Post()));

        BlogAPIException exception = assertThrows(BlogAPIException.class, () ->
                commentService.getCommentById(differentPostId, commentId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Comment does not not belong to post", exception.getMessage());

    }
    @Test
    public void givenCommentObject_whenUpdateComment_thenReturnUpdatedPostObject() {
        long postId = 1L;

        long commentId = 1L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);


        CommentDto commentDto = new CommentDto();
        commentDto.setName("name");
        commentDto.setEmail("email");
        commentDto.setBody("body");

        Comment updatedComment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        CommentDto expectedCommentDto = new CommentDto();
        expectedCommentDto.setId(commentId);
        expectedCommentDto.setName(commentDto.getName());
        expectedCommentDto.setEmail(commentDto.getEmail());
        expectedCommentDto.setBody(commentDto.getBody());



        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        when(commentRepository.save(comment)).thenReturn(updatedComment);

        when(modelMapper.map(updatedComment,CommentDto.class)).thenReturn(expectedCommentDto);

        CommentDto result = commentService.updateComment(postId, commentId, commentDto);

        assertNotNull(result);
        assertEquals(expectedCommentDto.getName(), result.getName());
        assertEquals(expectedCommentDto.getEmail(), result.getEmail());
        assertEquals(expectedCommentDto.getBody(), result.getBody());

    }
    @Test
    public void givenCommentObject_whenUpdateComment_thenReturnError() {

        long postId = 1L;

        long commentId = 1L;

        long differentPostId = 3L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);


        CommentDto commentDto = new CommentDto();
        commentDto.setName("name");
        commentDto.setEmail("email");
        commentDto.setBody("body");


        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        when(postRepository.findById(differentPostId)).thenReturn(Optional.of(new Post()));

        BlogAPIException exception = assertThrows(BlogAPIException.class, () ->
                commentService.updateComment(differentPostId, commentId, commentDto)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Comment does not belongs to post", exception.getMessage());

    }

    @Test
    public void givenPostIdAndCommentId_whenDeleteCommentObject_thenRemoveCommentObject() {

        long postId = 1L;

        long commentId = 1L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));


        assertDoesNotThrow(() ->
                commentService.deleteComment(postId, commentId)
        );


        verify(commentRepository, times(1)).delete(comment);

    }

    @Test
    public void givenPostIdAndCommentId_whenDeleteCommentObject_thenReturnError() {

        long postId = 1L;

        long commentId = 1L;

        long differentPostId = 2L;

        Post post = new Post();
        post.setId(postId);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);

        when(postRepository.findById(differentPostId)).thenReturn(Optional.of(new Post()));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        BlogAPIException exception = assertThrows(BlogAPIException.class, () ->
                commentService.deleteComment(differentPostId, commentId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Comment does not belongs to post", exception.getMessage());

    }



}
