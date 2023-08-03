package com.jtspringproject.JtSpringProject.services;

import com.jtspringproject.JtSpringProject.dao.basketProductDao;
import com.jtspringproject.JtSpringProject.dao.basketDao;
import com.jtspringproject.JtSpringProject.models.Basket;
import com.jtspringproject.JtSpringProject.models.BasketProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.HashMap;

@Service
public class basketService {

    private final basketDao basketDao;
    private final basketProductDao basketProductDao;

    @Autowired
    public basketService(basketDao basketDao, basketProductDao basketProductDao) {
        this.basketDao = basketDao;
        this.basketProductDao = basketProductDao;
    }

    @Transactional
    public void addBasket(Basket basket) {
        basketDao.save(basket);
    }

    @Transactional
    public void updateBasket(Basket basket) {
        basketDao.update(basket);
    }

    @Transactional
    public void deleteBasket(int id) {
        basketDao.delete(id);
    }

    @Transactional
    public Basket findBasket(int id) {
        return basketDao.findById(id);
    }

    @Transactional
    public List<Basket> findAllBaskets() {
        return basketDao.findAll();
    }

    public List<Basket> findAllBasketsById(int user_id) {
        return basketDao.findAllBasketByUser(user_id);
    }

    // BASKET PRODUCT //

    @Transactional
    public void addProductToBasket(BasketProduct basketProduct) {
        basketProductDao.save(basketProduct);
    }

    @Transactional
    public void removeProductFromBasket(int id) {
        BasketProduct basketProduct = basketProductDao.findById(id);
        if (basketProduct != null) {
            System.out.println("\n\n\n\nBasketProduct found: " + basketProduct.toString());
            basketProductDao.delete(id);
        } else {
            System.out.println("\n\n\n\n\nBasketProduct not found for ID: " + id);
        }
    }
    //created this implementation for universal basket, we prob need to add more constraints for user
    //specific
    @Transactional
    public void removeProductFromBasketPID(int product_id) {
        BasketProduct basketProduct = basketProductDao.findByProductId(product_id);
        if (basketProduct != null) {
            basketProductDao.delete(basketProduct.getBasket_product_id());
        } else {
            System.out.println("BasketProduct why not not found for Product ID: " + product_id);
        }
    }

    @Transactional
    public void updateProductInBasket(BasketProduct basketProduct) {
        basketProductDao.update(basketProduct);
    }

    @Transactional
    public BasketProduct findProductInBasket(int id) {
        return basketProductDao.findById(id);
    }

    @Transactional
    public List<BasketProduct> findAllBasketProducts() {
        return basketProductDao.findAll();
    }

    @Transactional
    public List<BasketProduct> findAllProductInBasketByBasketId(int basket_id) {
        List<BasketProduct> allBasketsProducts = basketProductDao.findAll();
        return allBasketsProducts.stream()
                .filter(basketproduct -> basket_id == (basketproduct.getBasket().getBasketId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeAllProductsFromBasket() {
        basketProductDao.deleteAll();
    }

    @Transactional
    public List<Basket> findAllRegularBaskets() {
        List<Basket> allBaskets = basketDao.findAll();
        return allBaskets.stream()
                .filter(basket -> "BASKET".equals(basket.getBasketType()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Basket> findAllCustomBaskets() {
        List<Basket> allBaskets = basketDao.findAll();
        return allBaskets.stream()
                .filter(basket -> "CUSTOM_BASKET".equals(basket.getBasketType()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void combineBaskets(Basket c_basket, Basket r_basket) {
        List<BasketProduct> custom_basket_products = findAllProductInBasketByBasketId(c_basket.getBasketId());
        List<BasketProduct> regular_basket_products = findAllProductInBasketByBasketId(r_basket.getBasketId());
    
        List<Integer> product_id_list = regular_basket_products.stream()
                .map(basketproduct -> basketproduct.getProduct().getId())
                .collect(Collectors.toList());
        for (BasketProduct c_product : custom_basket_products) {
            int productId = c_product.getProduct().getId();
            int newQuantity = c_product.getQuantity();

            if (product_id_list.contains(productId)) {
                // Product ID exists in regular_basket_products, update the quantity
                for (BasketProduct r_product : regular_basket_products) {
                    if (r_product.getProduct().getId() == productId) {
                        r_product.setQuantity( r_product.getQuantity() + newQuantity );
                        updateProductInBasket(r_product);
                        break;
                    }
                }
            } 
            else {
                // if custom product not already in regular basket
                BasketProduct new_product = new BasketProduct();   
                new_product.setBasket(r_basket);
                new_product.setProduct(c_product.getProduct());
                new_product.setQuantity(newQuantity);       
                addProductToBasket(new_product);
            }
        }
    }

    @Transactional
    public void addCustomBasketToBasket(int user_id) {
        List<Basket> allBaskets = basketDao.findAll();
        // get users baskets
        List<Basket> usersBaskets = allBaskets.stream()
                .filter(basket -> user_id == basket.getUser().getId())
                .collect(Collectors.toList());
        // get custom
        Basket custom_basket = (usersBaskets.stream()
                .filter(basket_c -> "CUSTOM_BASKET".equals(basket_c.getBasketType()))
                .collect(Collectors.toList())).get(0);
        // get regular
        Basket basket = (usersBaskets.stream()
                .filter(basket_r -> "BASKET".equals(basket_r.getBasketType()))
                .collect(Collectors.toList())).get(0);

        combineBaskets(custom_basket, basket);
    }
}




