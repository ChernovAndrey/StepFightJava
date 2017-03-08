package services.rowmaps;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import objects.ObjUser;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Created by andrey on 08.03.17.
 */
public class userMapper implements RowMapper<ObjUser> {
    public ObjUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        ObjUser user = new ObjUser();
        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        return user;
        }
    }
