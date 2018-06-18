package com.db.courseproject.musicstore.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Entity for creating and updating songs.
 * <p>
 * Created on 5/18/2018.
 *
 * @author Vasilii Komarov
 */
@Data
@Accessors(chain = true)
public class ExtendedSong extends Song {
    private Long albumId;
}
