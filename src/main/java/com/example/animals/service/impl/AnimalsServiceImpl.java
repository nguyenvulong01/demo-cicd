package com.example.animals.service.impl;

import com.example.animals.entity.Animal;
import com.example.animals.repository.AnimalRepository;
import com.example.animals.service.AnimalsService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class AnimalsServiceImpl implements AnimalsService {

    private static final Logger logger = LoggerFactory.getLogger(AnimalsServiceImpl.class);
    private final AnimalRepository animalRepository;
    private static final String FILE_PATH = "D:\\learnByMyself\\gateway\\myself\\animals";

    @Override
    public List<com.example.animals.dto.Animal> getAnimals() {
        logger.info("CALL ...");
        return new ArrayList<>(List.of(new com.example.animals.dto.Animal(UUID.randomUUID(), "Cat"), new com.example.animals.dto.Animal(UUID.randomUUID(), "Dog")));
    }

    @Override
    public void importFile(String fileName) {
        LocalDateTime now = LocalDateTime.now();
//        logger.info("START === {}", now);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int i=1; i<5; ++i) {
            int finalI = i;
            executorService.submit(() -> {
                this.readExcel(fileName + finalI + ".xlsx");
            });
        }

        executorService.shutdown();
//        logger.info("TOTAL === {}", Duration.between(now, LocalDateTime.now()).toSeconds());
    }

    private void readExcel(String filename) {
        LocalDateTime now = LocalDateTime.now();
        logger.info("FILE START - {} - {}", filename, now);
        try {
            FileInputStream file = new FileInputStream(new File(FILE_PATH + "\\" + filename));

            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            List<Animal> animals = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Animal animal = new Animal();
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                int i=0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String field = getCellValueAsString(cell);

                    if (!Strings.isEmpty(field)
                            && !field.equalsIgnoreCase("id")
                            && !field.equalsIgnoreCase("name")
                            && !field.equalsIgnoreCase("price")) {
                        if (i == 0) {
                            animal.setId(Long.valueOf(field));
                        } else if (i == 1) {
                            animal.setName(field);
                        } else {
                            animal.setPrice(Double.valueOf(field));
                        }
                    }
                    i++;
                }

                animals.add(animal);
            }

//            animalRepository.saveAll(animals);
            file.close();
            logger.info("FILE END - {} - {}", filename, Duration.between(now, LocalDateTime.now()).toSeconds());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case FORMULA:
                RichTextString richTextString = cell.getRichStringCellValue();
                return richTextString.toString();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    return dateFormat.format(date);
                } else {
                    NumberFormat numberFormat = new DecimalFormat("0");
                    return numberFormat.format(cell.getNumericCellValue());
                }

            case STRING:
                return cell.getStringCellValue();

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
                return "";

            default:
                return "Unsupported data type";
        }
    }
}
