package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.ProducerDAO;
import com.db.courseproject.musicstore.model.Producer;

import java.util.List;

/**
 * Service for working with {@link Producer}.
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class ProducerService extends AbstractService<Producer> {
    private final ProducerDAO producerDAO;

    public ProducerService(ProducerDAO producerDAO) {
        super(producerDAO);
        this.producerDAO = producerDAO;
    }

    public List<Producer> findAllByName(String name) {
        return producerDAO.findAllByName(name);
    }
}
