package com.db.courseproject.musicstore.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Abstract parent for members of music industry.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
@Accessors(chain = true)
public abstract class Member {
    private Long id;
    private FullName name;
    private Date birthDate;
}
