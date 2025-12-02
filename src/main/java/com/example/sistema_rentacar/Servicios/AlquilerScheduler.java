package com.example.sistema_rentacar.Servicios;

import com.example.sistema_rentacar.Repository.AlquilerRepository;
import com.example.sistema_rentacar.Modelos.Alquiler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class AlquilerScheduler {

    private static Timer timer;
    private static AlquilerRepository alquilerRepo;
    private static boolean isRunning = false;
    private static final double FACTOR_PENALIZACION = 1.5; // 150% de recargo


    public static void iniciar() {
        if (isRunning) {
            System.out.println("Programador ya está en ejecución");
            return;
        }

        alquilerRepo = new AlquilerRepository();
        timer = new Timer("AlquilerScheduler", true);

        // Ejecutar inmediatamente y luego cada hora
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                verificarYActualizarEstados();
            }
        }, 0, 3600000); // 3600000 ms = 1 hora

        isRunning = true;
        System.out.println("Programador de alquileres iniciado - Verificación cada 1 hora");
        //System.out.println("Acciones: Actualizar estados, calcular penalizaciones, alertas");
    }

    //detender el programaor
    public static void detener() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            isRunning = false;
            System.out.println("Programador de alquileres detenido");
        }
    }


    private static void verificarYActualizarEstados() {
        try {
            //System.out.println("\nIniciando verificación de alquileres");

            List<Alquiler> alquileresActivos = alquilerRepo.obtenerActivosYRetrasados();
            LocalDate hoy = LocalDate.now();

            int actualizados = 0;
            int retrasados = 0;
            int porVencer = 0;

            for (Alquiler alquiler : alquileresActivos) {
                if (alquiler.getFechaFinEstimada() == null) continue;

                LocalDate fechaLimite = alquiler.getFechaFinEstimada()
                        .toLocalDateTime()
                        .toLocalDate();
                long diasDiferencia = ChronoUnit.DAYS.between(hoy, fechaLimite);

                String estadoActual = alquiler.getEstado();
                String nuevoEstado = estadoActual;

                // Determinar nuevo estado
                if (hoy.isAfter(fechaLimite)) {
                    // RETRASADO
                    nuevoEstado = "Retrasado";
                    int diasRetraso = (int) Math.abs(diasDiferencia);
                    double penalizacion = calcularPenalizacion(
                            alquiler.getTarifaDiaria(),
                            diasRetraso
                    );

                    if (!estadoActual.equals("Retrasado")) {
                        // Primera vez que entra en retraso
                        alquilerRepo.cambiarEstado(alquiler.getIdAlquiler(), "Retrasado");
                        alquilerRepo.actualizarPenalizacion(
                                alquiler.getIdAlquiler(),
                                penalizacion,
                                diasRetraso
                        );
                        actualizados++;
                        retrasados++;

                        System.out.println(String.format(
                                "ALQUILER #%d - RETRASADO",
                                alquiler.getIdAlquiler()
                        ));
                        System.out.println(String.format(
                                "   Cliente: %s | Vehículo: %s",
                                alquiler.getNombreCliente(),
                                alquiler.getVehiculo()
                        ));
                        System.out.println(String.format(
                                "   Días de retraso: %d | Penalización: $%.2f",
                                diasRetraso,
                                penalizacion
                        ));

                        //enciar notificaciones al cliente y a los empleados
                        // NotificacionService.alertaRetraso(alquiler, diasRetraso, penalizacion);

                    } else {
                        // Ya estaba retrasado, actualizar penalización
                        alquilerRepo.actualizarPenalizacion(
                                alquiler.getIdAlquiler(),
                                penalizacion,
                                diasRetraso
                        );
                        System.out.println(String.format(
                                "Alquiler #%d - Penalización actualizada: $%.2f (%d días)",
                                alquiler.getIdAlquiler(),
                                penalizacion,
                                diasRetraso
                        ));
                    }

                } else if (diasDiferencia == 0 || diasDiferencia == 1) {
                    // POR VENCER (hoy o mañana)
                    nuevoEstado = "Por Vencer";

                    if (!estadoActual.equals("Por Vencer")) {
                        alquilerRepo.cambiarEstado(alquiler.getIdAlquiler(), "Por Vencer");
                        actualizados++;
                        porVencer++;

                        System.out.println(String.format(
                                "Alquiler #%d - POR VENCER en %d día(s)",
                                alquiler.getIdAlquiler(),
                                diasDiferencia
                        ));
                        System.out.println(String.format(
                                "   Cliente: %s | Vehículo: %s",
                                alquiler.getNombreCliente(),
                                alquiler.getVehiculo()
                        ));

                        //enviar recordatorio al cliente
                        // NotificacionService.recordatorioVencimiento(alquiler, diasDiferencia);
                    }
                }
            }

//            System.out.println("\n=== Resumen de verificación ===");
//            System.out.println(String.format("Total verificados: %d", alquileresActivos.size()));
//            System.out.println(String.format("Estados actualizados: %d", actualizados));
//            System.out.println(String.format("Alquileres retrasados: %d", retrasados));
//            System.out.println(String.format("Alquileres por vencer: %d", porVencer));
//            System.out.println("Verificación completada\n");

        } catch (Exception e) {
            System.err.println("Error en verificación de alquileres: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static double calcularPenalizacion(double tarifaDiaria, int diasRetraso) {
        // Penalización progresiva
        double factor = FACTOR_PENALIZACION;

        if (diasRetraso > 7) {
            factor = 2.5; // 250% después de 7 días
        } else if (diasRetraso > 3) {
            factor = 2.0; // 200% después de 3 días
        }

        return tarifaDiaria * factor * diasRetraso;
    }


    public static void verificarManualmente() {
        //System.out.println("Ejecutando verificación manual...");
        verificarYActualizarEstados();
    }


    public static boolean estaActivo() {
        return isRunning;
    }

    public static String obtenerEstadisticas() {
        try {
            List<Alquiler> alquileres = alquilerRepo.obtenerActivosYRetrasados();
            int activos = 0, retrasados = 0, porVencer = 0;
            double totalPenalizaciones = 0;

            LocalDate hoy = LocalDate.now();

            for (Alquiler a : alquileres) {
                if (a.getFechaFinEstimada() == null) continue;

                LocalDate fechaLimite = a.getFechaFinEstimada()
                        .toLocalDateTime()
                        .toLocalDate();
                long diasDif = ChronoUnit.DAYS.between(hoy, fechaLimite);

                if (hoy.isAfter(fechaLimite)) {
                    retrasados++;
                    int diasRetraso = (int) Math.abs(diasDif);
                    totalPenalizaciones += calcularPenalizacion(a.getTarifaDiaria(), diasRetraso);
                } else if (diasDif <= 1) {
                    porVencer++;
                } else {
                    activos++;
                }
            }

            return String.format(
                    "🟢 Activos: %d | 🟡 Por vencer: %d | 🔴 Retrasados: %d | 💰 Penalizaciones: $%.2f",
                    activos, porVencer, retrasados, totalPenalizaciones
            );

        } catch (Exception e) {
            return "Error al obtener estadísticas";
        }
    }
}