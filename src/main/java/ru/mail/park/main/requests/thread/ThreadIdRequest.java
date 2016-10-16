package ru.mail.park.main.requests.thread;

import ru.mail.park.main.requests.Request;

/**
 * Created by farid on 16.10.16.
 */
public class ThreadIdRequest extends Request {
    int thread;

    public ThreadIdRequest(int thread) {
        this.thread = thread;
    }

    public ThreadIdRequest() {
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}
