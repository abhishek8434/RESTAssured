package test;

public class POJO_PostRequest {
	
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String[] getGrades() {
		return grades;
	}
	public void setGrades(String[] grades) {
		this.grades = grades;
	}
	public String getYearsOld() {
		return yearsOld;
	}
	public void setYearsOld(String yearsOld) {
		this.yearsOld = yearsOld;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	String active;
	String grades[];
	String yearsOld;
	String color;

}
