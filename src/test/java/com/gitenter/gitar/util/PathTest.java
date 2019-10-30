package com.gitenter.gitar.util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class PathTest {

	@Test
	public void test() {
		
		assertEquals(Paths.get("").normalize().toString(), "");
		assertEquals(Paths.get(".").normalize().toString(), "");
		assertEquals(Paths.get("./abc/..").normalize().toString(), "");
		assertEquals(Paths.get("").isAbsolute(), false);
		assertEquals(Paths.get("").startsWith(".."), false);
		assertEquals(Paths.get("").getNameCount(), 1);

		assertEquals(Paths.get("abc/def/../").normalize().toString(), "abc");
		assertEquals(Paths.get("./abc/def/../ghi").normalize().toString(), "abc/ghi");
		assertEquals(Paths.get("abc").isAbsolute(), false);
		assertEquals(Paths.get("abc").startsWith(".."), false);
		assertEquals(Paths.get("abc").getNameCount(), 1);
		assertEquals(Paths.get("abc/def").getNameCount(), 2);
		assertEquals(Paths.get("abc/def").getName(0).toString(), "abc");
		assertEquals(Paths.get("abc/def").getName(1).toString(), "def");
		
		assertEquals(Paths.get("./../abc/../../ghi").normalize().toString(), "../../ghi");
		assertEquals(Paths.get("./../abc/def/../ghi").normalize().toString(), "../abc/ghi");
		assertEquals(Paths.get("../abc").isAbsolute(), false);
		assertEquals(Paths.get("../abc").startsWith(".."), true);
		
		assertEquals(Paths.get("/abc/def/../ghi").normalize().toString(), "/abc/ghi");
		assertEquals(Paths.get("/abc").isAbsolute(), true);
		assertEquals(Paths.get("/abc").getNameCount(), 1);
		assertEquals(Paths.get("/").getNameCount(), 0);
	}
}
