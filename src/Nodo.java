import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

public class Nodo implements Comparable<Nodo> {

	static private int idMax = 0;
	static private String estrategia;

	private int id;
	private Estado estado;
	private double valor;
	private int profundidad;
	private double costo;
	private float h;
	private int[] accion;
	private Nodo padre;

	public Nodo(Estado estado, String estrategia) {
		id = idMax;
		idMax++;
		this.estado = estado;
		costo = 0;
		accion = null;
		padre = null;
		Nodo.estrategia = estrategia;
		profundidad = 0;
		funcionHeuristica();
		setValor();
	}

	public Nodo(Estado estado, double costo, int[] accion, Nodo padre) {
		id = idMax;
		idMax++;
		this.estado = estado;
		this.costo = costo;
		this.accion = accion;
		this.padre = padre;
		profundidad = padre.getProfundidad() + 1;
		funcionHeuristica();
		setValor();
	}

	public int getId() {
		return id;
	}

	public Estado getEstado() {
		return estado;
	}

	public double getValor() {
		return valor;
	}
	
	private String imprimirValor() {
        return new DecimalFormat("#.##").format(valor);
	}

	private void setValor() {
		switch (estrategia) {
		case "BREADTH":
			valor = profundidad;
			break;
		case "DEPTH":
			valor = 1.0 / (1 + profundidad);
			break;
		case "UNIFORM":
			valor = costo;
			break;
		case "GREEDY":
			valor = h;
			break;
		case "A":
			valor = h + costo;
			break;
		}
	}
	
	private void funcionHeuristica() {
		ArrayList<Stack<Liquido>> botellas = estado.clonarEstado();
		ArrayList<Integer> tiposVistos = new ArrayList<Integer>();
		int contador, color, numBotellas = botellas.size();
		
		for (int i = 0; i < numBotellas; i++) {
			contador = botellas.get(i).size();
			
			if (contador == 0)
				h++;
			else {
				h += contador;
				color = botellas.get(i).peek().getColor();
				
				if (!tiposVistos.contains(color))
					tiposVistos.add(color);
				else
					h++;
			}
		}
		
		h -= numBotellas;
	}

	public int getProfundidad() {
		return profundidad;
	}

	public double getCosto() {
		return costo;
	}

	public float getH() {
		return h;
	}

	private String cadenaAccion() {
		return accion != null ? "(" + accion[0] + "," + accion[1] + "," + accion[2] + ")" : null;
	}

	public Nodo getPadre() {
		return padre;
	}
	
	public static String getEstrategia() {
		return estrategia;
	}

	public String toString() {
		return padre != null
				? "[" + id + "][" + costo + "," + estado.getMd5() + "," + padre.getId() + "," + cadenaAccion() + ","
						+ profundidad + "," + h + "," + imprimirValor() + "]"
				: "[" + id + "][" + costo + "," + estado.getMd5() + "," + padre + "," + cadenaAccion() + "," + profundidad + ","
						+ h + "," + imprimirValor() + "]";
	}

	public int compareTo(Nodo nodo) {
		int compare = -1;

		if (valor > nodo.getValor())
			compare = 1;
		else if (valor == nodo.getValor() && id > nodo.getId())
			compare = 1;

		return compare;
	}
}