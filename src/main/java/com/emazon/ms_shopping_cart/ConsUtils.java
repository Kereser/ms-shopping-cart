package com.emazon.ms_shopping_cart;

public class ConsUtils {
    private ConsUtils() {
    }

    public static PathBuilder builderPath() {
        return new PathBuilder();
    }

    public static final String ARTICLE_ENTITY = "Article";
    public static final String MS_STOCK = "MS-STOCK";
    public static final String MS_STOCK_URL = "${external.feign.url.ms-stock}";

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
    public static final String BASIC_URL = "/carts";
    public static final String WITH_CART_ID_URL = "/{cartId}";
    public static final String WITH_ARTICLE_ID_URL = "/{articleId}";
    public static final String WITH_ARTICLES_IDS_URL = "/{articleIds}";
    public static final String WITH_ARTICLES_URL = "/articles";

    public static final String DELETE_ITEM_ROUTE = WITH_CART_ID_URL + WITH_ARTICLES_URL + WITH_ARTICLE_ID_URL;
    public static final String GET_ALL_ITEMS = WITH_CART_ID_URL + WITH_ARTICLES_URL;
    public static final String GET_ALL_ITEMS_FROM_STOCK = BASIC_URL + WITH_ARTICLES_URL + WITH_ARTICLES_IDS_URL;
    public static final String GET_ITEMS_WITH_PRICE = WITH_ARTICLES_URL + WITH_ARTICLES_IDS_URL;

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

        public String build() {
            return finalPath.toString();
        }
    }
}
