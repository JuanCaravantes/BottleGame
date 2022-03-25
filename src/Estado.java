import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONArray;

public class Estado {
	private JSONArray json;
	private ArrayList<Stack<Liquido>> botellas;
	private int tamañoBotella;
	private String md5;

	public Estado(JSONArray json, ArrayList<Stack<Liquido>> botellas) {
		this.json = json;
		this.botellas = botellas;
		calcularMD5();
	}

	public Estado(ArrayList<Stack<Liquido>> botellas, int tamañoBotella) {
		this.botellas = botellas;
		this.tamañoBotella = tamañoBotella;
		calcularMD5();
	}

	public ArrayList<Sucesor> generarSucesores() {
		ArrayList<Sucesor> sucesores = new ArrayList<Sucesor>();
		Estado nuevoEstado;
		Stack<Liquido> origen;
		int[] accion;
		int cantidad;

		for (int i = 0; i < botellas.size(); i++) {
			origen = clonarBotella(botellas.get(i));

			if (origen.isEmpty())
				continue;

			for (int j = 0; j < botellas.size(); j++)
				if (j != i && !isFull(botellas.get(j))) {
					cantidad = origen.peek().getCantidad();
					nuevoEstado = Accion(i, j, cantidad);

					if (nuevoEstado == null)
						continue;

					accion = generarAccion(i, j, cantidad);

					sucesores.add(new Sucesor(accion, nuevoEstado));
				}
		}

		return sucesores;
	}

	private int[] generarAccion(int origen, int destino, int cantidad) {
		return new int[] { origen, destino, cantidad };
	}

	private boolean ES_AccionPosible(int origen, int destino, int cantidad) {
		Stack<Liquido> botellaOrigen = clonarBotella(botellas.get(origen));
		Stack<Liquido> botellaDestino = clonarBotella(botellas.get(destino));

		boolean posible = false;

		int cantidadOrigen = cantidadPila(botellaOrigen);

		if (cantidadOrigen >= cantidad) {
			int cantidadDestino = cantidadPila(botellaDestino);

			if (tamañoBotella - cantidadDestino >= cantidad)
				posible = ES_Valido(botellaOrigen, botellaDestino, cantidad);
		}

		return posible;
	}

	private boolean ES_Valido(Stack<Liquido> origen, Stack<Liquido> destino, int cantidad) {
		int cantidadOrigen = origen.peek().getCantidad();
		boolean esValido = true;

		if (cantidadOrigen != cantidad || (!destino.isEmpty() && origen.peek().getColor() != destino.peek().getColor()))
			esValido = false;

		return esValido;
	}

	public boolean isFull(Stack<Liquido> pila) {
		return tamañoBotella == cantidadPila(pila);
	}

	public int cantidadPila(Stack<Liquido> pila) {
		Stack<Liquido> clon = clonarBotella(pila);

		int cantidad = 0;

		while (!clon.isEmpty())
			cantidad += clon.pop().getCantidad();

		return cantidad;
	}

	public Estado Accion(int origen, int destino, int cantidad) {
		Estado estado;
		ArrayList<Stack<Liquido>> nuevoEstado = null;

		if (ES_AccionPosible(origen, destino, cantidad)) {
			Liquido tupla;
			nuevoEstado = clonarEstado();

			tupla = nuevoEstado.get(origen).pop();
			añadirTupla(tupla, nuevoEstado.get(destino));
		}

		if (nuevoEstado == null)
			estado = null;
		else
			estado = new Estado(nuevoEstado, tamañoBotella);

		return estado;
	}

	private void añadirTupla(Liquido tupla, Stack<Liquido> botella) {
		if (botella.isEmpty() || tupla.getColor() != botella.peek().getColor())
			botella.push(tupla);
		else
			botella.peek().sumar(tupla.getCantidad());
	}

	public static Stack<Liquido> clonarBotella(Stack<Liquido> botella) {
		Stack<Liquido> clon = new Stack<Liquido>();
		Iterator<Liquido> it = botella.iterator();

		while (it.hasNext())
			clon.push(it.next().clonar());

		return clon;
	}

	public ArrayList<Stack<Liquido>> clonarEstado() {
		ArrayList<Stack<Liquido>> clon = new ArrayList<Stack<Liquido>>();

		for (int i = 0; i < botellas.size(); i++)
			clon.add(clonarBotella(botellas.get(i)));

		return clon;
	}

	public void setTamañoBotella(int tamañoBotella) {
		this.tamañoBotella = tamañoBotella;
	}

	public String getMd5() {
		return md5;
	}

	public String toString() {
		String cadena = "[";
		ArrayList<Stack<Liquido>> clon = clonarEstado();
		int tamaño;

		for (int i = 0; i < clon.size(); i++) {
			cadena += "[";
			tamaño = clon.get(i).size();

			for (int j = 0; j < tamaño; j++)
				if (j != tamaño - 1)
					cadena += clon.get(i).pop().toString() + ",";
				else
					cadena += clon.get(i).pop().toString();

			if (i != clon.size() - 1)
				cadena += "],";
			else
				cadena += "]";
		}

		cadena += "]";

		return cadena;
	}

	private void calcularMD5() {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(toString().getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			md5 = number.toString(16);

			while (md5.length() < 32) {
				md5 = "0" + md5;
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean equals(Object obj) {
		boolean igual;

		if (this == obj)
			igual = true;
		else if (obj == null)
			igual = false;
		else if (getClass() != obj.getClass())
			igual = false;
		else {
			Estado other = (Estado) obj;

			igual = Objects.equals(md5, other.getMd5());
		}

		return igual;
	}
}