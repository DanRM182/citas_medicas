package com.dan.pacientes.entity;

import com.dan.commons.enums.EstadoRegistro;
import com.dan.commons.utils.StringCustomUtils;
import com.dan.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTES")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", length=50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO",  nullable = false)
    private Double peso;

    @Column(name = "ESTATURA",  nullable = false)
    private Double estatura;

    @Column(name = "IMC",  nullable = false)
    private Double imc;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", length = 20, nullable = false)
    private String numExpediente;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "DIRECCION", length = 150, nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public void asignarImcNumExpEstadoReg(Double imc, String numExpediente, EstadoRegistro estadoRegistro) {
        this.imc = imc;
        this.numExpediente = numExpediente;
        this.estadoRegistro = estadoRegistro;
    }

    public String obtenerNombreCompleto(String nombre, String apellidoPaterno, String apellidoMaterno) {
        return String.join( " ",nombre,apellidoPaterno, apellidoMaterno);
    }

    private void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, Double imc, String email, String numExpediente, String telefono, String direccion, EstadoRegistro estadoRegistro) {
        StringCustomUtils.validarTamanio(nombre,1,50,
                "El nombre es requerido y debe de contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno,1,50,
                "El apellido paterno es requerido y debe de contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno,1,50,
                "El apellido materno y debe de contener entre 1 y 50 caracteres");

        ValoresNumericosUtils.validarRangoShort(edad,(short) 1,(short) 100,
                "La edad es requerida y debe de tener entre 1 y 100 años");

        ValoresNumericosUtils.validarRangoDouble(peso,0.1, 200.0,
                "El peso es requerido y debe de tener entre 0.1 y 200 kgs");

        ValoresNumericosUtils.validarRangoDouble(estatura,1.0, 2.0,
                "La estatura es requerida y debe de tener entre 1.0 y 2.0 mts");

        StringCustomUtils.validarTamanio(email,1,100,
                "El email es requerido y debe de tener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(telefono,10,10,
                "El telefono es requerido y debe tener exactamente 10 digitos (0-9)");

        StringCustomUtils.validarTamanio(direccion,1,150,
                "La dirección es requerida y debe de contener entre 1 y 150 caracteres");
    }

    private void validarNoEliminado() {
        if(this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("El paciente ya tiene estatus eliminado");
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, Double imc, String email, String numExpediente, String telefono, String direccion, EstadoRegistro estadoRegistro) {
        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura, imc, email, numExpediente, telefono, direccion, estadoRegistro);

        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = imc;
        this.email = email;
        this.numExpediente = numExpediente;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estadoRegistro = estadoRegistro;
    }

    public void eliminar() {
        validarNoEliminado();

        estadoRegistro = EstadoRegistro.ELIMINADO;
    }
}
