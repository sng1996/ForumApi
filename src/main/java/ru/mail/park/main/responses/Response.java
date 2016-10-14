package ru.mail.park.main.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mail.park.main.ErrorCodes;

/**
 * Created by farid on 13.10.16.
 */
public class Response {

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
        return "{\"code\": 0, \"response\": " + toJson() + '}';
    }
}
