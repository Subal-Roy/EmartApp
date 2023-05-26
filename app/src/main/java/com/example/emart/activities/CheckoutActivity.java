package com.example.emart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.emart.adapters.CartAdapter;
import com.example.emart.databinding.ActivityCheckoutBinding;
import com.example.emart.models.Product;
import com.example.emart.utilities.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;

    double totalPrice = 0;
    final int tax = 11;

    ProgressDialog progressDialog;
    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();

        cart = TinyCartHelper.getCart();

        for (Map.Entry<Item, Integer> item: cart.getAllItemsWithQty().entrySet()){
            Product product = (Product)item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        /* demo data
        products.add(new Product("Product 1", "---", "available",100,20,10,1));
        products.add(new Product("Product 2", "---", "available",100,20,10,1));
        products.add(new Product("Product 3", "---", "available",100,20,10,1));

         */
        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("Tk. %.2f",cart.getTotalPrice()));
                totalPrice = cart.getTotalPrice().doubleValue() * tax/100  + cart.getTotalPrice().doubleValue();
                binding.total.setText(String.format("Tk. "+totalPrice));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format("Tk. %.2f",cart.getTotalPrice()));
        totalPrice = cart.getTotalPrice().doubleValue() * tax/100  + cart.getTotalPrice().doubleValue();
        binding.total.setText(String.format("Tk. "+totalPrice));

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void processOrder(){
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject  dataObject = new JSONObject();
        JSONObject product_order = new JSONObject();
        try {
            product_order.put("address", binding.addressBox.getText().toString());
            product_order.put("buyer",binding.nameBox.getText().toString());
            product_order.put("comment",binding.commentBox.getText().toString());
            product_order.put("created_at", Calendar.getInstance().getTimeInMillis());
            product_order.put("last_update", Calendar.getInstance().getTimeInMillis());
            product_order.put("date_ship", Calendar.getInstance().getTimeInMillis());
            product_order.put("email",binding.emailBox.getText().toString());
            product_order.put("phone",binding.phoneBox.getText().toString());
            product_order.put("serial","9839580385038");
            product_order.put("shipping","");
            product_order.put("shipping_location","");
            product_order.put("shipping_rate","0.0");
            product_order.put("status","WAITING");
            product_order.put("tax",tax);
            product_order.put("total_fees",totalPrice);

            JSONArray product_order_detail = new JSONArray();
            for (Map.Entry<Item, Integer> item: cart.getAllItemsWithQty().entrySet()){
                Product product = (Product)item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);

                JSONObject productObj = new JSONObject();
                productObj.put("amount",quantity);
                productObj.put("price_item",product.getPrice());
                productObj.put("product_id",product.getId());
                productObj.put("product_name",product.getName());

                product_order_detail.put(productObj);

            }

            dataObject.put("product_order", product_order);
            dataObject.put("product_order_detail", product_order_detail);
            Log.e("err",dataObject.toString());

        }
        catch (JSONException e){

        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getString("status").equals("success")){
                        Toast.makeText(CheckoutActivity.this, "Success Order",Toast.LENGTH_SHORT).show();
                        String orderNumber = response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Successful")
                                .setCancelable(false)
                                        .setMessage("Your Order Number is: "+orderNumber)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                                        intent.putExtra("orderCode",orderNumber);
                                        startActivity(intent);
                                    }
                                }).show();
                        Log.e("res",response.toString());
                    }
                    else{
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Failed")
                                .setCancelable(false)
                                .setMessage("Something went wrong, please try again.")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        Toast.makeText(CheckoutActivity.this, "Failed Order",Toast.LENGTH_SHORT).show();

                        Log.e("res",response.toString());
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    //throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Security","secure_code");
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}