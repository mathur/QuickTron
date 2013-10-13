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
import org.opencv.core.Rect;
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
    private Point[] mContour = null;
    private Point[] mOrientationLine = null;
    private List<MatOfPoint> mGreens = new ArrayList<MatOfPoint>();
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
        MatOfPoint hull = new MatOfPoint();
        MatOfPoint2f hull2f = new MatOfPoint2f();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
//            Core.multiply(contour, new Scalar(4,4), contour);
            MatOfInt indices = new MatOfInt();
            Imgproc.convexHull(contour, indices);
            Point[] pts = new Point[(int)indices.size().height];
            for(int i=0;i<pts.length;++i) {
            	pts[i] = new Point();
            }
            hull.fromArray(pts);
            for(int j = 0; j < indices.size().height; j++){
                int index = (int) indices.get(j, 0)[0];
                hull.put(j, 0, contour.get(index, 0));
            }
            hull2f.fromArray(hull.toArray());

            Imgproc.approxPolyDP(hull2f, hull2f, 15.0, true);
            hull.fromArray(hull2f.toArray());
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
        mContour = (maxContour != null) ? maxContour.toArray() :
        								  null;
        if(mContour != null) {
        	List<Integer> nearlyHoriz = new ArrayList<Integer>(),
        				  nearlyVert  = new ArrayList<Integer>();
        	int nPoints = (int)mContour.length;
        	for(int i=0;i<nPoints;++i) {
        		Point a = mContour[i],
        			  b = mContour[(i+1)%nPoints];
        		double dx = b.x-a.x,
        			   dy = b.y-a.y;
        		if(Math.abs(dx)>Math.abs(dy)) {
        			nearlyHoriz.add(i);
        		} else {
        			nearlyVert.add(i);
        		}
        	}
        	if(nearlyHoriz.size() < 2 || nearlyVert.size() < 2) {
        		mOrientationLine = null;
    	        mGreens.clear();
        		return;
        	}
        	Comparator<Integer> compare = new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
		        	int nPoints = (int)mContour.length;
	        		Point a = mContour[arg0],
	          			  b = mContour[(arg0+1)%nPoints];
	        		Point c = mContour[arg1],
	          			  d = mContour[(arg1+1)%nPoints];
	        		double dx1 = b.x-a.x,dy1 = b.y-a.y,
	        			   dx2 = d.x-c.x,dy2 = d.y-c.y;
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
    		Point a = mContour[horiz[0]],
        		  b = mContour[(horiz[0]+1)%nPoints];
      		Point c = mContour[vert[0]],
			      d = mContour[(vert[0]+1)%nPoints];
	   		double dx1 = b.x-a.x,dy1 = b.y-a.y,
	   			   dx2 = d.x-c.x,dy2 = d.y-c.y;
	   		boolean isLandscape = (dx1*dx1+dy1*dy1 > dx2*dx2+dy2*dy2);
	   		Point[] orientationLine = new Point[2];
	   		if(!isLandscape) {
	   			orientationLine[0] = new Point((a.x+b.x)/2, (a.y+b.y)/2);
	    		a = mContour[horiz[1]];
	    		b = mContour[(horiz[1]+1)%nPoints];
	   			orientationLine[1] = new Point((a.x+b.x)/2, (a.y+b.y)/2);
	   		} else {
	   			orientationLine[0] = new Point((c.x+d.x)/2, (c.y+d.y)/2);
	    		c = mContour[vert[1]];
	    		d = mContour[(vert[1]+1)%nPoints];
	   			orientationLine[1] = new Point((c.x+d.x)/2, (c.y+d.y)/2);
	   		}
	   		mOrientationLine = orientationLine;
	   		Scalar maxGreen = new Scalar(160,160,200,255),
	   			   minGreen = new Scalar(100, 60, 20,  0);

	        Core.inRange(mHsvMat, minGreen, maxGreen, mMask);
	        mGreens.clear();
	        Imgproc.findContours(mMask, mGreens, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	        List<Point[]> greens = new ArrayList<Point[]>();
	        int aboveLineCount = 0;
        	MatOfPoint2f mainContour = new MatOfPoint2f(mContour);
        	Point p1,p2;
        	if((isLandscape && orientationLine[0].x < orientationLine[1].x) ||
        	   orientationLine[0].y < orientationLine[0].y){
        		p1 = orientationLine[0];
        		p2 = orientationLine[1];
        	} else {
        		p1 = orientationLine[1];
        		p2 = orientationLine[0];
        	}
        	MatOfInt hullInd = new MatOfInt();
	        for(MatOfPoint green:mGreens) {
	        	Imgproc.convexHull(green, hullInd);
	        	Point[] hullPts = new Point[hullInd.rows()];
	        	for(int i=0;i<hullPts.length;++i) {
	        		double[] pt = green.get((int)hullInd.get(i,0)[0], 0);
	        		hullPts[i] = new Point(pt[0],pt[1]);
	        	}
	        	boolean onScantron = true;
	        	int lineRelation = 0;
	        	for(Point p:hullPts) {
	        		if(Imgproc.pointPolygonTest(mainContour, p, false) < 0) {
	        			onScantron = false;
	        			break;
	        		}
	        		if((p2.x-p1.x)*(p.x-p1.x)-(p2.y-p1.y)*(p.x-p1.x) > 0) {
	        			lineRelation++;
	        		} else {
	        			lineRelation--;
	        		}
	        	}
	        	if(onScantron) {
	        		greens.add(green.toArray());
	        		aboveLineCount += lineRelation;
	        	}
	        }
	        mGreens.clear();
	        for(Point[] contour:greens) {
	        	mGreens.add(new MatOfPoint(contour));
	        }
	        if(aboveLineCount >= 0) {
	        	orientationLine[0] = p2;
	        	orientationLine[1] = p1;
	        } else {
	        	orientationLine[0] = p2;
	        	orientationLine[1] = p1;
	        }
	        int[] edges = new int[4];
	        if(isLandscape) {
	        	if(Math.signum(orientationLine[1].x-orientationLine[0].x) == Math.signum(mContour[vert[0]].x - mContour[vert[1]].x)) {
	        		edges[0] = vert[0];
	        		edges[2] = vert[1];
	        	} else {
	        		edges[0] = vert[1];
	        		edges[2] = vert[0];
	        	}
	        	if(Math.signum(mContour[(edges[0]+1)%mContour.length].y-mContour[edges[0]].y) == Math.signum(mContour[horiz[0]].y-mContour[horiz[1]].y)) {
	        		edges[1] = horiz[0];
	        		edges[3] = horiz[1];
	        	} else {
	        		edges[1] = horiz[1];
	        		edges[3] = horiz[0];
	        	}
	        } else {
	        	if(Math.signum(orientationLine[1].y-orientationLine[0].y) == Math.signum(mContour[horiz[0]].y - mContour[horiz[1]].y)) {
	        		edges[0] = horiz[0];
	        		edges[2] = horiz[1];
	        	} else {
	        		edges[0] = horiz[1];
	        		edges[2] = horiz[0];
	        	}
	        	if(Math.signum(mContour[(edges[0]+1)%mContour.length].x-mContour[edges[0]].x) == Math.signum(mContour[vert[0]].x-mContour[vert[1]].x)) {
	        		edges[1] = vert[0];
	        		edges[3] = vert[1];
	        	} else {
	        		edges[1] = vert[1];
	        		edges[3] = vert[0];
	        	}
	        }
	        Point[] contour = new Point[4];
	        for(int i=0;i<edges.length;++i) {
	        	int prevI = i == 0 ? edges.length-1 :
	        						 i-1;
	        	contour[i] = _intersect(mContour[edges[prevI]],mContour[(edges[prevI]+1)%mContour.length],
	        							 mContour[edges[i]],mContour[(edges[i]+1)%mContour.length]);
	        }
	        mContour = contour;
        }
    }
<<<<<<< HEAD
    public double distance(Point p1, Point p2) { //guys, i'm clearly good at this shit #burnout
    	return Math.sqrt(Math.pow((p1.x-p2.x),2) + Math.pow((p1.y-p2.y),2));
    }
    public void processRect(Mat rgbaImage, Point[] reference) {
    	double longside = distance(reference[0],reference[1]);
    	double shortside = distance(reference[1],reference[2]);
    	
      	int numRows = 50;//should be 50 questions //HOLYSHIT I HAVE NO IDEA WTF IM ACTUALLY FKING DOING
    	double ydistance = reference[0].y+0.36842105263*shortside; //distance from the left side (remember the fucking phone is landfuckinscape [WHAT THE FLYINF FUCK])
    	double xdistance = reference[3].x+0.01714285714*longside; //distance from the top...#WTFISTHISSHIT HAHAHAHAH ...wtfactually

    	double width = 0.01514285714*longside; //width of each row
    	double length = 0.36842105263*shortside; //length of each row
    	
    	double xoffset = 0.01714285714*longside; //multiplier to get each row's x-coordinate

    	List<MatOfPoint> rectPoints = new ArrayList<MatOfPoint>(50);
    	
    	for(int i = 0; i < numRows;i++) {
        	Point[] points = new Point[4];
    		points[0] = new Point(xoffset*i+xdistance, ydistance);
    		points[1] = new Point(xoffset*i+width+xdistance, ydistance);
    		points[2] = new Point(xoffset*i+width+xdistance, ydistance+length);
    		points[3] = new Point(xoffset*i+xdistance, ydistance+length);

    		rectPoints.add(i, new MatOfPoint(points));
    	}
		
		//rectPoints.fromArray(points);
        Imgproc.drawContours(rgbaImage, rectPoints, -1, new Scalar(255,0,0,255));

		//Imgproc.boundingRect(points);
    }
=======
    
    private double _cross(Point a,Point b) {
    	return (a.x*b.y-a.y*b.x);
    }
    private Point _intersect(Point a1,Point b1,Point a2,Point b2) {
    	Point r = new Point(b1.x-a1.x,b1.y-a1.y),
    		  s = new Point(b2.x-a2.x,b2.y-a2.y);
    	double t = _cross(new Point(a2.x-a1.x,a2.y-a1.y),s)/_cross(r,s);
    	return new Point(a1.x+t*r.x,a1.y+t*r.y);
    }

>>>>>>> d2cd68b401c6938ac5d7b7a948b184b8395fad73
    public MatOfPoint getContour() {
        return new MatOfPoint(mContour);
    }
    public MatOfPoint getOrientationLine() {
    	if(mOrientationLine == null) {
    		return null;
    	}
    	Point[] line = new Point[6];
    	line[0] = mOrientationLine[0];
    	line[1] = mOrientationLine[1];
    	line[2] = new Point(line[0].x*0.25+line[1].x*0.75,line[0].y*0.25+line[1].y*0.75);
    	double dx = line[2].x-line[1].x,
    		   dy = line[2].y-line[1].y;
    	line[3] = new Point(line[2].x+dy,line[2].y-dx);
    	line[4] = new Point(line[2].x-dy,line[2].y+dx);
    	line[5] = line[2];
    	return new MatOfPoint(line);
    }
    public List<MatOfPoint> getGreenishThings() {
    	return mGreens;
    }
}
