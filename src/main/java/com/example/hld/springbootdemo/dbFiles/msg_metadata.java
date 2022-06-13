package com.example.hld.springbootdemo.dbFiles;


import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;

@Entity
public class msg_metadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String msg_id;
    private String prior_msg_id;
    private String mdn_id;
    private String direction;
    private String is_resend;
    private Long resend_count;
    private String sender_id;
    private String receiver_id;
    private String status;
    private String state;
    private String signature_algorithm;
    private String encryption_algorithm;
    private String compression;
    private String file_name;
    private String content_type;
    private String content_transfer_encoding;
    private String mdn_mode;
    private String mdn_response;
    private String state_msg;
    private Timestamp create_dt;
    private Timestamp update_dt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    @Override
    public String toString() {
        return "Info{" +
                "id=" + id +
                ", msg_id='" + msg_id + '\'' +
                '}';
    }
}

