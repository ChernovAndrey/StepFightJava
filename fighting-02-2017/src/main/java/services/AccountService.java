package services;

import objects.HttpStatus;
import objects.ObjUser;
import org.eclipse.jetty.util.Fields;
import org.json.JSONArray;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import services.rowmaps.userMapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by Denis on 21.02.2017.
 */
@Service
public class AccountService{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private AtomicLong IDGEN;
    public interface Callback {
        void onSuccess(String status);

        void onError(String status);
    }
    public AccountService(@NotNull JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    public interface CallbackWithUser {
        void onSuccess(String status, ObjUser objUser);

        void onError(String status);
    }

    private Map<String, ObjUser> db;

   /* public AccountService() {
        db = new HashMap<>();
        IDGEN = new AtomicLong(0);
    }*/

    public void register(ObjUser objUser, Callback callback){
       /* objUser.setId(String.valueOf(IDGEN.getAndIncrement()));
        if (db.put(objUser.getLogin(), objUser) == null) {
            callback.onSuccess(new HttpStatus().getOk());
        } else {
            IDGEN.getAndDecrement();
            callback.onError(new HttpStatus().getForbidden());
        }*/
        try{
                jdbcTemplate.update(
                    "INSERT INTO users (login,password) values(?,?)", objUser.getLogin(),objUser.getPassword());
                }
                catch (Exception e){
                callback.onError(new HttpStatus().getForbidden());
                return;
                }
                callback.onSuccess(new HttpStatus().getOk());
    }

    public void login(ObjUser objUser, CallbackWithUser callbackWithUser) {
       /* final String login = objUser.getLogin();
        final String password = objUser.getPassword();
        if (db.get(login) != null && db.get(login).getPassword().equals(password)) {
            callbackWithUser.onSuccess(new HttpStatus().getOk(), db.get(login));
        } else {
            callbackWithUser.onError(new HttpStatus().getNotFound());
        }*/

        String SQL = "select * from users where (login,password) = (?,?)";
        ObjUser user=null;
        try {
            user = jdbcTemplate.queryForObject(SQL,
                    new Object[]{objUser.getLogin(), objUser.getPassword()}, new userMapper());
        }
        catch(Exception e){
            callbackWithUser.onError(new HttpStatus().getNotFound());
            return;
        }
        callbackWithUser.onSuccess(new HttpStatus().getOk(), user);
    }

    public void update(ObjUser newObjUser, CallbackWithUser callbackWithUser) {
        /*final String login = newObjUser.getLogin();
        final String newlogin = newObjUser.getNewlogin();
        if (login != null && newlogin != null && db.get(login) != null) {
            final ObjUser objUser = db.get(login);
            objUser.setLogin(newlogin);
            if (db.remove(login) != null && db.put(newlogin, objUser) == null) {
                callbackWithUser.onSuccess(new HttpStatus().getOk(), objUser);
            } else {
                callbackWithUser.onError(new HttpStatus().getBadRequest());
            }
        } else {
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }*/

           int rownum= jdbcTemplate.update(
                    "Update users set login= ? where login = ?", newObjUser.getNewlogin(),newObjUser.getLogin());

        if(rownum==0){
            callbackWithUser.onError(new HttpStatus().getBadRequest());
            return;
        }
        callbackWithUser.onSuccess(new HttpStatus().getOk(), newObjUser);

    }

    public void changePass(ObjUser objUser, CallbackWithUser callbackWithUser) {
       /* final String login = objUser.getLogin();
        final String password = objUser.getPassword();
        final String newpassword = objUser.getNewpassword();
        if (login != null && db.get(login) != null && password != null && newpassword != null) {
            final ObjUser currObjUser = db.get(login);
            if (currObjUser.getPassword().equals(password)) {
                currObjUser.setPassword(newpassword);
                if (db.replace(login, currObjUser) != null) {
                    callbackWithUser.onSuccess(new HttpStatus().getOk(), currObjUser);
                } else {
                    callbackWithUser.onError(new HttpStatus().getBadRequest());
                }
            } else {
                callbackWithUser.onError(new HttpStatus().getForbidden());
            }
        } else {
            callbackWithUser.onError(new HttpStatus().getBadRequest());
        }*/
        String SQL = "Update users set password= ? where (login,password) =(?,?)";
        int rownum= jdbcTemplate.update(
                    SQL, objUser.getNewpassword(),objUser.getLogin(),objUser.getPassword());

        if (rownum==0){
            callbackWithUser.onError(new HttpStatus().getBadRequest());
            return;
        }
        callbackWithUser.onSuccess(new HttpStatus().getOk(), objUser);
    }

    public JSONArray getLeaders(){
        final JSONArray jsonArray = new JSONArray();
        for(Map.Entry<String, ObjUser> entry : db.entrySet()) {
            jsonArray.put(entry.getValue().getJson());
        }
        return jsonArray;
    }
}

