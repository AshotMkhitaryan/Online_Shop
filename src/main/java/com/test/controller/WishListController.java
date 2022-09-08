package com.test.controller;

import com.test.common.ApiResponse;
import com.test.dto.product.ProductDto;
import com.test.model.Product;
import com.test.model.User;
import com.test.model.WishList;
import com.test.service.AuthenticationService;
import com.test.service.ProductService;
import com.test.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishListController {

    private final WishListService wishListService;

    private final AuthenticationService authenticationService;

    @Autowired
    public WishListController(WishListService wishListService, AuthenticationService authenticationService) {
        this.wishListService = wishListService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<List<ProductDto>> getWishList(@PathVariable("token") String token) {
        int user_id = authenticationService.getUser(token).getId();
        List<WishList> body = wishListService.readWishList(user_id);
        List<ProductDto> products = new ArrayList<>();
        for (WishList wishList : body) {
            products.add(ProductService.getDtoFromProduct(wishList.getProduct()));
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addWishList(@RequestBody Product product, @RequestParam("token") String token) {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);
        WishList wishList = new WishList(user, product);
        wishListService.createWishlist(wishList);
        return new ResponseEntity<>(new ApiResponse(true, "Add to wishlist"), HttpStatus.CREATED);

    }
}
