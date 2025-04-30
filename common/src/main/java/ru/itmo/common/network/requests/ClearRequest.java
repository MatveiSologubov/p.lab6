package ru.itmo.common.network.requests;

public class ClearRequest extends Request {
    public ClearRequest(String[] commandArgs) {
        super("clear");
    }
}
