package ru.mail.park.main.controllers.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.post.PostQueries;
import ru.mail.park.main.controllers.tools.ToolsQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.user.UserCreationRequest;
import ru.mail.park.main.requests.user.UserFollowRequest;
import ru.mail.park.main.requests.user.UserUpdateRequest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("Duplicates")
@RestController
public class UserController extends Controller {

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity createUser (@RequestBody UserCreationRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            body.setId(Database.update(UserQueries.createUserQuery(body)));

            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.USER_ALREADY_EXISTS));
        }
    }

    @RequestMapping(path = "db/api/user/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam("user") String email) {

        ObjectMapper mapper = new ObjectMapper();

        if (email.isEmpty()) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        try {
            final ObjectNode userInfo = UserQueries.getUserInfoByEmail(email);

            ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userInfo);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "/db/api/user/follow", method = RequestMethod.POST)
    public ResponseEntity followUser (@RequestBody UserFollowRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            Database.update(UserQueries.createFollowQuery(body));

            final ObjectNode userInfo = UserQueries.getUserInfoByEmail(body.getFollower());

            ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userInfo);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "db/api/user/listFollowers/", method = RequestMethod.GET)
    public ResponseEntity getFolloweeList(@RequestParam(value = "since", required = false) Integer startId,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "user", required = false) String userEmail) {

        if (userEmail == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        if (order == null) order = "desc";

        try {
            final ObjectMapper mapper = new ObjectMapper();

            ArrayNode userList;

            Map<String, String> userSource = new HashMap<>();

            userSource.put("followees", userEmail);

            userList = UserQueries.getUserList(userSource, order, limit, startId);

            final ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userList);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "db/api/user/listFollowing/", method = RequestMethod.GET)
    public ResponseEntity getFollowerList(@RequestParam(value = "since", required = false) Integer startId,
                                           @RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "order", required = false) String order,
                                           @RequestParam(value = "user", required = false) String userEmail) {

        if (userEmail == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        if (order == null) order = "desc";

        try {
            final ObjectMapper mapper = new ObjectMapper();

            ArrayNode userList;

            Map<String, String> userSource = new HashMap<>();

            userSource.put("followers", userEmail);

            userList = UserQueries.getUserList(userSource, order, limit, startId);

            final ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userList);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "db/api/user/listPosts/", method = RequestMethod.GET)
    public ResponseEntity getPostList(@RequestParam(value = "since", required = false) String startDate,
                                           @RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "order", required = false) String order,
                                           @RequestParam(value = "user", required = false) String userEmail) {

        if (userEmail == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        if (order == null) order = "desc";

        try {
            final ObjectMapper mapper = new ObjectMapper();

            ArrayNode userList;

            Map<String, String> postSource = new HashMap<>();

            postSource.put("user", userEmail);

            userList = PostQueries.getPostList(postSource, limit, startDate, order, null);

            final ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userList);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "/db/api/user/unfollow", method = RequestMethod.POST)
    public ResponseEntity unfollowUser (@RequestBody UserFollowRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            Database.update(UserQueries.createUnfollowQuery(body));

            final ObjectNode userInfo = UserQueries.getUserInfoByEmail(body.getFollower());

            ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userInfo);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "/db/api/user/updateProfile", method = RequestMethod.POST)
    public ResponseEntity updateUser (@RequestBody UserUpdateRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            Database.update(UserQueries.createUpdateQuery(body));

            final ObjectNode userInfo = UserQueries.getUserInfoByEmail(body.getUser());

            ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", userInfo);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR));
        }
    }
}
