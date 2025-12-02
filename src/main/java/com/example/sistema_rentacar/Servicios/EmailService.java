package com.example.sistema_rentacar.Servicios;

import com.example.sistema_rentacar.Utilidades.EmailUtilidades;
import com.example.sistema_rentacar.Modelos.Alquiler;
import com.example.sistema_rentacar.Modelos.Pago;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailService {


     // Envíar la factura de alquiler por correo electrónico

    public static boolean enviarFacturaAlquiler(
            String emailDestino,
            String nombreCliente,
            Alquiler alquiler,
            Pago pago,
            String vehiculo) {
        try {

            // Configurar propiedades del servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", EmailUtilidades.SMTP_HOST);
            props.put("mail.smtp.port", EmailUtilidades.SMTP_PORT);
            props.put("mail.smtp.auth", EmailUtilidades.SMTP_AUTH);
            props.put("mail.smtp.starttls.enable", EmailUtilidades.SMTP_STARTTLS);
            props.put("mail.smtp.ssl.trust", EmailUtilidades.SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Crear sesión con autenticación
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            EmailUtilidades.EMAIL_FROM,
                            EmailUtilidades.EMAIL_PASSWORD
                    );
                }
            });

            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailUtilidades.EMAIL_FROM, EmailUtilidades.EMAIL_FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject("Factura de Alquiler #" + alquiler.getIdAlquiler() + " - " + EmailUtilidades.COMPANY_NAME);

            // Generar contenido HTML de la factura
            String htmlContent = generarHTMLFactura(nombreCliente, alquiler, pago, vehiculo);

            // Establecer contenido HTML
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Enviar el mensaje
            Transport.send(message);

            System.out.println("Correo enviado exitosamente a: " + emailDestino);
            return true;

        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private static String generarHTMLFactura(
            String nombreCliente,
            Alquiler alquiler,
            Pago pago,
            String vehiculo
    ) {

        if (alquiler == null) {
            throw new IllegalArgumentException("El alquiler no puede ser null");
        }
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser null");
        }
        if (alquiler.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio del alquiler no puede ser null");
        }
        if (alquiler.getFechaFinEstimada() == null) {
            throw new IllegalArgumentException("La fecha fin estimada del alquiler no puede ser null");
        }
        if (pago.getFechaPago() == null) {
            throw new IllegalArgumentException("La fecha de pago no puede ser null");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");


        String fechaInicio = formatearFecha(alquiler.getFechaInicio(), sdfHora, "Fecha no disponible");
        String fechaFinEstimada = formatearFecha(alquiler.getFechaFinEstimada(), sdf, "Fecha no disponible");
        String fechaPago = formatearFecha(pago.getFechaPago(), sdfHora, "Fecha no disponible");

        String metodoPago = pago.esPagoConTarjeta()
                ? pago.getTipoTarjeta() + " - " + pago.getTarjetaEnmascarada()
                : "Efectivo";

        return "<!DOCTYPE html>" +
                "<html lang='es'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Factura de Alquiler</title>" +
                "</head>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table width='100%' cellpadding='0' cellspacing='0' style='background-color: #f4f4f4; padding: 20px;'>" +
                "        <tr>" +
                "            <td align='center'>" +
                "                <!-- Contenedor principal -->" +
                "                <table width='600' cellpadding='0' cellspacing='0' style='background-color: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                "                    " +
                "                    <!-- Header -->" +
                "                    <tr>" +
                "                        <td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "                            <h1 style='color: white; margin: 0; font-size: 28px;'>🚗 " + EmailUtilidades.COMPANY_NAME + "</h1>" +
                "                            <p style='color: rgba(255,255,255,0.9); margin: 5px 0 0 0; font-size: 14px;'>Factura de Alquiler</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Información del cliente -->" +
                "                    <tr>" +
                "                        <td style='padding: 30px;'>" +
                "                            <h2 style='color: #333; margin: 0 0 20px 0; font-size: 20px;'>Hola " + nombreCliente + ",</h2>" +
                "                            <p style='color: #666; line-height: 1.6; margin: 0 0 20px 0;'>" +
                "                                Gracias por confiar en nosotros. A continuación encontrarás los detalles de tu alquiler:" +
                "                            </p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Detalles del alquiler -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 30px 20px 30px;'>" +
                "                            <table width='100%' cellpadding='0' cellspacing='0' style='background-color: #f8f9fa; border-radius: 6px; padding: 20px;'>" +
                "                                <tr>" +
                "                                    <td colspan='2' style='padding-bottom: 15px; border-bottom: 2px solid #667eea;'>" +
                "                                        <h3 style='color: #667eea; margin: 0; font-size: 18px;'>📋 Detalles del Alquiler</h3>" +
                "                                    </td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Número de Alquiler:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>#" + alquiler.getIdAlquiler() + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Vehículo:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>" + vehiculo + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Fecha de Inicio:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>" + fechaInicio + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Fecha de Fin Estimada:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>" + fechaFinEstimada + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Días de Alquiler:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>" + alquiler.getDiasAlquiler() + " días</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Tarifa por Día:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>$" + String.format("%.2f", alquiler.getTarifaDiaria()) + "</td>" +
                "                                </tr>" +
                "                                <tr style='border-top: 1px solid #dee2e6;'>" +
                "                                    <td style='padding: 15px 0 10px 0; color: #333; font-weight: bold; font-size: 16px;'>Costo Total:</td>" +
                "                                    <td style='padding: 15px 0 10px 0; color: #667eea; text-align: right; font-weight: bold; font-size: 18px;'>$" + String.format("%.2f", alquiler.getCostoTotal()) + "</td>" +
                "                                </tr>" +
                "                            </table>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Detalles del pago -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 30px 30px 30px;'>" +
                "                            <table width='100%' cellpadding='0' cellspacing='0' style='background-color: #e8f5e9; border-radius: 6px; padding: 20px;'>" +
                "                                <tr>" +
                "                                    <td colspan='2' style='padding-bottom: 15px; border-bottom: 2px solid #27ae60;'>" +
                "                                        <h3 style='color: #27ae60; margin: 0; font-size: 18px;'>💳 Información del Pago</h3>" +
                "                                    </td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Depósito Pagado:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right; font-weight: bold;'>$" + String.format("%.2f", pago.getMonto()) + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Método de Pago:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>" + metodoPago + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Código de Autorización:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right; font-family: monospace;'>" + pago.getReferencia() + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Fecha de Pago:</td>" +
                "                                    <td style='padding: 10px 0; color: #333; text-align: right;'>" + fechaPago + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='padding: 10px 0; color: #666; font-weight: bold;'>Estado:</td>" +
                "                                    <td style='padding: 10px 0; text-align: right;'>" +
                "                                        <span style='background-color: #27ae60; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: bold;'>✓ " + pago.getEstadoPago().toUpperCase() + "</span>" +
                "                                    </td>" +
                "                                </tr>" +
                "                            </table>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Nota importante -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 30px 30px 30px;'>" +
                "                            <div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; border-radius: 4px;'>" +
                "                                <p style='margin: 0; color: #856404; font-size: 14px; line-height: 1.6;'>" +
                "                                    <strong>📌 Importante:</strong> Por favor, presenta este correo o el código de autorización al momento de recoger el vehículo. " +
                "                                    El saldo restante de <strong>$" + String.format("%.2f", alquiler.getCostoTotal() - pago.getMonto()) + "</strong> deberá ser pagado al finalizar el alquiler." +
                "                                </p>" +
                "                            </div>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Footer -->" +
                "                    <tr>" +
                "                        <td style='background-color: #f8f9fa; padding: 25px; text-align: center; border-radius: 0 0 8px 8px; border-top: 1px solid #dee2e6;'>" +
                "                            <p style='margin: 0 0 10px 0; color: #666; font-size: 14px;'>¿Tienes preguntas? Contáctanos:</p>" +
                "                            <p style='margin: 0; color: #667eea; font-size: 14px;'>" +
                "                                📞 " + EmailUtilidades.COMPANY_PHONE + " | 📧 " + EmailUtilidades.COMPANY_EMAIL +
                "                            </p>" +
                "                            <p style='margin: 15px 0 0 0; color: #999; font-size: 12px;'>" +
                "                                " + EmailUtilidades.COMPANY_NAME + " - " + EmailUtilidades.COMPANY_ADDRESS +
                "                            </p>" +
                "                            <p style='margin: 5px 0 0 0; color: #999; font-size: 11px;'>" +
                "                                Este es un correo automático, por favor no respondas a este mensaje." +
                "                            </p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }


    private static String formatearFecha(Date fecha, SimpleDateFormat formato, String valorPorDefecto) {
        if (fecha == null) {
            System.err.println("Advertencia: Se intentó formatear una fecha null");
            return valorPorDefecto;
        }
        try {
            return formato.format(fecha);
        } catch (Exception e) {
            System.err.println("Error al formatear fecha: " + e.getMessage());
            return valorPorDefecto;
        }
    }


    public static boolean enviarCorreoPrueba(String emailDestino) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", EmailUtilidades.SMTP_HOST);
            props.put("mail.smtp.port", EmailUtilidades.SMTP_PORT);
            props.put("mail.smtp.auth", EmailUtilidades.SMTP_AUTH);
            props.put("mail.smtp.starttls.enable", EmailUtilidades.SMTP_STARTTLS);
            props.put("mail.smtp.ssl.trust", EmailUtilidades.SMTP_HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            EmailUtilidades.EMAIL_FROM,
                            EmailUtilidades.EMAIL_PASSWORD
                    );
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailUtilidades.EMAIL_FROM, EmailUtilidades.EMAIL_FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject("Prueba de Configuración - " + EmailUtilidades.COMPANY_NAME);
            message.setText("¡La configuración de correo funciona correctamente!");

            Transport.send(message);
            System.out.println("Correo de prueba enviado exitosamente");
            return true;

        } catch (Exception e) {
            System.err.println("Error al enviar correo de prueba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean enviarCodigoRecuperacion(
            String emailDestino,
            String nombreCliente,
            String codigo) {
        try {
            // Configurar propiedades del servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", EmailUtilidades.SMTP_HOST);
            props.put("mail.smtp.port", EmailUtilidades.SMTP_PORT);
            props.put("mail.smtp.auth", EmailUtilidades.SMTP_AUTH);
            props.put("mail.smtp.starttls.enable", EmailUtilidades.SMTP_STARTTLS);
            props.put("mail.smtp.ssl.trust", EmailUtilidades.SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Crear sesión con autenticación
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            EmailUtilidades.EMAIL_FROM,
                            EmailUtilidades.EMAIL_PASSWORD
                    );
                }
            });

            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailUtilidades.EMAIL_FROM, EmailUtilidades.EMAIL_FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject("Código de Recuperación de Contraseña - " + EmailUtilidades.COMPANY_NAME);

            // Generar contenido HTML
            String htmlContent = generarHTMLRecuperacion(nombreCliente, codigo);

            // Establecer contenido HTML
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Enviar el mensaje
            Transport.send(message);

            System.out.println("Código de recuperación enviado exitosamente a: " + emailDestino);
            return true;

        } catch (Exception e) {
            System.err.println("Error al enviar código de recuperación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

//genera un html con el codigo de recuperacion que es el que se envia al correo electronico
    private static String generarHTMLRecuperacion(String nombreCliente, String codigo) {
        return "<!DOCTYPE html>" +
                "<html lang='es'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Recuperación de Contraseña</title>" +
                "</head>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table width='100%' cellpadding='0' cellspacing='0' style='background-color: #f4f4f4; padding: 20px;'>" +
                "        <tr>" +
                "            <td align='center'>" +
                "                <table width='600' cellpadding='0' cellspacing='0' style='background-color: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                "                    " +
                "                    <!-- Header -->" +
                "                    <tr>" +
                "                        <td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "                            <h1 style='color: white; margin: 0; font-size: 28px;'>🔐 " + EmailUtilidades.COMPANY_NAME + "</h1>" +
                "                            <p style='color: rgba(255,255,255,0.9); margin: 5px 0 0 0; font-size: 14px;'>Recuperación de Contraseña</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Contenido -->" +
                "                    <tr>" +
                "                        <td style='padding: 40px 30px;'>" +
                "                            <h2 style='color: #333; margin: 0 0 20px 0; font-size: 20px;'>Hola " + nombreCliente + ",</h2>" +
                "                            <p style='color: #666; line-height: 1.6; margin: 0 0 20px 0;'>" +
                "                                Recibimos una solicitud para restablecer la contraseña de tu cuenta. " +
                "                                Usa el siguiente código de verificación para continuar con el proceso:" +
                "                            </p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Código -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 30px 30px 30px;'>" +
                "                            <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; border-radius: 8px; text-align: center;'>" +
                "                                <p style='color: rgba(255,255,255,0.9); margin: 0 0 10px 0; font-size: 14px; letter-spacing: 1px;'>TU CÓDIGO DE VERIFICACIÓN</p>" +
                "                                <p style='color: white; margin: 0; font-size: 42px; font-weight: bold; letter-spacing: 8px; font-family: monospace;'>" + codigo + "</p>" +
                "                            </div>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Advertencia -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 30px 30px 30px;'>" +
                "                            <div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; border-radius: 4px;'>" +
                "                                <p style='margin: 0; color: #856404; font-size: 14px; line-height: 1.6;'>" +
                "                                    <strong>⏱️ Importante:</strong> Este código es válido solo por <strong>15 minutos</strong>. " +
                "                                    Si no solicitaste este cambio, ignora este correo y tu contraseña permanecerá sin cambios." +
                "                                </p>" +
                "                            </div>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Seguridad -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 30px 30px 30px;'>" +
                "                            <div style='background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; border-radius: 4px;'>" +
                "                                <p style='margin: 0; color: #0d47a1; font-size: 13px; line-height: 1.6;'>" +
                "                                    <strong>🛡️ Consejos de seguridad:</strong><br>" +
                "                                    • Nunca compartas este código con nadie<br>" +
                "                                    • Nuestro equipo nunca te pedirá este código por teléfono<br>" +
                "                                    • Asegúrate de crear una contraseña segura y única" +
                "                                </p>" +
                "                            </div>" +
                "                        </td>" +
                "                    </tr>" +
                "                    " +
                "                    <!-- Footer -->" +
                "                    <tr>" +
                "                        <td style='background-color: #f8f9fa; padding: 25px; text-align: center; border-radius: 0 0 8px 8px; border-top: 1px solid #dee2e6;'>" +
                "                            <p style='margin: 0 0 10px 0; color: #666; font-size: 14px;'>¿Necesitas ayuda? Contáctanos:</p>" +
                "                            <p style='margin: 0; color: #667eea; font-size: 14px;'>" +
                "                                📞 " + EmailUtilidades.COMPANY_PHONE + " | 📧 " + EmailUtilidades.COMPANY_EMAIL +
                "                            </p>" +
                "                            <p style='margin: 15px 0 0 0; color: #999; font-size: 12px;'>" +
                "                                " + EmailUtilidades.COMPANY_NAME + " - " + EmailUtilidades.COMPANY_ADDRESS +
                "                            </p>" +
                "                            <p style='margin: 5px 0 0 0; color: #999; font-size: 11px;'>" +
                "                                Este es un correo automático, por favor no respondas a este mensaje." +
                "                            </p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }
}