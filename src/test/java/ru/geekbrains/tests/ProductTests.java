package ru.geekbrains.tests;

import com.github.javafaker.Faker;
import ru.geekbrains.db.dao.CategoriesMapper;
import ru.geekbrains.db.model.Categories;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.geekbrains.db.dao.ProductsMapper;
import ru.geekbrains.dto.Product;
import ru.geekbrains.enums.CategoryType;
import ru.geekbrains.dto.Category;
import ru.geekbrains.service.CategoryService;
import ru.geekbrains.service.ProductService;
import ru.geekbrains.utils.DbUtils;
import ru.geekbrains.utils.RetrofitUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ru.geekbrains.TestData .*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductTests {
    int productId;
    static ProductsMapper productsMapper = DbUtils.getProductsMapper();
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    static CategoriesMapper categoriesMapper = DbUtils.getCategoriesMapper();
    Faker faker = new Faker();
    Product product;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);

    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());

        Response<Product> response = null;
        try {
            response = productService.createProduct(product).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        productId = Objects.requireNonNull(response.body()).getId();

    }

    @Test
         // Создание продукта
    void postProductTest() throws IOException {
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product).execute();
        Integer countProductsAfter = DbUtils.countProducts(productsMapper);
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        productId = response.body().getId();
    }


    @Test
        //Нахождение товара по ID
    void getProductIdPositiveTest() throws IOException {
        Response<Product> response = productService.getProduct(productId)
                .execute();
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }


    @Test
        //Чтение списка всех продуктов
    void getListAllProductsTest() throws IOException {
        Response<List<Product>> response = productService
                .getProducts()
                .execute();
        assertThat(response.raw(), CoreMatchers.not(equalTo("0")));
    }

    @Test
        //Нахождение категории по ID
    void getCategoryByIdTest() throws IOException {
        Integer id = CategoryType.FOOD.getId();
        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
        assertThat(response.body().getId(), equalTo(id));
    }


    @Test
        //Нахождение категории по неактуальному ID
    void getCategoryWrongIdTest() throws IOException {
        Integer id = irrelevantCategoryId;
        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
        assertThat(response.code(), equalTo(404));
    }


    @Test
        //Изменить данные в продукте
    void updateProductTest() throws IOException {
        Product newProduct = new Product()
                .withId(productId)
                .withCategoryTitle(CategoryType.FOOD.getTitle())
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());
        Response<Product> response = productService
                .updateProduct(newProduct)
                .execute();
        assertThat(response.body().getId(), equalTo(productId));
        assertThat(response.body().getPrice(), equalTo(newProduct.getPrice()));
        assertThat(response.body().getTitle(), equalTo(newProduct.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo(newProduct.getCategoryTitle()));
    }


    @Test
        //Создать длинное название продукта
    void createProductLongTitleTest() throws IOException {
        Response<Product> response = productService
                .createProduct(new Product().withTitle(faker.lorem().fixedString(5555)))
                .execute();
        Objects.requireNonNull(response.errorBody()).string();
        assertThat(response.code(), equalTo(500));
    }



    @AfterEach
    void tearDown() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(productId).execute();
        assertThat(response.isSuccessful(), is(true));
    }
}
