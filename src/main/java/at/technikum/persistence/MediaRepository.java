package at.technikum.persistence;

import at.technikum.data.Database;
import at.technikum.domain.Media;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaRepository {//Repository = Kapselung aller SQL-Operationen, KEIN http json oÄ

    private final Database db; // db verb

    public MediaRepository(Database db) { // übergibt database klasse von aussen
        this.db = db;
    }

    // ---------------------------
    // CREATE
    // ---------------------------
    public Media save(Media media) { // media in db speichern
        final String sql = """
            INSERT INTO media (media_type, title, description, release_year, age_restriction, genres, creator_id)
            VALUES (?, ?, ?, ?, ?, ?, ?) 
            """;

        try (Connection conn = db.getConnection(); // db verb geöffnet
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // db gibt id zrk

            fillStatement(ps, media); // füllt ?`s
            ps.executeUpdate();// führt insert aus -> media jz in db

            try (ResultSet keys = ps.getGeneratedKeys()) { // holt die von der db erzeugte id
                if (keys.next()) {
                    media.setId(keys.getInt(1)); // media bekommt id-> persistiert
                    return media;
                }
                throw new SQLException("No generated ID returned");
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to store media entry", ex);
        }
    }

    // ---------------------------
    // READ ALL
    // ---------------------------
    public List<Media> findAll() {
        final String sql = "SELECT * FROM media ORDER BY id";

        List<Media> list = new ArrayList<>(); // alle media aus db gesammelt

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) { // führt select aus; ergebnis = tabelle

            while (rs.next()) { // map(rs) baut ein media objekt für jede db zeile
                list.add(map(rs)); // wird zur liste hinzugefügt
            } //Repository übersetzt DB → Domain-Objekt

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load media entries", ex);
        }

        return list;
    }

    // ---------------------------
    // READ BY ID
    // ---------------------------
    public Media findById(int id) {
        final String sql = "SELECT * FROM media WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // setzt id sicher ein, 1st ?
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
            return null; //404 Not Found wenn nichts gefunden wurde

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to find media with id=" + id, ex);
        }
    }

    // ---------------------------
    // UPDATE
    // ---------------------------
    public void update(Media media) {
        final String sql = """
            UPDATE media
            SET media_type = ?, title = ?, description = ?, release_year = ?, age_restriction = ?, genres = ?, creator_id = ?
            WHERE id = ?
            """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, media);
            ps.setInt(8, media.getId());
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update media entry", ex);
        }
    }

    // ---------------------------
    // DELETE
    // ---------------------------
    public void delete(int id) {
        final String sql = "DELETE FROM media WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete media with id=" + id, ex);
        }
    }

    // ---------------------------
    // HELPER: Build Media object
    // ---------------------------

    //  immer wenn SELECT etw findet
    private Media map(ResultSet rs) throws SQLException { //DB → Java-Objekt // rs enthält zeile aus db
        String genresRaw = rs.getString("genres"); // holt wert dr spalte genre aus db
        List<String> genres = (genresRaw == null || genresRaw.isBlank())// leere liste statt null
                ? new ArrayList<>()
                : Arrays.asList(genresRaw.split(","));

        return Media.builder()
                .id(rs.getInt("id")) // liest spalte id aus db
                .mediaType(rs.getString("media_type"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseYear(rs.getInt("release_year"))
                .ageRestriction(rs.getInt("age_restriction"))
                .genres(genres)
                .creatorId(rs.getInt("creator_id"))
                .build();
    } //map() übersetzt genau eine Datenbankzeile in ein Media-Domain-Objekt.

    // ---------------------------
    // HELPER: Set SQL parameters
    // ---------------------------
    private void fillStatement(PreparedStatement ps, Media m) throws SQLException {//Java-Objekt → DB
        ps.setString(1, m.getMediaType());
        ps.setString(2, m.getTitle());
        ps.setString(3, m.getDescription());
        ps.setInt(4, m.getReleaseYear());
        ps.setInt(5, m.getAgeRestriction());
        ps.setString(6, String.join(",", m.getGenres()));
        ps.setInt(7, m.getCreatorId());
    }
}


//Java-Objekt ── fillStatement() ──▶ DB
//DB ── map() ──▶ Java-Objekt