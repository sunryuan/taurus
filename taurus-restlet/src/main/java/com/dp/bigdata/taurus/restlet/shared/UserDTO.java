package com.dp.bigdata.taurus.restlet.shared;

import java.io.Serializable;

import com.dp.bigdata.taurus.generated.module.User;

/**
 * UserDTO
 * 
 * @author damon.zhu
 */
public class UserDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -271557894670140723L;
    private int id;
    private String name;
    private String mail;
    private String tel;
    private String group;
    
    public UserDTO(){
    }

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

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	
	
	public void setId(int id) {
		this.id = id;
	}

	public User getUser(){
		User user = new User();
		user.setId(id);
		user.setMail(mail);
		user.setName(name);
		user.setTel(tel);
		return user;
	}
    
}
