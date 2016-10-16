package ru.mail.park.main.controllers.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.thread.ThreadQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.Request;
import ru.mail.park.main.requests.post.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("OverlyComplexBooleanExpression")
@RestController
public class PostController extends Controller {

    @RequestMapping(path = "/db/api/post/create", method = RequestMethod.POST)
    public ResponseEntity createPost(@RequestBody PostCreationRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        try {
            final Integer forumId = ForumQueries.getForumIdByShortName(body.getForum());
            final Integer userId = UserQueries.getUserIdByEmail(body.getUser());
            final Integer parentId = (body.getParent() != null) ? PostQueries.getPostById(body.getParent()) : null;
            final Integer threadId = body.getThread();

            if (forumId == null    ||
                    userId == null ||
                    threadId == null) {
                return ResponseEntity.ok().body(
                        ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
                );
            }

            body.setId(Database.update(PostQueries.createPostQuery(body, threadId, userId, forumId, parentId)));
            String parentPath = "/";

            if (body.getParent() != null) {
                parentPath = Database.select("SELECT path FROM posts WHERE postID=" + body.getParent(),
                        result -> {
                            if (result.next()) return result.getString("path") + '/';
                            return "/";
                        });
            }

            final String currentPath = String.format("%08d", body.getId());

            Database.update("UPDATE posts SET path='" + parentPath + currentPath +
                    "' WHERE postID="+body.getId());
            System.out.println(body.responsify());

            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND)
            );
        }
    }

    @RequestMapping(path = "/db/api/post/details", method = RequestMethod.GET)
    public ResponseEntity getPostInfo(@RequestParam(value = "related", required = false) ArrayList<String> related,
                                      @RequestParam(value = "post") Integer postId) {
        if (postId == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        try {
            final ObjectMapper mapper = new ObjectMapper();

            final ObjectNode postInfo = PostQueries.getPostInfoById(postId);

            final ObjectNode response = mapper.createObjectNode();

            if (related == null) {
                response.put("code", 0);
                response.set("response", postInfo);
                return ResponseEntity.ok().body(mapper.writeValueAsString(response));
            }

            if (related.contains("user")) postInfo.set("user",
                    UserQueries.getUserInfoByEmail(postInfo.get("user").asText()));

            //FIXME: false stands for not including user into forum (bug-prone)
            if (related.contains("forum")) postInfo.set("forum",
                    ForumQueries.getForumInfoByShortName(postInfo.get("forum").asText(), false));

            if (related.contains("thread")) postInfo.set("thread",
                    ThreadQueries.getThreadInfoById(postInfo.get("thread").asInt()));

            response.put("code", 0);
            response.set("response", postInfo);
            //System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "/db/api/post/list/", method = RequestMethod.GET)
    public ResponseEntity getPostList(@RequestParam(value = "since", required = false) String startDate,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "forum", required = false) String forumShortName,
                                      @RequestParam(value = "thread", required = false) Integer threadId) {

        if (forumShortName == null && threadId == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        if (order == null) order = "desc";

        try {
            final ObjectMapper mapper = new ObjectMapper();

            ArrayNode postList;

            if (threadId != null) {
                postList = PostQueries.getPostList(threadId, limit, startDate, order, "plain");
            } else {
                postList = PostQueries.getPostList(forumShortName, limit, startDate, order);
            }

            final ObjectNode response = mapper.createObjectNode();

            response.put("code", 0);
            response.set("response", postList);

            System.out.println(mapper.writeValueAsString(response));

            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/post/remove/", method = RequestMethod.POST)
    public ResponseEntity removePost(@RequestBody PostRemovalRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = PostQueries.createPostRemovalQuery(body.getPost());
        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/post/restore/", method = RequestMethod.POST)
    public ResponseEntity restorePost(@RequestBody PostRestoreRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = PostQueries.createPostRestoreQuery(body.getPost());
        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/post/update/", method = RequestMethod.POST)
    public ResponseEntity updatePost(@RequestBody PostUpdateRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = PostQueries.createPostUpdateQuery(body);
        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/post/vote/", method = RequestMethod.POST)
    public ResponseEntity voteForPost(@RequestBody PostVoteRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = PostQueries.createPostVoteQuery(body);
        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }
}
