package ru.mail.park.main.controllers.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.main.ErrorCodes;
import ru.mail.park.main.controllers.Controller;
import ru.mail.park.main.controllers.forum.ForumQueries;
import ru.mail.park.main.controllers.post.PostQueries;
import ru.mail.park.main.controllers.user.UserQueries;
import ru.mail.park.main.database.Database;
import ru.mail.park.main.requests.thread.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farid on 12.10.16.
 */
@SuppressWarnings("Duplicates")
@RestController
public class ThreadController extends Controller {

    @RequestMapping(path = "/db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity createThread(@RequestBody ThreadCreationRequest body) {

        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
        }

        try {
            final Integer forumId = ForumQueries.getForumIdByShortName(body.getForum());
            final Integer userId = UserQueries.getUserIdByEmail(body.getUser());

            body.setId(Database.update(ThreadQueries.createThreadQuery(body, userId, forumId)));

            //System.out.println(body.responsify());

            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "db/api/thread/details/", method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestParam(value = "related", required = false) ArrayList<String> related,
                                      @RequestParam("thread") Integer threadId) {

        if (threadId == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        try {
            final ObjectMapper mapper = new ObjectMapper();

            final ObjectNode threadInfo = ThreadQueries.getThreadInfoById(threadId);
            final ObjectNode response = mapper.createObjectNode();

            if (threadInfo == null) {
                return ResponseEntity.ok().body(
                        ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));
            }

            if (related == null) {
                response.put("code", 0);
                response.set("response", threadInfo);
                return ResponseEntity.ok().body(mapper.writeValueAsString(response));
            }

            if (related.contains("user")) threadInfo.set("user",
                    UserQueries.getUserInfoByEmail(threadInfo.get("user").asText()));

            //FIXME: false stands for not including user into forum (bug-prone)
            if (related.contains("forum")) threadInfo.set("forum",
                    ForumQueries.getForumInfoByShortName(threadInfo.get("forum").asText(), false));

            response.put("code", 0);
            response.set("response", threadInfo);
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

    @RequestMapping(path = "/db/api/thread/list/", method = RequestMethod.GET)
    public ResponseEntity getThreadList(@RequestParam(value = "since", required = false) String startDate,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "forum", required = false) String forumShortName,
                                      @RequestParam(value = "user", required = false) String userEmail) {

        if (forumShortName == null && userEmail == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        if (order == null) order = "desc";

        try {
            final ObjectMapper mapper = new ObjectMapper();

            ArrayNode postList;

            Map<String, String> threadSource = new HashMap<String, String>();

            if (userEmail != null) {
                threadSource.put("user", userEmail);
                postList = ThreadQueries.getThreadList(threadSource, limit, startDate, order, null);
            } else {
                threadSource.put("forum", forumShortName);
                postList = ThreadQueries.getThreadList(threadSource, limit, startDate, order, null);
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

    @RequestMapping(path="/db/api/thread/close/", method = RequestMethod.POST)
    public ResponseEntity closeThread(@RequestBody ThreadIdRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = ThreadQueries.createThreadCloseQuery(body.getThread());
        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/thread/open/", method = RequestMethod.POST)
    public ResponseEntity openThread(@RequestBody ThreadIdRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = ThreadQueries.createThreadOpenQuery(body.getThread());
        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/thread/remove/", method = RequestMethod.POST)
    public ResponseEntity removeThread(@RequestBody ThreadIdRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String threadRemovalQuery = ThreadQueries.createThreadRemovalQuery(body.getThread());
        String relatedPostsRemovalQuery = ThreadQueries.createRelatedPostsRemovalQuery(body.getThread());
        try {
            Database.update(relatedPostsRemovalQuery);
            Database.update(threadRemovalQuery);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/thread/restore/", method = RequestMethod.POST)
    public ResponseEntity restoreThread(@RequestBody ThreadIdRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = ThreadQueries.createThreadRestoreQuery(body.getThread());
        String relatedPostsRestoreQuery = ThreadQueries.createRelatedPostsRestoreQuery(body.getThread());
        try {
            Database.update(query);
            Database.update(relatedPostsRestoreQuery);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "/db/api/thread/listPosts/", method = RequestMethod.GET)
    public ResponseEntity getPostList(@RequestParam(value = "since", required = false) String startDate,
                                      @RequestParam(value = "sort", required = false) String sortType,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "order", required = false) String order,
                                      @RequestParam(value = "thread", required = false) Integer threadId) {

        if (threadId == null) return ResponseEntity.ok().body(
                ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST));

        if (order == null) order = "desc";

        try {
            final ObjectMapper mapper = new ObjectMapper();

            ArrayNode postList = null;

            postList = PostQueries.getPostList(threadId, limit, startDate, order, sortType, null);

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

    @RequestMapping(path="/db/api/thread/subscribe/", method = RequestMethod.POST)
    public ResponseEntity subscribeToThread(@RequestBody ThreadSubscriptionRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        try {
            String query = ThreadQueries.createSubscriptionQuery(body);

            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/thread/unsubscribe/", method = RequestMethod.POST)
    public ResponseEntity unsubscribeFromThread(@RequestBody ThreadSubscriptionRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        try {
            String query = ThreadQueries.createUnsubscriptionQuery(body);

            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/thread/update/", method = RequestMethod.POST)
    public ResponseEntity updateThread(@RequestBody ThreadUpdateRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        String query = ThreadQueries.createThreadUpdateQuery(body);

        try {
            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path="/db/api/thread/vote/", method = RequestMethod.POST)
    public ResponseEntity voteForThread(@RequestBody ThreadVoteRequest body) {
        if (!validator.validate(body).isEmpty()) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.INCORRECT_REQUEST)
            );
        }

        try {
            String query = ThreadQueries.createThreadVoteQuery(body);

            Database.update(query);
            return ResponseEntity.ok().body(body.responsify());
        } catch (SQLException ex) {
            return ResponseEntity.ok().body(
                    ErrorCodes.codeToJson(ErrorCodes.OBJECT_NOT_FOUND));
        }
    }

}
