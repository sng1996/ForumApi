package ru.mail.park.main.controllers.tools;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.main.database.Database;

import java.sql.SQLException;

/**
 * Created by farid on 13.10.16.
 */

@RestController
public class ToolsController {

    @RequestMapping(path = "/db/api/clear")
    public ResponseEntity cleaDb() {
        try {
            Database.update("DELETE FROM users");
            Database.update("DELETE FROM forums");
            Database.update("DELETE FROM threads");
            Database.update("DELETE FROM posts");
            Database.update("UPDATE counters SET count=0");
        }
        catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
        return ResponseEntity.ok().body("{\"code\": 0, \"response\": \"OK\"}");
    }
}
