package com.db.courseproject.musicstore.service;

import com.db.courseproject.musicstore.dao.AlbumDAO;
import com.db.courseproject.musicstore.dao.ArtistDAO;
import com.db.courseproject.musicstore.exception.ForeignKeyViolationException;
import com.db.courseproject.musicstore.model.Artist;

import java.util.List;

import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_ARTIST_ID;
import static com.db.courseproject.musicstore.util.DBConstants.ALBUMS_TABLE;

/**
 * <p>
 * Created on 6/17/2018.
 *
 * @author Vasilii Komarov
 */
public class ArtistService extends AbstractService<Artist> {
    private final ArtistDAO artistDAO;
    private final AlbumDAO albumDAO;

    public ArtistService(ArtistDAO artistDAO, AlbumDAO albumDAO) {
        super(artistDAO);
        this.artistDAO = artistDAO;
        this.albumDAO = albumDAO;
    }

    public List<Artist> findAllByName(String name) {
        return artistDAO.findAllByName(name);
    }

    @Override
    protected void checkForeignRelations(Long id) {
        if (!albumDAO.findAllByArtistId(id).isEmpty()) {
            throw new ForeignKeyViolationException(
                    String.format("The %s table has still contained records with %s = %s",
                            ALBUMS_TABLE, ALBUMS_ARTIST_ID, id)
            );
        }
    }
}
