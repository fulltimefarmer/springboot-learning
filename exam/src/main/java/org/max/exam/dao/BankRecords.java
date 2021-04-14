package org.max.exam.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Max zhou
 * @date: March 8th, 2021
 */
public class BankRecords implements Serializable {

	private static final long serialVersionUID = 2917933893017824641L;
	
	// setup static objects for IO processing
	// array of BankRecords objects
	static protected BankRecords robjs[] = new BankRecords[600];
	// arraylist to hold spreadsheet rows & columns
	private static ArrayList<List<String>> array = new ArrayList<>();

	// instance fields
	private String id;
	private int age;
	private String sex;
	private String region;
	private double income;
	private String married;
	private int children;
	private String car;
	private String saveAct;
	private String currentAct;
	private String mortgage;
	private String pep;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public String getMarried() {
		return married;
	}

	public void setMarried(String married) {
		this.married = married;
	}

	public int getChildren() {
		return children;
	}

	public void setChildren(int children) {
		this.children = children;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getSaveAct() {
		return saveAct;
	}

	public void setSaveAct(String saveAct) {
		this.saveAct = saveAct;
	}

	public String getCurrentAct() {
		return currentAct;
	}

	public void setCurrentAct(String currentAct) {
		this.currentAct = currentAct;
	}

	public String getMortgage() {
		return mortgage;
	}

	public void setMortgage(String mortgage) {
		this.mortgage = mortgage;
	}

	public String getPep() {
		return pep;
	}

	public void setPep(String pep) {
		this.pep = pep;
	}
}
