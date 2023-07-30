package nagp.java.assignment;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class FileReading {
	private ApplicationProperties properties;
	private String directory;

	FileReading() {
		this.properties = new ApplicationProperties();
		this.directory = properties.readProperty("directorypath");
	}

	public List<BrandInfo> FileReadData(File file) {
		List<BrandInfo> records = new ArrayList<BrandInfo>();
		CSVReader csvReader = null;
		try (FileReader fileReader = new FileReader(file)) {
			CSVParser parser = new CSVParserBuilder().withSeparator('|').build();
			csvReader = new CSVReaderBuilder(fileReader).withCSVParser(parser).withSkipLines(1).build();

			csvReader.readAll().forEach(tshirtRecord -> {
				BrandInfo shirt = new BrandInfo();
				shirt.setId(tshirtRecord[0]);
				shirt.setName(tshirtRecord[1]);
				shirt.setColour(tshirtRecord[2]);
				shirt.setGenderRecommendation(tshirtRecord[3]);
				shirt.setSize(tshirtRecord[4]);
				shirt.setPrice(Double.parseDouble(tshirtRecord[5]));
				shirt.setRating(Double.parseDouble(tshirtRecord[6]));
				shirt.setAvailability(tshirtRecord[7]);
				records.add(shirt);
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return records;

	}

	public List<BrandInfo> readAllFiles() {
		FilenameFilter filter = (file, name) -> name.endsWith(".csv");
		File[] allFile = new File(directory).listFiles(filter);
		List<BrandInfo> filesRecord = new ArrayList<BrandInfo>();
		for (File file : allFile) {
			filesRecord.addAll(FileReadData(file));
		}
		return filesRecord;
	}

}
