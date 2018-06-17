package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.ProducerDAO;
import com.db.courseproject.musicstore.model.Producer;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class ProducerService extends AbstractService<Producer> {
    public ProducerService(ProducerDAO producerDAO) {
        super(producerDAO);
    }
}
