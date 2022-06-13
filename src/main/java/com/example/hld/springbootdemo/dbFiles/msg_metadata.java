package com.example.hld.springbootdemo.dbFiles;


import java.io.Serializable;
import java.sql.Time;
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

    public String getPrior_msg_id() {
        return prior_msg_id;
    }

    public void setPrior_msg_id(String prior_msg_id) {
        this.prior_msg_id = prior_msg_id;
    }

    public String getMdn_id() {
        return mdn_id;
    }

    public void setMdn_id(String mdn_id) {
        this.mdn_id = mdn_id;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getIs_resend() {
        return this.is_resend;
    }

    public void setIs_resend(String is_resend) {
        this.is_resend = is_resend;
    }

    public Long getResend_count() {
        return this.resend_count;
    }

    public void setResend_count(Long resend_count) {
        this.resend_count = resend_count;
    }

    public String getSender_id() {
        return this.sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return this.receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSignature_algorithm() {
        return this.signature_algorithm;
    }

    public void setSignature_algorithm(String signature_algorithm) {
        this.signature_algorithm = signature_algorithm;
    }

    public String getEncryption_algorithm() {
        return this.encryption_algorithm;
    }

    public void setEncryption_algorithm(String encryption_algorithm) {
        this.encryption_algorithm = encryption_algorithm;
    }

    public String getCompression() {
        return this.compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public String getFile_name() {
        return this.file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getContent_type() {
        return this.content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_transfer_encoding() {
        return this.content_transfer_encoding;
    }
    public void setContent_transfer_encoding(String content_transfer_encoding) {
        this.content_transfer_encoding = content_transfer_encoding;
    }

    public String getMdn_mode() {
        return this.mdn_mode;
    }

    public void setMdn_mode(String mdn_mode) {
        this.mdn_mode = mdn_mode;
    }

    public String getMdn_response() {
        return this.mdn_response;
    }

    public void setMdn_response(String mdn_response) {
        this.mdn_response = mdn_response;
    }

    public String getState_msg() {
        return this.state_msg;
    }

    public void setState_msg(String state_msg) {
        this.state_msg = state_msg;
    }

    public Timestamp getCreate_dt() {
        return this.create_dt;
    }

    public void setCreate_dt(Timestamp create_dt) {
        this.create_dt = create_dt;
    }

    public Timestamp getUpdate_dt() {
        return this.update_dt;
    }

    public void setUpdate_dt(Timestamp update_dt) {
        this.update_dt = update_dt;
    }

    @Override
    public String toString() {
        return "Info{" +
                "id=" + id +
                ", msg_id='" + msg_id + '\'' +
                '}';
    }
}

