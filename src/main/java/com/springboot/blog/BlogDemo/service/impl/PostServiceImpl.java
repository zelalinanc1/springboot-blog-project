package com.springboot.blog.BlogDemo.service.impl;

import com.springboot.blog.BlogDemo.entity.Category;
import com.springboot.blog.BlogDemo.entity.Post;
import com.springboot.blog.BlogDemo.exception.ResourceNotFoundException;
import com.springboot.blog.BlogDemo.payload.PostDto;
import com.springboot.blog.BlogDemo.payload.PostResponse;
import com.springboot.blog.BlogDemo.repository.CategoryRepository;
import com.springboot.blog.BlogDemo.repository.PostRepository;
import com.springboot.blog.BlogDemo.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper mapper;


    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }


    @Override
    public PostDto createPost(PostDto postDto) {

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",postDto.getCategoryId()));

        //convert Dto to entity
        Post post= mapToEntity(postDto);
        post.setCategory(category);

        Post newPost = postRepository.save(post);

        //convert entity to Dto

        PostDto postResponse = mapToDto(newPost);


        return postResponse;

    }

    @Override
    public PostResponse getAllPosts(int pageNo,int pageSize,String sortBy,String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content = listOfPosts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(postResponse.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);

        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostByCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        List<Post> posts = postRepository.findByCategoryId(categoryId);

        return posts.stream().map((post) -> mapToDto(post))
                .collect(Collectors.toList());
    }


    //convert entity to Dto
    public PostDto mapToDto(Post post){

        PostDto postDto = mapper.map(post,PostDto.class);
        return postDto;

    }
    //convert Dto to entity
    public Post mapToEntity(PostDto postDto ) {

        Post post = mapper.map(postDto,Post.class);
        return  post;

    }
}
