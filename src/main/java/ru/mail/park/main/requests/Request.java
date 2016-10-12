package ru.mail.park.main.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mail.park.main.ErrorCodes;

/**
 * Created by farid on 12.10.16.
 */
public class Request {

    public String toJson() {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
            return ErrorCodes.codeToJson(ErrorCodes.UNKNOWN_ERROR);
        }
    }

    public String responsify() {
        return "{\"\": 0, \"response\": " + toJson();
    }
}
