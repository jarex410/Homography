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
import boofcv.alg.feature.UtilFeature;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.gui.feature.AssociationPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import georegression.struct.point.Point2D_F64;

import org.ddogleg.struct.FastQueue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * After interest points have been detected in two images the next step is to
 * associate the two sets of images so that the relationship can be found. This
 * is done by computing descriptors for each detected feature and associating
 * them together. In the code below abstracted interfaces are used to allow
 * different algorithms to be easily used. The cost of this abstraction is that
 * detector/descriptor specific information is thrown away, potentially slowing
 * down or degrading performance.
 *
 * @author Peter Abeles
 */
public class ExampleAssociatePoints<T extends ImageSingleBand, TD extends TupleDesc> {

	// algorithm used to detect and describe interest points
	DetectDescribePoint<T, TD> detDesc;
	// Associated descriptions together by minimizing an error metric
	AssociateDescription<TD> associate;

	// location of interest points
	public List<Point2D_F64> pointsA;
	public List<Point2D_F64> pointsB;

	public List<Point2D_F64> getPointsA() {
		return pointsA;
	}

	public List<Point2D_F64> getPointsB() {
		return pointsB;
	}

	Class<T> imageType;

	public ExampleAssociatePoints(DetectDescribePoint<T, TD> detDesc,
			AssociateDescription<TD> associate, Class<T> imageType) {
		this.detDesc = detDesc;
		this.associate = associate;
		this.imageType = imageType;
	}

	/**
	 * Detect and associate point features in the two images. Display the
	 * results.
	 * 
	 * @throws IOException
	 */
	public void associate(BufferedImage imageA, BufferedImage imageB,
			String nazwa, String nazwa2) throws IOException {
		T inputA = ConvertBufferedImage.convertFromSingle(imageA, null,
				imageType);
		T inputB = ConvertBufferedImage.convertFromSingle(imageB, null,
				imageType);

		// stores the location of detected interest points
		pointsA = new ArrayList<Point2D_F64>();
		pointsB = new ArrayList<Point2D_F64>();
		// stores the description of detected interest points
		FastQueue<TD> descA = UtilFeature.createQueue(detDesc, 100);
		FastQueue<TD> descB = UtilFeature.createQueue(detDesc, 100);

		// describe each image using interest points
		describeImage(inputA, pointsA, descA, nazwa, 0);
		describeImage(inputB, pointsB, descB, nazwa2, 1);

		// Associate features between the two images
		associate.setSource(descA);
		associate.setDestination(descB);
		associate.associate();

		String dir2 = "D:/IN¯YNIERKA/Points/";
		File plik = new File(dir2 + "Points " + nazwa2 + ".txt");
		FileWriter writer = new FileWriter(plik, true);

		writer.write(pointsB.toString());
		writer.close();

		// display the results
		// AssociationPanel panel = new AssociationPanel(20);
		// panel.setAssociation(pointsA,pointsB,associate.getMatches());
		// panel.setImages(imageA,imageB);

		// ShowImages.showWindow(panel,"Associated Features");
	}

	/**
	 * Detects features inside the two images and computes descriptions at those
	 * points.
	 * 
	 * @throws IOException
	 */
	private void describeImage(T input, List<Point2D_F64> points,
			FastQueue<TD> descs, String nazwa2, int pom) throws IOException {

		detDesc.detect(input);
		String dir2 = "D:/IN¯YNIERKA/Desc/";
		File plik = new File(dir2 + "Desc " + nazwa2 + ".txt");
		FileWriter writer = new FileWriter(plik, true);
		for (int i = 0; i < detDesc.getNumberOfFeatures(); i++) {

			points.add(detDesc.getLocation(i).copy());
			descs.grow().setTo(detDesc.getDescription(i));
			if (pom == 1) {
				for (int j = 0; j < 64; j++) {

					writer.write(detDesc.getDescription(i).getDouble(j) + "\n");
				}
				//writer.write("\n");
			}
		}

		writer.close();
	}

	/*
	 * public static void main( String args[] ) {
	 * 
	 * Class imageType = ImageFloat32.class;
	 * 
	 * // select which algorithms to use DetectDescribePoint detDesc =
	 * FactoryDetectDescribe.surfStable( new ConfigFastHessian(1, 2, 200, 1, 9,
	 * 4, 4), null,null, imageType);
	 * 
	 * ScoreAssociation scorer =
	 * FactoryAssociation.defaultScore(detDesc.getDescriptionType());
	 * AssociateDescription associate = FactoryAssociation.greedy(scorer,
	 * Double.MAX_VALUE, true);
	 * 
	 * // load and match images ExampleAssociatePoints app = new
	 * ExampleAssociatePoints(detDesc,associate,imageType);
	 * 
	 * BufferedImage imageA = UtilImageIO.loadImage("G:/testy/img1.jpg");
	 * BufferedImage imageB = UtilImageIO.loadImage("G:/testy/img1.jpg");
	 * 
	 * app.associate(imageA,imageB); }
	 */
}
