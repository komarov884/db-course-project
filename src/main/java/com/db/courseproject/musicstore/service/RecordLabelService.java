package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.RecordLabelDAO;
import com.db.courseproject.musicstore.model.RecordLabel;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class RecordLabelService extends AbstractService<RecordLabel> {
    public RecordLabelService(RecordLabelDAO recordLabelDAO) {
        super(recordLabelDAO);
    }
}
