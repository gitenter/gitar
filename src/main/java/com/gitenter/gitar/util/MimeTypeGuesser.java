package com.gitenter.gitar.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.commons.io.FilenameUtils;

import eu.medsea.mimeutil.MimeUtil;

public class MimeTypeGuesser {

	public static String guess(String name, byte[] blobContent) throws IOException {
		
InputStream is = new BufferedInputStream(new ByteArrayInputStream(blobContent));
		
		/*
		 * Refer to:
		 * https://stackoverflow.com/questions/51438/getting-a-files-mime-type-in-java/18640199
		 * https://stackoverflow.com/questions/33998407/how-to-fetch-the-mime-type-from-byte-array-in-java-6
		 * https://docs.oracle.com/javaee/5/api/javax/activation/MimetypesFileTypeMap.html
		 * https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html
		 */
		String mimeType;
		
		mimeType = URLConnection.guessContentTypeFromStream(is);
		if (mimeType != null) {
			return mimeType;
		}
		
		mimeType = URLConnection.guessContentTypeFromName(name);
		if (mimeType != null) {
			return mimeType;
		}
		
		/*
		 * MimeUtil will get markdown MIME type "application/octet-stream",
		 * which is not correct.
		 */
		if (FilenameUtils.getExtension(name).equals("md")) {
			return "text/markdown";
		}
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeType = MimeUtil.getMimeTypes(is).iterator().next().toString();
		MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		if (mimeType != null) {
			return mimeType;
		}
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeType = MimeUtil.getMimeTypes(name).iterator().next().toString();
		MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		if (mimeType != null) {
			return mimeType;
		}
		
		return null;
	}
}
