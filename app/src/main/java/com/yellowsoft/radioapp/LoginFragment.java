package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sriven on 4/26/2016.
 */
public class LoginFragment extends Fragment {
    TextView sign_in_hint,sign_up_hint,sign_in_txt,sign_up_txt;
    ViewFlipper viewFlipper;
    EditText email,name,password,username,password_log;
    LinearLayout sign_in_btn,sign_up_btn;
    FragmentTouchListner mCallBack;
    public interface FragmentTouchListner {
        public void log_in_success();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBack = (NavigationActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listner");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_activity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getView();
        sign_in_hint = (TextView) v.findViewById(R.id.sign_in_prompt);
        sign_up_hint = (TextView) v.findViewById(R.id.sign_up_prompt);
        sign_in_txt = (TextView) v.findViewById(R.id.sign_in_txt);
        sign_up_txt = (TextView) v.findViewById(R.id.sign_up_txt);

        viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper2);
        email = (EditText) v.findViewById(R.id.email_address);
        name = (EditText) v.findViewById(R.id.name);
        password = (EditText) v.findViewById(R.id.password);
        username = (EditText) v.findViewById(R.id.username);
        password_log = (EditText) v.findViewById(R.id.password_log);
        sign_in_btn = (LinearLayout) v.findViewById(R.id.sign_in_btn);
        sign_up_btn = (LinearLayout) v.findViewById(R.id.sign_up_btn);
        sign_up_hint.setText(Settings.getword(getActivity(), "signup_prompt"));
        sign_in_hint.setText(Settings.getword(getActivity(),"signin_prompt"));
        sign_in_txt.setText(Settings.getword(getActivity(),"signin"));
        sign_up_txt.setText(Settings.getword(getActivity(),"signup"));
        email.setHint(Settings.getword(getActivity(), "email"));
        password.setHint(Settings.getword(getActivity(), "password"));
        name.setHint(Settings.getword(getActivity(), "name"));
        username.setHint(Settings.getword(getActivity(), "name"));
        password_log.setHint(Settings.getword(getActivity(), "password"));

        sign_in_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(1);
            }
        });

        sign_up_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals(""))
                    Toast.makeText(getActivity(), Settings.getword(getActivity(), "enter_email"), Toast.LENGTH_SHORT).show();
                else if (name.getText().toString().equals(""))
                    Toast.makeText(getActivity(), Settings.getword(getActivity(), "pls_enter_name"), Toast.LENGTH_SHORT).show();
                else if (password.getText().toString().equals("") || password.getText().toString().length() < 6)
                    Toast.makeText(getActivity(), Settings.getword(getActivity(), "enter_vpass_5char"), Toast.LENGTH_SHORT).show();
                else {
                    get_data();
                }

            }
        });

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals(""))
                    Toast.makeText(getActivity(), Settings.getword(getActivity(), "enter_email"), Toast.LENGTH_SHORT).show();
                else if (password_log.getText().toString().equals("") || password_log.getText().toString().length() < 6)
                    Toast.makeText(getActivity(), Settings.getword(getActivity(), "enter_vpass_5char"), Toast.LENGTH_SHORT).show();
                else
                    log_in_user();
            }
        });




    }

    private void get_data(){

        final ProgressDialog progressDialog= new ProgressDialog(getActivity());
        progressDialog.setMessage(Settings.getword(getActivity(),"please_wait"));
        progressDialog.show();
        String url = null;

        try {
            url = Settings.SERVER_URL+"add-member.php?name="+ URLEncoder.encode(name.getText().toString(), "utf-8")+
                    "&email="+ URLEncoder.encode(email.getText().toString(), "utf-8")
                    +"&password="+ URLEncoder.encode(password.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("register url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.e("response is",jsonObject.toString());
                try{
                    Log.e("response is",jsonObject.getString("response"));
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String result = jsonObject1.getString("status");
                    if(result.equals("Success")) {
                        String member_id = jsonObject1.getString("member_id");
                      //  Toast.makeText(getActivity(), Settings.getword(getActivity(),"acc_crer_succ"), Toast.LENGTH_SHORT).show();
                        Settings.set_user_id(getActivity(), member_id);
                        mCallBack.log_in_success();
                       // viewFlipper.setDisplayedChild(1);
                    }
                    else{
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Toast.makeText(getActivity(), Settings.getword(getActivity(), "cant_rech_server"), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void log_in_user(){
        final ProgressDialog progressDialog= new ProgressDialog(getActivity());
        progressDialog.setMessage(Settings.getword(getActivity(), "please_wait"));
        progressDialog.show();
        String url = null;
        try {
            url = Settings.SERVER_URL+"login.php?email="+username.getText().toString()+
                    "&password="+ URLEncoder.encode(password_log.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.e("response is", jsonObject.toString());
                try{
                    Log.e("response is",jsonObject.getString("response"));
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String result = jsonObject1.getString("status");
                    if(result.equals("Success")) {
                        String name = jsonObject1.getString("name");
                        String member_id = jsonObject1.getString("member_id");
                        Log.e("act_code:", name);
                        Settings.set_user_id(getActivity(), member_id);
                        //Toast.makeText(getActivity(), Settings.getword(getActivity(), "sign_succ"), Toast.LENGTH_SHORT).show();
                        mCallBack.log_in_success();
                    }
                    else{
                        Toast.makeText(getActivity(), jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("error response is:", error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }


}
