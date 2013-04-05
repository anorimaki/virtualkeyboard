package com.vkb.app;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;

public class ProvaIntDif {
	public static void main (String[] args) {
	  double[] fY={0,10,20,30,40,50,60,70,80,90};
	  double[] fYder={0,0,0,0,0,0,0,0,0,0};
	  double[] fX={0,1,2,3,4,5,6,7,8,9};
	  double angle=0.0;

	  UnivariateInterpolator interpolator = new LoessInterpolator( 1.0d, 0, LoessInterpolator.DEFAULT_ACCURACY );
          
	  UnivariateFunction res=interpolator.interpolate(fX, fY);

	  FiniteDifferencesDifferentiator differentiator=new FiniteDifferencesDifferentiator( 6, 0.25d, fX[0],fX[fX.length-1]);
	 // FiniteDifferencesDifferentiator differentiator=new FiniteDifferencesDifferentiator( 6, 0.1d);
	  UnivariateDifferentiableFunction dres=differentiator.differentiate(res);
	  
	  for(int i=0;i<fX.length;i++)
	  {
		  	DerivativeStructure dt = new DerivativeStructure(1, 1, 0, fX[i]);
		  	DerivativeStructure dx = dres.value(dt);
		  	fYder[i]=dx.getPartialDerivative(1) ;
	  }
	  
	  TrapezoidIntegrator integrator=new TrapezoidIntegrator();
	  double sumd=integrator.integrate(100000, res, fX[0],fX[fX.length-1]);

	
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
