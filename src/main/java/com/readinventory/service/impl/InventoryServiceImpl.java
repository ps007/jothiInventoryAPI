package com.readinventory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.readinventory.model.AddInventoryResponse;
import com.readinventory.model.Inventory;
import com.readinventory.model.InventoryAPICall;
import com.readinventory.repository.InventoryRepository;
import com.readinventory.service.InventoryService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.codec.binary.Base64;

@Service
public class InventoryServiceImpl implements InventoryService {
	
	
	@Autowired
	InventoryRepository inventoryRepository;

	@Override
	public AddInventoryResponse fileUpload(InventoryAPICall inventoryAPICall) throws Exception {
		try {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Iterator<Row> rowIterator;
		String error="";
		String getExcelExt = FilenameUtils.getExtension(inventoryAPICall.getFileName());
		String fileNameWithOutExt = FilenameUtils.removeExtension(inventoryAPICall.getFileName())
				+ dateFormat.format(date);
		Path tempFile = Files.createTempFile(fileNameWithOutExt, getExcelExt);
		byte[] decodedBytes = Base64.decodeBase64(inventoryAPICall.getFileContent().getBytes());
		Files.write(tempFile, decodedBytes);
		FileInputStream fileName = new FileInputStream(new File(tempFile.toString()));
		if ((getExcelExt.compareToIgnoreCase("xls") != 0) & (getExcelExt.compareToIgnoreCase("xlsx") != 0)) {
			throw new Exception("Invalid file");
		}
		if (getExcelExt.compareToIgnoreCase("xls") == 0) {
			HSSFWorkbook wb = new HSSFWorkbook(fileName);
			// creating a Sheet object to retrieve the object
			HSSFSheet sheet = wb.getSheetAt(0);
			rowIterator = sheet.iterator();
		} else {
			XSSFWorkbook workbook = new XSSFWorkbook(fileName);
			DataFormatter formatter = new DataFormatter();

			// Get first/desired sheet from the workbook
			XSSFSheet xlsxSheet = workbook.getSheetAt(0);
			rowIterator = xlsxSheet.iterator();

		}

		ArrayList dataList = new ArrayList();
		ArrayList list = new ArrayList();
		int i=0;
		while (rowIterator.hasNext()) {
			list = new ArrayList();
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				continue;
			}

			Iterator<Cell> cellIterator = row.cellIterator();
			// For each row, iterate through all the columns
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				switch (cell.getCellType()) {

				case NUMERIC:
					list.add(NumberToTextConverter.toText(cell.getNumericCellValue()));
					break;
				case STRING:
					list.add(cell.getStringCellValue());
					break;
				default:
					list.add(cell.getStringCellValue());
				break;

				}
				
			}
			dataList.add(list);
		}
		
			List<Inventory> inventoryList = new ArrayList<Inventory>();
			for(int j=0;j<dataList.size();j++)
			{
				Inventory inventory = new Inventory();
				List temp = new ArrayList();
				temp = (List) dataList.get(j);
				inventory.setSku((String) temp.get(0));
				inventory.setProduct_name((String) temp.get(1));
				inventory.setQuantity(Integer.parseInt((String) temp.get(2)));
				inventory.setPrice(Double.parseDouble((String) temp.get(3)));
				inventory.setCondition((String) temp.get(4));
				inventory.setIsbn((String) temp.get(5));
				inventory.setCreated_by("SYSTEM");
				inventory.setModified_by("SYSTEM");
				if(inventoryRepository.findByIsbn((String) temp.get(5)).size()>0)
				{
					error=error+(String) temp.get(5)+" Already Exist ";
				}
				inventoryList.add(inventory);
								
			}
			AddInventoryResponse res = new AddInventoryResponse();
			if(save(inventoryList)!=null && error.equalsIgnoreCase("")) {
			inventoryList=(List<Inventory>) inventoryRepository.findAll();
			res.setInventoryList(inventoryList);
			res.setError("success");
			}
			else
			{
				res.setInventoryList(inventoryList);
				res.setError(error);
				
				
			}
			return res;
		
		}
		catch(Exception e)
		{
		throw e;	
		}

	}
	
	@Transactional(rollbackOn = { Exception.class })
	public String save(List<Inventory> inventoryList)
	{
		try {
		
			Iterator<?> itr=inventoryList.iterator();
		while(itr.hasNext())
		{
			Inventory temp = (Inventory) itr.next();
			inventoryRepository.save(temp);
		}
		return "Success";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

}