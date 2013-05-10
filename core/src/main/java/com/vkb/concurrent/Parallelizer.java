package com.vkb.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Parallelizer<T> {
	private ExecutorService executor;
	private List<Future<T>> results;
	
	public Parallelizer( ExecutorService executor ) {
		this.executor = executor;
		results = new ArrayList<Future<T>>();
	}
	
	public Future<T> submit( Callable<T> job ) {
		Future<T> ret = executor.submit(job);
		results.add( ret );
		return ret;
	}
	
	public List<T> join() throws Exception {
		List<T> ret = new ArrayList<T>();
		for( Future<T> result : results ) {
			ret.add( result.get() );
		}
		results.clear();
		return ret;
	}
}
