package core.api.entity;

import lombok.Data;

@Data
public class User {
	private long id;
	private String userName;
	private String password;
}
