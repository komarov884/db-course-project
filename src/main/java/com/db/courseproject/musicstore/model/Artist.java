package com.db.courseproject.musicstore.model;

import lombok.Data;

import java.util.Set;

/**
 * Artist entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
public class Artist extends Member {
    private Set<Album> albums;
}
