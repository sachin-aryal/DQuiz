package org.sss.dquiz.model;

/**
 * Created by iam on 1/13/17.
 */

public class User {


    private String userId;
    private String name;

    public User(String userId,String name){
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
