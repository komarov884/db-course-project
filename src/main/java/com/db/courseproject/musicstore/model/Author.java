package com.db.courseproject.musicstore.model;

import lombok.Data;

/**
 * Author entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
public class Author extends Member {
    private AuthorType authorType;
}
