package com.db.courseproject.musicstore.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Album entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
@Accessors(chain = true)
public class Album {
    private Long id;
    private String title;
    private Integer issueYear;
    private Integer price;
    private String genre;
    private Artist artist;
    private List<Producer> producers;
    private RecordLabel recordLabel;
    private List<Song> songs;
}
