package com.db.courseproject.musicstore.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Author entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
@Accessors(chain = true)
public class Author extends Member {
    private AuthorType authorType;
}
