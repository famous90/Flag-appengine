package com.flag.engine.servelets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;

public class ServeImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ImagesService imagesService = ImagesServiceFactory.getImagesService();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Image image = ImagesServiceFactory.makeImageFromBlob(new BlobKey(req.getParameter("blob-key")));
		Transform dummy = ImagesServiceFactory.makeRotate(360);
		image = imagesService.applyTransform(dummy, image);
		int width = 0;
		int height = 0;
		
		try {
			width = Integer.valueOf(req.getParameter("width"));
			height = Integer.valueOf(req.getParameter("height"));
		} catch (NumberFormatException e) {
			width = 360;
			height = 360;
		}
		
		if (width != 0 && height != 0) {
			Transform resize = makeResize(image, width, height);
			image = imagesService.applyTransform(resize, image);

			Transform centerCrop = makeCenterCrop(image, width, height);
			OutputSettings settings = new OutputSettings(ImagesService.OutputEncoding.JPEG);
			settings.setQuality(60);
			image = imagesService.applyTransform(centerCrop, image, settings);
		}
		
		res.addHeader("Content-Type", "image/" + image.getFormat().toString());
		res.getOutputStream().write(image.getImageData());
	}

	private Transform makeResize(Image image, float targetWidth, float targetHeight) {
		float width = image.getWidth();
		float height = image.getHeight();
		float wratio = width / targetWidth;
		float hratio = height / targetHeight;
		int size;
		
		if (wratio > hratio) {
			size = (int) (width / hratio);
		} else {
			size = (int) (height / wratio);
		}
		
		return ImagesServiceFactory.makeResize(size, size);
	}

	private Transform makeCenterCrop(Image image, float targetWidth, float targetHeight) {
		float width = image.getWidth();
		float height = image.getHeight();
		float wratio = width / targetWidth;
		float hratio = height / targetHeight;
		
		float leftX = 0;
		float topY = 0;
		float rightX = 1;
		float bottomY = 1;

		if (wratio > hratio) {
			float move = ((width - height * (targetWidth / targetHeight)) / 2) / width;
			leftX += move;
			rightX -= move;
		} else {
			float move = ((height - width * (targetHeight / targetWidth)) / 2) / height;
			topY += move;
			bottomY -= move;
		}
		
		return ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY);
	}
}
