package com.proj.individual;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        String sql = "SELECT * FROM usuario";
        List<Usuario> usuarios = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Usuario.class));
        return ResponseEntity.status(200).body(usuarios);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<Usuario> listarPorId(@PathVariable Integer id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        List<Usuario> usuarios = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Usuario.class), id);

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(200).body(usuarios.getLast());
    }

    @PostMapping
    public ResponseEntity<Usuario>criar (@RequestBody Usuario criarUsuario) {
        String sql = "INSERT INTO usuario (nome, username, email, senha) VALUES" +
                "(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps  = con.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, criarUsuario.getNome());
            ps.setString(2, criarUsuario.getUsername());
            ps.setString(3, criarUsuario.getEmail());
            ps.setString(4, criarUsuario.getSenha());
            return ps;
        }, keyHolder);

        Integer idGerado =keyHolder.getKeyAs(Integer.class);
        criarUsuario.setId(idGerado);
        return ResponseEntity.status(201).body(criarUsuario);
    }

}
