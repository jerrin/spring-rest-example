package com.restsample.exercise.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.restsample.exercise.domain.UserRealm;

@Repository
public class UserRealmDAO {

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public UserRealm find(int id) {

		log.debug("get user realm for id: " + id);

		String sql = "select id, name, description, encryptionkey from UserRealm where id = :id";

		SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);

		log.debug("get user realm query: " + sql);

		UserRealm userRealm = jdbcTemplate.queryForObject(sql, namedParameters,
				new UserRealmMapper());

		return userRealm;
	}

	public UserRealm save(UserRealm userRealm) {

		log.debug("save user realm: " + userRealm);

		String sqlFindUserRealm = "select count(*) from UserRealm where name = :name";

		SqlParameterSource params = new MapSqlParameterSource("name",
				userRealm.getName());

		int rowCount = jdbcTemplate.queryForInt(sqlFindUserRealm, params);

		if (rowCount == 0) {

			String sql = "INSERT INTO UserRealm ( id, name, description, encryptionkey ) VALUES ( :id, :name, :description, :key)";

			SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(
					userRealm);

			log.debug("get user realm query: " + sql);

			jdbcTemplate.update(sql, namedParameters);

			int lastInsertId = this.getInsertedID();
			
			userRealm = find(lastInsertId);

		}

		return userRealm;
	}

	protected int getInsertedID() {
		return jdbcTemplate.queryForInt("SELECT LAST_INSERT_ID()",
				new MapSqlParameterSource());
	}

	private class UserRealmMapper implements ParameterizedRowMapper<UserRealm> {
		public UserRealm mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserRealm userRealm = new UserRealm(rs.getInt("id"),
					rs.getString("name"), rs.getString("description"),
					rs.getString("encryptionkey"));
			return userRealm;
		}
	}

}
