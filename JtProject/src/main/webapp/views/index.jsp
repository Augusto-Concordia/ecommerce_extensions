<%@page import="java.sql.*" %>
  <%@page import="java.util.*" %>
    <%@page import="java.text.*" %>
      <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

          <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
            <meta http-equiv="X-UA-Compatible" content="ie=edge">
            <script src="https://code.jquery.com/jquery-3.7.0.slim.min.js" integrity="sha256-tG5mcZUtJsZvyKAxYLVXrmjKBVLd6VpVccqz/r4ypFE=" crossorigin="anonymous"></script>
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link href="https://fonts.googleapis.com/css2?family=Autour+One&family=Cabin:ital,wght@0,400;0,500;0,600;0,700;1,400;1,500;1,600;1,700&display=swap" rel="stylesheet">
            <link rel="stylesheet" type="text/css" href="style.css">
            <title>Store Front</title>

            <script type="text/javascript">
                let slideIndex = 1;
                const slideInterval = 3000;
                let pauseAutoSlide = false;
                
                function changeSlide(x) {
                  showSlides(slideIndex += x);
                }

                function showSlides(x) {
                  let slides = document.querySelectorAll("#store-carousel .product");
                  
                  if (x > slides.length) 
                    slideIndex = 1;

                  if (x < 1) 
                    slideIndex = slides.length;

                  for (let i = 0; i < slides.length; i++) 
                    slides[i].style.display = "none";

                  slides[slideIndex - 1].style.display = "block";
                }

                function autoSlide() {
                  if (!pauseAutoSlide)
                    changeSlide(1);
                  setTimeout(autoSlide, slideInterval);
                }

              $(document).ready(function () {
                autoSlide();

                $(".btn-basket-carousel").on("click", function() {
                  $(this).nextAll(".store-unit-dropdown-basket").toggle();
                  pauseAutoSlide = true;
                });
                $(".btn-custombasket-carousel").on("click", function() {
                  $(this).nextAll(".store-unit-dropdown-custombasket").toggle();
                  pauseAutoSlide = true;
                });
                
                $(".btn-basket").on("click", function() {
                  $(this).nextAll(".store-unit-dropdown").toggle();
                });
                $(".btn-custombasket").on("click", function() {
                  $(this).nextAll(".store-unit-dropdown").toggle();
                });

                $(".unit-add-button").on("click", function() {
                    $(this).parent(".store-unit-dropdown").toggle();
                    $(this).parent().find(".unit-count").text("1");

                    if ($(".store-unit-dropdown-basket").is(":notvisible") && $(".store-unit-dropdown-custombasket").is(":notvisible"))
                      pauseAutoSlide = false;
                });

                $(".product").on("click", ".unit-increment-button", function() {
                  var unitCountSpan = $(this).siblings(".unit-count");
                  var unitCount = parseInt(unitCountSpan.text(), 10);
                  unitCount++;
                  unitCountSpan.text(unitCount.toString());
                });
                $(".product").on("click", ".unit-decrement-button", function() {
                  var unitCountSpan = $(this).siblings(".unit-count");
                  var unitCount = parseInt(unitCountSpan.text(), 10);
                  if (unitCount > 0)
                    unitCount--;
                  else
                    unitCount = 0;
                  unitCountSpan.text(unitCount.toString());
                });

                let isCustomBasket = false;

                let basket = $("#basket");
                let customBasket = $("#custom-basket");

                // Toggle between the two baskets
                $(".basket-type-switch").on("click", toggleBasketType);

                // Redirect to the admin portal
                $("#admin-btn").on("click", () => location.href = "admin/loginvalidate");

                // Logout
                $("#logout-btn").on("click", () => location.href = "userlogout");

                // Show the basket overlay after 5 seconds
                setTimeout(function () {
                  $(".basket #overlay").trigger("basketOverlayOpen");

                  setTimeout(function () {
                    $(".basket #overlay").trigger("basketOverlayClose");
                  }, 5000);
                }, 2000);

                // Toggle between login and register
                function toggleBasketType() {
                  isCustomBasket = !isCustomBasket;

                  updateBasket();
                }

                // Updates the currently displayed basket
                function updateBasket() {
                  if (isCustomBasket) {
                    customBasket.removeClass("disabled");
                    customBasket.addClass("enabled");
                    basket.addClass("disabled");
                    basket.removeClass("enabled");
                  } else {
                    customBasket.addClass("disabled");
                    customBasket.removeClass("enabled");
                    basket.addClass("enabled");
                    basket.removeClass("disabled");
                  }
                }
              });
            </script>
          </head>

          <header>
            <img id="logo" src="images/nyan_logo_nobg.png" alt="Store icon" width="48px" height="48px">

            <h3>
              Nyan Groceries
            </h3>

            <c:if test="${user.getRole() == 'ROLE_ADMIN'}">
              <img id="admin-btn" src="images/icons/admin.png" alt="Admin portal icon" class="btn btn-icon">
            </c:if>

            <img id="logout-btn" src="images/icons/exit.png" alt="Admin portal icon" class="btn btn-icon">
          </header>

          <body id="store-body">

            <!-- Storefront -->
            <div id="storefront">

              <!-- Product Carousel -->
              <div id="store-carousel">
                <div class="carousel-wrapper">
                  <c:forEach var="product" items="${products}" varStatus="loopStatus">
                    <c:if test="${loopStatus.index < 3}">
                      <div class="product">
                        <div class="product-details">
                          <h5 class="product-name">${product.name}</h5>
                          <h5 class="product-price">$${product.price}</h5>
                        </div>
                        <img class="product-img" src="${product.image}" alt="Product">
                        <div class="product-buttons">
                          <a class="btn btn-primary btn-basket-carousel"><img src="images/icons/basket.png" alt="Basket" width="40"></a>
                          <div class="store-unit-dropdown store-unit-dropdown-basket">
                            <div class="unit-selection-box">
                              <span class="unit-count">1</span>
                              <button class="unit-increment-button"><img src="images/icons/dropdown_arrow.png" alt="Up arrow" width="12"></button>
                              <button class="unit-decrement-button"><img src="images/icons/dropdown_arrow.png" alt="Down arrow" width="12"></button><br>
                            </div>
                            <button class="unit-add-button carousel">Add</button>
                          </div>
                          <a class="btn btn-primary btn-custombasket-carousel"><img src="images/icons/custombasket.png" alt="Basket" width="40"></a>
                          <div class="store-unit-dropdown store-unit-dropdown-custombasket">
                            <div class="unit-selection-box">
                              <span class="unit-count">1</span>
                              <button class="unit-increment-button"><img src="images/icons/dropdown_arrow.png" alt="Up arrow" width="12"></button>
                              <button class="unit-decrement-button"><img src="images/icons/dropdown_arrow.png" alt="Down arrow" width="12"></button><br>
                            </div>
                            <button class="unit-add-button carousel">Add</button>
                          </div>
                        </div>
                      </div>
                    </c:if>
                  </c:forEach>
                </div>
                <button class="carousel-btn carousel-left-btn" onclick="changeSlide(-1)"><img src="images/icons/arrow_left.png" alt="Left arrow" width="30"></button>
                <button class="carousel-btn carousel-right-btn" onclick="changeSlide(1)"><img src="images/icons/arrow_right.png" alt="Right arrow" width="30"></button>
              </div>

              <!-- Store List -->
              <div id="store-list">
                <c:forEach var="product" items="${products}">
                  <div class="product">
                    <img class="product-img" src="${product.image}" alt="Product">
                    <div class="product-details">
                      <h5 class="product-name">${product.name}</h5>
                      <h5 class="product-price">$${product.price}</h5>
                    </div>
                    <div class="product-buttons">
                      <a class="btn btn-primary btn-basket"><img src="images/icons/basket.png" alt="Basket" width="25"></a>
                      <a class="btn btn-primary btn-custombasket"><img src="images/icons/custombasket.png" alt="Basket" width="25"></a>
                      <div class="store-unit-dropdown">
                        <div class="unit-selection-box">
                          <span class="unit-count">1</span>
                          <button class="unit-increment-button"><img src="images/icons/dropdown_arrow.png" alt="Up arrow" width="12"></button>
                          <button class="unit-decrement-button"><img src="images/icons/dropdown_arrow.png" alt="Down arrow" width="12"></button><br>
                        </div>
                        <button class="unit-add-button">Add</button>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </div>

            <!-- Baskets -->
            <div id="baskets-container">
              <jsp:include page="basket.jsp">
                <jsp:param name="visibility" value="enabled" />
                <jsp:param name="type" value="basket" />
                <jsp:param name="name" value="Basket" />
                <jsp:param name="basketSubtotalUntilCoupon" value="17.62" />
              </jsp:include>

              <jsp:include page="basket.jsp">
                <jsp:param name="visibility" value="disabled" />
                <jsp:param name="type" value="custom-basket" />
                <jsp:param name="name" value="Custom Basket" />
                <jsp:param name="basketSubtotalUntilCoupon" value="17.62" />
              </jsp:include>
            </div>

          </body>
        </html>