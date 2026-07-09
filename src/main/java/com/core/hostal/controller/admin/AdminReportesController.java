package com.core.hostal.controller.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.core.hostal.dto.EstadisticaMensualDTO;
import com.core.hostal.dto.HabitacionPorTipoDTO;
import com.core.hostal.dto.ResumenDashboardDTO;
import com.core.hostal.service.DashboardAdminService;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReportesController {

    private final DashboardAdminService dashboardAdminService;

    public AdminReportesController(DashboardAdminService dashboardAdminService) {
        this.dashboardAdminService = dashboardAdminService;
    }

    @GetMapping
    public String reportes(
            @RequestParam(required = false) Integer anio,
            Model model) {
        int anioSeleccionado = anio != null ? anio : LocalDate.now().getYear();
        ResumenDashboardDTO resumen = dashboardAdminService.obtenerResumen(anioSeleccionado);
        List<EstadisticaMensualDTO> stats =
            dashboardAdminService.obtenerEstadisticasMensuales(anioSeleccionado);
        List<HabitacionPorTipoDTO> tipos = dashboardAdminService.obtenerDistribucionPorTipo();

        model.addAttribute("anioSeleccionado", anioSeleccionado);
        model.addAttribute("aniosDisponibles", dashboardAdminService.obtenerAniosDisponibles());
        model.addAttribute("ingresosTotales", resumen.getIngresosAnio());
        model.addAttribute("totalReservas", resumen.getTotalReservasAnio());
        model.addAttribute("habitacionesOcupadas", resumen.getHabitacionesOcupadas());
        model.addAttribute("totalHabitaciones", resumen.getTotalHabitaciones());
        model.addAttribute("totalClientes", resumen.getTotalClientes());

        model.addAttribute("labelsJson", dashboardAdminService.generarLabelsJson(stats));
        model.addAttribute("ingresosJson", dashboardAdminService.generarIngresosJson(stats));
        model.addAttribute("ocupacionJson", dashboardAdminService.generarOcupacionJson(stats));
        model.addAttribute("reservasJson", dashboardAdminService.generarReservasJson(stats));
        model.addAttribute("tiposJson", dashboardAdminService.generarTiposJson(tipos));
        model.addAttribute("cantidadPorTipoJson", dashboardAdminService.generarCantidadPorTipoJson(tipos));
        model.addAttribute("estadisticas", stats);

        model.addAttribute("seccionActiva", "reportes");
        return "admin/reportes";
    }
}
