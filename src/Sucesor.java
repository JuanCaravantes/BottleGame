public class Sucesor {
	private int[] accion = new int[3];
	private Estado nuevoEstado;
	private double coste = 1;

	public Sucesor(int[] accion, Estado nuevoEstado) {
		this.accion = accion;
		this.nuevoEstado = nuevoEstado;
	}

	public int[] getAccion() {
		return accion;
	}

	public Estado getNuevoEstado() {
		return nuevoEstado;
	}

	public double getCoste() {
		return coste;
	}

	private String cadenaAccion() {
		return "(" + accion[0] + "," + accion[1] + "," + accion[2] + ")";
	}

	public String toString() {
		return "[" + cadenaAccion() + ", " + nuevoEstado + ", " + coste + "]";
	}
}