package sample.controllers;

import objects.HttpStatus;
import objects.ObjUser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import services.AccountService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Denis on 21.02.2017.
 */

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final String SESSIONKEY = "user";
    private final AccountService accountService;
    UserController(JdbcTemplate jdbcTemplate){
        this.accountService= new AccountService(jdbcTemplate);
    }

    /*public UserController() {
        this.accountService = new AccountService();
    }*/

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String loginUser(@RequestBody ObjUser body, HttpSession httpSession,
                            @RequestHeader(value = "Origin") String domain,
                            HttpServletResponse httpServletResponse) {
        final JSONObject answer = new JSONObject();
        accountService.login(body, new AccountService.CallbackWithUser() {
            @Override
            public void onSuccess(String status, ObjUser objUser) {
                answer.put("status", status);
                answer.put("user", objUser.getJson());
                httpSession.setAttribute(SESSIONKEY, objUser);
            }

            @Override
            public void onError(String status) {
                answer.put("status", status);
            }
        });
        return answer.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/signup", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String registerUser(@RequestBody ObjUser body,
                               @RequestHeader(value = "Origin") String domain,
                               HttpServletResponse httpServletResponse) {
        final JSONObject answer = new JSONObject();
        accountService.register(body, new AccountService.Callback() {
            @Override
            public void onSuccess(String status) {
                answer.put("status", status);
            }

            @Override
            public void onError(String status) {
                answer.put("status", status);
            }
        });
        return answer.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/get", method = RequestMethod.GET, produces = "application/json")
    public String getUser(@RequestHeader(value = "Origin") String domain,
                          HttpSession httpSession,
                          HttpServletResponse httpServletResponse) {

        final JSONObject answer = new JSONObject();
        final ObjUser objUser = (ObjUser) httpSession.getAttribute(SESSIONKEY);
        if (objUser != null) {
            answer.put("status", new HttpStatus().getOk());
            answer.put("user", objUser.getJson());
        } else {
            answer.put("status", new HttpStatus().getUnauthorized());
        }
        return answer.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json",
            consumes = "application/json")
    public String updateUser(@RequestBody ObjUser body,
                             @RequestHeader(value = "Origin") String domain,
                             HttpSession httpSession) {
        final JSONObject answer = new JSONObject();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            accountService.update(body, new AccountService.CallbackWithUser() {
                @Override
                public void onSuccess(String status, ObjUser objUser) {
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, objUser);
                    answer.put("status", status);
                }

                @Override
                public void onError(String status) {
                    answer.put("status", status);
                }
            });
        } else {
            answer.put("status", new HttpStatus().getUnauthorized());
        }
        return answer.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/changepass", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String changeUserPass(@RequestBody ObjUser body,
                                 @RequestHeader(value = "Origin") String domain,
                                 HttpSession httpSession, HttpServletResponse httpResponse) {
        final JSONObject answer = new JSONObject();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            accountService.changePass(body, new AccountService.CallbackWithUser() {
                @Override
                public void onSuccess(String status, ObjUser objUser) {
                    httpSession.removeAttribute(SESSIONKEY);
                    httpSession.setAttribute(SESSIONKEY, objUser);
                    answer.put("status", status);
                }

                @Override
                public void onError(String status) {
                    answer.put("status", status);
                }
            });
        } else {
            answer.put("status", new HttpStatus().getUnauthorized());
        }
        return answer.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/logout", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String logoutUser(@RequestHeader(value = "Origin") String domain,
                             HttpSession httpSession, HttpServletResponse httpResponse) {
        final JSONObject answer = new JSONObject();
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.removeAttribute(SESSIONKEY);
            answer.put("status", new HttpStatus().getOk());
        } else {
            answer.put("status", new HttpStatus().getBadRequest());
        }
        return answer.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
    @RequestMapping(path = "/leaders", method = RequestMethod.GET, produces = "application/json")
    public String getLeaders(@RequestHeader(value = "Origin") String domain,
                             HttpSession httpSession, HttpServletResponse httpResponse) {
        final JSONObject answer = new JSONObject();
        try {
            answer.put("leaders", accountService.getLeaders());
            answer.put("status", new HttpStatus().getOk());
        } catch (JSONException e){
            answer.put("status", new HttpStatus().getBadRequest());
        }
        return answer.toString();
    }
}
