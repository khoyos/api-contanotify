package co.java.app.contanotify.service.impl;

import co.java.app.contanotify.dto.CalendarioDTO;
import co.java.app.contanotify.dto.CalendarioSimpleDTO;
import co.java.app.contanotify.dto.FechaDTO;
import co.java.app.contanotify.model.Calendario;
import co.java.app.contanotify.model.Fecha;
import co.java.app.contanotify.repository.CalendarioRepository;
import co.java.app.contanotify.service.ICalendario;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CalendarioImpl implements ICalendario {

    private final CalendarioRepository calendarioRepository;

    public CalendarioImpl(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Override
    public Optional<CalendarioDTO> findByObligacionId(String id) {
        List<Calendario> calendario = getCalendarios(id);
        Optional<Calendario> ca = calendario.stream().findFirst();
        if(ca.isEmpty()){
           return Optional.empty();
        }
        CalendarioDTO calendarioDTO = new CalendarioDTO();

        calendarioDTO.setId(ca.get().getId());
        calendarioDTO.setNombre(ca.get().getNombre());
        calendarioDTO.setHasta(ca.get().getHasta());
        calendarioDTO.setObligacionId(ca.get().getObligacionId());
        calendarioDTO.setCalendario(ca.get().getCalendario());

        List<FechaDTO> fechas = new ArrayList<>();
        int countId = 0;
        for(Fecha fecha: ca.get().getFechas()){
            FechaDTO fechaDto = new FechaDTO();

            fechaDto.setId(countId++);
            fechaDto.setFecha(fecha.getFecha());
            fechaDto.setNit(fecha.getNit());

            fechas.add(fechaDto);
        }

        calendarioDTO.setFechas(fechas);

        return Optional.of(calendarioDTO) ;
    }

    private List<Calendario> getCalendarios(String id) {
        Optional<List<Calendario>> calendarios= calendarioRepository.findByObligacionId(id);
        List<Calendario> calendario = calendarios.stream().findFirst().get();
        return calendario;
    }

    @Override
    public Optional<CalendarioDTO> findByNombre(String nombre) {
        CalendarioDTO calendarioDTO = new CalendarioDTO();
        Optional<Calendario> opt=calendarioRepository.findByNombre(nombre);

        if(opt.isEmpty()){
            return Optional.empty();
        }

        calendarioDTO.setId(opt.get().getId());
        calendarioDTO.setNombre(opt.get().getNombre());
        calendarioDTO.setHasta(opt.get().getHasta());
        calendarioDTO.setObligacionId(opt.get().getObligacionId());
        calendarioDTO.setCalendario(opt.get().getCalendario());

        List<FechaDTO> fechas = new ArrayList<>();
        for(Fecha fecha: opt.get().getFechas()){
            FechaDTO fechaDto = new FechaDTO();

            fechaDto.setId(fecha.getId());
            fechaDto.setFecha(fecha.getFecha());
            fechaDto.setNit(fecha.getNit());

            fechas.add(fechaDto);
        }

        calendarioDTO.setFechas(fechas);

        return Optional.of(calendarioDTO);
    }

    @Override
    public void save(CalendarioDTO calendarioDTO) {
        Calendario calendario = new Calendario();

        calendario.setId(calendarioDTO.getId());
        calendario.setNombre(calendarioDTO.getNombre());
        calendario.setHasta(calendarioDTO.getHasta());
        calendario.setObligacionId(calendarioDTO.getObligacionId());
        calendario.setCalendario(calendarioDTO.getCalendario());

        List<Fecha> fechas = new ArrayList<>();
        for(FechaDTO fechaDto: calendarioDTO.getFechas()){
            Fecha fecha = new Fecha();
            fecha.setFecha(fechaDto.getFecha());
            fecha.setNit(fechaDto.getNit());

            fechas.add(fecha);
        }

        calendario.setFechas(fechas);

        calendarioRepository.save(calendario);
    }

    @Override
    public List<CalendarioSimpleDTO> getPagos(String obligacionId) {
        List<CalendarioSimpleDTO> listCalendarios = new ArrayList<>();
        List<Calendario> calendarios = getCalendarios(obligacionId);
        if(calendarios.isEmpty()){
            return new ArrayList<>();
        }

        for(Calendario calendario: calendarios){
            CalendarioSimpleDTO calendarioSimple = new CalendarioSimpleDTO();
            calendarioSimple.setId(calendario.getId());
            calendarioSimple.setNombre(calendario.getNombre());

            listCalendarios.add(calendarioSimple);
        }
        return listCalendarios;
    }
}
