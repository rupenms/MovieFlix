package com.example.movieflix;

public class MovieData {
    private String backDrop;
    private String poster;
    private String vote;
    private String title;
    private String overview;

    public MovieData(String backDrop,String poster,String vote,String title,String overview){
        this.backDrop = backDrop;
        this.poster = poster;
        this.vote = vote;
        this.title = title;
        this.overview = overview;
    }

    public String getBackDrop(){
        return backDrop;
    }

    public String getPoster(){
        return poster;
    }

    public String getVote(){
        return vote;
    }

    public String getTitle(){
        return title;
    }

    public String getOverview(){
        return overview;
    }
}
