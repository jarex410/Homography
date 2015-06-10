package inz;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ejml.data.DenseMatrix64F;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.geo.AssociatedPair;

public class Knn1 {

	public static void main(String[] args) throws IOException {
		String dir = "D:/IN¯YNIERKA/Desc/";
		String pom = "";
		String nazwa = "";
		String nazwa2 = "";
		String dir2 = "D:/IN¯YNIERKA/Knn/";
		String pom4 = "";
		ArrayList<String> lista = new ArrayList<String>();
		ArrayList<String> lista2 = new ArrayList<String>();

		int liczbaPkt1 = 0, liczbaPkt2 = 0, liczbaPkt = 0; // ZMIENNE DO
		// PRZECHOWYWANIA
		// WARTOSCI
		// ODNALEZIONYCH
		// PKT;
		int liczbaPar = 0;

		for (int i = 0; i < 50; i++) {

			if (i < 10) {
				nazwa = "0" + i + "_00_00.jpg";
			} else {
				nazwa = i + "_00_00.jpg";
			}

			for (int j = 0; j < 6; j++) {

				for (int k = 0; k < 10; k++) {

					if (i < 10) {
						nazwa2 = "0" + i + "_0" + j + "_0" + k + ".jpg";
						File plik = new File(dir2 + " KNN  " + nazwa + " TO  "
								+ nazwa2+".txt");
						FileWriter zapis = new FileWriter(plik, true);
						FileReader fr = new FileReader(dir + "Desc " + nazwa
								+ ".txt");
						BufferedReader bfr = new BufferedReader(fr);
						FileReader fr2 = new FileReader(dir + "Desc " + nazwa2
								+ ".txt");
						BufferedReader bfr2 = new BufferedReader(fr2);

						while ((pom = bfr.readLine()) != null) { // PIERWSZY
																	// PLIK
																	// WCZYTANIE
																	// DO
							// LISTY
							lista.add(pom);
							for (int ii = 0; ii < 64; ii++) {
								pom = bfr.readLine();
								lista.add(pom);
							}
							liczbaPkt1++;
						}

						while ((pom = bfr2.readLine()) != null) { // DRUGI PLIK
																	// WCZYTANIE
																	// DO
							// LISTY
							lista2.add(pom);
							for (int ii = 0; ii < 64; ii++) {
								pom = bfr2.readLine();
								lista2.add(pom);
							}
							liczbaPkt2++;
						}

						if (liczbaPkt1 > liczbaPkt2) // OBLICZANIE MINIMALNEJ
														// LICZBY PKT
							// WLASCIWIIE NIEPOTRZEBNE
							liczbaPkt = liczbaPkt2;
						else
							liczbaPkt = liczbaPkt1;

					// ZMIENNE POMOCNICZE DO
											// PRZECHOWYWANIA DANYCH DO
						// OBLICZEN
						String pom5 = "";
						String pom6 = "";
						String pom7 = "";
						String wsp1Max = "";
						String wsp2Max = "";
						double suma = 0;
						double min1 = 100;
						double min2 = 100;
						int z = 0;

						// ZAPIS
						// WYNIKOW
						// DO PLIKU
						Iterator<String> it = lista.iterator(); // DESKRYPTORY
																// PIERWSZEGO
						// OBRAZKA
						Iterator<String> it2 = lista2.iterator();// DESKRYPTORY
																	// 2 OBRAZKA
						int skok = 0;
						double[] tabSum = new double[liczbaPkt1 * liczbaPkt2]; // TABLICA
						String pomZapas = ""; // PRZECHOWUJACA
						// WYNIKI // DO
						// KNN
						int licznikObrotu = 0; // ZMIENNE POMOCNA PRZY
												// WYLICZENIU PRZESUNIECIA

						while (it.hasNext() && it2.hasNext()) { // PETELKA
																// PRZECHODZ¥CA
																// PO
							// KOLEKCJACH
							// pom6 = it.next();

							for (int kk = 0; kk < liczbaPkt2 && kk < liczbaPkt; kk++) // PETLA
							// KTORA
							// OBSLUGUJE
							// SPRAWDZANIE PKTx1 z listy
							// 1 z PKTx1....xn z listy 2

							{
								pom6 = it.next(); // WSPOLRZEDNE PKT
								pom7 = it2.next();
								// System.out.println(pom6 + " POOOOOM 66");
								// System.out.println(pom7 + " POOOOOM 77");

								// System.out.println(pom4 + "POM4 PKT");
								for (int jj = 0; jj < 64; jj++) { // PRZECHODZENIE
																// PO KOLEKCJACH
																// W
									// CELU POBRANIA DANYCH DESC
									pom4 = it.next();
									pom5 = it2.next();

									// System.out.println("POM4 = " + pom4 +
									// "POM5 = "+ pom5);
									// //OBLICZANIE KNN SASIADOW
									suma += (Double.parseDouble(pom4) - Double
											.parseDouble(pom5))
											* (Double.parseDouble(pom4) - Double
													.parseDouble(pom5));

								}
								suma = Math.sqrt(suma);

								if (kk == 0) {
									min1 = suma;
									wsp1Max = pom6 + pom7;
								} else if (kk == 1) {
									min2 = suma;
									wsp2Max = pom6 + pom7;
								}
								if (suma < 0.8 && suma < min1) {
									min1 = suma;
									wsp1Max = pom6 + pom7;
								} else if (suma < 0.8 && suma < min2) {
									min2 = suma;
									wsp2Max = pom6 + pom7;
								}
								/*
								 * if(kk<1 && suma < 0.8) { zapis.write(pom6 +
								 * pom7 + "\n"); // FORMAT liczbaPar++; pom6 =
								 * ""; }
								 */
								tabSum[z++] = suma; // WRZUCENIE SUMY DO TABLICY
													// W CELACH
								// SPRAWDZENIOWYCH

								// System.out.println("SUMA = " +suma);
								suma = 0;
								it = lista.iterator();
								for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE
																	// ITERATORA
									// LISTY PIERWSZEJ W CELU
									// JEGO ODPOWIEDNIGO
									// UMIEJSCOWIENIA
									it.next();
								}
							}
							zapis.write(wsp1Max + "\n" + wsp2Max + "\n"); // FORMAT
							liczbaPar += 2;
							pom6 = "";
							// licznikObrotu++;
							/*
							 * if (licznikObrotu <= liczbaPkt1) licznikObrotu++;
							 * else licznikObrotu = licznikObrotu;
							 */
							it2 = lista2.iterator(); // ZEROWANIE ITERATOROW
							it = lista.iterator();
							skok = 65 * ++licznikObrotu; // WYLICZANIE WARTOSCI
															// PRZESUNIÊCIA
							// ITERATORA PIERWSZEJ LISTY
							for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE
																// ITERATORA
																// LISTY
								// PIERWSZEJ W CELU JEGO
								// ODPOWIEDNIGO UMIEJSCOWIENIA
								it.next();

							}

							// it=lista.iterator();
						}
						/*
						 * for (int zz = 0; zz < z; zz++) // PETELKA DO
						 * WYŒWIETLANIA SUM { System.out.println(tabSum[zz] +
						 * " Element =" + zz); }
						 */
						zapis.write("Liczba Par = " + liczbaPar);
						zapis.close();

						// in2.close();
						fr.close();
						fr2.close();

					} else {
						nazwa2 = i + "_0" + j + "_0" + k + ".jpg";
						File plik = new File(dir2 + " KNN  " + nazwa + " TO  "
								+ nazwa2+".txt");
						FileWriter zapis = new FileWriter(plik, true);
						FileReader fr = new FileReader(dir + "Desc " + nazwa
								+ ".txt");
						BufferedReader bfr = new BufferedReader(fr);
						FileReader fr2 = new FileReader(dir + "Desc " + nazwa2
								+ ".txt");
						BufferedReader bfr2 = new BufferedReader(fr2);


						while ((pom = bfr.readLine()) != null) { // PIERWSZY
																	// PLIK
																	// WCZYTANIE
																	// DO
							// LISTY
							lista.add(pom);
							for (int ii = 0; ii < 64; ii++) {
								pom = bfr.readLine();
								lista.add(pom);
							}
							liczbaPkt1++;
						}

						while ((pom = bfr2.readLine()) != null) { // DRUGI PLIK
																	// WCZYTANIE
																	// DO
							// LISTY
							lista2.add(pom);
							for (int ii = 0; ii < 64; ii++) {
								pom = bfr2.readLine();
								lista2.add(pom);
							}
							liczbaPkt2++;
						}

						if (liczbaPkt1 > liczbaPkt2) // OBLICZANIE MINIMALNEJ
														// LICZBY PKT
							// WLASCIWIIE NIEPOTRZEBNE
							liczbaPkt = liczbaPkt2;
						else
							liczbaPkt = liczbaPkt1;

											// PRZECHOWYWANIA DANYCH DO
						// OBLICZEN
						String pom5 = "";
						String pom6 = "";
						String pom7 = "";
						String wsp1Max = "";
						String wsp2Max = "";
						double suma = 0;
						double min1 = 100;
						double min2 = 100;
						int z = 0;

						// ZAPIS
						// WYNIKOW
						// DO PLIKU
						Iterator<String> it = lista.iterator(); // DESKRYPTORY
																// PIERWSZEGO
						// OBRAZKA
						Iterator<String> it2 = lista2.iterator();// DESKRYPTORY
																	// 2 OBRAZKA
						int skok = 0;
						double[] tabSum = new double[liczbaPkt1 * liczbaPkt2]; // TABLICA
						String pomZapas = ""; // PRZECHOWUJACA
						// WYNIKI // DO
						// KNN
						int licznikObrotu = 0; // ZMIENNE POMOCNA PRZY
												// WYLICZENIU PRZESUNIECIA

						while (it.hasNext() && it2.hasNext()) { // PETELKA
																// PRZECHODZ¥CA
																// PO
							// KOLEKCJACH
							// pom6 = it.next();

							for (int kk = 0; kk < liczbaPkt2 && kk < liczbaPkt; kk++) // PETLA
							// KTORA
							// OBSLUGUJE
							// SPRAWDZANIE PKTx1 z listy
							// 1 z PKTx1....xn z listy 2

							{
								pom6 = it.next(); // WSPOLRZEDNE PKT
								pom7 = it2.next();
								// System.out.println(pom6 + " POOOOOM 66");
								// System.out.println(pom7 + " POOOOOM 77");

								// System.out.println(pom4 + "POM4 PKT");
								for (int jj = 0; jj < 64; jj++) { // PRZECHODZENIE
																// PO KOLEKCJACH
																// W
									// CELU POBRANIA DANYCH DESC
									pom4 = it.next();
									pom5 = it2.next();

									// System.out.println("POM4 = " + pom4 +
									// "POM5 = "+ pom5);
									// //OBLICZANIE KNN SASIADOW
									suma += (Double.parseDouble(pom4) - Double
											.parseDouble(pom5))
											* (Double.parseDouble(pom4) - Double
													.parseDouble(pom5));

								}
								suma = Math.sqrt(suma);

								if (kk == 0) {
									min1 = suma;
									wsp1Max = pom6 + pom7;
								} else if (kk == 1) {
									min2 = suma;
									wsp2Max = pom6 + pom7;
								}
								if (suma < 0.8 && suma < min1) {
									min1 = suma;
									wsp1Max = pom6 + pom7;
								} else if (suma < 0.8 && suma < min2) {
									min2 = suma;
									wsp2Max = pom6 + pom7;
								}
								/*
								 * if(kk<1 && suma < 0.8) { zapis.write(pom6 +
								 * pom7 + "\n"); // FORMAT liczbaPar++; pom6 =
								 * ""; }
								 */
								tabSum[z++] = suma; // WRZUCENIE SUMY DO TABLICY
													// W CELACH
								// SPRAWDZENIOWYCH

								// System.out.println("SUMA = " +suma);
								suma = 0;
								it = lista.iterator();
								for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE
																	// ITERATORA
									// LISTY PIERWSZEJ W CELU
									// JEGO ODPOWIEDNIGO
									// UMIEJSCOWIENIA
									it.next();
								}
							}
							zapis.write(wsp1Max + "\n" + wsp2Max + "\n"); // FORMAT
							liczbaPar += 2;
							pom6 = "";
							// licznikObrotu++;
							/*
							 * if (licznikObrotu <= liczbaPkt1) licznikObrotu++;
							 * else licznikObrotu = licznikObrotu;
							 */
							it2 = lista2.iterator(); // ZEROWANIE ITERATOROW
							it = lista.iterator();
							skok = 65 * ++licznikObrotu; // WYLICZANIE WARTOSCI
															// PRZESUNIÊCIA
							// ITERATORA PIERWSZEJ LISTY
							for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE
																// ITERATORA
																// LISTY
								// PIERWSZEJ W CELU JEGO
								// ODPOWIEDNIGO UMIEJSCOWIENIA
								it.next();

							}

							// it=lista.iterator();
						}
						/*
						 * for (int zz = 0; zz < z; zz++) // PETELKA DO
						 * WYŒWIETLANIA SUM { System.out.println(tabSum[zz] +
						 * " Element =" + zz); }
						 */
						zapis.write("Liczba Par = " + liczbaPar);
						zapis.close();

						// in2.close();
						fr.close();
						fr2.close();
					}

				}

			}


		}

	}

}
