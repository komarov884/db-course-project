package com.db.courseproject.musicstore.model;

import lombok.Data;

import java.util.Date;

/**
 * Member.
 * <p>
 * Created on 5/14/2018.
 *
 * @author Vasilii Komarov
 */
@Data
public abstract class Member {
    private long id;
    private FullName name;
    private Date birthDate;
}
