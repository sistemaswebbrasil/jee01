package br.com.siswbrasil.jee01.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Getter;
import lombok.Setter;

//@Embeddable
@MappedSuperclass
@Getter
@Setter
public class Audit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = true, columnDefinition = "datetime")
	private LocalDateTime createAt ;

	@Column(nullable = true, columnDefinition = "datetime")
	private LocalDateTime updateAt ;

	@PrePersist
	private void onCreate() {
		createAt = LocalDateTime.now();  
	}

	@PreUpdate
	private void onUpdate() {
		updateAt = LocalDateTime.now();
	}

}
