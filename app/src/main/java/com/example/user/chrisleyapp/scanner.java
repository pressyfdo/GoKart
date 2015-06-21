package com.example.user.chrisleyapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import google.zxing.integration.android.IntentIntegrator;
import google.zxing.integration.android.IntentResult;


public class scanner extends Activity implements View.OnClickListener {

    Button btn, complete_btn ;
    BluetoothAdapter
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    TableLayout table_layout;
    TextView textView;
    static SQLController sqlcon;
    ProgressDialog PD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        sqlcon=new SQLController(getApplicationContext());
        sqlcon.open();
        btn = (Button) findViewById(R.id.button);
        complete_btn = (Button) findViewById(R.id.btn1);
        table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        textView = (TextView) findViewById(R.id.textView);
        btn.setOnClickListener(this);
        complete_btn.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.button){

            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }

        if(v.getId() == R.id.btn1){
            Cursor c = sqlcon.readEntry();
            int rows = c.getCount();
            int cols = c.getColumnCount();
            c.moveToFirst();
            float total=0;
            for (int i = 0; i < rows; i++) {
                total = total + Float.parseFloat(c.getString(2));
                c.moveToNext();
            }
            textView.setText("Total : " + total);

        }
    }

    public void onActivityResult(int requestcode, int resultcode, Intent intent){

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestcode, resultcode, intent);
        if(scanningResult != null){
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            Toast toast = Toast.makeText(getApplicationContext(),"Format : "+scanFormat+"\n Content : "+scanContent,Toast.LENGTH_SHORT);
            toast.show();
            sendPostRequest(scanContent);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received...", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void sendPostRequest(String givenMsg) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            protected void onPreExecute(){
                super.onPreExecute();
                table_layout.removeAllViews();
            }

            private void BuildTable() {
                Toast toast = Toast.makeText(getApplicationContext(), "No scan data received...", Toast.LENGTH_SHORT);
                toast.show();
                sqlcon.open();
                Cursor c = sqlcon.readEntry();
                int rows = 1;//c.getCount();
                int cols = 1;//c.getColumnCount();
                //outer for loop
                /*for (int i = 0; i < rows; i++) {
                    TableRow row = new TableRow(getApplicationContext());
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                    // inner for loop
                    for (int j = 0; j < cols; j++) {
                        TextView tv = new TextView(getApplicationContext());
                        tv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                        tv.setBackgroundResource(R.drawable.cell_shape);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(18);
                        tv.setPadding(0, 5, 0, 5);
                        tv.setText("chumma");//c.getString(j));
                        row.addView(tv);
                    }
                    //c.moveToNext();
                    table_layout.addView(row);
                }*/
                sqlcon.close();
            }

            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];

                System.out.println("*** doInBackground ** paramUsername " + paramUsername );

                HttpClient httpClient = new DefaultHttpClient();

                // In a POST request, we don't pass the values in the URL.
                //Therefore we use only the web page URL as the parameter of the HttpPost argument
                HttpPost httpPost = new HttpPost("http://192.168.43.224/http/test.php");

                // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                //uniquely separate by the other end.
                //To achieve that we use BasicNameValuePair
                //Things we need to pass with the POST request
                BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("message", paramUsername);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(usernameBasicNameValuePair);

                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                JSONObject posts;
                String name = null;
                String price=null;
                String weight=null;
                try {
                    posts = new JSONObject(result);
                    name=posts.getString("product_name");
                    price=posts.getString("price");
                    weight=posts.getString("weight");
                    Toast.makeText(getApplicationContext(), "HTTP POST is working..." + name + "\n" + price + "\n" + weight, Toast.LENGTH_LONG).show();

                    sqlcon.insertData(name, price, weight);

                    //BuildTable();
                    //Toast toast = Toast.makeText(getApplicationContext(), "No scan data received...", Toast.LENGTH_SHORT);
                    //toast.show();

                    Cursor c = null;
                    c = sqlcon.readEntry();
                    int rows = c.getCount();
                    int cols = c.getColumnCount();
                    c.moveToFirst();
                    //outer for loop
                    TableRow r = new TableRow(table_layout.getContext());
                    r.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                    TextView t = new TextView(r.getContext());
                    t.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    //tv.setBackgroundResource(R.drawable.cell_shape);
                    t.setGravity(Gravity.CENTER);
                    t.setTextSize(18);
                    t.setPadding(0, 5, 0, 5);
                    t.setText("S.No");
                    r.addView(t);
                    TextView t1 = new TextView(r.getContext());
                    t1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    t1.setGravity(Gravity.CENTER);
                    t1.setTextSize(18);
                    t1.setPadding(0, 5, 0, 5);
                    t1.setText("Product Name");
                    r.addView(t1);
                    TextView t2 = new TextView(r.getContext());
                    t2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    t2.setGravity(Gravity.CENTER);
                    t2.setTextSize(18);
                    t2.setPadding(0, 5, 0, 5);
                    t2.setText("Price");
                    r.addView(t2);
                    TextView t3 = new TextView(r.getContext());
                    t3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    t3.setGravity(Gravity.CENTER);
                    t3.setTextSize(18);
                    t3.setPadding(0, 5, 0, 5);
                    t3.setText("Weight");
                    r.addView(t3);
                    table_layout.addView(r);

                    for (int i = 0; i < rows; i++) {
                    TableRow row = new TableRow(table_layout.getContext());
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));

                    // inner for loop
                    for (int j = 0; j < cols; j++) {
                        TextView tv = new TextView(row.getContext());
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        //tv.setBackgroundResource(R.drawable.cell_shape);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(18);
                        tv.setPadding(0, 5, 0, 5);
                        tv.setText(c.getString(j));
                        row.addView(tv);

                    }
                    c.moveToNext();
                    table_layout.addView(row);
                    }
                    bluetoothAdapter.disable();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }
                    Set<BluetoothDevice> boundDevices = bluetoothAdapter.getBondedDevices();
                    Toast toast1 = Toast.makeText(getApplicationContext(), "String transferred to " + boundDevices.size(), Toast.LENGTH_SHORT);
                    toast1.show();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (boundDevices.size() > 0) {
                        BluetoothDevice[] devices = (BluetoothDevice[]) boundDevices.toArray();
                        BluetoothDevice device = devices[0];
                        ParcelUuid[] uuids = device.getUuids();
                        try {
                            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            socket.connect();
                            String str = "*100";
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(str.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //sqlcon.close();

               /* if(result.equals(result)){
                     Toast.makeText(getApplicationContext(), "HTTP POST is working..." + name + "\n" + price + "\n" + weight, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Invalid POST req...", Toast.LENGTH_LONG).show();
                }*/
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenMsg);
    }

}