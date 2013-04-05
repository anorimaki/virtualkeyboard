package com.vkb.app;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.*;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class ProvaDif {
	public static void main (String[] args) {
	  double[] fX=new double[63];
	  double[] fY=new double[63];
	  double[] fYder=new double[63];
	  double angle=0.0, sumd=0.0;

          for(int i=0;i<fY.length;i++)
	  {
	        fX[i]=angle;
		fY[i]=Math.sin(angle)+(Math.random()/5.0);	  
		angle=angle+0.1;
	  }

	  UnivariateInterpolator interpolator = new LoessInterpolator( 0.1d, 0, LoessInterpolator.DEFAULT_ACCURACY );
          
	  UnivariateFunction res=interpolator.interpolate(fX, fY);

	  FiniteDifferencesDifferentiator differentiator=new FiniteDifferencesDifferentiator( 5, 0.5d, fX[0], fX[62]);
//	  FiniteDifferencesDifferentiator differentiator=new FiniteDifferencesDifferentiator( 6, 0.1d);
	  UnivariateDifferentiableFunction dres=differentiator.differentiate(res);
	  
	  for(int i=0;i<fX.length;i++)
	  {
		  /*     parameters - number of free parameters
    			 order - derivation order
    		     index - index of the variable (from 0 to parameters - 1)
    			 value - value of the variable
		   */
		  	DerivativeStructure dt = new DerivativeStructure(1, 1, 0, fX[i]);
		  	DerivativeStructure dx = dres.value(dt);
		  	fYder[i]=dx.getPartialDerivative(1) ;
	  }	  
	  

	  //TrapezoidIntegrator integrator=new TrapezoidIntegrator();
	  TrapezoidIntegrator integrator=new TrapezoidIntegrator();
	  sumd=integrator.integrate(10000, res,fX[0],fX[fX.length-1]);
	
	 System.out.println("");
	 System.out.print("a=[");
	 for(int i=0;i<fX.length;i++)
	 {
//		System.out.println("X:"+fX[i]+" Y:"+res.value(fX[i])+" <- fY("+fY[i]+")");

		// Per Matlab
		System.out.println(""+fX[i]+","+res.value(fX[i])+","+fY[i]+","+fYder[i]+";");
	 }
	 System.out.println("]");
	 System.out.println("Integral: "+sumd);

	}
}












