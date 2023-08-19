package com.example.sammwangi.DAOs;

import java.util.List;

public class Page<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    // Other properties and getters/setters

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}


