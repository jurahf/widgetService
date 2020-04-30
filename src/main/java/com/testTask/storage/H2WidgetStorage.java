package com.testTask.storage;

import com.testTask.domain.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.NotSupportedException;
import javax.transaction.Transaction;
import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class H2WidgetStorage // сделать бы его тоже Generic...
    implements IStorage<Widget> {

    private List<PreparedStatementCreator> preparedQueries = new ArrayList<>();

    @Autowired
    JdbcTemplate jtm;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jtm = jdbcTemplate;
    }

    private final AtomicInteger idGenerator = new AtomicInteger();
    private void setEntityId(Widget entity) {
        int id = idGenerator.incrementAndGet();
        entity.setId(id);
    }

    private PreparedStatementCreator CreateInsertStatement(Widget ent) {
        String sql = "INSERT INTO Widget (id, x, y, z_index, width, height, lastModificationDateTime) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatementCreatorFactory pscf =
                new PreparedStatementCreatorFactory(sql, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP);
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(ent.getId(), ent.getX(), ent.getY(), ent.getZ_index(), ent.getWidth(), ent.getHeight(), ent.getLastModificationDateTime()));

        return psc;
    }

    private PreparedStatementCreator CreateUpdateStatement(Widget ent) {
        String sql = "UPDATE Widget SET x = ?, y = ?, z_index = ?, width = ?, height = ?, lastModificationDateTime = ? WHERE id = ?";

        PreparedStatementCreatorFactory pscf =
                new PreparedStatementCreatorFactory(sql, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.TIMESTAMP, Types.INTEGER);
        pscf.setReturnGeneratedKeys(true);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(
                Arrays.asList(ent.getX(), ent.getY(), ent.getZ_index(), ent.getWidth(), ent.getHeight(), ent.getLastModificationDateTime(), ent.getId()));

        return psc;
    }

    private PreparedStatementCreator CreateDeleteStatement(int id) {
        String sql = "DELETE FROM Widget WHERE id = ?";

        PreparedStatementCreatorFactory pscf =
                new PreparedStatementCreatorFactory(sql, Types.INTEGER);
        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Arrays.asList(id));

        return psc;
    }

    private int insert(Widget ent) {
        setEntityId(ent);
        PreparedStatementCreator psc = CreateInsertStatement(ent);
        jtm.update(psc);
        return ent.getId();
    }

    private int update(Widget ent) {
        PreparedStatementCreator psc = CreateUpdateStatement(ent);
        jtm.update(psc);
        return ent.getId();
    }

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
        PreparedStatementCreator psc = CreateDeleteStatement(id);
        int rowsAffected = jtm.update(psc);

        return rowsAffected > 0;
    }

    @Override
    public void addChanges(Widget w, ChangeKind change) {
        PreparedStatementCreator psc = null;

        if (change == ChangeKind.SAVE) {
            if (w.isSaved()) {
                psc = CreateUpdateStatement(w);
            }
            else {
                setEntityId(w);
                psc = CreateInsertStatement(w);
            }
        }
        else {
            psc = CreateDeleteStatement(1);
        }

        preparedQueries.add(psc);
    }

    @Override
    @Transactional
    public void commitAllChanges() {
        for (var psc : preparedQueries) {
            // batch, похоже, умеет только с одним и тем же sql и разными параметрами. А разные sql не умеет.
            // поэтому используем @Transactional
            jtm.update(psc);
        }

        preparedQueries = new ArrayList<>();
    }

}
