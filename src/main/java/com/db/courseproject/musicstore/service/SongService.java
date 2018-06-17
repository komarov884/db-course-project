package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.SongDAO;
import com.db.courseproject.musicstore.model.Song;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class SongService extends AbstractService<Song> {
    public SongService(SongDAO songDAO) {
        super(songDAO);
    }
}
