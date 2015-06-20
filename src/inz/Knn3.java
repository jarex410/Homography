package inz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class Knn3 {

	public static void main(String[] args) throws IOException {
		String dir = "D:/IN¯YNIERKA/Desc/";
		String nazwa = "";
		String nazwa2 = "";
		String dir2 = "D:/IN¯YNIERKA/knn2/";
		String pom4 = "";
		ArrayList<String> lista = new ArrayList<String>(); // lista na obrazek
															// porównywany
		ArrayList<String> lista2 = new ArrayList<String>(); // lista na obrazek
															// bazowy
		// ZMIENNE POMOCNICZE DO
		// PRZECHOWYWANIA DANYCH DO
		// OBLICZEN
		String pom5 = "";
		String pom6 = "";
		String pom7 = "";

		double suma = 0;

		int PoprawneKlasyfikacje = 0;

		// int z = 0;

		// ZMIENNE DO
		// PRZECHOWYWANIA
		// WARTOSCI
		// ODNALEZIONYCH
		// PKT;

		for (Integer i = 4; i <5 ; i++) {

			for (int j = 0; j < 6; j++) {

				for (int k = 0; k < 10; k++) {
					 if (j == 0 && k == 0)
					 continue;
					 if (j == 0 && k < 9)
					 continue;

					String TYP = "";
					int liczbaPar = 0; // próg 0.80

					if (i < 10) {

						nazwa2 = "0" + i + "_0" + j + "_0" + k + ".jpg";

					} else {

						nazwa2 = i + "_0" + j + "_0" + k + ".jpg";

					}
					FileReader fr2 = new FileReader(dir + "Desc " + nazwa2
							+ ".txt");
					BufferedReader bfr2 = new BufferedReader(fr2);
					String pom;
					int liczbaPkt1 = 0;
					while ((pom = bfr2.readLine()) != null) { // wcytanie
						// obrazka
						// do
						// sklasyfikowania

						lista.add(pom);
						for (int ii = 0; ii < 64; ii++) {
							pom = bfr2.readLine();
							lista.add(pom);
						}
						liczbaPkt1++;
					}
					bfr2.close();
					fr2.close();

					System.out.println("OBRAZEK       =      " + nazwa2
							+ "\t Poprawnie sklasyfikowane = "
							+ PoprawneKlasyfikacje);
					File plik = new File(dir2 + " KNN  " + nazwa2 + ".txt");
					FileWriter zapis = new FileWriter(plik, true);
					

					for (int ij = 0; ij < 50; ij++) { // petla przeszukujaca
														// obrazy wzorcowe

						int LiczbaParPom = 0;
						if (ij < 10) {
							nazwa = "0" + ij + "_00_00.jpg";
						} else {
							nazwa = ij + "_00_00.jpg";
						}

						FileReader fr = new FileReader(dir + "Desc " + nazwa
								+ ".txt");
						BufferedReader bfr = new BufferedReader(fr);

						int liczbaPkt2 = 0;// liczbaPkt = 0;

						while ((pom = bfr.readLine()) != null) { // PIERWSZY
																	// PLIK
																	// WCZYTANIE
																	// DO
							// LISTY
							lista2.add(pom);
							for (int ii = 0; ii < 64; ii++) {
								pom = bfr.readLine();
								lista2.add(pom);
							}
							liczbaPkt2++;
						}
						bfr.close();
						fr.close();

					//	Iterator<String> it = lista.iterator(); // DESKRYPTORY
																// PIERWSZEGO/OBRAZKA
					//	Iterator<String> it2 = lista2.iterator();// DESKRYPTORY
								// 2 OBRAZKA
						int skok = 0;
						int licznikObrotu = 0; // ZMIENNE POMOCNA PRZY
						//int z=0;		

						// WYLICZENIU PRZESUNIECIA
						for(int zz=0;zz < liczbaPkt1;zz++) { // PETELKA
													// PRZECHODZ¥CA
													// PO
													// KOLEKCJACH
							double min1 = 100; // Najblizsi sasiedzi
							double min2 = 100;
							// String wsp1Max = "";
							// String wsp2Max = "";

							for (int kk = 0; kk <liczbaPkt2; kk++) // PETLA
							// KTORA
							// OBSLUGUJE
							// SPRAWDZANIE PKTx1 z listy
							// 1 z PKTx1....xn z listy 2

							{ 
								pom6 = lista.get(skok); // WSPOLRZEDNE PKT
								pom7 = lista2.get(kk*65);
								//System.out.println("POM6 " + pom6+ " \t" + pom7);

								for (int jj = 1; jj <= 64; jj++) { // PRZECHODZENIE
																	// PO
																	// KOLEKCJACH
																	// W
									// CELU POBRANIA DANYCH DESC
									pom4 = lista.get(skok+jj);
									pom5 = lista2.get(kk*65+jj);
									suma += (Double.parseDouble(pom4) - Double
											.parseDouble(pom5))
											* (Double.parseDouble(pom4) - Double
													.parseDouble(pom5));

								}
								
								suma = Math.sqrt(suma);

								if (suma < min1) {
									min2 = min1;
									min1 = suma;


								} else if (suma < min2) {
									min2 = suma;

								}

								suma = 0;

/*								it = lista.iterator();
								for (int zz = 0; zz < skok; zz++) { // PRZESUWANIE
									// ITERATORA
									// LISTY
									// PIERWSZEJ W CELU JEGO
									// ODPOWIEDNIGO UMIEJSCOWIENIA
									it.next();
								}*/

							}
						//	it2 = lista2.iterator();

							skok = 65 * ++licznikObrotu; // WYLICZANIE WARTOSCI
							// PRZESUNIÊCIA
							// ITERATORA PIERWSZEJ LISTY
							

							if (min1 / min2 < 0.8) { // zliczanie par
								LiczbaParPom++;

							}

							//pom6 = "";
							
							
						}


						if (LiczbaParPom > liczbaPar) {
							liczbaPar = LiczbaParPom;
							TYP = nazwa;
						}

						if (liczbaPar == liczbaPkt1 || liczbaPar > 150) {

							if (i == ij) {

							}
							System.out.println("LICZBA PAR = " + liczbaPar);
							break;
							
						}
						System.out.println("Porownanie z   " + ij
								+ "\n TYP ===" + TYP + "\n" + liczbaPar);
						lista2.clear();
	
					}
					if (i < 10) {
						if (TYP.startsWith(i.toString(), 1))
							PoprawneKlasyfikacje++;
					} else if (TYP.startsWith(i.toString()))
						PoprawneKlasyfikacje++;

					zapis.write("Liczba Par = " + liczbaPar + "\n KLASA = "
							+ TYP);

					zapis.close();
					lista.clear();
				}

			}
			File plik = new File(dir2 + " WYNIKI dla " + nazwa2.substring(6,8) + ".txt");
			FileWriter zapis = new FileWriter(plik, true);
			zapis.write("Liczba poprawnych klasyfikacji ="
					+ PoprawneKlasyfikacje);
			zapis.close();
		}

	}

}
