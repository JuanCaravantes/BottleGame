import java.util.ArrayList;
import java.util.PriorityQueue;

public class WP {
	private String id;
	private Estado estadoInicial;
	private ArrayList<Nodo> espacioEstados = new ArrayList<Nodo>();
	private int profMax = 1000000;

	public WP(String id, Estado estadoInicial) {
		this.id = id;
		this.estadoInicial = estadoInicial;
	}

	public Nodo algoritmoBusqueda(String estrategia) {
		PriorityQueue<Nodo> frontera = new PriorityQueue<Nodo>();

		ArrayList<Estado> visitados = new ArrayList<Estado>();
		ArrayList<Sucesor> sucesores = new ArrayList<Sucesor>();

		Nodo aux = null;

		frontera.add(new Nodo(estadoInicial, estrategia));

		while (!frontera.isEmpty()) {			
			aux = frontera.poll();
			
			if (funcionObjetivo(aux))
				break;
			else if (aux.getProfundidad() <= profMax && !visitados.contains(aux.getEstado())) {
				visitados.add(aux.getEstado());
				espacioEstados.add(aux);
				sucesores = aux.getEstado().generarSucesores();
				añadirSucesores(frontera, sucesores, aux);
			}
		}
		
		return aux;
	}

	private void añadirSucesores(PriorityQueue<Nodo> frontera, ArrayList<Sucesor> sucesores, Nodo padre) {
		Sucesor sucesor;

		for (int i = 0; i < sucesores.size(); i++) {
			sucesor = sucesores.get(i);
			frontera.add(new Nodo(sucesor.getNuevoEstado(), sucesor.getCoste() + padre.getCosto(), sucesor.getAccion(), padre)); // cambio
		}
	}

	public ArrayList<Nodo> solucionar(String estrategia) {
		ArrayList<Nodo> solucion = new ArrayList<Nodo>();
		Nodo nodoSolucion = algoritmoBusqueda(estrategia);

		if (nodoSolucion == null)
			solucion = null;
		else {
			do {
				solucion.add(nodoSolucion);
				nodoSolucion = nodoSolucion.getPadre();
			} while (nodoSolucion != null);
		}

		return solucion;
	}
	
	public boolean funcionObjetivo(Nodo nodo) {
		return nodo.getH() == 0;
	}

	public String getId() {
		return id;
	}
}