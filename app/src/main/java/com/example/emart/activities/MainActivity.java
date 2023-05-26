package com.example.emart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.emart.R;
import com.example.emart.adapters.CategoryAdapter;
import com.example.emart.adapters.ProductAdapter;
import com.example.emart.databinding.ActivityMainBinding;
import com.example.emart.models.Category;
import com.example.emart.models.Product;
import com.example.emart.utilities.Constants;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;

    ProductAdapter productAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCategoris();
        initProducts();
        initSlider();

    }

    private void initSlider() {
        binding.carousel.addData(new CarouselItem("https://th.bing.com/th/id/OIP.LAn9Q9RwF4wQ2VTUCOH2xgHaF7?w=254&h=202&c=7&r=0&o=5&dpr=1.3&pid=1.7", "Some Caption Here"));
        binding.carousel.addData(new CarouselItem("https://tutorials.mianasad.com/ecommerce/uploads/news/mens%20offers.jpg", "Some Caption Here"));
        binding.carousel.addData(new CarouselItem("https://i.pinimg.com/originals/21/4c/03/214c035eaee40a3cb5209af31dd6c99e.jpg", "Some Caption Here"));
    }

    void getCategories(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("status").equals("success")){
                        JSONArray categoriesArray = mainObj.getJSONArray("categories");

                        for(int i = 0; i < categoriesArray.length(); i++){
                            JSONObject object = categoriesArray.getJSONObject(i);

                            Category category = new Category(
                                    object.getString("name"),
                                    Constants.CATEGORIES_IMAGE_URL+ object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            categories.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }else{
                        //Do nothing
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    void getRecentProducts(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL + "?count=8";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString("status").equals("success")){
                        JSONArray productArray = object.getJSONArray("products");
                        for(int i = 0; i < productArray.length(); i++){
                            JSONObject childObj = productArray.getJSONObject(i);

                            Product product = new Product(
                                    childObj.getString("name"),
                                    Constants.PRODUCTS_IMAGE_URL+childObj.getString("image"),
                                    childObj.getString("status"),
                                    childObj.getDouble("price"),
                                    childObj.getDouble("price_discount"),
                                    childObj.getInt("stock"),
                                    childObj.getInt("id")
                            );
                            products.add(product);
                        }
                        productAdapter.notifyDataSetChanged();

                    }else{

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }


    void initCategoris(){
        categories = new ArrayList<>();

        /* Sample data

        categories.add(new Category("Sports", "https://tutorials.mianasad.com/ecommerce/uploads/category/1682611198060.png",
                "#18ab4e", "Some Description", 1));
        categories.add(new Category("Sports", "https://tutorials.mianasad.com/ecommerce/uploads/category/1682611198060.png",
                "#18ab4e", "Some Description", 2));
        categories.add(new Category("Sports", "https://tutorials.mianasad.com/ecommerce/uploads/category/1682611198060.png",
                "#18ab4e", "Some Description", 3));
        categories.add(new Category("Sports", "https://tutorials.mianasad.com/ecommerce/uploads/category/1682611198060.png",
                "#18ab4e", "Some Description", 4));
        categories.add(new Category("Sports", "https://tutorials.mianasad.com/ecommerce/uploads/category/1682611198060.png",
                "#18ab4e", "Some Description", 5));

         */

        categoryAdapter = new CategoryAdapter(this, categories);

        getCategories();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }
    void initProducts(){
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this,products);


        /*sample data
        products.add(new Product("Blender", "https://tutorials.mianasad.com/ecommerce/uploads/product/1684419605749.jpg",
                "", 100, 10, 14, 1));
        products.add(new Product("Blender", "https://tutorials.mianasad.com/ecommerce/uploads/product/1684419605749.jpg",
                "", 100, 10, 14, 2));
        products.add(new Product("Blender", "https://tutorials.mianasad.com/ecommerce/uploads/product/1684419605749.jpg",
                "", 100, 10, 14, 3));
        products.add(new Product("Blender", "https://tutorials.mianasad.com/ecommerce/uploads/product/1684419605749.jpg",
                "", 100, 10, 14, 4));
        products.add(new Product("Blender", "https://tutorials.mianasad.com/ecommerce/uploads/product/1684419605749.jpg",
                "", 100, 10, 14, 5));
        products.add(new Product("Blender", "https://tutorials.mianasad.com/ecommerce/uploads/product/1684419605749.jpg",
                "", 100, 10, 14, 6));

        productAdapter = new ProductAdapter(this, products);
        */

        getRecentProducts();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query",text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }
}