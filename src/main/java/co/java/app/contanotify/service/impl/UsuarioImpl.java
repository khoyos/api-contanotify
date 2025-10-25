package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.UsuarioDTO;
import co.java.app.contanotify.model.TipoUsuario;
import co.java.app.contanotify.model.Usuario;
import co.java.app.contanotify.repository.TipoUsuarioRepository;
import co.java.app.contanotify.repository.UsuarioRepository;
import co.java.app.contanotify.service.IUsuario;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioImpl implements IUsuario {

    private UsuarioRepository usuarioRepository;
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public UsuarioImpl(UsuarioRepository usuarioRepository,
                       TipoUsuarioRepository tipoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @Override
    public UsuarioDTO save(UsuarioDTO usuarioDTO) {

        if (usuarioRepository.findByDocumento(usuarioDTO.getDocumento()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con este documento");
        }

        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Error al acrear usuario");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setRazonSocial(usuarioDTO.getRazonSocial());
        usuario.setTipoDocumento(usuarioDTO.getTipoDocumento().toLowerCase());
        usuario.setDocumento(usuarioDTO.getDocumento());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setActive(true);

        String TIPO_USUARIO = "cliente";
        ObjectId tipoUsuarioId = null;

        if (tipoUsuarioRepository.findByName(TIPO_USUARIO.toLowerCase()).isPresent()) {
            tipoUsuarioId = new ObjectId(tipoUsuarioRepository.findByName(TIPO_USUARIO).get().getId());
        }else{
            TipoUsuario tipoUsuario = new TipoUsuario();
            tipoUsuario.setName(TIPO_USUARIO);

            tipoUsuario.setPublicId(UUID.randomUUID().toString());
            tipoUsuarioRepository.save(tipoUsuario);
            tipoUsuarioId = new ObjectId(tipoUsuarioRepository.findByName(TIPO_USUARIO).get().getId());
        }
        usuario.setTipoUsuarioId(tipoUsuarioId);
        usuario.setEstado(true);

        usuario.setPublicId(UUID.randomUUID().toString());
        usuarioRepository.save(usuario);

        return usuarioDTO;
    }

    @Override
    public Page<UsuarioDTO> getAll(String nombre, String documento, String email, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criterios = new ArrayList<>();

        //Filtros opcionales con búsqueda parcial (regex)
        if (nombre != null && !nombre.isBlank()) {
            criterios.add(Criteria.where("nombre").regex(nombre, "i"));
        }
        if (documento != null && !documento.isBlank()) {
            criterios.add(Criteria.where("documento").regex(documento, "i"));
        }
        if (email != null && !email.isBlank()) {
            criterios.add(Criteria.where("email").regex(email, "i"));
        }

        String id = tipoUsuarioRepository.findByName("cliente").get().getId();
        query.addCriteria(Criteria.where("tipoUsuarioId").is(new ObjectId(id)));

        //Solo usuarios activos
        query.addCriteria(Criteria.where("active").is(true));

        if (!criterios.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criterios.toArray(new Criteria[0])));
        }

        // Consulta paginada
        List<Usuario> usuarios = mongoTemplate.find(query, Usuario.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Usuario.class);

        // Mapeo de entidad a DTO
        List<UsuarioDTO> dtos = usuarios.stream().map(usuario -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getPublicId().toString());
            dto.setDocumento(usuario.getDocumento());
            dto.setNombre(usuario.getNombre());
            dto.setEmail(usuario.getEmail());
            dto.setTelefono(usuario.getTelefono());
            dto.setTipoDocumento(usuario.getTipoDocumento());
            dto.setRazonSocial(usuario.getRazonSocial());
            return dto;
        }).toList();

        return new PageImpl<>(dtos, pageable, total);
    }

    @Override
    public UsuarioDTO findByTipoDocumentoAndDocumento(String tipoDocumento, String documento) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        Optional<Usuario> usuario = usuarioRepository.findByTipoDocumentoAndDocumento(tipoDocumento, documento);

        if(usuario.isEmpty()){
            throw new RuntimeException("Error al consultar el usuario");
        }

        usuarioDTO.setId(usuario.get().getPublicId().toString());
        usuarioDTO.setNombre(usuario.get().getNombre());
        usuarioDTO.setDocumento(usuario.get().getDocumento());
        usuarioDTO.setTelefono(usuario.get().getTelefono());
        usuarioDTO.setEmail(usuario.get().getEmail());
        usuarioDTO.setTipoDocumento(usuario.get().getTipoDocumento());

        return usuarioDTO;
    }

    @Override
    public UsuarioDTO findById(String id) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        Optional<Usuario> usuario = usuarioRepository.findByPublicId(id);

        if(usuario.isEmpty()){
            throw new RuntimeException("Error al consultar el usuario");
        }

        usuarioDTO.setId(usuario.get().getId());
        usuarioDTO.setNombre(usuario.get().getNombre());
        usuarioDTO.setDocumento(usuario.get().getDocumento());
        usuarioDTO.setTelefono(usuario.get().getTelefono());
        usuarioDTO.setEmail(usuario.get().getEmail());
        usuarioDTO.setTipoDocumento(usuario.get().getTipoDocumento());
        usuarioDTO.setRazonSocial(usuario.get().getRazonSocial());

        return usuarioDTO;
    }

    @Override
    public void update(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByPublicId(usuarioDTO.getId());

        if(usuarioOptional.isEmpty()){
           throw new RuntimeException("Usuario invalido");
        }
        usuario = usuarioOptional.get();

        usuario.setId(usuarioOptional.get().getId());
        usuario.setPublicId(usuarioOptional.get().getPublicId());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setDocumento(usuarioDTO.getDocumento());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTipoDocumento(usuarioDTO.getTipoDocumento());
        usuario.setRazonSocial(usuarioDTO.getRazonSocial());

        usuario.setActive(true);

        ObjectId tipoUsuarioId = null;

        Optional<TipoUsuario> tipoUsuarioOptional = tipoUsuarioRepository.findByName("cliente");
        if (!tipoUsuarioOptional.isEmpty()) {
            tipoUsuarioId = new ObjectId(tipoUsuarioOptional.get().getId());
        }

        usuario.setTipoUsuarioId(tipoUsuarioId);

        usuarioRepository.save(usuario);
    }

    @Override
    public void delete(UsuarioDTO usuarioDTO) {
        Optional<Usuario> optUsuario = usuarioRepository.findByPublicId(usuarioDTO.getId());

        if(optUsuario.isEmpty()){
            throw new RuntimeException("No pudo eliminar");
        }

        optUsuario.get().setActive(false);

        usuarioRepository.save(optUsuario.get());
    }
}
