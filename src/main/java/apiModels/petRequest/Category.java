package apiModels.petRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Category{

	@JsonProperty("name")
	private String name;

	@JsonProperty("id")
	private int id;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Category category)) return false;
		return getId() == category.getId() && getName().equals(category.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getId());
	}
}