package com.db.courseproject.musicstore.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Song entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
@Accessors(chain = true)
public class Song {
    private Long id;
    private Integer orderNumber;
    private String title;
    private List<Author> authors;
}
