package com.nisum.userws.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.text.ParseException;
import java.util.Date;

@Getter
@Setter
@ToString
@MappedSuperclass
public class Auditory {

    @Column(name = "CREATED")
    private Date created;

    @Column(name = "MODIFIED")
    private Date modified;

    @PrePersist
    protected void onCreate() throws ParseException {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() throws ParseException {
        modified = new Date();
    }

}
