package ru.itmo.common.network.requests;

public class ShowRequest extends Request {
    public ShowRequest(String[] args) {
        super("show", args);
    }
}
