package com.vkb.quality.farfrr;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ERRCalculatorTest {
	@Test
	public void testDivergent() throws Exception {
		List<FARFRRCalculator.Result> intputData = new ArrayList<FARFRRCalculator.Result>();
		
		intputData.add( new FARFRRCalculator.Result( null, 2, 3 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 4 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 4 ) );
		double[] intputThresholds = { 1, 2, 3 }; 
		
		ERRCalculator.Result result = ERRCalculator.calculate( intputData, intputThresholds );
		assertEquals( 0, result.getThreshold(), 0.01 );
		assertEquals( 2, result.getValue(), 0.01 );
	}
	
	@Test
	public void testEqualBeginingAndDivergent() throws Exception {
		List<FARFRRCalculator.Result> intputData = new ArrayList<FARFRRCalculator.Result>();
		
		intputData.add( new FARFRRCalculator.Result( null, 2, 3 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 3 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 4 ) );
		double[] intputThresholds = { 4, 5, 6 }; 
		
		ERRCalculator.Result result = ERRCalculator.calculate( intputData, intputThresholds );
		assertEquals( 2, result.getThreshold(), 0.01 );
		assertEquals( 2, result.getValue(), 0.01 );
	}
	
	@Test
	public void testConvergent() throws Exception {
		List<FARFRRCalculator.Result> intputData = new ArrayList<FARFRRCalculator.Result>();
		
		intputData.add( new FARFRRCalculator.Result( null, 2, 6 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 5 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 3 ) );
		double[] intputThresholds = { 4, 5, 6 }; 
		
		ERRCalculator.Result result = ERRCalculator.calculate( intputData, intputThresholds );
		assertEquals( 6.5, result.getThreshold(), 0.01 );
		assertEquals( 2, result.getValue(), 0.01 );
	}
	
	@Test
	public void testConvergentEqualEnding() throws Exception {
		List<FARFRRCalculator.Result> intputData = new ArrayList<FARFRRCalculator.Result>();
		
		intputData.add( new FARFRRCalculator.Result( null, 2, 6 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 5 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 5 ) );
		double[] intputThresholds = { 4, 5, 6 }; 
		
		ERRCalculator.Result result = ERRCalculator.calculate( intputData, intputThresholds );
		assertEquals( 12, result.getThreshold(), 0.01 );
		assertEquals( 2, result.getValue(), 0.01 );
	}
	
	@Test(expected = Exception.class)  
	public void testParallel() throws Exception {
		List<FARFRRCalculator.Result> intputData = new ArrayList<FARFRRCalculator.Result>();
		
		intputData.add( new FARFRRCalculator.Result( null, 2, 5 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 5 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 5 ) );
		double[] intputThresholds = { 4, 5, 6 }; 
		
		ERRCalculator.calculate( intputData, intputThresholds );
	}
	
	@Test
	public void testIntersection() throws Exception {
		List<FARFRRCalculator.Result> intputData = new ArrayList<FARFRRCalculator.Result>();
		
		intputData.add( new FARFRRCalculator.Result( null, 2, 6 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 4 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 4 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 1 ) );
		intputData.add( new FARFRRCalculator.Result( null, 2, 1 ) );
		double[] intputThresholds = { 4, 5, 6, 7, 8 }; 
		
		ERRCalculator.Result result = ERRCalculator.calculate( intputData, intputThresholds );
		assertEquals( 6.66, result.getThreshold(), 0.01 );
		assertEquals( 2, result.getValue(), 0.01 );
	}
}
