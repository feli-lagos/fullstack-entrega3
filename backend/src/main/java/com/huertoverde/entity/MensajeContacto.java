package com.huertoverde.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_contacto")
@Getter
@Setter
@NoArgsConstructor
public class MensajeContacto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
	@Column(nullable = false, length = 100)
	private String nombre;

	@Size(max = 100, message = "El correo no puede superar los 100 caracteres")
	@Pattern(regexp = Usuario.CORREO_PERMITIDO, message = "Use un correo @duoc.cl, @profesor.duoc.cl o @gmail.com")
	@Column(length = 100)
	private String correo;

	@NotBlank(message = "El motivo es obligatorio")
	@Pattern(regexp = "consulta|pedido|reclamo|sugerencia", message = "El motivo debe ser consulta, pedido, reclamo o sugerencia")
	@Column(nullable = false, length = 20)
	private String motivo;

	@NotBlank(message = "El comentario es obligatorio")
	@Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
	@Column(nullable = false, length = 500)
	private String comentario;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime fechaEnvio;
}
