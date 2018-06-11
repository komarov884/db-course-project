package com.db.courseproject.musicstore.model;

import lombok.Data;

/**
 * Record label entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
public class RecordLabel {
    private long id;
    private String name;
    private String country;
    private int foundationYear;
}
