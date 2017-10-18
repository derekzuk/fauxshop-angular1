package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.FauxshopApp;
import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.*;
import com.mycompany.myapp.service.dto.CartDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.web.rest.vm.KeyAndPasswordVM;
import com.mycompany.myapp.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.SysexMessage;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CartResource REST controller.
 *
 * @see CartResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FauxshopApp.class)
public class CartResourceIntTest {

    @Autowired
    private HttpMessageConverter[] httpMessageConverters;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductsService productsService;

    @Autowired
    private ProductsDescriptionService productsDescriptionService;

    @Mock
    private MailService mockMailService;

    @Mock
    private CartService mockCartService;

    @Mock
    private ProductsService mockProductsService;

    @Mock
    private ProductsDescriptionService mockProductsDescriptionService;

    private MockMvc restUserMockMvc;

    private MockMvc restMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendActivationEmail(anyObject());

        CartResource cartResource =
            new CartResource(cartService, productsService, productsDescriptionService);

        CartResource cartUserMockResource =
            new CartResource(mockCartService, mockProductsService, mockProductsDescriptionService);

        this.restMvc = MockMvcBuilders.standaloneSetup(cartResource)
            .setMessageConverters(httpMessageConverters)
            .build();
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(cartUserMockResource).build();
    }

    @Test
    @Transactional
    public void testGetEmptyCartById() throws Exception {
        List<Cart> cartList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setId(2L);
        cart.setProductsId(3L);
        cart.setCartItemQuantity(10);
        cart.setCartItemTotalPrice(BigDecimal.TEN);
        cartList.add(cart);
        Optional<List<Cart>> cartOptional = Optional.of(cartList);

        Products products = new Products();
        products.setProductsId(3L);
        products.setProductsQuantity(55);
        Optional<Products> productsOptional = Optional.of(products);

        ProductsDescription productsDescription = new ProductsDescription();
        productsDescription.setProductsDescription("description");
        productsDescription.setProductsId(3L);
        productsDescription.setProductsName("productsName");
        productsDescription.setProductsURL("url");
        productsDescription.setProductsViewed(1);
        Optional<ProductsDescription> productsDescriptionOptional = Optional.of(productsDescription);

        when(mockCartService.findAllById(2L)).thenReturn(cartOptional);
        when(mockProductsService.getProductsByProductsId(3L)).thenReturn(productsOptional);
        when(mockProductsDescriptionService.getProductsDescriptionByProductsId(3L)).thenReturn(productsDescriptionOptional);

        restUserMockMvc.perform(get("/api/cart/2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$[0].id").value("2"))
            .andExpect(jsonPath("$[0].productsName").value("productsName"))
            .andExpect(jsonPath("$[0].productsQuantity").value("55"));
    }
}
