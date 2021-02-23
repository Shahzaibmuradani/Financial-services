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
            String line1 [] = new String [3];
            String name="";
            String symbol_code = "";
            int i = 0;
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
                            Document doc = Jsoup.connect("https://dps.psx.com.pk/company/"+symbol_code).userAgent("Google/").get();
                            Elements elements = doc.select("div.profile__item");
                            Elements elements1 = doc.select("div.quote__name");
                            name = elements1.select("div.quote__name").text();
                            for (Element e : elements) {
                                line1[i] = e.getElementsByTag("p").text();
                                i++;
                                if (i == 3) {
                                    break;
                                }
                            }
                            System.out.println(name);
                            for(i=0; i<line1.length; i++){
                                System.out.println(line1[i]);
                            }
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
