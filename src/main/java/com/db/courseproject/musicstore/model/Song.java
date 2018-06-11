package com.db.courseproject.musicstore.model;

import lombok.Data;

import java.util.Set;

/**
 * Song entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
public class Song {
    private long id;
    private Album album;
    private int orderNumber;
    private String title;
    private Set<Author> authors;
}
