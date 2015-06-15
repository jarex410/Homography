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
		String nazwa = "";
		String nazwa2 = "";
		String dir2 = "D:/IN¯YNIERKA/knn2/";
		String pom4 = "";

		// ZMIENNE POMOCNICZE DO
		// PRZECHOWYWANIA DANYCH DO
		// OBLICZEN
		String pom5 = "";
		String pom6 = "";
		String pom7 = "";

		double suma = 0;

		int z = 0;

		// ZMIENNE DO
		// PRZECHOWYWANIA
		// WARTOSCI
		// ODNALEZIONYCH
		// PKT;
		int liczbaPar = 0;

		for (int i = 0; i < 50; i++) {

			for (int j = 0; j < 6; j++) {

				for (int k = 0; k < 10; k++) {
					String TYP = "";
				
					
					ArrayList<String> lista2 = new ArrayList<String>();

					if (i < 10) {

						nazwa2 = "0" + i + "_0" + j + "_0" + k + ".jpg";

					} else {

						nazwa2 = i + "_0" + j + "_0" + k + ".jpg";

					}
					FileReader fr2 = new FileReader(dir + "Desc " + nazwa2
							+ ".txt");
					BufferedReader bfr2 = new BufferedReader(fr2);
					String pom;
					int liczbaPkt2 = 0;
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

					System.out.println("OBRAZEK       =      " + nazwa2);
					File plik = new File(dir2 + " KNN  " + nazwa2 + ".txt");
					FileWriter zapis = new FileWriter(plik, true);

					for (int ij = 0; ij < 50; ij++) {
						ArrayList<String> lista = new ArrayList<String>();
						int LiczbaParPom = 0;
						if (ij < 10) {
							nazwa = "0" + ij + "_00_00.jpg";
						} else {
							nazwa = ij + "_00_00.jpg";
						}

						FileReader fr = new FileReader(dir + "Desc " + nazwa
								+ ".txt");
						BufferedReader bfr = new BufferedReader(fr);

						
						int liczbaPkt1 = 0;// liczbaPkt = 0;

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

				//		if (liczbaPkt1 > liczbaPkt2) // OBLICZANIE MINIMALNEJ
														// LICZBY PKT
							// WLASCIWIIE NIEPOTRZEBNE
					//		liczbaPkt = liczbaPkt2;
				//		else
				//			liczbaPkt = liczbaPkt1;

						Iterator<String> it = lista.iterator(); // DESKRYPTORY//
																// PIERWSZEGO//
																// OBRAZKA
						Iterator<String> it2 = lista2.iterator();// DESKRYPTORY
																	// 2 OBRAZKA
						int skok = 0;
						int licznikObrotu = 0; // ZMIENNE POMOCNA PRZY
												// WYLICZENIU PRZESUNIECIA

						while (it.hasNext() && it2.hasNext()) { // PETELKA
																// PRZECHODZ¥CA
																// PO
							// KOLEKCJACH
							// pom6 = it.next();
							double min1 = 100;
							double min2 = 100;
							String wsp1Max = "";
							String wsp2Max = "";
							for (int kk = 0; kk < liczbaPkt2 && kk < liczbaPkt1; kk++) // PETLA
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
																	// PO
																	// KOLEKCJACH
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
								/*
								 * if (kk == 0) { min1 = suma; wsp1Max = pom6 +
								 * pom7; } else if (kk == 1) { min2 = suma;
								 * wsp2Max = pom6 + pom7; }
								 */
								if (suma < 0.8 && suma < min1) {
									min1 = suma;
									wsp1Max = pom6 + pom7 + "\n";

								} else if (suma < 0.8 && suma < min2
										&& suma > min1) {
									min2 = suma;
									wsp2Max = pom6 + pom7 + "\n";

								}
								if (min1 >5) 
									wsp1Max = "";

								if (min2 > 5)
									wsp2Max = "";


								/*
								 * if(kk<1 && suma < 0.8) { zapis.write(pom6 +
								 * pom7 + "\n"); // FORMAT liczbaPar++; pom6 =
								 * ""; }
								 */
								// tabSum[z++] = suma; // WRZUCENIE SUMY DO
								// TABLICY
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
							if (!wsp1Max.equals(""))
								LiczbaParPom++;
							if (!wsp2Max.equals(""))
								LiczbaParPom++;
							// zapis.write(wsp1Max + wsp2Max); // FORMAT

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
						}

						/*
						 * for (int zz = 0; zz < z; zz++) // PETELKA DO
						 * WYŒWIETLANIA SUM { System.out.println(tabSum[zz] +
						 * " Element =" + zz); }
						 */
						if (LiczbaParPom > liczbaPar) {
							liczbaPar = LiczbaParPom;
							TYP = nazwa;
						}
						
						fr.close();
						fr2.close();
						System.out.println("TYP ===" + TYP + "\n" + liczbaPar);

					}
					zapis.write("Liczba Par = " + liczbaPar + "\n KLASA = "
							+ TYP);

					zapis.close();

					// in2.close();

				}

			}

		}

	}

}
