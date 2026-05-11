package com.jamilekerber.usuario.business;

import com.jamilekerber.usuario.business.converter.UsuarioConverter;
import com.jamilekerber.usuario.business.dto.UsuarioDTO;
import com.jamilekerber.usuario.infrastructure.entity.Usuario;
import com.jamilekerber.usuario.infrastructure.exceptions.ConflictException;
import com.jamilekerber.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.jamilekerber.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExistente(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExistente(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email já cadastrado." + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado." + e.getCause());
        }
    }
    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email não encontrado" + email));
    }
    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }
}
