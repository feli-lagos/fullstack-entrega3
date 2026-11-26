package com.huertoverde.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

	public static final String CORREO_PERMITIDO = "(?i)^[^@\\s]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El RUN es obligatorio")
	@Pattern(regexp = "^[0-9]{6,8}[0-9Kk]$", message = "El RUN debe ingresarse sin puntos ni guion. Ejemplo: 19011029K")
	@Column(nullable = false, unique = true, length = 9)
	private String run;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
	@Column(nullable = false, length = 50)
	private String nombre;

	@NotBlank(message = "Los apellidos son obligatorios")
	@Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
	@Column(nullable = false, length = 100)
	private String apellidos;

	@NotBlank(message = "El correo es obligatorio")
	@Size(max = 100, message = "El correo no puede superar los 100 caracteres")
	@Pattern(regexp = CORREO_PERMITIDO, message = "El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com")
	@Column(nullable = false, unique = true, length = 100)
	private String correo;

	// Contraseña en texto plano: solo se recibe en las solicitudes, nunca se guarda ni se devuelve
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
	private String contrasena;

	@JsonIgnore
	@Column(name = "contrasena_hash", nullable = false, length = 100)
	private String contrasenaHash;

	@NotBlank(message = "Debe seleccionar el tipo de usuario")
	@Pattern(regexp = "administrador|vendedor|cliente", message = "El tipo de usuario debe ser administrador, vendedor o cliente")
	@Column(nullable = false, length = 20)
	private String tipo;

	@NotNull(message = "Debe seleccionar una comuna: \"comuna\": {\"id\": ...}")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "comuna_id", nullable = false)
	private Comuna comuna;

	@NotBlank(message = "La dirección es obligatoria")
	@Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
	@Column(nullable = false, length = 300)
	private String direccion;

	@Pattern(regexp = "^\\+569[0-9]{8}$", message = "Ingrese un teléfono válido, por ejemplo +56912345678")
	@Column(length = 15)
	private String telefono;

	@Past(message = "La fecha de nacimiento debe ser una fecha pasada")
	private LocalDate fechaNacimiento;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime fechaRegistro;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime ultimoAcceso;
}
