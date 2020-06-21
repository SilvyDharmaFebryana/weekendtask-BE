package com.cimb.weekendtask.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;

import com.cimb.weekendtask.dao.ProdukRepo;
import com.cimb.weekendtask.entity.Produk;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/produk")
@CrossOrigin
public class ProdukController {
    

    private String uploadPath = System.getProperty("user.dir") + "\\weekendtask\\weekendtask\\src\\main\\resources\\static\\images\\";

    @Autowired
    private ProdukRepo produkRepo;

    @GetMapping
    public Iterable<Produk> getProduk() {
        return produkRepo.findAll();
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fieldData") String produkString) throws JsonMappingException, JsonProcessingException {

        Date date = new Date();

        Produk produk = new ObjectMapper().readValue(produkString, Produk.class);

        String fileExtension = file.getContentType().split("/")[1];
        String newFileName = "PROD-" + date.getTime() + "." + fileExtension;

        String fileName = StringUtils.cleanPath(newFileName);

        Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return fileName + " has been upload";

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/produk/download/")
                .path(fileName).toUriString();

        produk.setGambar(fileDownloadUri);;
        produkRepo.save(produk);
        
        return fileDownloadUri;
    }


    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
        Path path = Paths.get(uploadPath, fileName);

        Resource resource = null;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + resource.getFilename() + "\"").body(resource);
    }

    @GetMapping("/all")
    public Iterable<Produk> getAllProduk() {
        return produkRepo.findProduk();
    }
    

    @PutMapping("/{id}")
	public String editProduct(@RequestParam("file") MultipartFile file, @RequestParam("fieldData") String produkString, @PathVariable int id) throws JsonMappingException, JsonProcessingException {
		
		Produk findProduk = produkRepo.findById(id).get();
		
		
		findProduk = new ObjectMapper().readValue(produkString, Produk.class);
		Date date = new Date();
		
		String fileExtension = file.getContentType().split("/")[1];
		String newFileName = "PROD-" + date.getTime() + "." + fileExtension;
		
		String fileName = StringUtils.cleanPath(newFileName);
		
		Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);
		
		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/produk/download/")
				.path(fileName).toUriString();
		
		
		findProduk.setGambar(fileDownloadUri);
		
		produkRepo.save(findProduk);
		
		return fileDownloadUri;
    }
    
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable() int id) {

        Produk findProduct = produkRepo.findById(id).get();
        
		if (findProduct.toString() == "Optional.empty") {
			throw new RuntimeException("Product Not Found");
        }
        
		produkRepo.deleteById(id);
		
	}
    
}