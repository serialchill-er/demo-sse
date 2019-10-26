package ch.rasc.eventbus.demo;

public class Person {

	public Person(int nextInt, String string) {
		// TODO Auto-generated constructor stub
		this.id = nextInt;
		this.name = string;
	}

	private int id;
	private String name;

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person() {
		super();
	}


}
