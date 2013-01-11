package com.dp.bigdata.taurus.restlet.shared;

/**
 * UserDTO
 * 
 * @author damon.zhu
 */
public class UserDTO {

    private final int id;
    private String name;
    private String mail;
    private String tel;

    public UserDTO(int id, String name, String mail, String tel) {
        super();
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.tel = tel;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getTel() {
        return tel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
