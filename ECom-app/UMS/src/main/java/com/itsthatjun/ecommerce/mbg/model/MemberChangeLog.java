package com.itsthatjun.ecommerce.mbg.model;

import java.util.Date;

public class MemberChangeLog {
    private Integer id;

    private Integer memberId;

    private String updateAction;

    private String changeOperator;

    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }

    public String getChangeOperator() {
        return changeOperator;
    }

    public void setChangeOperator(String changeOperator) {
        this.changeOperator = changeOperator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}