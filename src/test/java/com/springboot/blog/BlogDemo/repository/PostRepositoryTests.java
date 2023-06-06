package com.springboot.blog.BlogDemo.repository;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.entity.Post;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class PostRepositoryTests {

    @Autowired
    private  PostRepository postRepository;

    @Autowired
    private  CategoryRepository categoryRepository;

    private Post post;
    private Post savedPost;


    private Category category;
    private Category savedCategory;



    @BeforeEach
    public void setUp() {
        category = Category.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

         savedCategory = categoryRepository.save(category);

        post = Post.builder()
                .title("title")
                .description("description")
                .content("content")
                .category(savedCategory)
                .build();

         savedPost = postRepository.save(post);



    }


    @Test
    public void givenPostObject_whenSave_thenReturnSavedPost() {


        Assertions.assertThat(savedPost).isNotNull();

        Assertions.assertThat(savedPost.getId()).isGreaterThan(0);
    }
    @Test
    public void givenPostObject_whenFindById_thenReturnPostObject() {



        Post returnedPost=postRepository.findById(savedPost.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Post","id",savedPost.getId()));

        Assertions.assertThat(returnedPost).isNotNull();

    }

    @Test
    public void givenCategoryId_whenFindByCategoryId_thenReturnListOfPost() {
        long categoryId=1L;

        Post post1 = Post.builder()
                .title("title1")
                .description("description1")
                .content("content1")
                .category(savedCategory)
                .build();


        postRepository.save(post1);

        List<Post> expectedPosts = new ArrayList<>();
        expectedPosts.add(post);
        expectedPosts.add(post1);

        List<Post> actualPosts = postRepository.findByCategoryId(categoryId);

        // Assert the results using assertThat
        assertThat(actualPosts, hasSize(expectedPosts.size()));
        assertThat(actualPosts.get(0).getId(), equalTo(expectedPosts.get(0).getId()));
        assertThat(actualPosts.get(0).getTitle(), equalTo(expectedPosts.get(0).getTitle()));
        assertThat(actualPosts.get(1).getId(), equalTo(expectedPosts.get(1).getId()));
        assertThat(actualPosts.get(1).getTitle(), equalTo(expectedPosts.get(1).getTitle()));

    }

    @Test
    public void givenPostList_whenFindAll_thenPostList(){

        Post post1 = Post.builder()
                .title("title1")
                .description("description1")
                .content("content1")
                .category(savedCategory)
                .build();


        postRepository.save(post1);

        List<Post> postList = postRepository.findAll();

        Assertions.assertThat(postList).isNotNull();
        Assertions.assertThat(postList.size()).isEqualTo(2);

    }


    @Test
    public void givenPostObject_whenUpdatePost_thenReturnUpdatedPost(){

        savedPost=postRepository.findById(post.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Post","id",post.getId()));


        savedPost.setTitle("updateTitle");

        savedPost.setDescription("updateDescription");

        savedPost.setContent("updateContent");

        Post updatedPost=postRepository.save(savedPost);

        Assertions.assertThat(updatedPost.getTitle()).isEqualTo("updateTitle");

        Assertions.assertThat(updatedPost.getDescription()).isEqualTo("updateDescription");

        Assertions.assertThat(updatedPost.getContent()).isEqualTo("updateContent");

    }

    @Test
    public void givenPostObject_whenDelete_thenRemovePost(){

        postRepository.deleteById(post.getId());

        Optional<Post> postOptional = postRepository.findById(post.getId());

        Assertions.assertThat(postOptional).isEmpty();


    }
}
