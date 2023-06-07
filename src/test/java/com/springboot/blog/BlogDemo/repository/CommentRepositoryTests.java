package com.springboot.blog.BlogDemo.repository;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.entity.Comment;
import com.springboot.blog.BlogDemo.entity.Post;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private  CommentRepository commentRepository;
    @Autowired
    private   PostRepository postRepository;

    private Comment comment;
    private Comment savedComment;

    private Post post;
    private Post savedPost;

    @BeforeEach
    public void setUp() {

         post = Post.builder()
                .title("title")
                .description("description")
                .content("content")
                .build();

        savedPost = postRepository.save(post);

        comment = Comment.builder()
                .name("name")
                .email("email@gmail.com")
                .body("body")
                .post(savedPost)
                .build();

        savedComment = commentRepository.save(comment);
    }

    @Test
    public void givenCommentObject_whenSave_thenReturnSavedComment() {


        Assertions.assertThat(savedComment).isNotNull();

        Assertions.assertThat(savedComment.getId()).isGreaterThan(0);

    }

    @Test
    public void givenCommentObject_whenFindById_thenReturnCommentObject() {

        Comment returnedComment=commentRepository.findById(savedComment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment","id",savedComment.getId()));

        Assertions.assertThat(returnedComment).isNotNull();

    }
    @Test
    public void givenPostId_whenFindByPostId_thenReturnListOfComment() {
        long postId= 1L;

        Comment comment1 = Comment.builder()
                .name("name1")
                .email("email1@gmail.com")
                .body("body1")
                .post(savedPost)
                .build();

        commentRepository.save(comment1);

        List<Comment> expectedComments = new ArrayList<>();
        expectedComments.add(comment);
        expectedComments.add(comment1);

        List<Comment> actualComments = commentRepository.findByPostId(postId);

        assertThat(actualComments, hasSize(expectedComments.size()));

        assertThat(actualComments.get(0).getName(), equalTo(expectedComments.get(0).getName()));
        assertThat(actualComments.get(1).getName(), equalTo(expectedComments.get(1).getName()));

        assertThat(actualComments.get(0).getEmail(), equalTo(expectedComments.get(0).getEmail()));
        assertThat(actualComments.get(1).getEmail(), equalTo(expectedComments.get(1).getEmail()));

    }

    @Test
    public void givenCommentList_whenFindAll_thenReturnCommentList(){

        Comment comment1 = Comment.builder()
                .name("name1")
                .email("email1@gmail.com")
                .body("body1")
                .post(savedPost)
                .build();

        commentRepository.save(comment1);

        List<Comment> expectedComments = commentRepository.findAll();

        Assertions.assertThat(expectedComments).isNotNull();
        Assertions.assertThat(expectedComments.size()).isEqualTo(2);

    }

    @Test
    public void givenCommentObject_whenUpdateComment_thenReturnUpdatedComment(){

        savedComment=commentRepository.findById(comment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment","id",comment.getId()));

        savedComment.setName("updatedName");
        savedComment.setEmail("updatedEmail@gmail.com");
        savedComment.setBody("updatedBody");

        Comment updatedComment=commentRepository.save(savedComment);

        Assertions.assertThat(updatedComment.getName()).isEqualTo("updatedName");

        Assertions.assertThat(updatedComment.getEmail()).isEqualTo("updatedEmail@gmail.com");

        Assertions.assertThat(updatedComment.getBody()).isEqualTo("updatedBody");


    }
    @Test
    public void givenCommentObject_whenDelete_thenRemoveComment(){

        commentRepository.deleteById(comment.getId());

        Optional<Comment> commentOptional = commentRepository.findById(comment.getId());

        Assertions.assertThat(commentOptional).isEmpty();

    }

}
