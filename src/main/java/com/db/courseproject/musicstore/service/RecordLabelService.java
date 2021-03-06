package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.RecordLabelDAO;
import com.db.courseproject.musicstore.exception.DAOException;
import com.db.courseproject.musicstore.exception.ServiceException;
import com.db.courseproject.musicstore.model.RecordLabel;

import java.util.List;

/**
 * Service for working with {@link RecordLabel}.
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

    public List<RecordLabel> findAllByName(String name) throws ServiceException {
        try {
            return recordLabelDAO.findAllByName(name);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
