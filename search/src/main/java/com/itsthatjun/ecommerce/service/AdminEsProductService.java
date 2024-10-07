package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import com.itsthatjun.ecommerce.dto.ProductDTO;
import com.itsthatjun.ecommerce.dto.ProductDTOMapper;
import com.itsthatjun.ecommerce.repository.elasticsearch.EsProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@Service
public class AdminEsProductService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminEsProductService.class);

    private EsProductRepository productRepository;

    private ProductDTOMapper productDTOMapper;

    private final WebClient webClient;

    private final String PMS_SERVICE_URL = "http://pms/product";

    @Autowired
    public AdminEsProductService(WebClient webClient, ProductDTOMapper productDTOMapper, EsProductRepository productRepository) {
        this.webClient = webClient;
        this.productDTOMapper = productDTOMapper;
        this.productRepository = productRepository;
    }

    public Mono<String> importAll() {
        String url = PMS_SERVICE_URL + "/listAll";

        return fetchProducts(url)
                .map(productDTOMapper::productDTOToEsProduct) // Transform ProductDTO to ESProduct
                .flatMap(productRepository::save) // Save each ESProduct
                .doOnNext(product -> LOG.info("Product imported: {}", product.getName())) // Log each imported product
                .count() // Count the number of products imported
                .flatMap(count -> {
                    return Mono.just("Imported " + count + " products.");
                })
                .onErrorResume(e -> {
                    LOG.error("Failed to import products from PMS", e);
                    return Mono.just("Failed to import products.");
                });
    }

    private Flux<ProductDTO> fetchProducts(String url) {
        return webClient.get().uri(url)
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .log(LOG.getName(), FINE)
                .onErrorResume(e -> {
                    LOG.error("Failed to fetch products from PMS", e);
                    return Flux.empty();
                });
    }

    public Flux<EsProduct> listAllImportedProduct() {
        return productRepository.findAll()
                .log(LOG.getName(), FINE)
                .onErrorResume(e -> {
                    LOG.error("Failed to list imported products", e);
                    return Flux.empty();
                });
    }

//    public EsProduct create(Long id) {
//        EsProduct result = null;
//        List<EsProduct> productList = new ArrayList<>(); // TODO: get it from PMS api
//        if (productList.size() > 0) {
//            EsProduct newProduct = productList.get(0);
//            result = productRepository.save(newProduct);
//        }
//        return result;
//    }
//
//    public EsProduct update(Long id) {
//        productRepository.deleteById(id);
//    }
//
//    public void delete(Long id) {
//        productRepository.deleteById(id);
//    }
//
//    public void delete(List<Long> ids) {
//        if (!CollectionUtils.isEmpty(ids)) {
//            List<EsProduct> esProductList = new ArrayList<>();
//            for(Long id: ids) {
//                EsProduct esproduct = new EsProduct();
//                esproduct.setId(id);
//                esProductList.add(esproduct);
//            }
//            productRepository.deleteAll(esProductList);
//        }
//    }
}
