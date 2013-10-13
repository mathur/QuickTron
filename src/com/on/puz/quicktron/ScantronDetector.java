package com.on.puz.quicktron;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class ScantronDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(0,50,50,0);
    private Mat mSpectrum = new Mat();
    private MatOfPoint mContour = null;
    private MatOfPoint mOrientationLine = null;
//    private static final int MIN_NULL_COUNT = 4;
//    private int nullCount = 0;

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minV = (hsvColor.val[2] >= mColorRadius.val[2]) ? hsvColor.val[2]-mColorRadius.val[2] : 0;
        double maxV = 255;

        mLowerBound.val[0] = 0;
        mUpperBound.val[0] = 255;

        mLowerBound.val[1] = 0;
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = 255;

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxV-minV), CvType.CV_8UC3);

        for (int j = 0; j < maxV-minV; j++) {
            byte[] tmp = {(byte)(0), (byte)(maxV-minV), (byte)0};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
//        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
//        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

//        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
        Imgproc.cvtColor(rgbaImage, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        // Filter contours by area and resize to fit the original image size
        List<MatOfPoint> goodContours = new ArrayList<MatOfPoint>();
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
//            Core.multiply(contour, new Scalar(4,4), contour);
            MatOfInt indices = new MatOfInt();
            Imgproc.convexHull(contour, indices);
            Point[] pts = new Point[(int)indices.size().height];
            for(int i=0;i<pts.length;++i) {
            	pts[i] = new Point();
            }
            MatOfPoint hull = new MatOfPoint(pts);
            for(int j = 0; j < indices.size().height; j++){
                int index = (int) indices.get(j, 0)[0];
                hull.put(j, 0, contour.get(index, 0));
            }
            MatOfPoint2f hull2f = new MatOfPoint2f(hull.toArray());

            Imgproc.approxPolyDP(hull2f, hull2f, 15.0, true);
            hull = new MatOfPoint(hull2f.toArray());
            Log.d("Num verts",""+hull.size().height + " from " + indices.size().height);
        	goodContours.add(hull);
        }
        // Find max contour area
        double maxArea = 0;
        MatOfPoint maxContour = null;
        for(MatOfPoint contour:goodContours) {
            double area = Math.abs(Imgproc.contourArea(contour));
            if (maxContour == null || area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }
//        if(maxContour != null) {
    	mContour = maxContour;
//        	nullCount = 0;
//        } else {
//    		nullCount++;
//        	if(nullCount > MIN_NULL_COUNT) {
//        		mContour = null;
//        	}
//        }
        if(mContour != null) {
        	List<Integer> nearlyHoriz = new ArrayList<Integer>(),
        				  nearlyVert  = new ArrayList<Integer>();
        	int nPoints = (int)mContour.size().height;
        	for(int i=0;i<nPoints;++i) {
        		double[] a = mContour.get(i, 0),
        				 b = mContour.get((i+1)%nPoints, 0);
        		double dx = b[0]-a[0],
        			   dy = b[1]-a[1];
        		if(Math.abs(dx)>Math.abs(dy)) {
        			nearlyHoriz.add(i);
        		} else {
        			nearlyVert.add(i);
        		}
        	}
        	if(nearlyHoriz.size() < 2 || nearlyVert.size() < 2) {
        		mOrientationLine = null;
        		return;
        	}
        	Comparator<Integer> compare = new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
		        	int nPoints = (int)mContour.size().height;
	        		double[] a = mContour.get(arg0, 0),
	        				 b = mContour.get((arg0+1)%nPoints, 0);
	        		double[] c = mContour.get(arg1, 0),
	        				 d = mContour.get((arg1+1)%nPoints, 0);
	        		double dx1 = b[0]-a[0],dy1 = b[1]-a[1],
	        			   dx2 = d[0]-c[0],dy2 = d[1]-c[1];
	        		double magSq1 = dx1*dx1+dy1*dy1,
	        			   magSq2 = dx2*dx2+dy2*dy2;
					return magSq1 < magSq2 ?  1 :
						   magSq1 > magSq2 ? -1 :
							   				  0;
				}
        	};
        	Collections.sort(nearlyHoriz,compare);
        	Collections.sort(nearlyVert,compare);
        	int[] horiz = new int[]{nearlyHoriz.get(0),nearlyHoriz.get(1)},
        		  vert  = new int[]{nearlyVert.get(0),nearlyVert.get(1)};
    		double[] a = mContour.get(horiz[0], 0),
	   				 b = mContour.get((horiz[0]+1)%nPoints, 0);
	   		double[] c = mContour.get(vert[0], 0),
	   				 d = mContour.get((vert[0]+1)%nPoints, 0);
	   		double dx1 = b[0]-a[0],dy1 = b[1]-a[1],
	   			   dx2 = d[0]-c[0],dy2 = d[1]-c[1];
	   		boolean isLandscape = (dx1*dx1+dy1*dy1 > dx2*dx2+dy2*dy2);
	   		Point[] orientationLine = new Point[2];
	   		if(!isLandscape) {
	   			orientationLine[0] = new Point((a[0]+b[0])/2, (a[1]+b[1])/2);
	    		a = mContour.get(horiz[1], 0);
	    		b = mContour.get((horiz[1]+1)%nPoints, 0);
	   			orientationLine[1] = new Point((a[0]+b[0])/2, (a[1]+b[1])/2);
	   		} else {
	   			orientationLine[0] = new Point((c[0]+d[0])/2, (c[1]+d[1])/2);
	    		c = mContour.get(vert[1], 0);
	    		d = mContour.get((vert[1]+1)%nPoints, 0);
	   			orientationLine[1] = new Point((c[0]+d[0])/2, (c[1]+d[1])/2);
	   		}
	   		mOrientationLine = new MatOfPoint(orientationLine);
        }
    }

    public MatOfPoint getContour() {
        return mContour;
    }
    public MatOfPoint getOrientationLine() {
    	return mOrientationLine;
    }
}
