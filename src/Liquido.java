public class Liquido {
	private int color;
	private int cantidad;

	public Liquido(int color, int cantidad) {
		this.color = color;
		this.cantidad = cantidad;
	}

	public int getColor() {
		return color;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void sumar(int num) {
		cantidad += num;
	}

	public Liquido clonar() {
		return new Liquido(color, cantidad);
	}

	public String toString() {
		return "[" + color + "," + cantidad + "]";
	}
}