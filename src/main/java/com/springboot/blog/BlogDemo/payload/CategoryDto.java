package com.springboot.blog.BlogDemo.payload;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private  Long id;
    private String name;
    private String description;
}
