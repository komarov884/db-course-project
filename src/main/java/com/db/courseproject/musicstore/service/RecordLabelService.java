package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.RecordLabelDAO;
import com.db.courseproject.musicstore.model.RecordLabel;

import java.util.List;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class RecordLabelService extends AbstractService<RecordLabel> {
    private final RecordLabelDAO recordLabelDAO;

    public RecordLabelService(RecordLabelDAO recordLabelDAO) {
        super(recordLabelDAO);
        this.recordLabelDAO = recordLabelDAO;
    }

    public List<RecordLabel> findAllByName(String name) {
        return recordLabelDAO.findAllByName(name);
    }
}
