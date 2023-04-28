package com.example.gobitecustomer.data.retrofit

import com.example.gobitecustomer.data.modelNew.*
import retrofit2.Response
import retrofit2.http.*

interface CustomApi {

    //USER REPO
    @POST("/v2/auth/otp")
    suspend fun getOTP(@Body OTPRequest: OTPRequest): Response<OTP>

    @POST("/v2/auth/signup_with_mobile")
    suspend fun registerUser(@Body signUpRequestNew: SignUpRequestNew) : Response<SignupResult>

    @POST("/v2/auth/login")
    suspend fun LoginUser(@Body loginRequestNew: LoginRequestNew) : Response<LoginResponse>

    @GET("/v2/shop/list")
    suspend fun getShopsList(): Response<shopsList>


    @GET("/v2/shop/{shopId}/list_items")
    suspend fun getMenu(@Path("shopId") shopId : String) : Response<MenuItem>

    @PUT("/v2/user_profile/modify")
    suspend fun updateUser(@Body userRequest: UpdateUserRequest) : Response<UserUpdateResponse>



    //OLD WORK


//    @PATCH("/user/place") //This can be used for both sign-up and updating profile
//    suspend fun updateUser(@Body updateUserRequest: UpdateUserRequest): Responsesd<String>
//    @PATCH("/user/notif")
//    suspend fun updateFcmToken(@Body notificationTokenUpdateModel: NotificationTokenUpdate): Responsesd<String>
//
//    //SHOP REPO
//    @GET("/shop/place/{placeId}")
//    suspend fun getShops(@Path("placeId") placeId: String): Responsesd<List<ShopConfigurationModel>>
//
//    //PLACE REPO
//    @GET("/place")
//    suspend fun getPlaceList(): Responsesd<List<PlaceModel>>
//
//    //ITEM REPO
//    @GET("/menu/{placeId}/{query}")
//    suspend fun searchItems(@Path("placeId") placeId: String, @Path("query") query: String): Responsesd<List<MenuItemModel>>
////    @GET("/menu/shop/{shopId}")
////    suspend fun getMenu(@Path("shopId") shopId: String): Responsesd<List<MenuItemModel>>
//
//    //ORDER REPO
//    @GET("/order/{orderId}")
//    suspend fun getOrderById(@Path("orderId") orderId: Int): Responsesd<OrderItemListModel>
//    @GET("/order/customer/{userId}/{pageNum}/{pageCount}")
//    suspend fun getOrders(
//        @Path("userId") id: String,
//        @Path("pageNum") pageNum: Int,
//        @Path("pageCount") pageCount: Int): Responsesd<List<OrderItemListModel>>
//    @POST("/order")
//    suspend fun insertOrder(@Body placeOrderRequest: PlaceOrderRequest): Responsesd<VerifyOrderResponse>
//    @POST("/order/place/{orderId}")
//    suspend fun placeOrder(@Path("orderId") orderId: String): Responsesd<String>
//    @PATCH("/order/rating")
//    suspend fun rateOrder(@Body ratingRequest: RatingRequest): Responsesd<String>
//    @PATCH("/order/status")
//    suspend fun cancelOrder(@Body orderStatusRequest: OrderStatusRequest): Responsesd<String>


}