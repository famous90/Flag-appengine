package com.flag.engine.servelets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.flag.engine.exceptions.InvalidSheetFormatException;
import com.flag.engine.models.Item;
import com.flag.engine.models.PMF;

public class ItemSheet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletFileUpload upload = new ServletFileUpload();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		long shopId = 0;
		List<Item> items = new ArrayList<Item>();

		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (item.isFormField() && item.getFieldName().equals("shopId")) {
					String shopIdString = Streams.asString(item.openStream());
					if (shopIdString == null || shopIdString.isEmpty()) {
						res.getWriter().write("shop id is missing");
						return;
					} else {
						try {
							shopId = Long.valueOf(shopIdString);
						} catch (NumberFormatException e) {
							res.getWriter().write("shop id must be a number : " + shopIdString);
							return;
						}
					}
				} else {
					Workbook wb;
					if (item.getName().endsWith(".xlsx"))
						wb = new XSSFWorkbook(item.openStream());
					else if (item.getName().endsWith(".xls"))
						wb = new HSSFWorkbook(item.openStream());
					else
						throw new InvalidSheetFormatException();

					Sheet sheet = wb.getSheetAt(0);
					Row row;
					Cell cell;

					int rowCount;
					rowCount = sheet.getPhysicalNumberOfRows();

					for (int r = 1; r < rowCount; r++) {
						row = sheet.getRow(r);
						if (row != null) {
							String[] dataArray = new String[6];
							for (int c = 0; c < 6; c++) {
								cell = row.getCell(c);
								if (cell != null)
									dataArray[c] = cell.toString();
								else
									dataArray[c] = "";
							}

							items.add(new Item(shopId, dataArray));
						}
					}
				}
			}

			if (shopId != 0)
				saveItems(items);

		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (InvalidSheetFormatException e) {
			e.printStackTrace();
		}
	}

	private void saveItems(List<Item> items) {
		PersistenceManager pm = PMF.getPersistenceManagerSQL();
		pm.makePersistentAll(items);
		pm.close();
	}

}
