package com.example.comandera.data;

import com.example.comandera.utils.DetalleDocumento;
import com.example.comandera.utils.Mesa;
import com.example.comandera.utils.Ticket;
import com.example.comandera.utils.ZonaVenta;

import android.content.Context;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketBD {
    private SQLServerConnection sqlServerConnection;

    public TicketBD(SQLServerConnection connection) {

        sqlServerConnection = connection;
    }

    public Ticket getTicketForMesa(int mesaId, int dispositivoId, int seccionId) {
        Ticket ticket = null;
        if (sqlServerConnection.getConexion() != null) {
            String query = "SELECT id, estado_documento, fecha, numero, serie_id, num_comensales,escribiendo FROM Cabecera_Documentos_Venta WHERE mesa_id = ? AND estado_documento = 0 "+/*AND dispositivo_id = ?*/" AND tipo = 5 AND seccion_id = ?";
            try {
                PreparedStatement statement = sqlServerConnection.getConexion().prepareStatement(query);
                statement.setInt(1, mesaId);
                //El dispositivo con el que se haya echo el ticket no importa
                /*statement.setInt(2, dispositivoId);*/
                statement.setInt(2, seccionId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    ticket = new Ticket();
                    ticket.setId(resultSet.getInt("id"));
                    ticket.setEstadoDocumento(resultSet.getInt("estado_documento"));
                    ticket.setFecha(resultSet.getString("fecha"));
                    ticket.setNumero(resultSet.getDouble("numero"));
                    ticket.setSerieId(resultSet.getDouble("serie_id"));
                    ticket.setComensales(resultSet.getInt("num_comensales"));
                    ticket.setEscribiendo(resultSet.getBoolean("escribiendo"));
                }
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
            }
        }
        return ticket;
    }

    public void marcarTicketComoPagado(int ticketID){
        if(sqlServerConnection.getConexion()!=null){
            try{
                String query = "UPDATE Cabecera_Documentos_Venta SET estado_documento = 1 WHERE id = ?;";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, ticketID);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void cargarDetallesEnTicket(Ticket ticket,Context context){
        if (ticket!=null){
            if (sqlServerConnection.getConexion() != null) {
                String query = "SELECT articulo_id, descripcion_articulo, descripcion_larga, cantidad, precio, total_linea, cuota_iva,Orden_preparacion FROM Detalle_Documentos_Venta WHERE cabecera_id= ?";
                try {
                    PreparedStatement statement = sqlServerConnection.getConexion().prepareStatement(query);
                    statement.setInt(1, ticket.getId());
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {

                        //AÑADIENDO INFORMACION A LOS DETALLES
                        DetalleDocumento detalle = new DetalleDocumento();
                        detalle.setArticuloID(resultSet.getInt("articulo_id"));
                        detalle.setDescripcion((resultSet.getString("descripcion_articulo")));
                        detalle.setDescripcionLarga(resultSet.getString("descripcion_larga"));
                        detalle.setCantidad(resultSet.getInt("cantidad"));
                        detalle.setTotalLinea(resultSet.getBigDecimal("total_linea").doubleValue());
                        detalle.setPrecio(resultSet.getDouble("precio"));
                        detalle.setCuotaIva(resultSet.getDouble("cuota_iva"));
                        detalle.setPvp(resultSet.getDouble("precio")+resultSet.getDouble("cuota_iva"));
                        detalle.setOrdenPreparacion(resultSet.getInt("Orden_preparacion"));
                        ticket.getDetallesTicket().add(detalle);
                    }
                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }else{
            Toast.makeText(context,"No se encontraron detalles en este ticket",Toast.LENGTH_SHORT).show();
        }
    }


    public long crearTicket(int idSerie, int idSeccion, int idDispositivo, int idMesa, int idUsuarioTpv, int comensales, int zonaID) {
        long newRowId = -1;
        if (sqlServerConnection.getConexion() != null) {
            try {
                int numeroCorriente = incrementarNumero();

                String insertQuery = "INSERT INTO Cabecera_Documentos_Venta (Tipo, Fecha, Fecha_Contable, Serie_Id, Seccion_Id, Dispositivo_Id, Mesa_Id, Estado_Documento, Numero, Usuario_Ticket_Id, num_comensales, zona_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement insertStatement = sqlServerConnection.getConexion().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                insertStatement.setInt(1, 5);
                Timestamp currentTimestamp = new Timestamp(new Date().getTime());
                insertStatement.setTimestamp(2, currentTimestamp);
                insertStatement.setTimestamp(3, currentTimestamp);
                insertStatement.setDouble(4, idSerie);
                insertStatement.setDouble(5, idSeccion);
                insertStatement.setDouble(6, idDispositivo);
                insertStatement.setDouble(7, idMesa);
                insertStatement.setInt(8, 0);  // Estado_Documento
                insertStatement.setDouble(9, numeroCorriente);
                insertStatement.setDouble(10, idUsuarioTpv);
                insertStatement.setInt(11, comensales);
                insertStatement.setInt(12,zonaID);
                insertStatement.executeUpdate();

                // Obtener el ID de la cabecera recién creada
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newRowId = generatedKeys.getLong(1);
                }

                generatedKeys.close();
                insertStatement.close();
                insertStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
            }
        }
        actualizaMesaAOcupada(idMesa);
        return newRowId;
    }

    public int incrementarNumero() throws SQLException {
        String getMaxNumeroQuery = "SELECT MAX(Numero) AS MaxNumero FROM Cabecera_Documentos_Venta";
        PreparedStatement getMaxNumeroStatement = sqlServerConnection.getConexion().prepareStatement(getMaxNumeroQuery);

        ResultSet resultSet = getMaxNumeroStatement.executeQuery();
        double numeroCorriente = 1;

        if (resultSet.next()) {
            double maxNumero = resultSet.getDouble("MaxNumero");
            numeroCorriente = maxNumero + 1;
        }

        resultSet.close();
        getMaxNumeroStatement.close();
        return (int) numeroCorriente;
    }


    public int borrarTicket(int ticketID,int idMesa) {
        int columnasBorradas=0;
        if (sqlServerConnection.getConexion() != null) {
            try {

                String insertQuery = "DELETE FROM CABECERA_DOCUMENTOS_VENTA WHERE id = ?";
                PreparedStatement insertStatement = sqlServerConnection.getConexion().prepareStatement(insertQuery);
                insertStatement.setInt(1,ticketID);
                columnasBorradas=insertStatement.executeUpdate();
                insertStatement.close();
                borrarDetalles(ticketID);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
            }
        }
        actualizaMesaALibre(idMesa);
        return columnasBorradas;
    }

    //Borra todos los detalles de un ticket con su id
    public void borrarDetalles(int ticketID){
        System.out.println("BorrarDetalles");
        if (sqlServerConnection.getConexion() != null) {
            try {
                String insertQuery = "DELETE FROM Detalle_Documentos_Venta WHERE cabecera_id = ?";
                PreparedStatement insertStatement = sqlServerConnection.getConexion().prepareStatement(insertQuery);
                insertStatement.setInt(1,ticketID);
                insertStatement.executeUpdate();
                insertStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error en borraDetalles");
            }
        }
    }

    //Añade todas las lineas de detalles de un ticket
    public void actualizarTicket(List<DetalleDocumento> detalles, int ticketID){
        System.out.println("ActualizarTicket");
        if (sqlServerConnection.getConexion() != null) {
            try {
                String insertQuery = "INSERT INTO Detalle_Documentos_Venta (Cabecera_Id, Articulo_Id, Cantidad, Descripcion_articulo, Descripcion_larga, precio, cuota_iva, total_linea, Orden_preparacion) VALUES ";
                for (DetalleDocumento detalle:detalles) {
                    String detalleEscrito="("+ticketID+", "+detalle.getArticuloID()+", "+detalle.getCantidad()+", "+"'"+detalle.getDescripcion()+"'"+", "+"'"+detalle.getDescripcionLarga()+"'"+", "+detalle.getPrecio()+", "+detalle.getCuotaIva()+", "+detalle.getTotalLinea()+", "+detalle.getOrdenPreparacion()+") ,";
                    System.out.println(detalleEscrito);
                    insertQuery=insertQuery + detalleEscrito;
                }
                insertQuery=insertQuery.substring(0,insertQuery.length()-1);
                insertQuery=insertQuery+";";
                PreparedStatement insertStatement = sqlServerConnection.getConexion().prepareStatement(insertQuery);
                insertStatement.executeUpdate();
                insertStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error en actualizarTicket");
            }
        }

    }


    public void actualizaMesaALibre(int idMesa){
        if(sqlServerConnection.getConexion()!=null){
            try{
                String query = "UPDATE MESAS SET estado_mesa = 1 WHERE id = ?;";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, idMesa);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void actualizaMesaAOcupada(int idMesa){
        if(sqlServerConnection.getConexion()!=null){
            try{
                String query = "UPDATE MESAS SET estado_mesa = 2 WHERE id = ?;";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, idMesa);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void actualizaMesaAReservada(int idMesa){
        if(sqlServerConnection.getConexion()!=null){
            try{
                String query = "UPDATE MESAS SET estado_mesa = 3 WHERE id = ?;";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, idMesa);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void actualizarEstadoMesas(List<ZonaVenta> zonas){
        if(sqlServerConnection.getConexion()!=null){
            try{
                for (ZonaVenta zona:zonas) {
                    String query = "SELECT estado_mesa FROM Mesas WHERE zona_id = ?";
                    PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                    preparedStatement.setInt(1, zona.getId());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    int contador=0;
                    while (resultSet.next()) {
                        int estadoMesa = resultSet.getInt("estado_mesa");
                        zona.getListaMesas().get(contador).setEstado(estadoMesa);
                        contador++;
                    }
                    resultSet.close();
                    preparedStatement.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void actualizaEscribiendo(boolean escribiendo,int ticketID){
        if(sqlServerConnection.getConexion()!=null){
            try{
                System.out.println("ActualizaEscribiendo");
                String query = "UPDATE Cabecera_Documentos_Venta SET escribiendo = ? WHERE id = ?;";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setBoolean(1, escribiendo);
                preparedStatement.setInt(2,ticketID);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
                System.out.println("Error en actualizaescribiendo");
            }
        }
    }

    public boolean isEscribiendo(int ticketID){
        boolean escribiendo=false;
        if(sqlServerConnection.getConexion()!=null){
            try{
                String query = "SELECT escribiendo FROM Cabecera_Documentos_Venta WHERE id = ?;";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, ticketID);
                ResultSet resultSet=preparedStatement.executeQuery();
                while (resultSet.next()) {
                    escribiendo=resultSet.getBoolean("escribiendo");
                }
                resultSet.close();
                preparedStatement.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return escribiendo;
    }

    public void mandarCocina(int ticketID){
        if(sqlServerConnection.getConexion()!=null){
            try{
                String query = "INSERT INTO Temporal_Impresion_Cocina (id_cabecera) VALUES (?);";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, ticketID);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
