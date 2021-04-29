package www.example.getsocial.Models;

import java.util.ArrayList;

public class Post {
    private String text;
    private User createdBy;
    private Long createdAt;
    private ArrayList<String> likedBy=new ArrayList<>();

    public Post() {
    }


    public Post(String text, User createdBy, Long createdAt, ArrayList<String> likedBy) {
        this.text = text;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.likedBy = likedBy;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }
}
