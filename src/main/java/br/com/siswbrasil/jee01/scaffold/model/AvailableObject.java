package br.com.siswbrasil.jee01.scaffold.model;



import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AvailableObject {
	
	private String id;
	private String name;
	private String type;
	private String path;
	private List<AvaliableProperties> properties = new ArrayList<AvailableObject.AvaliableProperties>();	
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class AvaliableProperties {
		private String type;
		private String name;		
		private Boolean isId;		
		private String value;   
	}

}
