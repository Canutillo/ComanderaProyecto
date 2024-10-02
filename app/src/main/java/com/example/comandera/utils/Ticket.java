package com.example.comandera.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Ticket implements Parcelable {
    private boolean nuevo=false;
    private int id;
    private int comensales;
    private int estadoDocumento;
    private String fecha;
    private double numero;
    private double serieId;
    private List<DetalleDocumento> detallesTicket=new ArrayList<>();
    private boolean escribiendo;

    // Constructor
    public Ticket() {
    }

    // Getters and Setters


    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComensales() {
        return comensales;
    }

    public void setComensales(int comensales) {
        this.comensales = comensales;
    }

    public int getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(int estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getNumero() {
        return numero;
    }

    public void setNumero(double numero) {
        this.numero = numero;
    }

    public double getSerieId() {
        return serieId;
    }

    public void setSerieId(double serieId) {
        this.serieId = serieId;
    }

    public List<DetalleDocumento> getDetallesTicket() {
        return detallesTicket;
    }

    public void setDetallesTicket(List<DetalleDocumento> detalles) {
        this.detallesTicket = detalles;
    }

    public boolean isEscribiendo() {
        return escribiendo;
    }

    public void setEscribiendo(boolean escribiendo) {
        this.escribiendo = escribiendo;
    }

    public void anadirDetalleDocumentoVenta(Articulo articulo, List<TipoIVA> tiposDeIVA, List <TarifasDeVenta> tarifasDeVentas, String descripcion_larga, int idTarifaVenta, int ordenPreparacion,boolean nuevaEntrada){
        DetalleDocumento detalle=new DetalleDocumento();
        detalle.setArticuloID(articulo.getId());
        detalle.setOrdenPreparacion(ordenPreparacion);
        detalle.setDescripcion(articulo.getNombre());
        detalle.setEstadoComanda(0);
        detalle.setOtraRonda(true);
        String descLarga;
        if(descripcion_larga.equals("")){
            descLarga=articulo.getNombre();
        }else{
            descLarga=articulo.getNombre()+"\n"+descripcion_larga;
        }
        detalle.setDescripcionLarga(descLarga);
        detalle.setDescripcionLarga(descLarga);
        //Calcular Precio
        double iva=0;
        for (TipoIVA tipoIVA:tiposDeIVA) {
            if(articulo.getTipoIVAid()==tipoIVA.getId()){
                iva=tipoIVA.getValorIVA();
                break;
            }
        }
        detalle.setIva(iva);
        double tarifa=0;
        for (TarifasDeVenta tarifasDeVenta:tarifasDeVentas) {
            if(tarifasDeVenta.getArticuloId()==articulo.getId() && tarifasDeVenta.getTipoTarifa()==idTarifaVenta){
                tarifa=tarifasDeVenta.getPrecio();
                break;
            }
        }
        detalle.setPrecio(tarifa);
        detalle.setPvp(tarifa*(1+iva));
        detalle.setCuotaIva(detalle.getPrecio()*detalle.getIva());

        //Este boolean se encarga de hacer que se agrupen las cosas o no
        Boolean agrupar=false;
        if(this.detallesTicket.size()>0){
            if(detalle.getDescripcionLarga().equals(this.detallesTicket.get(this.detallesTicket.size()-1).getDescripcionLarga())){
                agrupar=true;
            }
        }

        if(agrupar && !nuevaEntrada){
            //Controlar cantidad
            Boolean anadir=true;
            for (int i = this.detallesTicket.size() - 1; i >= 0; i--) {
                DetalleDocumento detalleLista=this.detallesTicket.get(i);
                if (detalleLista.getDescripcionLarga().equals(detalle.getDescripcionLarga()) && detalleLista.getOrdenPreparacion()==ordenPreparacion) {
                    detalleLista.setCantidad(detalleLista.getCantidad() + 1);
                    detalleLista.setTotalLinea(detalleLista.getTotalLinea().doubleValue()+tarifa*(1+iva));
                    anadir=false;
                    System.out.println("Repetido");
                    break;
                }
            };
            if(anadir){
                detalle.setTotalLinea(tarifa*(1+iva));
                detalle.setCantidad(1);
                this.getDetallesTicket().add(detalle);
            }
            nuevaEntrada=false;
        }else{
            detalle.setTotalLinea(tarifa*(1+iva));
            detalle.setCantidad(1);
            this.getDetallesTicket().add(detalle);

        }


        System.out.println(detalle.toString());
    }

    public void anadirUnidad(int posicionLista){
        this.detallesTicket.get(posicionLista).setCantidad(this.detallesTicket.get(posicionLista).getCantidad()+1);
        this.detallesTicket.get(posicionLista).setTotalLinea(this.detallesTicket.get(posicionLista).getTotalLinea().doubleValue()+this.detallesTicket.get(posicionLista).getPvp().doubleValue());

    }

    public void quitarUnidad(int posicionLista){
        if(this.detallesTicket.get(posicionLista).getCantidad()<=1){
            this.detallesTicket.remove(posicionLista);
        }else{
            this.detallesTicket.get(posicionLista).setCantidad(this.detallesTicket.get(posicionLista).getCantidad()-1);
            this.detallesTicket.get(posicionLista).setTotalLinea(this.detallesTicket.get(posicionLista).getTotalLinea().doubleValue()-this.detallesTicket.get(posicionLista).getPvp().doubleValue());
        }
    }


    @Override
    public String toString() {
        return nuevo+" || "+
                id + " || " +
                comensales + " || " +
                estadoDocumento + " || " +
                fecha + " || " +
                numero + " || " +
                serieId + " || " +
                (detallesTicket != null ? detallesTicket.toString() : "null");
    }
    //Esta implementacion no es usada DE MOMENTO
    // Parcelable implementation
    protected Ticket(Parcel in) {
        estadoDocumento = in.readInt();
        fecha = in.readString();
        numero = in.readDouble();
        serieId = in.readDouble();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(estadoDocumento);
        parcel.writeString(fecha);
        parcel.writeDouble(numero);
        parcel.writeDouble(serieId);
    }
}
