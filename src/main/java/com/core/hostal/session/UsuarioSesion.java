package com.core.hostal.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioSesion implements Serializable, UserDetails {

    private Integer id;
    private String nombre;
    private String email;
    private TipoUsuario tipo;

    public UsuarioSesion() {
    }

    public UsuarioSesion(Integer id, String nombre, String email, TipoUsuario tipo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
    }

    public static UsuarioSesion cliente(Integer id, String nombre, String email) {
        return new UsuarioSesion(id, nombre, email, TipoUsuario.CLIENTE);
    }

    public static UsuarioSesion admin(Integer id, String nombre, String email) {
        return new UsuarioSesion(id, nombre, email, TipoUsuario.ADMIN);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public boolean esCliente() {
        return tipo == TipoUsuario.CLIENTE;
    }

    public boolean esAdmin() {
        return tipo == TipoUsuario.ADMIN;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = tipo == TipoUsuario.ADMIN ? "ROLE_ADMIN" : "ROLE_CLIENTE";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
