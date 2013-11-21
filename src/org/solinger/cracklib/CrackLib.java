package org.solinger.cracklib;

import java.io.IOException;


public class CrackLib {
	public static final String[] destructors = new String[] {
		":",                        // noop - must do this to test raw word. 

		"[",                        // trimming leading/trailing junk
		"]",
		"[[",
		"]]",
		"[[[",
		"]]]",

		"/?p@?p",                   // purging out punctuation/symbols/junk
		"/?s@?s",
		"/?X@?X",

		// attempt reverse engineering of password strings

		"/$s$s",
		"/$s$s/0s0o",
		"/$s$s/0s0o/2s2a",
		"/$s$s/0s0o/2s2a/3s3e",
		"/$s$s/0s0o/2s2a/3s3e/5s5s",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/1s1i",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/1s1l",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/1s1i/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/1s1i/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/1s1l/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/1s1l/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/5s5s/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/1s1i",
		"/$s$s/0s0o/2s2a/3s3e/1s1l",
		"/$s$s/0s0o/2s2a/3s3e/1s1i/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/1s1i/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/1s1l/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/1s1l/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/4s4h",
		"/$s$s/0s0o/2s2a/3s3e/4s4a",
		"/$s$s/0s0o/2s2a/3s3e/4s4h",
		"/$s$s/0s0o/2s2a/5s5s",
		"/$s$s/0s0o/2s2a/5s5s/1s1i",
		"/$s$s/0s0o/2s2a/5s5s/1s1l",
		"/$s$s/0s0o/2s2a/5s5s/1s1i/4s4a",
		"/$s$s/0s0o/2s2a/5s5s/1s1i/4s4h",
		"/$s$s/0s0o/2s2a/5s5s/1s1l/4s4a",
		"/$s$s/0s0o/2s2a/5s5s/1s1l/4s4h",
		"/$s$s/0s0o/2s2a/5s5s/4s4a",
		"/$s$s/0s0o/2s2a/5s5s/4s4h",
		"/$s$s/0s0o/2s2a/5s5s/4s4a",
		"/$s$s/0s0o/2s2a/5s5s/4s4h",
		"/$s$s/0s0o/2s2a/1s1i",
		"/$s$s/0s0o/2s2a/1s1l",
		"/$s$s/0s0o/2s2a/1s1i/4s4a",
		"/$s$s/0s0o/2s2a/1s1i/4s4h",
		"/$s$s/0s0o/2s2a/1s1l/4s4a",
		"/$s$s/0s0o/2s2a/1s1l/4s4h",
		"/$s$s/0s0o/2s2a/4s4a",
		"/$s$s/0s0o/2s2a/4s4h",
		"/$s$s/0s0o/2s2a/4s4a",
		"/$s$s/0s0o/2s2a/4s4h",
		"/$s$s/0s0o/3s3e",
		"/$s$s/0s0o/3s3e/5s5s",
		"/$s$s/0s0o/3s3e/5s5s/1s1i",
		"/$s$s/0s0o/3s3e/5s5s/1s1l",
		"/$s$s/0s0o/3s3e/5s5s/1s1i/4s4a",
		"/$s$s/0s0o/3s3e/5s5s/1s1i/4s4h",
		"/$s$s/0s0o/3s3e/5s5s/1s1l/4s4a",
		"/$s$s/0s0o/3s3e/5s5s/1s1l/4s4h",
		"/$s$s/0s0o/3s3e/5s5s/4s4a",
		"/$s$s/0s0o/3s3e/5s5s/4s4h",
		"/$s$s/0s0o/3s3e/5s5s/4s4a",
		"/$s$s/0s0o/3s3e/5s5s/4s4h",
		"/$s$s/0s0o/3s3e/1s1i",
		"/$s$s/0s0o/3s3e/1s1l",
		"/$s$s/0s0o/3s3e/1s1i/4s4a",
		"/$s$s/0s0o/3s3e/1s1i/4s4h",
		"/$s$s/0s0o/3s3e/1s1l/4s4a",
		"/$s$s/0s0o/3s3e/1s1l/4s4h",
		"/$s$s/0s0o/3s3e/4s4a",
		"/$s$s/0s0o/3s3e/4s4h",
		"/$s$s/0s0o/3s3e/4s4a",
		"/$s$s/0s0o/3s3e/4s4h",
		"/$s$s/0s0o/5s5s",
		"/$s$s/0s0o/5s5s/1s1i",
		"/$s$s/0s0o/5s5s/1s1l",
		"/$s$s/0s0o/5s5s/1s1i/4s4a",
		"/$s$s/0s0o/5s5s/1s1i/4s4h",
		"/$s$s/0s0o/5s5s/1s1l/4s4a",
		"/$s$s/0s0o/5s5s/1s1l/4s4h",
		"/$s$s/0s0o/5s5s/4s4a",
		"/$s$s/0s0o/5s5s/4s4h",
		"/$s$s/0s0o/5s5s/4s4a",
		"/$s$s/0s0o/5s5s/4s4h",
		"/$s$s/0s0o/1s1i",
		"/$s$s/0s0o/1s1l",
		"/$s$s/0s0o/1s1i/4s4a",
		"/$s$s/0s0o/1s1i/4s4h",
		"/$s$s/0s0o/1s1l/4s4a",
		"/$s$s/0s0o/1s1l/4s4h",
		"/$s$s/0s0o/4s4a",
		"/$s$s/0s0o/4s4h",
		"/$s$s/0s0o/4s4a",
		"/$s$s/0s0o/4s4h",
		"/$s$s/2s2a",
		"/$s$s/2s2a/3s3e",
		"/$s$s/2s2a/3s3e/5s5s",
		"/$s$s/2s2a/3s3e/5s5s/1s1i",
		"/$s$s/2s2a/3s3e/5s5s/1s1l",
		"/$s$s/2s2a/3s3e/5s5s/1s1i/4s4a",
		"/$s$s/2s2a/3s3e/5s5s/1s1i/4s4h",
		"/$s$s/2s2a/3s3e/5s5s/1s1l/4s4a",
		"/$s$s/2s2a/3s3e/5s5s/1s1l/4s4h",
		"/$s$s/2s2a/3s3e/5s5s/4s4a",
		"/$s$s/2s2a/3s3e/5s5s/4s4h",
		"/$s$s/2s2a/3s3e/5s5s/4s4a",
		"/$s$s/2s2a/3s3e/5s5s/4s4h",
		"/$s$s/2s2a/3s3e/1s1i",
		"/$s$s/2s2a/3s3e/1s1l",
		"/$s$s/2s2a/3s3e/1s1i/4s4a",
		"/$s$s/2s2a/3s3e/1s1i/4s4h",
		"/$s$s/2s2a/3s3e/1s1l/4s4a",
		"/$s$s/2s2a/3s3e/1s1l/4s4h",
		"/$s$s/2s2a/3s3e/4s4a",
		"/$s$s/2s2a/3s3e/4s4h",
		"/$s$s/2s2a/3s3e/4s4a",
		"/$s$s/2s2a/3s3e/4s4h",
		"/$s$s/2s2a/5s5s",
		"/$s$s/2s2a/5s5s/1s1i",
		"/$s$s/2s2a/5s5s/1s1l",
		"/$s$s/2s2a/5s5s/1s1i/4s4a",
		"/$s$s/2s2a/5s5s/1s1i/4s4h",
		"/$s$s/2s2a/5s5s/1s1l/4s4a",
		"/$s$s/2s2a/5s5s/1s1l/4s4h",
		"/$s$s/2s2a/5s5s/4s4a",
		"/$s$s/2s2a/5s5s/4s4h",
		"/$s$s/2s2a/5s5s/4s4a",
		"/$s$s/2s2a/5s5s/4s4h",
		"/$s$s/2s2a/1s1i",
		"/$s$s/2s2a/1s1l",
		"/$s$s/2s2a/1s1i/4s4a",
		"/$s$s/2s2a/1s1i/4s4h",
		"/$s$s/2s2a/1s1l/4s4a",
		"/$s$s/2s2a/1s1l/4s4h",
		"/$s$s/2s2a/4s4a",
		"/$s$s/2s2a/4s4h",
		"/$s$s/2s2a/4s4a",
		"/$s$s/2s2a/4s4h",
		"/$s$s/3s3e",
		"/$s$s/3s3e/5s5s",
		"/$s$s/3s3e/5s5s/1s1i",
		"/$s$s/3s3e/5s5s/1s1l",
		"/$s$s/3s3e/5s5s/1s1i/4s4a",
		"/$s$s/3s3e/5s5s/1s1i/4s4h",
		"/$s$s/3s3e/5s5s/1s1l/4s4a",
		"/$s$s/3s3e/5s5s/1s1l/4s4h",
		"/$s$s/3s3e/5s5s/4s4a",
		"/$s$s/3s3e/5s5s/4s4h",
		"/$s$s/3s3e/5s5s/4s4a",
		"/$s$s/3s3e/5s5s/4s4h",
		"/$s$s/3s3e/1s1i",
		"/$s$s/3s3e/1s1l",
		"/$s$s/3s3e/1s1i/4s4a",
		"/$s$s/3s3e/1s1i/4s4h",
		"/$s$s/3s3e/1s1l/4s4a",
		"/$s$s/3s3e/1s1l/4s4h",
		"/$s$s/3s3e/4s4a",
		"/$s$s/3s3e/4s4h",
		"/$s$s/3s3e/4s4a",
		"/$s$s/3s3e/4s4h",
		"/$s$s/5s5s",
		"/$s$s/5s5s/1s1i",
		"/$s$s/5s5s/1s1l",
		"/$s$s/5s5s/1s1i/4s4a",
		"/$s$s/5s5s/1s1i/4s4h",
		"/$s$s/5s5s/1s1l/4s4a",
		"/$s$s/5s5s/1s1l/4s4h",
		"/$s$s/5s5s/4s4a",
		"/$s$s/5s5s/4s4h",
		"/$s$s/5s5s/4s4a",
		"/$s$s/5s5s/4s4h",
		"/$s$s/1s1i",
		"/$s$s/1s1l",
		"/$s$s/1s1i/4s4a",
		"/$s$s/1s1i/4s4h",
		"/$s$s/1s1l/4s4a",
		"/$s$s/1s1l/4s4h",
		"/$s$s/4s4a",
		"/$s$s/4s4h",
		"/$s$s/4s4a",
		"/$s$s/4s4h",
		"/0s0o",
		"/0s0o/2s2a",
		"/0s0o/2s2a/3s3e",
		"/0s0o/2s2a/3s3e/5s5s",
		"/0s0o/2s2a/3s3e/5s5s/1s1i",
		"/0s0o/2s2a/3s3e/5s5s/1s1l",
		"/0s0o/2s2a/3s3e/5s5s/1s1i/4s4a",
		"/0s0o/2s2a/3s3e/5s5s/1s1i/4s4h",
		"/0s0o/2s2a/3s3e/5s5s/1s1l/4s4a",
		"/0s0o/2s2a/3s3e/5s5s/1s1l/4s4h",
		"/0s0o/2s2a/3s3e/5s5s/4s4a",
		"/0s0o/2s2a/3s3e/5s5s/4s4h",
		"/0s0o/2s2a/3s3e/5s5s/4s4a",
		"/0s0o/2s2a/3s3e/5s5s/4s4h",
		"/0s0o/2s2a/3s3e/1s1i",
		"/0s0o/2s2a/3s3e/1s1l",
		"/0s0o/2s2a/3s3e/1s1i/4s4a",
		"/0s0o/2s2a/3s3e/1s1i/4s4h",
		"/0s0o/2s2a/3s3e/1s1l/4s4a",
		"/0s0o/2s2a/3s3e/1s1l/4s4h",
		"/0s0o/2s2a/3s3e/4s4a",
		"/0s0o/2s2a/3s3e/4s4h",
		"/0s0o/2s2a/3s3e/4s4a",
		"/0s0o/2s2a/3s3e/4s4h",
		"/0s0o/2s2a/5s5s",
		"/0s0o/2s2a/5s5s/1s1i",
		"/0s0o/2s2a/5s5s/1s1l",
		"/0s0o/2s2a/5s5s/1s1i/4s4a",
		"/0s0o/2s2a/5s5s/1s1i/4s4h",
		"/0s0o/2s2a/5s5s/1s1l/4s4a",
		"/0s0o/2s2a/5s5s/1s1l/4s4h",
		"/0s0o/2s2a/5s5s/4s4a",
		"/0s0o/2s2a/5s5s/4s4h",
		"/0s0o/2s2a/5s5s/4s4a",
		"/0s0o/2s2a/5s5s/4s4h",
		"/0s0o/2s2a/1s1i",
		"/0s0o/2s2a/1s1l",
		"/0s0o/2s2a/1s1i/4s4a",
		"/0s0o/2s2a/1s1i/4s4h",
		"/0s0o/2s2a/1s1l/4s4a",
		"/0s0o/2s2a/1s1l/4s4h",
		"/0s0o/2s2a/4s4a",
		"/0s0o/2s2a/4s4h",
		"/0s0o/2s2a/4s4a",
		"/0s0o/2s2a/4s4h",
		"/0s0o/3s3e",
		"/0s0o/3s3e/5s5s",
		"/0s0o/3s3e/5s5s/1s1i",
		"/0s0o/3s3e/5s5s/1s1l",
		"/0s0o/3s3e/5s5s/1s1i/4s4a",
		"/0s0o/3s3e/5s5s/1s1i/4s4h",
		"/0s0o/3s3e/5s5s/1s1l/4s4a",
		"/0s0o/3s3e/5s5s/1s1l/4s4h",
		"/0s0o/3s3e/5s5s/4s4a",
		"/0s0o/3s3e/5s5s/4s4h",
		"/0s0o/3s3e/5s5s/4s4a",
		"/0s0o/3s3e/5s5s/4s4h",
		"/0s0o/3s3e/1s1i",
		"/0s0o/3s3e/1s1l",
		"/0s0o/3s3e/1s1i/4s4a",
		"/0s0o/3s3e/1s1i/4s4h",
		"/0s0o/3s3e/1s1l/4s4a",
		"/0s0o/3s3e/1s1l/4s4h",
		"/0s0o/3s3e/4s4a",
		"/0s0o/3s3e/4s4h",
		"/0s0o/3s3e/4s4a",
		"/0s0o/3s3e/4s4h",
		"/0s0o/5s5s",
		"/0s0o/5s5s/1s1i",
		"/0s0o/5s5s/1s1l",
		"/0s0o/5s5s/1s1i/4s4a",
		"/0s0o/5s5s/1s1i/4s4h",
		"/0s0o/5s5s/1s1l/4s4a",
		"/0s0o/5s5s/1s1l/4s4h",
		"/0s0o/5s5s/4s4a",
		"/0s0o/5s5s/4s4h",
		"/0s0o/5s5s/4s4a",
		"/0s0o/5s5s/4s4h",
		"/0s0o/1s1i",
		"/0s0o/1s1l",
		"/0s0o/1s1i/4s4a",
		"/0s0o/1s1i/4s4h",
		"/0s0o/1s1l/4s4a",
		"/0s0o/1s1l/4s4h",
		"/0s0o/4s4a",
		"/0s0o/4s4h",
		"/0s0o/4s4a",
		"/0s0o/4s4h",
		"/2s2a",
		"/2s2a/3s3e",
		"/2s2a/3s3e/5s5s",
		"/2s2a/3s3e/5s5s/1s1i",
		"/2s2a/3s3e/5s5s/1s1l",
		"/2s2a/3s3e/5s5s/1s1i/4s4a",
		"/2s2a/3s3e/5s5s/1s1i/4s4h",
		"/2s2a/3s3e/5s5s/1s1l/4s4a",
		"/2s2a/3s3e/5s5s/1s1l/4s4h",
		"/2s2a/3s3e/5s5s/4s4a",
		"/2s2a/3s3e/5s5s/4s4h",
		"/2s2a/3s3e/5s5s/4s4a",
		"/2s2a/3s3e/5s5s/4s4h",
		"/2s2a/3s3e/1s1i",
		"/2s2a/3s3e/1s1l",
		"/2s2a/3s3e/1s1i/4s4a",
		"/2s2a/3s3e/1s1i/4s4h",
		"/2s2a/3s3e/1s1l/4s4a",
		"/2s2a/3s3e/1s1l/4s4h",
		"/2s2a/3s3e/4s4a",
		"/2s2a/3s3e/4s4h",
		"/2s2a/3s3e/4s4a",
		"/2s2a/3s3e/4s4h",
		"/2s2a/5s5s",
		"/2s2a/5s5s/1s1i",
		"/2s2a/5s5s/1s1l",
		"/2s2a/5s5s/1s1i/4s4a",
		"/2s2a/5s5s/1s1i/4s4h",
		"/2s2a/5s5s/1s1l/4s4a",
		"/2s2a/5s5s/1s1l/4s4h",
		"/2s2a/5s5s/4s4a",
		"/2s2a/5s5s/4s4h",
		"/2s2a/5s5s/4s4a",
		"/2s2a/5s5s/4s4h",
		"/2s2a/1s1i",
		"/2s2a/1s1l",
		"/2s2a/1s1i/4s4a",
		"/2s2a/1s1i/4s4h",
		"/2s2a/1s1l/4s4a",
		"/2s2a/1s1l/4s4h",
		"/2s2a/4s4a",
		"/2s2a/4s4h",
		"/2s2a/4s4a",
		"/2s2a/4s4h",
		"/3s3e",
		"/3s3e/5s5s",
		"/3s3e/5s5s/1s1i",
		"/3s3e/5s5s/1s1l",
		"/3s3e/5s5s/1s1i/4s4a",
		"/3s3e/5s5s/1s1i/4s4h",
		"/3s3e/5s5s/1s1l/4s4a",
		"/3s3e/5s5s/1s1l/4s4h",
		"/3s3e/5s5s/4s4a",
		"/3s3e/5s5s/4s4h",
		"/3s3e/5s5s/4s4a",
		"/3s3e/5s5s/4s4h",
		"/3s3e/1s1i",
		"/3s3e/1s1l",
		"/3s3e/1s1i/4s4a",
		"/3s3e/1s1i/4s4h",
		"/3s3e/1s1l/4s4a",
		"/3s3e/1s1l/4s4h",
		"/3s3e/4s4a",
		"/3s3e/4s4h",
		"/3s3e/4s4a",
		"/3s3e/4s4h",
		"/5s5s",
		"/5s5s/1s1i",
		"/5s5s/1s1l",
		"/5s5s/1s1i/4s4a",
		"/5s5s/1s1i/4s4h",
		"/5s5s/1s1l/4s4a",
		"/5s5s/1s1l/4s4h",
		"/5s5s/4s4a",
		"/5s5s/4s4h",
		"/5s5s/4s4a",
		"/5s5s/4s4h",
		"/1s1i",
		"/1s1l",
		"/1s1i/4s4a",
		"/1s1i/4s4h",
		"/1s1l/4s4a",
		"/1s1l/4s4h",
		"/4s4a",
		"/4s4h",
		"/4s4a",
		"/4s4h"
	};

	public static final String[] constructors = {
		":",
		"r",
		"d",
		"f",
		"dr",
		"fr",
		"rf"
	};
	
	public static String find(Packer packer, String password) throws IOException {
		for (int i = 0; i < CrackLib.destructors.length; i++) {
			String mp;
	
			if ((mp = Rules.mangle(password, CrackLib.destructors[i])) == null) {
				continue;
			}
	
			if (packer.find(mp) != -1) {
				return mp;
			}
		}
	
		password = Rules.reverse(password);
	
		for (int i=0;i < CrackLib.destructors.length; i++) {
			String mp;
	
			if ((mp = Rules.mangle(password, CrackLib.destructors[i])) == null) {
				continue;
			}
	
			if (packer.find(mp) != -1) {
				return mp;
			}
		}
		
		return null;
	}
}

