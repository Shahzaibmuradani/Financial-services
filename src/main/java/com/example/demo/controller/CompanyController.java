package com.example.demo.controller;

import com.example.demo.model.Company;
import com.example.demo.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyRepository companyRepository;

//    @PostMapping("/lotsize")
//        public ResponseEntity<String> createEntry(@RequestBody MultipartFile file) throws IOException {
//
//        File convFile = new File(file.getOriginalFilename());
////        convFile.createNewFile();
////        FileOutputStream fos = new FileOutputStream(convFile);
////        fos.write(file.getBytes());
////        fos.close();
//
//        try {
//            Company company;
//            Scanner sc = new Scanner(convFile);
//            while(sc.hasNextLine()){
//                company = new Company();
//                String line = sc.nextLine();
//              //  System.out.println(line);
//                String[] details = line.split(",");
//                String Reg = details[0];
//                String symbol = details[1];
//                long boardlot = Long.parseLong(details[2]);
//                System.out.println(Reg);
//                System.out.println(symbol);
//                System.out.println(boardlot);
//                company.setReg(Reg);
//                company.setSymbol_Code(symbol);
//                company.setBoardlot(boardlot);
//                //this.companyRepository.save(company);
//            }
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(String.format("File uploaded successfully: %s", file.getName()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(String.format("Could not upload the file: %s!", file.getName()));
//        }
//    }

    @PostMapping("/lotsize")
    public ResponseEntity uploadLotSizes(@RequestParam(value = "file") MultipartFile file) {
        try {
            String symbol_code = "", name="",sector="",price,gcProfile="",address="",
                    website="",auditor="",registrar="",fiscal_year_end="",key_people="";
            double currentPrice = 0.00,lotszize = 1;
            if (file != null && !file.isEmpty()) {
                File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
                FileOutputStream fos = new FileOutputStream(convFile);
                fos.write(file.getBytes());
                Scanner sc = new Scanner(convFile);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.startsWith("REG,")) {
                        String[] data = line.substring(0, line.length() - 1).split(",");
                        Company foundCompany = companyRepository.getCompanyBySymbol_Code(data[1].trim());
                        System.out.println("Symbol Code that is not available in databases : ");
                        if(foundCompany == null)
                        {
//                            System.out.println(data[1].trim());
                            symbol_code = data[1].trim();
                            lotszize = Double.parseDouble(data[2].trim());
                            Document doc = Jsoup.connect("https://dps.psx.com.pk/company/"+symbol_code).userAgent("Google/").get();

                            // fetching profile,address,website,auditor,registrar,fis_year
                            Elements elements = doc.select("div.profile__item > p");
                            Elements bio = elements.eq(0);
                            Elements add = elements.eq(1);
                            Elements web = elements.eq(2);
                            Elements aud = elements.eq(3);
                            Elements reg = elements.eq(4);
                            Elements fis_year = elements.eq(5);

                            gcProfile = bio.select("p").text();
                            address = add.select("p").text();
                            website = web.select("p").text();
                            registrar = reg.select("p").text();
                            auditor = aud.select("p").text();
                            fiscal_year_end = fis_year.select("p").text();

                            //fetching data for name,sector,current,price
                            Elements eleName = doc.select("div.quote__name");
                            Elements eleSector = doc.select("div.quote__sector");
                            Elements elecurrentPrice = doc.select("div.quote__close");
                            name = eleName.select("div.quote__name").text();
                            sector = eleSector.select("div.quote__sector").text();
                            price = elecurrentPrice.select("div.quote__close").text();
                            currentPrice = Double.parseDouble(price.substring(3));

                            //fetching table for key people
                            Elements table = doc.select("tbody.tbl__body");
                            Elements tds = table.eq(0);
                            key_people = tds.select("td").text();

                            String CEO [] = key_people.split(("CEO "));
                            String Chairman[] = (CEO[1].split("Chairman "));
                            String Secretary [] = Chairman[0].split("Secretary");

                            // Print all required data
                            System.out.println("symbol:"+symbol_code);
                            System.out.println("market:REG");
                            System.out.println("gcName:"+name);
                            System.out.println("key_people:"+CEO[0]+"@CEO|"+Chairman[0]+"@Chairman|"+Secretary[0]+"@Company Secretary");
                            System.out.println("address:"+address);
                            System.out.println("website:"+website);

                            System.out.println("auditor:"+auditor);
                            System.out.println("fiscal_year_end:"+fiscal_year_end);
                            System.out.println("gcLotSize:"+lotszize);
                            if (lotszize != 1){
                                System.out.println("lotSizeEnabled:"+true);
                            }
                            System.out.println("gcProfile:"+gcProfile);
                            System.out.println("gcSector:"+sector);
                            System.out.println("registrar:"+registrar);
                            System.out.println("currentPrice:"+currentPrice);
                        }
//                        if (foundCompany != null) {
//                            int lotSize = Integer.parseInt(data[2]);
//                            if (foundCompany.getGcLotSize() != lotSize) {
//                                logger.info("Updating Lot Size of: " + foundCompany.getGcSymbol() + " - Current LotSize: " + foundCompany.getGcLotSize() + " - Updated LotSize: " + lotSize);
//                                foundCompany.setLotSizeEnabled(lotSize != 1);
//                                foundCompany.setGcLotSize(lotSize);
//                                companyRepository.save(foundCompany);
//                            }
//                        } else {
//                            logger.info("We don't have company: " + data[1].trim());
//                        }
                    }
                }
                fos.close();
                convFile.delete();
            }
        } catch (Exception ex) {
            //return FyntrosResponseEntity.create(StatusCode.SOMETHING_UNEXPECTED_HAPPENED, ex.getMessage(), HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(String.format("Could not upload the file: %s!", file.getName()));
        }
       // return FyntrosResponseEntity.create(StatusCode.OK, "Lot Sizes Updated Successfully", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", file.getName()));
    }



    // update boardlot
//    @PutMapping("/{symbol_code}")
//    public ResponseEntity<Company> updateBoardlot (@PathVariable (value = "symbol_code") String symbol_code, @RequestBody Company companyDetails) throws ResourceNotFoundException{
//        Company company = companyRepository.getOne(symbol_code);
//        System.out.println(symbol_code);
//        company.setBoardlot(company.getBoardlot());
//
//        return ResponseEntity.ok(this.companyRepository.save(company));
//    }
}
