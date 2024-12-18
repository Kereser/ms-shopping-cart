package com.emazon.ms_shopping_cart;

public class ConsUtils {
  private ConsUtils() {}

  public static PathBuilder builderPath() {
    return new PathBuilder();
  }

  public static final String COMMA_DELIMITER = ",";
  public static final String COLON_DELIMITER = ":";
  public static final String EMPTY = "";
  public static final String ASC = "ASC";
  public static final String INTEGER_STR_0 = "0";
  public static final String INTEGER_STR_20 = "20";

  public static final String ARTICLE_ENTITY = "Article";
  public static final String MS_STOCK = "MS-STOCK";
  public static final String MS_TRANSACTIONS = "MS-TRANSACTIONS";
  public static final String MS_REPORT = "MS-REPORT";
  public static final String MS_STOCK_URL = "${external.feign.url.ms-stock}";
  public static final String MS_REPORT_URL = "${external.feign.url.ms-report}";
  public static final String MS_TRANSACTIONS_URL = "${external.feign.url.ms-transactions}";

  public static final String JWT_KEY = "${security.jwt.key.private}";
  public static final String JWT_USER = "${security.jwt.user.generator}";

  public static final String CLIENT = "CLIENT";
  public static final String ADMIN = "ADMIN";
  public static final String AUX_DEPOT = "AUX_DEPOT";

  public static final String WRONG_SORT_PARAM = "ASCss";
  public static final String NAME = "name";
  public static final String DIRECTION_PARAM = "direction";

  public static final String USER_ID = "userId";
  public static final String USERNAME = "USERNAME";
  public static final String PASSWORD = "password";
  public static final String ROLE = "ROLE_";
  public static final Long PLUS_30_MINUTES = 1800000L;

  public static final String UNEXPECTED_EXCEPTION = "Unexpected exception";
  public static final String BASIC = "basic";
  public static final Integer SUBSTRING_INDEX = 7;

  public static final String AUTHORITIES = "authorities";

  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";

  public static final String FIELD_MESSAGE = "$.message";
  public static final String FIELD_ITEMS = "$.fieldErrors.items";
  public static final String FIELD_DIRECTION = "$.fieldErrors.direction";
  public static final String FIELD_ERRORS_ID = "$.fieldErrors.id";
  public static final String FIELD_QUANTITY_PATH_ARRAY = "$.fieldErrors['items[].quantity']";
  public static final String FIELD_ARTICLE_ID_PATH_ARRAY = "$.fieldErrors['items[].articleId']";
  public static final String REQUIRED_BODY = "Required request body is missing";

  public static final Integer INTEGER_0 = 0;
  public static final Integer INTEGER_1 = 1;
  public static final Integer INTEGER_2 = 2;
  public static final Integer INTEGER_20 = 20;

  public static final Long LONG_0 = 0L;
  public static final Long LONG_1 = 1L;
  public static final Long LONG_2 = 2L;

  /*** DB ***/
  public static final boolean FALSE = false;

  /*** Queries ***/
  public static final String GET_ALL_CARTS = "FROM cart";

  /*** Routes ***/
  public static final String SWAGGER_URL = "/swagger-ui/**";
  public static final String SWAGGER_DOCS_URL = "/v3/api-docs/**";
  public static final String BASIC_URL = "/cart";
  public static final String ALL_URL = "/all";
  public static final String WITH_CHECKOUT_URL = "/checkout";
  public static final String WITH_CART_ID_URL = "/{cartId}";
  public static final String WITH_ARTICLE_ID_URL = "/{articleId}";
  public static final String WITH_ARTICLES_IDS_URL = "/{articleIds}";
  public static final String WITH_ARTICLES_URL = "/articles";
  public static final String WITH_PURCHASE_URL = "/purchase";
  public static final String WITH_SALES_URL = "/sales";
  public static final String WITH_ROLLBACK_URL = "/rollback";
  public static final String WITH_USER_URL = "/user";
  public static final String FRONT_URL = "http://localhost:4200";
  public static final String MATCH_ALL_URL = "/**";

  /*** Methods ***/
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String REQUESTED_WITH = "X-Requested-With";
  public static final String CONTENT_TYPE = "Content-Type";



  public static final String VALIDATE_STOCK_FOR_CART = BASIC_URL + WITH_ARTICLES_URL;

  public static final String DELETE_ITEM_ROUTE = WITH_CART_ID_URL + WITH_ARTICLES_URL + WITH_ARTICLE_ID_URL;
  public static final String GET_ALL_ITEMS_FOR_USER = WITH_USER_URL + WITH_ARTICLES_URL;
  public static final String GET_ALL_ITEMS_FROM_STOCK = BASIC_URL + WITH_ARTICLES_URL + WITH_ARTICLES_IDS_URL;
  public static final String GET_ITEMS_WITH_PRICE = WITH_ARTICLES_URL + WITH_ARTICLES_IDS_URL;

  public static final String PROCESS_PURCHASE_ON_STOCK = BASIC_URL + WITH_ARTICLES_URL + WITH_PURCHASE_URL;
  public static final String PROCESS_CART_ROLLBACK_URL = BASIC_URL + WITH_ARTICLES_URL + WITH_ROLLBACK_URL;
  public static final String GET_ALL_ARTICLES_FROM_STOCK = WITH_ARTICLES_URL + ALL_URL;

  public static class PathBuilder {
    private final StringBuilder finalPath = new StringBuilder(BASIC_URL);

    public PathBuilder withCartId() {
      this.finalPath.append(WITH_CART_ID_URL);
      return this;
    }

    public PathBuilder withArticleId() {
      this.finalPath.append(WITH_ARTICLE_ID_URL);
      return this;
    }

    public PathBuilder withArticles() {
      this.finalPath.append(WITH_ARTICLES_URL);
      return this;
    }

    public PathBuilder withCheckout() {
      this.finalPath.append(WITH_CHECKOUT_URL);
      return this;
    }

    public PathBuilder withUser() {
      this.finalPath.append(WITH_USER_URL);
      return this;
    }

    public String build() {
      return finalPath.toString();
    }
  }
}
