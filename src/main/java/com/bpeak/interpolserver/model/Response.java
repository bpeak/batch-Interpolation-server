package com.bpeak.interpolserver.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Response {
    private final List<Move> moves;
    private final List<String> leftUserIds;

    public Response(List<Move> moveList, List<String> leftUserIds) {
        this.moves = moveList;
        this.leftUserIds = leftUserIds;
    }
}
