package com.jmd0.events.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.jmd0.events.models.EventEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EventRepository implements EventRepositoryInterface {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<EventEntity> findByOrganizerid(Long organizerid) {
        String sql = "SELECT * FROM events WHERE organizerid = ?";
        Object[] params = {organizerid};
        int[] types = {java.sql.Types.BIGINT};
        return jdbcTemplate.query(sql, params, types, new EventModelRowMapper());
    }

    @Override
    public List<EventEntity> findAll() {
        String sql = "SELECT * FROM events";
        return jdbcTemplate.query(sql, new EventModelRowMapper());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM events WHERE id = ?";
        Object[] params = {id};
        int[] types = {java.sql.Types.BIGINT};
        jdbcTemplate.update(sql, params, types);
    }

    @Override
    public EventEntity save(EventEntity event) {
        if (event.getId() == null) {
            String sql = "INSERT INTO events (name, date, location, organizerid, description) VALUES (?, ?, ?, ?, ?)";
            Object[] params = {event.getName(), event.getDate(), event.getLocation(), event.getOrganizerid(), event.getDescription()};
            int[] types = {java.sql.Types.BIGINT, java.sql.Types.DATE, java.sql.Types.VARCHAR, java.sql.Types.BIGINT, java.sql.Types.VARCHAR};
            jdbcTemplate.update(sql);
            Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", params, types, Long.class);
            event.setId(id);
        } else {
            String sql = "UPDATE events SET name = ?, date = ?, location = ?, organizerid = ?, description = ? WHERE id = ?";
            Object[] params = {event.getName(), event.getDate(), event.getLocation(), event.getOrganizerid(), event.getDescription(), event.getId()};
            int[] types = {java.sql.Types.BIGINT, java.sql.Types.DATE, java.sql.Types.VARCHAR, java.sql.Types.BIGINT, java.sql.Types.VARCHAR, java.sql.Types.BIGINT};
            jdbcTemplate.update(sql, params, types);
        }
        return event;
    }

    @Override
    public EventEntity findById(Long id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        Object[] params = {id};
        int[] types = {java.sql.Types.BIGINT};
        return jdbcTemplate.queryForObject(sql, params, types, new EventModelRowMapper());
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM events WHERE id = ?";
        Object[] params = {id};
        int[] types = {java.sql.Types.BIGINT};
        Integer count = jdbcTemplate.queryForObject(sql, params, types, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public List<EventEntity> findByDescription(String description) { 
        String sql = "SELECT * FROM events WHERE description LIKE '%" + description + "%'";
        return jdbcTemplate.query(sql, new EventModelRowMapper());
    }

    private static class EventModelRowMapper implements RowMapper<EventEntity> {
        @Override
        public EventEntity mapRow(@SuppressWarnings("null") ResultSet rs, int rowNum) throws SQLException {
            EventEntity event = new EventEntity();
            event.setId(rs.getLong("id"));
            event.setName(rs.getString("name"));
            event.setDate(rs.getDate("date"));
            event.setLocation(rs.getString("location"));
            event.setOrganizerid(rs.getString("organizerid"));
            event.setDescription(rs.getString("description"));
            return event;
        }
    }

}
