package com.nowinski.kamil.flighttracker.Services;

public interface AsyncResponse<T> {
    void processFinish(T response);
}
