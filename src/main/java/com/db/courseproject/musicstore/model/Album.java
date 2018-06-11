package com.db.courseproject.musicstore.model;

import lombok.Data;

import java.util.Set;

/**
 * Album entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
public class Album {
    private long id;
    private String title;
    private int issueYear;
    private int price;
    private String genre;
    private Artist artist;
    private Set<Producer> producers;
    private RecordLabel recordLabel;
    private Set<Song> songs;
}
