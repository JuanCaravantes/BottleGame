import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;
import org.json.*;

public class Leer {

	private static Estado leerEstado(String estado) throws FormatException {
		ArrayList<Stack<Liquido>> botellas = new ArrayList<Stack<Liquido>>();
		JSONArray json = new JSONArray();

		leerCadenaEstado(estado, json);
		leerJSON(json, botellas);

		return new Estado(json, botellas);
	}

	public static Estado leerEstados(String nombreFichero, int numLinea) throws FormatException {
		String linea;
		ArrayList<Stack<Liquido>> botellas = new ArrayList<Stack<Liquido>>();
		JSONArray json = new JSONArray();

		try {
			FileReader fr = new FileReader(nombreFichero);
			BufferedReader br = new BufferedReader(fr);

			for (int i = 1; i < numLinea; i++)
				br.readLine();

			linea = br.readLine();
			leerCadenaEstado(linea, json);
			leerJSON(json, botellas);
			br.close();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

		return new Estado(json, botellas);
	}

	public static void exportarSolución(String id, ArrayList<Nodo> solucion) {
		File ruta = new File("Soluciones\\" + id + "_" + Nodo.getEstrategia() + ".txt");

		try {
			if (ruta.createNewFile() || ruta.exists()) {
				FileWriter fichero = null;
				PrintWriter pw = null;

				try {
					fichero = new FileWriter(ruta);
					pw = new PrintWriter(fichero);

					for (int i = solucion.size() - 1; i >= 0; i--)
						pw.println(solucion.get(i));

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (null != fichero)
							fichero.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else
				System.out.println("No ha podido ser creado el fichero");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void leerJSON(JSONArray json, ArrayList<Stack<Liquido>> botellas) {
		for (int i = 0; i < json.length(); i++)
			botellas.add(leerBotella(json.getJSONArray(i)));

		Collections.reverse(botellas);
	}

	private static void leerCadenaEstado(String linea, JSONArray json) throws FormatException {
		char[] caracteres = linea.toCharArray();
		int[] i = new int[1];
		i[0] = caracteres.length - 1;

		if (caracteres[i[0]] == ']') {
			json.put(leerPila(caracteres, i));

			for (i[0]--; i[0] >= 0; i[0]--) {
				if (caracteres[i[0]] == ',')
					json.put(leerPila(caracteres, i));
				else if (caracteres[i[0]] == '[')
					break;
				else
					throw new FormatException();
			}
		} else
			throw new FormatException();
	}

	private static Stack<Liquido> leerBotella(JSONArray array) {
		Stack<Liquido> pila = new Stack<Liquido>();
		JSONObject liquido;

		for (int j = 0; j < array.length(); j++) {
			liquido = array.getJSONObject(j);
			pila.push(new Liquido(liquido.getInt("color"), liquido.getInt("cantidad")));
		}

		return pila;
	}

	private static JSONArray leerPila(char[] caracteres, int[] i) throws FormatException {
		JSONArray pila = new JSONArray();
		JSONObject object;
		i[0]--;

		if (caracteres[i[0]] == ']') {
			object = leerObject(caracteres, i);

			if (object != null) {
				pila.put(object);

				for (i[0]--; i[0] >= 0; i[0]--)
					if (caracteres[i[0]] == ',')
						pila.put(leerObject(caracteres, i));
					else if (caracteres[i[0]] == '[')
						break;
					else
						throw new FormatException();
			}
		} else
			throw new FormatException();

		return pila;
	}

	private static JSONObject leerObject(char[] caracteres, int[] i) throws FormatException {
		JSONObject json = null;
		int color = -1, cantidad = -1;
		boolean correcto = false, vacio = false;
		i[0]--;

		if (caracteres[i[0]] == ']') {
			i[0]--;

			if (Character.isDigit(caracteres[i[0]])) {
				cantidad = Character.getNumericValue(caracteres[i[0]]);

				if (cantidad != 0) {
					i[0]--;

					if (caracteres[i[0]] == ',') {
						i[0]--;

						if (Character.isDigit(caracteres[i[0]])) {
							color = Character.getNumericValue(caracteres[i[0]]);
							i[0]--;

							if (caracteres[i[0]] == '[') {
								correcto = true;
								json = new JSONObject("{\"color\":" + color + ",\"cantidad\":" + cantidad + "}");
							}
						}
					}
				}
			}
		} else if (caracteres[i[0]] == '[')
			vacio = true;

		if (!correcto && !vacio)
			throw new FormatException();

		return json;
	}

	public static WP leerProblema(String nombreFichero) throws FormatException {
		WP problema = null;
		String[] aux;
		String[] linea;

		try {
			FileReader fr = new FileReader(nombreFichero);
			BufferedReader br = new BufferedReader(fr);

			linea = br.readLine().replaceAll(" ", "").split(",", 3);

			br.close();

			aux = linea[0].split(":");

			if (!aux[0].contains("id"))
				throw new FormatException();

			String id = aux[1].replaceAll("\"", "");

			aux = linea[1].split(":");

			if (!aux[0].contains("bottleSize"))
				throw new FormatException();

			int tamañoBotella = Integer.parseInt(aux[1]);

			aux = linea[2].split(":");

			if (!aux[0].contains("initState"))
				throw new FormatException();

			Estado estadoInicial = leerEstado(aux[1].substring(0, aux[1].length() - 1));
			estadoInicial.setTamañoBotella(tamañoBotella);

			problema = new WP(id, estadoInicial);

		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

		return problema;
	}

	public static Estado[] leerAccion(String nombreFichero, int numLinea) throws FormatException { // nuevo
		String[] linea;
		int[] accion;
		Estado[] estados = new Estado[2];

		try {
			FileReader fr = new FileReader(nombreFichero);
			BufferedReader br = new BufferedReader(fr);

			for (int i = 1; i < numLinea; i++)
				br.readLine();

			linea = br.readLine().split(" Action:");
			br.close();

			estados[0] = leerEstado(linea[0].substring(6));

			linea = linea[1].split("New State:");

			accion = extraerAccion(linea[0]);

			estados[0] = estados[0].Accion(accion[0], accion[1], accion[2]);
			estados[1] = leerEstado(linea[1]);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

		return estados;
	}

	private static int[] extraerAccion(String cadAccion) {
		int[] accion = new int[3];
		String[] num = cadAccion.substring(1, cadAccion.length() - 2).split(", ");

		for (int i = 0; i < num.length; i++)
			accion[i] = Integer.parseInt(num[i]);

		return accion;
	}

}

class FormatException extends Exception {
	public FormatException() {
		super("Error en el formato de la cadena leida");
	}
}