package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class HorarioRemoteDto {

    private Long id;

    private DayOfWeek diaSemana;

    private LocalTime horaInicio;

    private LocalTime horaFin;
}
