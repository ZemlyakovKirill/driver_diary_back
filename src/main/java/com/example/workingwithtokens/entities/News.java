package com.example.workingwithtokens.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long news_id;

    @Column(name = "title", nullable = false, length = 500)
    @NotNull(message = "Title has to be not null")
    @Size(max = 500, message = "Title has to be less than 500")
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    @NotNull(message = "Description has to be not null")
    @Size(max = 1500, message = "Description has to be less than 1500")
    private String description;

    @Column(name = "img_link", length = 200)
    @Size(max = 200, message = "Image link has to be less than 200")
    private String imgLink;

    @Column(name = "author", length = 100)
    @Size(max = 100, message = "Author has to be less than 100")
    private String author;

    @Column(name = "pub_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @Check(constraints = "pub_date >= CURRENT_DATE")
    @NotNull(message = "Publication Date has to be not null")
    private Date pubDate;

    public News() {
    }

    public News(Long news_id, String title, String description, String imgLink, String author, Date pubDate) {
        this.news_id = news_id;
        this.title = title;
        this.description = description;
        this.imgLink = imgLink;
        this.author = author;
        this.pubDate = pubDate;
    }

    public News(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public News(String title, String description, String imgLink) {
        this.title = title;
        this.description = description;
        this.imgLink = imgLink;
    }

    public News(String title, String description, String imgLink, String author) {
        this.title = title;
        this.description = description;
        this.imgLink = imgLink;
        this.author = author;
    }

    public News(String title, String description, String imgLink, String author, Date pubDate) {
        this.title = title;
        this.description = description;
        this.imgLink = imgLink;
        this.author = author;
        this.pubDate = pubDate;
    }

    public Long getNews_id() {
        return news_id;
    }

    public void setNews_id(Long news_id) {
        this.news_id = news_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "News{" +
                "news_id=" + news_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", author='" + author + '\'' +
                ", pubDate=" + pubDate +
                '}';
    }
}
