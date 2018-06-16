package com.db.courseproject.musicstore.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Record label entity.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
@Accessors(chain = true)
public class RecordLabel {
    private Long id;
    private String name;
    private String country;
    private Integer foundationYear;
}
