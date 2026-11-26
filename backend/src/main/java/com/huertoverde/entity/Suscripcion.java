package com.huertoverde.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Correo que se suscribió a las ofertas desde el formulario "Mantente en contacto" del footer
@Entity
@Table(name = "suscripciones")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Suscripcion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "Ingresa un correo válido, por ejemplo nombre@gmail.com")
	@Size(max = 100, message = "El correo no puede superar los 100 caracteres")
	@Column(nullable = false, unique = true, length = 100)
	private String correo;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime fechaSuscripcion;
}
