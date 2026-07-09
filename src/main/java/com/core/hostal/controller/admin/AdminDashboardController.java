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
import com.core.hostal.dto.ReservaResumenDTO;
import com.core.hostal.dto.ResumenDashboardDTO;
import com.core.hostal.service.DashboardAdminService;
import com.core.hostal.service.ReservaService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final DashboardAdminService dashboardAdminService;
    private final ReservaService reservaService;

    public AdminDashboardController(
            DashboardAdminService dashboardAdminService,
            ReservaService reservaService) {
        this.dashboardAdminService = dashboardAdminService;
        this.reservaService = reservaService;
    }

    @GetMapping({"", "/"})
    public String redirigir() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) Integer anio,
            Model model) {
        int anioSeleccionado = anio != null ? anio : LocalDate.now().getYear();
        ResumenDashboardDTO resumen = dashboardAdminService.obtenerResumen(anioSeleccionado);
        List<EstadisticaMensualDTO> stats =
            dashboardAdminService.obtenerEstadisticasMensuales(anioSeleccionado);
        List<HabitacionPorTipoDTO> tipos = dashboardAdminService.obtenerDistribucionPorTipo();

        model.addAttribute("totalHabitaciones", resumen.getTotalHabitaciones());
        model.addAttribute("habitacionesOcupadas", resumen.getHabitacionesOcupadas());
        model.addAttribute("habitacionesDisponibles", resumen.getHabitacionesDisponibles());
        model.addAttribute("reservasHoy", resumen.getReservasActivas());
        model.addAttribute("ingresosMes", resumen.getIngresosAnio());
        model.addAttribute("tasaOcupacion", resumen.getTasaOcupacion());
        model.addAttribute("totalClientes", resumen.getTotalClientes());

        model.addAttribute("labelsJson", dashboardAdminService.generarLabelsJson(stats));
        model.addAttribute("ingresosJson", dashboardAdminService.generarIngresosJson(stats));
        model.addAttribute("ocupacionJson", dashboardAdminService.generarOcupacionJson(stats));
        model.addAttribute("reservasJson", dashboardAdminService.generarReservasJson(stats));
        model.addAttribute("tiposJson", dashboardAdminService.generarTiposJson(tipos));
        model.addAttribute("cantidadPorTipoJson", dashboardAdminService.generarCantidadPorTipoJson(tipos));

        model.addAttribute("aniosDisponibles", dashboardAdminService.obtenerAniosDisponibles());
        model.addAttribute("anioSeleccionado", anioSeleccionado);

        List<ReservaResumenDTO> recientes = reservaService.listarTodas();
        model.addAttribute("reservasRecientes",
            recientes.subList(0, Math.min(5, recientes.size())));

        model.addAttribute("seccionActiva", "dashboard");
        return "admin/dashboard";
    }
}
