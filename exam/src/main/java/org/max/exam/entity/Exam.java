package org.max.exam.entity;

import java.io.Serializable;
import java.util.List;

public class Exam implements Serializable {

	private static final long serialVersionUID = -8332719597645165088L;

    private int id;
    private String username;
    private String score;
    List<Item> itemList;
    
	public Exam() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
}