package com.vkb.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

public class ProvaInterpol {
	public static void main (String[] args) {
	  double[] fX=new double[63];
	  double[] fY=new double[63];
	  double angle=0.0;

          for(int i=0;i<fY.length;i++)
	  {
	        fX[i]=angle;
		fY[i]=Math.sin(angle)+(Math.random()/5.0);	  
		angle=angle+0.1;
	  }

	  UnivariateInterpolator interpolator = new LoessInterpolator( 0.1d, 0, LoessInterpolator.DEFAULT_ACCURACY );
          
	  UnivariateFunction res=interpolator.interpolate(fX, fY);
	
	 System.out.println("");
	 System.out.print("a=[");
	 for(int i=0;i<fX.length;i++)
	 {
//		System.out.println("X:"+fX[i]+" Y:"+res.value(fX[i])+" <- fY("+fY[i]+")");

		// Per Matlab
		System.out.println(""+fX[i]+","+res.value(fX[i])+","+fY[i]+";");
	 }
	 System.out.println("]");

	}
}
