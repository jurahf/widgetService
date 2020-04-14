package com.testTask.storage;

import com.testTask.domain.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

public class H2Storage
    implements IStorage<Widget> {

    @Autowired
    JdbcTemplate jtm;

    @Override
    public int save(Widget ent)
        throws IllegalArgumentException {
        if (ent == null)
            throw new IllegalArgumentException("Entity for saving can't be empty.");

        if (ent.isSaved()) {
            return update(ent);
        }
        else {
            return insert(ent);
        }
    }

    private int insert(Widget ent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO Widget (x, y, z_index, width, height, lastModificationDateTime) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatementCreatorFactory pscf =
                new PreparedStatementCreatorFactory(sql, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP);
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(ent.getX(), ent.getY(), ent.getZ_index(), ent.getWidth(), ent.getHeight(), ent.getLastModificationDateTime()));

        jtm.update(psc, keyHolder);

        return (int)keyHolder.getKey();
    }

    private int update(Widget ent) {
        String sql = "UPDATE Widget SET x = ?, y = ?, z_index = ?, width = ?, height = ?, lastModificationDateTime = ? WHERE id = ?";
        return jtm.update(sql, ent.getX(), ent.getY(), ent.getZ_index(), ent.getWidth(), ent.getHeight(), ent.getLastModificationDateTime(), ent.getId());
    }

    @Override
    public Widget get(int id) {
        String sql = "SELECT * FROM Widget WHERE id = ?";

        var list = jtm.query(sql, new Object[] { id }, new BeanPropertyRowMapper<>(Widget.class));
        return list.stream().findFirst().orElse(null);
    }

    @Override
    public List<Widget> getAll() {
        String sql = "SELECT * FROM Widget";

        return jtm.query(sql, new BeanPropertyRowMapper<>(Widget.class));
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Widget WHERE id = ?";
        int rowsAffected = jtm.update(sql, id);

        return rowsAffected == 1;
    }
}
