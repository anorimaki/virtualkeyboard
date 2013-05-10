package com.vkb.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.vkb.app.model.User;
import com.vkb.concurrent.Parallelizer;

public class UserLoader {
	private ExecutorService executor;
	
	public UserLoader( ExecutorService executor ) {
		this.executor = executor;
	}
	
	public List<User> load( File[] inputFolders ) throws Exception {
		Parallelizer<User> parallelizer = new Parallelizer<User>(executor);
		for( File inputFolder : inputFolders ) {
			final File userFolder = inputFolder;
			
			Callable<User> job = new Callable<User>() {
					@Override
					public User call() throws Exception {
						return new User( userFolder );
					}
				};
			
			parallelizer.submit(job);
		}
		
		return parallelizer.join();
	}
}
