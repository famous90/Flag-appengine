package com.flag.engine.servelets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

public class Serve extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagesService imagesService = ImagesServiceFactory.getImagesService();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Image image = ImagesServiceFactory.makeImageFromBlob(new BlobKey(req.getParameter("blob-key")));

		Transform resize = ImagesServiceFactory.makeResize(360, 360, false);
		Image sizedImage = imagesService.applyTransform(resize, image, OutputEncoding.JPEG);

		Transform centerCrop = makeCenterCrop(sizedImage);
		Image croppedImage = imagesService.applyTransform(centerCrop, sizedImage);

		res.addHeader("Content-Type", "image/" + croppedImage.getFormat().toString());
		res.getOutputStream().write(croppedImage.getImageData());
	}

	private Transform makeCenterCrop(Image image) {
		int width = image.getWidth();
		int height = image.getHeight();
		float leftX = 0;
		float topY = 0;
		float rightX = 1;
		float bottomY = 1;

		if (width > height) {
			float move = (width - height) / 2;
			move = move / width;
			leftX += move;
			rightX -= move;
		} else {
			float move = (height - width) / 2;
			move = move / height;
			topY += move;
			bottomY -= move;
		}
		
		return ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
	}
}
