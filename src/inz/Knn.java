package inz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;


public class Knn {

	public static void main(String[] args) throws IOException {

		// File file = new File("G:/testy/img1PKT.txt");
		// Scanner in = new Scanner(file);
		// File file2 = new File("G:/testy/img2PKT.txt");
		// Scanner in2 = new Scanner(file2);
		ArrayList<String> lista = new ArrayList<String>();
		ArrayList<String> lista2 = new ArrayList<String>();
		FileReader fr = new FileReader("D:/IN¯YNIERKA/Desc/Desc 00_00_00.jpg.txt");
		BufferedReader bfr = new BufferedReader(fr);
		FileReader fr2 = new FileReader("D:/IN¯YNIERKA/Desc/Desc 00_00_01.jpg.txt");
		BufferedReader bfr2 = new BufferedReader(fr2);

		int liczbaPkt1 = 0, liczbaPkt2 = 0, liczbaPkt = 0; // ZMIENNE DO
															// PRZECHOWYWANIA
															// WARTOSCI
															// ODNALEZIONYCH
															// PKT;

		String pom;

		while ((pom = bfr.readLine()) != null) { // PIERWSZY PLIK WCZYTANIE DO
													// LISTY
			lista.add(pom);
			for (int i = 0; i < 64; i++) {
				pom = bfr.readLine();
				lista.add(pom);
			}
			liczbaPkt1++;
		}

		while ((pom = bfr2.readLine()) != null) { // DRUGI PLIK WCZYTANIE DO
													// LISTY
			lista2.add(pom);
			for (int i = 0; i < 64; i++) {
				pom = bfr2.readLine();
				lista2.add(pom);
			}
			liczbaPkt2++;
		} 

		if (liczbaPkt1 > liczbaPkt2) // OBLICZANIE MINIMALNEJ LICZBY PKT
										// WLASCIWIIE NIEPOTRZEBNE
			liczbaPkt = liczbaPkt2;
		else
			liczbaPkt = liczbaPkt1;

		String pom4 = ""; // ZMIENNE POMOCNICZE DO PRZECHOWYWANIA DANYCH DO
							// OBLICZEN
		String pom5 = "";
		String pom6 = "";
		String pom7 = "";
		double suma = 0;
		int z = 0;

		PrintWriter zapis = new PrintWriter("D:/IN¯YNIERKA/knn/knn.txt"); // PROWIZORYCZNY
																	// ZAPIS
																	// WYNIKOW
																	// DO PLIKU
		Iterator<String> it = lista.iterator(); // DESKRYPTORY PIERWSZEGO
												// OBRAZKA
		Iterator<String> it2 = lista2.iterator();// DESKRYPTORY 2 OBRAZKA
		int skok = 0;
		double[] tabSum = new double[liczbaPkt1 * liczbaPkt2]; // TABLICA
																// PRZECHOWUJACA
																// WYNIKI // DO
																// KNN
		int licznikObrotu = 0; // ZMIENNE POMOCNA PRZY WYLICZENIU PRZESUNIECIA

		while (it.hasNext() && it2.hasNext()) { // PETELKA PRZECHODZ¥CA PO
												// KOLEKCJACH
			// pom6 = it.next();

			for (int kk = 0; kk < liczbaPkt2 && kk < liczbaPkt; kk++) // PETLA KTORA OBSLUGUJE
													// SPRAWDZANIE PKTx1 z listy
													// 1 z PKTx1....xn z listy 2

			{

				pom6 = it.next(); // WSPOLRZEDNE PKT
				pom7 = it2.next();
				// System.out.println(pom6 + " POOOOOM 66");
				// System.out.println(pom7 + " POOOOOM 77");

				// System.out.println(pom4 + "POM4 PKT");
				for (int j = 0; j < 64; j++) { // PRZECHODZENIE PO KOLEKCJACH W
												// CELU POBRANIA DANYCH DESC
					pom4 = it.next();
					pom5 = it2.next();

					// System.out.println("POM4 = " + pom4 + "POM5 = "+ pom5);
					// //OBLICZANIE KNN SASIADOW
					suma += (Double.parseDouble(pom4) - Double
							.parseDouble(pom5))
							* (Double.parseDouble(pom4) - Double
									.parseDouble(pom5));

				}
				suma = Math.sqrt(suma);
				if (suma >= 0.8) {
					zapis.write(pom6 + pom7 + "\n"); // FORMAT
														// ZAPISU DO
														// PLIKU.
					pom6 = "";
				}
				tabSum[z++] = suma; // WRZUCENIE SUMY DO TABLICY W CELACH
									// SPRAWDZENIOWYCH

				// System.out.println("SUMA = " +suma);
				suma = 0;
				it = lista.iterator();
				for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE ITERATORA
													// LISTY PIERWSZEJ W CELU
													// JEGO ODPOWIEDNIGO
													// UMIEJSCOWIENIA
					it.next();
				}
			}
			// licznikObrotu++;
			/*
			 * if (licznikObrotu <= liczbaPkt1) licznikObrotu++; else
			 * licznikObrotu = licznikObrotu;
			 */
			it2 = lista2.iterator(); // ZEROWANIE ITERATOROW
			it = lista.iterator();
			skok = 65 * ++licznikObrotu; // WYLICZANIE WARTOSCI PRZESUNIÊCIA
											// ITERATORA PIERWSZEJ LISTY
			for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE ITERATORA LISTY
												// PIERWSZEJ W CELU JEGO
												// ODPOWIEDNIGO UMIEJSCOWIENIA
				it.next();

			}

			// it=lista.iterator();
		}

		for (int zz = 0; zz < z; zz++) // PETELKA DO WYŒWIETLANIA SUM
		{
			System.out.println(tabSum[zz] + " Element =" + zz);
		}

		zapis.close();

		// in2.close();
		fr.close();
		fr2.close();

		FileReader fr3 = new FileReader("G:/testy/Knn12.txt");
		BufferedReader bfr3 = new BufferedReader(fr3);
		int liczbaPar = 0;
		String pom8 = "", pom9;
		// Point pkt;
		ArrayList<String> listaPkt = new ArrayList<String>();
		String linia;
		int l2 = 0;
		DecimalFormat df = new DecimalFormat("#.###");

		while ((linia = bfr3.readLine()) != null) // PKT PLIK WCZYTANIE DO
		{

			for (int i = 0; i < linia.length(); i++) {
				if (Character.isDigit(linia.charAt(i))) {
					pom8 += linia.charAt(i);
					if (linia.charAt(i + 1) == '.')
						pom8 += linia.charAt(i + 1);
					// System.out.println("POM 88888888888 " + pom8);
				}

				if (linia.charAt(i) == ' ') {
					l2++;
					if (l2 == 2) {
						//System.out.println("POM 8 =" + pom8);
						listaPkt.add(pom8);
						pom8 = "";

					} else if (l2 == 3) {
						// System.out.println( " POOOOOOOOOOOOOOOOOO   "+pom8);
						listaPkt.add(pom8); // WRZUCANIE WSP PKT x oraz y
											// pierwsze 2 linie to pkt1 2
											// kolejne to jego sasiad.
						pom8 = "";
						l2 = 0;
					}

				}
			}

		}

		/*
		 * for(String pkt1 : listaPkt ) { System.out.println("pkt STR  : "
		 * +pkt1); }
		 */

		Double a = 0.0;

		ArrayList<Point> points = new ArrayList<Point>(); // LISTA NA PKT
															// DOPASOWANE
		Iterator<String> it3 = listaPkt.iterator();
		while (it3.hasNext()) {
			Point point = new Point();
			point.setX(Double.parseDouble(it3.next())); // PARSOWANIE STRINGA NA
														// DOULBE
			point.setY(Double.parseDouble(it3.next()));
			points.add(point);

		}
		PrintWriter zapis2 = new PrintWriter("G:/testy/Knn1-2.txt");
		for (Point pkt : points) // SPRAWDZANIE CZY DOBRZE WRZUCA
		{
		//	System.out.println("pkt x  : " + String.format("%.3f", pkt.getX())
		//			+ " Y  " + String.format("%.3f", pkt.getY()));
			zapis2.write(df.format(pkt.getX()) + " "
					+ df.format(pkt.getY()) + "\n");  //zapis do formatu taki jak w przykladach
		}
		fr3.close();
		zapis2.close();

	}

}
