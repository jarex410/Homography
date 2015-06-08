/*
 * Copyright (c) 2011-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package inz;

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.geo.Estimate1ofEpipolar;
import boofcv.abst.geo.fitting.DistanceFromModelResidual;
import boofcv.abst.geo.fitting.GenerateEpipolarMatrix;
import boofcv.abst.geo.fitting.ModelManagerEpipolarMatrix;
import boofcv.alg.geo.f.FundamentalResidualSampson;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.factory.geo.EnumEpipolar;
import boofcv.factory.geo.EpipolarError;
import boofcv.factory.geo.FactoryMultiView;
import boofcv.gui.feature.AssociationPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;

import org.ddogleg.fitting.modelset.ModelFitter;
import org.ddogleg.fitting.modelset.ModelManager;
import org.ddogleg.fitting.modelset.ModelMatcher;
import org.ddogleg.fitting.modelset.ransac.Ransac;
import org.ddogleg.struct.FastQueue;
import org.ejml.data.DenseMatrix64F;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A Fundamental matrix describes the epipolar relationship between two images.
 * If two points, one from each image, match, then the inner product around the
 * Fundamental matrix will be zero. If a fundamental matrix is known, then
 * information about the scene and its structure can be extracted.
 *
 * Below are two examples of how a Fundamental matrix can be computed using
 * different. The robust technique attempts to find the best fit Fundamental
 * matrix to the data while removing noisy matches, The simple version just
 * assumes that all the matches are correct. Similar techniques can be used to
 * fit various other types of motion or structural models to observations.
 *
 * The input image and associated features are displayed in a window. In another
 * window, inlier features from robust model fitting are shown.
 *
 * @author Peter Abeles
 */
public class ExampleFundamentalMatrix {

	/**
	 * Given a set of noisy observations, compute the Fundamental matrix while
	 * removing the noise.
	 *
	 * @param matches
	 *            List of associated features between the two images
	 * @param inliers
	 *            List of feature pairs that were determined to not be noise.
	 * @return The found fundamental matrix.
	 */
	public static DenseMatrix64F robustFundamental(
			List<AssociatedPair> matches, List<AssociatedPair> inliers) {

		// used to create and copy new instances of the fit model
		ModelManager<DenseMatrix64F> managerF = new ModelManagerEpipolarMatrix();
		// Select which linear algorithm is to be used. Try playing with the
		// number of remove ambiguity points
		Estimate1ofEpipolar estimateF = FactoryMultiView.computeFundamental_1(
				EnumEpipolar.FUNDAMENTAL_7_LINEAR, 2);
		// Wrapper so that this estimator can be used by the robust estimator
		GenerateEpipolarMatrix generateF = new GenerateEpipolarMatrix(estimateF);

		// How the error is measured
		DistanceFromModelResidual<DenseMatrix64F, AssociatedPair> errorMetric = new DistanceFromModelResidual<DenseMatrix64F, AssociatedPair>(
				new FundamentalResidualSampson());

		// Use RANSAC to estimate the Fundamental matrix
		ModelMatcher<DenseMatrix64F, AssociatedPair> robustF = new Ransac<DenseMatrix64F, AssociatedPair>(
				123123, managerF, generateF, errorMetric, 6000, 0.1);

		// Estimate the fundamental matrix while removing outliers
		if (!robustF.process(matches))
			throw new IllegalArgumentException("Failed");

		// save the set of features that were used to compute the fundamental
		// matrix
		inliers.addAll(robustF.getMatchSet());

		// Improve the estimate of the fundamental matrix using non-linear
		// optimization
		DenseMatrix64F F = new DenseMatrix64F(3, 3);
		ModelFitter<DenseMatrix64F, AssociatedPair> refine = FactoryMultiView
				.refineFundamental(1e-8, 400, EpipolarError.SAMPSON);
		if (!refine.fitModel(inliers, robustF.getModelParameters(), F))
			throw new IllegalArgumentException("Failed");

		// Return the solution
		return F;
	}

	/**
	 * If the set of associated features are known to be correct, then the
	 * fundamental matrix can be computed directly with a lot less code. The
	 * down side is that this technique is very sensitive to noise.
	 */
	public static DenseMatrix64F simpleFundamental(List<AssociatedPair> matches) {
		// Use the 8-point algorithm since it will work with an arbitrary number
		// of points
		Estimate1ofEpipolar estimateF = FactoryMultiView.computeFundamental_1(
				EnumEpipolar.FUNDAMENTAL_8_LINEAR, 0);

		DenseMatrix64F F = new DenseMatrix64F(3, 3);
		if (!estimateF.process(matches, F))
			throw new IllegalArgumentException("Failed");

		// while not done here, this initial linear estimate can be refined
		// using non-linear optimization
		// as was done above.
		return F;
	}

	/**
	 * Use the associate point feature example to create a list of
	 * {@link AssociatedPair} for use in computing the fundamental matrix.
	 * @param nazwa2 
	 * @param nazwa 
	 * @throws IOException 
	 */
	public static List<AssociatedPair> computeMatches(BufferedImage left,
			BufferedImage right, String nazwa, String nazwa2) throws IOException {
		DetectDescribePoint detDesc = FactoryDetectDescribe.surfStable(
				new ConfigFastHessian(1, 2, 200, 1, 9, 4, 4), null, null,
				ImageFloat32.class);
		// DetectDescribePoint detDesc = FactoryDetectDescribe.sift(null,new
		// ConfigSiftDetector(2,0,200,5),null,null);

		ScoreAssociation<SurfFeature> scorer = FactoryAssociation
				.scoreEuclidean(SurfFeature.class, true);
		AssociateDescription<SurfFeature> associate = FactoryAssociation
				.greedy(scorer, 1, true);

		ExampleAssociatePoints<ImageFloat32, SurfFeature> findMatches = new ExampleAssociatePoints<ImageFloat32, SurfFeature>(
				detDesc, associate, ImageFloat32.class);

		findMatches.associate(left, right, nazwa, nazwa2);

		List<AssociatedPair> matches = new ArrayList<AssociatedPair>();
		FastQueue<AssociatedIndex> matchIndexes = associate.getMatches();

		for (int i = 0; i < matchIndexes.size; i++) {
			AssociatedIndex a = matchIndexes.get(i);
			AssociatedPair p = new AssociatedPair(
					findMatches.pointsA.get(a.src),
					findMatches.pointsB.get(a.dst));
			matches.add(p);
		}

		return matches;
	}

	public static void main(String args[]) throws IOException {

		String dir = "D:/IN¯YNIERKA/Rzeszów1/";
		String pom = "";
		String nazwa = "";
		String nazwa2 = "";
		String dir2="D:/IN¯YNIERKA/testy/";
		BufferedImage imageA;
		BufferedImage imageB;
		String pom4="";

		for (int i = 0; i < 50; i++) {
			File plik = new File(dir2+"Homography_LOC_" + i +".txt");
			FileWriter writer = new FileWriter(plik, true);

			if (i < 10) {
				nazwa = "0" + i + "_00_00.jpg";
				imageA = UtilImageIO.loadImage(dir + nazwa);
			} else {
				nazwa = i + "_00_00.jpg";
				imageA = UtilImageIO.loadImage(dir + nazwa);
			}
			

			for (int j = 0; j < 6; j++) {

				for (int k = 0; k < 10; k++) {
					
					if (i < 10) {
						nazwa2 = "0" + i + "_0" + j + "_0" + k + ".jpg";
						imageB = UtilImageIO.loadImage(dir + nazwa2);
					} else {
						nazwa2 = i + "_0" + j + "_0" + k + ".jpg";
						imageB = UtilImageIO.loadImage(dir + nazwa2);
					}
					
					List<AssociatedPair> matches = computeMatches(imageA,
							imageB, nazwa, nazwa2);
					// Where the fundamental matrix is stored
					DenseMatrix64F F;
					// List of matches that matched the model
					List<AssociatedPair> inliers = new ArrayList<AssociatedPair>();

					// estimate and print the results using a robust and simple
					// estimator
					// The results should be difference since there are many
					// false
					// associations in the simple model
					// Also note that the fundamental matrix is only defined up
					// to a
					// scale factor.
					F = robustFundamental(matches, inliers);
				//	System.out.println("Robust");

					pom += "Homography img " + nazwa + " to img " + nazwa2
							+ " \n";
					for(int ii=0; ii<3; ii++)
					{
						for(int jj=0; jj<3; jj++)
						{
							pom4+=F.get(ii, jj) + "   ";
						}
							
					}
					pom4+="\n";
				//	 F.print();
				//	writer.write(pom);
					//writer.write(pom4);

					//F = simpleFundamental(matches);
					//System.out.println("Simple");
					// F.print();
					//System.out.println("i = " + i + " j = " + j + " k = " + k);
				//	System.out.println(pom);
					
					nazwa2 = "";
				}
				
				
			}
			nazwa = "";
			writer.write(pom);
			writer.write(pom4);
			pom=pom4="";
			writer.close();
			
		}
		// display the inlier matches found using the robust estimator
		// AssociationPanel panel = new AssociationPanel(20);
		// panel.setAssociation(inliers);
		// panel.setImages(imageA, imageB);

		// ShowImages.showWindow(panel, "Inlier Pairs");
		

	}
	

}
