import java.util.ArrayList;
import java.util.Scanner;

public class Ejecucion {

	public static void main(String[] args) {
		try {
			Scanner teclado = new Scanner(System.in);
			System.out.print("Introduzca el nombre del problema a resolver: ");
			String fichero = teclado.nextLine();

			WP problema = Leer.leerProblema("Problemas\\" + fichero + ".json");

			if (problema != null) {
				String estrategia;

				do {
					System.out.println("\nIntroduzca el nº correspondiente a la estrategia de búsqueda:");
					System.out.println("\t1. Depth");
					System.out.println("\t2. Breadth");
					System.out.println("\t3. Uniform");
					System.out.println("\t4. Greedy");
					System.out.println("\t5. A");
					estrategia = seleccionarEstrategia(teclado.nextInt());
				} while (estrategia == "error");

				teclado.close();

				ArrayList<Nodo> solucion = problema.solucionar(estrategia);

				Leer.exportarSolución(problema.getId(), solucion);

				for (int i = solucion.size() - 1; i >= 0; i--)
					System.out.println(solucion.get(i) + "\n");
			}

		} catch (FormatException fe) {
			System.out.println(fe.getMessage());
		}
	}

	private static String seleccionarEstrategia(int n) {
		String estrategia;

		switch (n) {
		case 1:
			estrategia = "DEPTH";
			break;
		case 2:
			estrategia = "BREADTH";
			break;
		case 3:
			estrategia = "UNIFORM";
			break;
		case 4:
			estrategia = "GREEDY";
			break;
		case 5:
			estrategia = "A";
			break;
		default:
			estrategia = "error";
			break;
		}

		return estrategia;
	}
}