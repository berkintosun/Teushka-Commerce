package com.teushka.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teushka.productservice.dto.ProductDTO;
import com.teushka.productservice.entity.Product;
import com.teushka.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.14");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TestDataUtility testDataUtility;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void before(){
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		Assertions.assertEquals(0,productRepository.count());
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(testDataUtility.getProductCreationRequest()))
		).andExpect(status().isCreated());
		Assertions.assertEquals(1,productRepository.count());
	}

	@Test
	void shouldListAllProducts() throws Exception {
		Assertions.assertEquals(0,productRepository.count());
		Product product = testDataUtility.createTempProduct();
		productRepository.save(product);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andReturn();
		ProductDTO[] productDTOS = {testDataUtility.mapProductToProductDTO(product)};
		Assertions.assertEquals(mapper.writeValueAsString(productDTOS),result.getResponse().getContentAsString());
	}
}
